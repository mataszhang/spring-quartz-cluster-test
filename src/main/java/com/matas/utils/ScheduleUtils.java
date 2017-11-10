package com.matas.utils;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.matas.enums.MisfirePocliy;
import com.matas.enums.SimpleTimeUnit;
import com.matas.exceptions.ScheduleException;
import com.matas.vo.ScheduleJobVo;

 
public class ScheduleUtils {

	/** 日志对象 */
	private static final Logger LOG = LoggerFactory.getLogger(ScheduleUtils.class);

	/**
	 * 获取触发器key
	 * 
	 * @param jobName
	 * @param jobGroup
	 * @return
	 */
	public static TriggerKey getTriggerKey(String jobName, String jobGroup) {

		return TriggerKey.triggerKey(jobName, jobGroup);
	}

	/**
	 * 获取表达式触发器
	 *
	 * @param scheduler
	 *            the scheduler
	 * @param jobName
	 *            the job name
	 * @param jobGroup
	 *            the job group
	 * @return cron trigger
	 */
	public static Trigger getTrigger(Scheduler scheduler, String jobName, String jobGroup) {
		try {
			TriggerKey triggerKey = TriggerKey.triggerKey(jobName, jobGroup);
			return scheduler.getTrigger(triggerKey);
		} catch (SchedulerException e) {
			LOG.error("获取定时任务CronTrigger出现异常", e);
			throw new ScheduleException("获取定时任务CronTrigger出现异常");
		}
	}

	/**
	 * 创建任务
	 *
	 * @param scheduler
	 *            the scheduler
	 * @param scheduleJob
	 *            the schedule job
	 */
	public static void createScheduleJob(Scheduler scheduler, ScheduleJobVo vo) {
		switch (vo.getScheduleType()) {
		case SIMPLE:
			createSimpleScheduleJob(scheduler, vo);
			break;
		case CRON:
			createCronScheduleJob(scheduler, vo);
			break;
		}
	}

	/**
	 * 创建CRON定时任务
	 * 
	 * @param scheduler
	 * @param bean
	 * @throws ScheduleException
	 */
	private static void createCronScheduleJob(Scheduler scheduler, ScheduleJobVo bean) throws ScheduleException {
		/** 任务名称 */
		String jobName = bean.getJobName();
		/** 任务分组 */
		String jobGroup = bean.getJobGroup();
		/** misfire策略【枚举】 */
		MisfirePocliy misfirePolicy = bean.getMisfirePolicy();
		/** 任务运行时间表达式 */
		String cronExpression = bean.getCronExpression();
		/** 任务类 */
		String jobBean = bean.getJobBean();
		/** 任务描述 */
		String description = bean.getDescription();

		Date now = new Date();
		bean.setCreateTime(now);
		bean.setModifyTime(now);

		Class<Job> jobClazz = getJobClass(jobBean);

		// 构建job信息
		JobDetail jobDetail = JobBuilder.newJob(jobClazz).withDescription(description).withIdentity(jobName, jobGroup).build();

		// 表达式调度构建器
		CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpression);
		setCronMisfire(misfirePolicy, scheduleBuilder);

		// 按新的cronExpression表达式构建一个新的trigger
		CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity(jobName, jobGroup).withSchedule(scheduleBuilder).build();
		String jobTrigger = trigger.getKey().getName();
		bean.setJobTrigger(jobTrigger);
		// 放入参数，运行时的方法可以获取
		jobDetail.getJobDataMap().put(ScheduleJobVo.JOB_PARAM_KEY, bean);
		trigger.getJobDataMap().put(ScheduleJobVo.JOB_PARAM_KEY, bean);
		try {
			scheduler.scheduleJob(jobDetail, trigger);
		} catch (SchedulerException e) {
			LOG.error("创建定时任务失败", e);
			throw new ScheduleException("创建定时任务失败");
		}
	}

	/**
	 * 创建SIMPLE定时任务
	 * 
	 * @param scheduler
	 * @param bean
	 * @throws ScheduleException
	 */
	private static void createSimpleScheduleJob(Scheduler scheduler, ScheduleJobVo bean) throws ScheduleException {
		/** 任务名称 */
		String jobName = bean.getJobName();
		/** 任务分组 */
		String jobGroup = bean.getJobGroup();
		/** 调度时间单位【枚举】IntervalUnit */
		SimpleTimeUnit scheduleTimeUnit = bean.getScheduleTimeUnit();
		/** 调度时间值 */
		Long scheduleTimeValue = bean.getScheduleTimeValue();
		/** 执行次数，-1表示永远执行 */
		Integer scheduleCount = bean.getScheduleCount();
		/** misfire策略【枚举】 */
		MisfirePocliy misfirePolicy = bean.getMisfirePolicy();
		/** 任务类 */
		String jobBean = bean.getJobBean();
		/** 任务描述 */
		String description = bean.getDescription();

		if (StringUtils.isEmpty(jobName)) {
			throw new ScheduleException("任务名称不能为空");
		}
		if (scheduleTimeUnit == null) {
			throw new ScheduleException("调度时间单位不能为空");
		}

		if (scheduleTimeValue == null || scheduleTimeValue <= 0) {
			throw new ScheduleException("调度时间值必须大于0");
		}

		Date now = new Date();
		bean.setCreateTime(now);
		bean.setModifyTime(now);

		Class<Job> jobClazz = getJobClass(jobBean);

		// 构建job信息
		JobDetail jobDetail = JobBuilder.newJob(jobClazz).withDescription(description).withIdentity(jobName, jobGroup).build();

		// 表达式调度构建器
		SimpleScheduleBuilder simpleSchedule = SimpleScheduleBuilder.simpleSchedule();

		if (null != scheduleCount) {
			if (scheduleCount == 0 || scheduleCount < -1) {
				throw new ScheduleException("调度时间值必须大于0或等于-1");
			}
			scheduleCount--;
		} else {
			scheduleCount = -1;
		}
		simpleSchedule.withRepeatCount(scheduleCount);

		setSimpleTimeunit(scheduleTimeUnit, scheduleTimeValue, simpleSchedule);
		setSimpleMisfile(misfirePolicy, simpleSchedule);

		// 按新的cronExpression表达式构建一个新的trigger
		SimpleTrigger trigger = TriggerBuilder.newTrigger().withIdentity(jobName, jobGroup).withSchedule(simpleSchedule).build();
		// 放入参数，运行时的方法可以获取
		jobDetail.getJobDataMap().put(ScheduleJobVo.JOB_PARAM_KEY, bean);
		trigger.getJobDataMap().put(ScheduleJobVo.JOB_PARAM_KEY, bean);
		try {
			scheduler.scheduleJob(jobDetail, trigger);
		} catch (SchedulerException e) {
			LOG.error("创建定时任务失败", e);
			throw new ScheduleException("创建定时任务失败");
		}
	}

	private static void setSimpleTimeunit(SimpleTimeUnit scheduleTimeUnit, Long scheduleTimeValue, SimpleScheduleBuilder simpleSchedule) {
		if (null != scheduleTimeUnit) {
			switch (scheduleTimeUnit) {
			case MILLI_SECOND:
				simpleSchedule.withIntervalInMilliseconds(scheduleTimeValue);
				break;
			case SECOND:
				simpleSchedule.withIntervalInSeconds(scheduleTimeValue.intValue());
				break;
			case MINUTE:
				simpleSchedule.withIntervalInMinutes(scheduleTimeValue.intValue());
				break;
			case HOUR:
				simpleSchedule.withIntervalInHours(scheduleTimeValue.intValue());
				break;
			default:
				break;
			}
		}
	}

	private static void setSimpleMisfile(MisfirePocliy misfirePolicy, SimpleScheduleBuilder simpleSchedule) {
		if (null != misfirePolicy) {
			switch (misfirePolicy) {
			case MISFIRE_INSTRUCTION_FIRE_NOW:
				simpleSchedule.withMisfireHandlingInstructionFireNow();
				break;
			case MISFIRE_INSTRUCTION_IGNORE_MISFIRE_POLICY:
				simpleSchedule.withMisfireHandlingInstructionIgnoreMisfires();
				break;
			case MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_EXISTING_COUNT:
				simpleSchedule.withMisfireHandlingInstructionNextWithExistingCount();
				break;
			case MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_REMAINING_COUNT:
				simpleSchedule.withMisfireHandlingInstructionNextWithRemainingCount();
				break;
			case MISFIRE_INSTRUCTION_RESCHEDULE_NOW_WITH_EXISTING_REPEAT_COUNT:
				simpleSchedule.withMisfireHandlingInstructionNowWithExistingCount();
				break;
			case MISFIRE_INSTRUCTION_RESCHEDULE_NOW_WITH_REMAINING_REPEAT_COUNT:
				simpleSchedule.withMisfireHandlingInstructionNowWithRemainingCount();
				break;
			default:
				break;
			}
		}
	}

	/**
	 * 更新CRON定时任务
	 * 
	 * @param scheduler
	 * @param bean
	 */
	private static void updateCronScheduleJob(Scheduler scheduler, ScheduleJobVo bean) {
		/** 任务名称 */
		String jobName = bean.getJobName();
		/** 任务分组 */
		String jobGroup = bean.getJobGroup();
		/** misfire策略【枚举】 */
		MisfirePocliy misfirePolicy = bean.getMisfirePolicy();
		/** 任务运行时间表达式 */
		String cronExpression = bean.getCronExpression();
		/** 任务类 */
		String jobBean = bean.getJobBean();
		/** 任务描述 */
		String description = bean.getDescription();

		Date now = new Date();
		bean.setModifyTime(now);

		Class<Job> jobClazz = getJobClass(jobBean);

		try {
			JobDetail jobDetail = scheduler.getJobDetail(getJobKey(jobName, jobGroup));
			JobBuilder jobBuilder = jobDetail.getJobBuilder();
			JobDataMap jobDataMap = jobDetail.getJobDataMap();
			jobDataMap.put(ScheduleJobVo.JOB_PARAM_KEY, bean);
			jobDetail = jobBuilder.ofType(jobClazz).withDescription(description).setJobData(jobDataMap).build();
			/**
			 * 覆盖原来的任务
			 */
			scheduler.addJob(jobDetail, true, true);
			TriggerKey triggerKey = ScheduleUtils.getTriggerKey(jobName, jobGroup);
			// 表达式调度构建器
			CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpression);
			setCronMisfire(misfirePolicy, scheduleBuilder);

			CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);

			// 按新的cronExpression表达式重新构建trigger
			trigger = trigger.getTriggerBuilder().withIdentity(triggerKey).withSchedule(scheduleBuilder).build();
			trigger.getJobDataMap().put(ScheduleJobVo.JOB_PARAM_KEY, bean);
			Trigger.TriggerState triggerState = scheduler.getTriggerState(trigger.getKey());
			// 按新的trigger重新设置job执行
			scheduler.rescheduleJob(triggerKey, trigger);
			if (triggerState.name().equalsIgnoreCase("PAUSED")) {
				pauseJob(scheduler, jobName, jobGroup);
			}
		} catch (SchedulerException e) {
			LOG.error("更新定时任务失败", e);
			throw new ScheduleException("更新定时任务失败");
		}
	}

	private static void setCronMisfire(MisfirePocliy misfirePolicy, CronScheduleBuilder scheduleBuilder) {
		if (null != misfirePolicy) {
			switch (misfirePolicy) {
			case MISFIRE_INSTRUCTION_DO_NOTHING:
				scheduleBuilder.withMisfireHandlingInstructionDoNothing();
				break;
			case MISFIRE_INSTRUCTION_FIRE_ONCE_NOW:
				scheduleBuilder.withMisfireHandlingInstructionFireAndProceed();
				break;
			case MISFIRE_INSTRUCTION_IGNORE_MISFIRE_POLICY:
				scheduleBuilder.withMisfireHandlingInstructionIgnoreMisfires();
				break;
			default:
				break;
			}
		}
	}

	/**
	 * 更新Simple定时任务
	 * 
	 * @param scheduler
	 * @param bean
	 */
	private static void updateSimpleScheduleJob(Scheduler scheduler, ScheduleJobVo bean) {
		/** 任务名称 */
		String jobName = bean.getJobName();
		/** 任务分组 */
		String jobGroup = bean.getJobGroup();
		/** 调度时间单位【枚举】IntervalUnit */
		SimpleTimeUnit scheduleTimeUnit = bean.getScheduleTimeUnit();
		/** 调度时间值 */
		Long scheduleTimeValue = bean.getScheduleTimeValue();
		/** 执行次数，-1表示永远执行 */
		Integer scheduleCount = bean.getScheduleCount();
		/** misfire策略【枚举】 */
		MisfirePocliy misfirePolicy = bean.getMisfirePolicy();
		/** 任务类 */
		String jobBean = bean.getJobBean();
		/** 任务描述 */
		String description = bean.getDescription();

		if (StringUtils.isEmpty(jobName)) {
			throw new ScheduleException("任务名称不能为空");
		}
		if (scheduleTimeUnit == null) {
			throw new ScheduleException("调度时间单位不能为空");
		}

		if (scheduleTimeValue == null || scheduleTimeValue <= 0) {
			throw new ScheduleException("调度时间值必须大于0");
		}

		Date now = new Date();
		bean.setModifyTime(now);

		Class<Job> jobClazz = getJobClass(jobBean);

		try {
			JobDetail jobDetail = getJobDetail(scheduler, jobName, jobGroup);
			if (null == jobDetail) {
				throw new ScheduleException("任务不存在:" + jobName + "[" + jobGroup + "]");
			}

			JobBuilder jobBuilder = jobDetail.getJobBuilder();
			JobDataMap jobDataMap = jobDetail.getJobDataMap();
			jobDataMap.put(ScheduleJobVo.JOB_PARAM_KEY, bean);
			jobDetail = jobBuilder.ofType(jobClazz).withDescription(description).setJobData(jobDataMap).build();
			/**
			 * 覆盖原来的任务
			 */
			scheduler.addJob(jobDetail, true, true);

			TriggerKey triggerKey = ScheduleUtils.getTriggerKey(jobName, jobGroup);
			// 表达式调度构建器
			SimpleScheduleBuilder simpleSchedule = SimpleScheduleBuilder.simpleSchedule();

			if (null != scheduleCount) {
				if (scheduleCount == 0 || scheduleCount < -1) {
					throw new ScheduleException("调度时间值必须大于0或等于-1");
				}
				scheduleCount--;
			} else {
				scheduleCount = -1;
			}
			simpleSchedule.withRepeatCount(scheduleCount);

			setSimpleTimeunit(scheduleTimeUnit, scheduleTimeValue, simpleSchedule);

			setSimpleMisfile(misfirePolicy, simpleSchedule);

			SimpleTrigger trigger = (SimpleTrigger) scheduler.getTrigger(triggerKey);
			// 按新的cronExpression表达式重新构建trigger
			trigger = trigger.getTriggerBuilder().withIdentity(triggerKey).withSchedule(simpleSchedule).build();
			trigger.getJobDataMap().put(ScheduleJobVo.JOB_PARAM_KEY, bean);
			Trigger.TriggerState triggerState = scheduler.getTriggerState(trigger.getKey());
			// 忽略状态为PAUSED的任务，解决集群环境中在其他机器设置定时任务为PAUSED状态后，集群环境启动另一台主机时定时任务全被唤醒的bug

			scheduler.rescheduleJob(triggerKey, trigger);
			if (triggerState.name().equalsIgnoreCase("PAUSED")) {
				pauseJob(scheduler, jobName, jobGroup);
			}
		} catch (SchedulerException e) {
			LOG.error("更新定时任务失败", e);
			throw new ScheduleException("更新定时任务失败");
		}
	}

	@SuppressWarnings("unchecked")
	private static Class<Job> getJobClass(String jobBean) {
		Class<Job> jobClazz = null;
		try {
			Class<?> clazz = Class.forName(jobBean);
			if (null == clazz) {
				throw new ScheduleException("创建Job失败:Job类加载失败");
			}
			if (Job.class.isAssignableFrom(clazz)) {
				jobClazz = (Class<Job>) clazz;
			} else {
				throw new ScheduleException("创建Job失败:Job类不是Job的子类");
			}
		} catch (Exception e) {
			LOG.error("创建定时任务失败", e);
			throw new ScheduleException("创建Job失败:" + e.getMessage());
		}
		return jobClazz;
	}

	/**
	 * 运行一次任务
	 * 
	 * @param scheduler
	 * @param jobName
	 * @param jobGroup
	 */
	public static void runOnce(Scheduler scheduler, String jobName, String jobGroup) {
		JobKey jobKey = JobKey.jobKey(jobName, jobGroup);
		try {
			scheduler.triggerJob(jobKey);
		} catch (SchedulerException e) {
			LOG.error("运行一次定时任务失败", e);
			throw new ScheduleException("运行一次定时任务失败");
		}
	}

	/**
	 * 暂停任务
	 * 
	 * @param scheduler
	 * @param jobName
	 * @param jobGroup
	 */
	public static void pauseJob(Scheduler scheduler, String jobName, String jobGroup) {

		JobKey jobKey = JobKey.jobKey(jobName, jobGroup);
		try {
			scheduler.pauseJob(jobKey);
		} catch (SchedulerException e) {
			LOG.error("暂停定时任务失败", e);
			throw new ScheduleException("暂停定时任务失败");
		}
	}

	/**
	 * 恢复任务
	 *
	 * @param scheduler
	 * @param jobName
	 * @param jobGroup
	 */
	public static void resumeJob(Scheduler scheduler, String jobName, String jobGroup) {

		JobKey jobKey = JobKey.jobKey(jobName, jobGroup);
		try {
			scheduler.resumeJob(jobKey);
		} catch (SchedulerException e) {
			LOG.error("暂停定时任务失败", e);
			throw new ScheduleException("暂停定时任务失败");
		}
	}

	/**
	 * 获取jobKey
	 *
	 * @param jobName
	 *            the job name
	 * @param jobGroup
	 *            the job group
	 * @return the job key
	 */
	public static JobKey getJobKey(String jobName, String jobGroup) {
		return JobKey.jobKey(jobName, jobGroup);
	}

	public static JobDetail getJobDetail(Scheduler scheduler, String jobName, String jobGroup) {
		try {
			return scheduler.getJobDetail(getJobKey(jobName, jobGroup));
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 更新定时任务
	 *
	 * @param scheduler
	 *            the scheduler
	 * @param scheduleJob
	 *            the schedule job
	 */
	public static void updateScheduleJob(Scheduler scheduler, ScheduleJobVo vo) {
		switch (vo.getScheduleType()) {
		case SIMPLE:
			updateSimpleScheduleJob(scheduler, vo);
			break;
		case CRON:
			updateCronScheduleJob(scheduler, vo);
			break;
		}
	}

	/**
	 * 删除定时任务
	 *
	 * @param scheduler
	 * @param jobName
	 * @param jobGroup
	 */
	public static void deleteScheduleJob(Scheduler scheduler, String jobName, String jobGroup) {
		try {
			scheduler.deleteJob(getJobKey(jobName, jobGroup));
		} catch (SchedulerException e) {
			LOG.error("删除定时任务失败", e);
			throw new ScheduleException("删除定时任务失败");
		}
	}
}
