package com.matas.service.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.quartz.JobDataMap;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ParameterizedPreparedStatementSetter;
import org.springframework.stereotype.Service;

import com.dexcoder.dal.JdbcDao;
import com.dexcoder.dal.build.Criteria;
import com.matas.enums.MisfirePocliy;
import com.matas.enums.ScheduleType;
import com.matas.enums.SimpleTimeUnit;
import com.matas.model.ScheduleJob;
import com.matas.service.ScheduleJobService;
import com.matas.utils.IdWorker;
import com.matas.utils.ScheduleUtils;
import com.matas.vo.ScheduleJobVo;

/**
 * 定时任务服务实现
 * 
 * @author MATAS
 *
 */
@Service
public class ScheduleJobServiceImpl implements ScheduleJobService {

	/** 调度工厂Bean */
	@Autowired
	private Scheduler scheduler;

	/** 通用dao */
	@Autowired
	private JdbcDao jdbcDao;
	@Resource
	private JdbcTemplate jdbcTemplate;

	public void syncStatus() {
		List<ScheduleJob> scheduleJobList = jdbcDao.queryList(Criteria.select(ScheduleJob.class).where("status", "<>", new String[] { "COMPLETE" }));
		if (CollectionUtils.isEmpty(scheduleJobList)) {
			return;
		}

		List<ScheduleJob> updateList = new ArrayList<ScheduleJob>();
		for (ScheduleJob scheduleJob : scheduleJobList) {
			Long scheduleJobId = scheduleJob.getScheduleJobId();
			String jobName = scheduleJob.getJobName();
			String jobGroup = scheduleJob.getJobGroup();

			Trigger trigger = ScheduleUtils.getTrigger(scheduler, jobName, jobGroup);
			if (null == trigger) {
				scheduleJob.setStatus("COMPLETE");
			} else {
				try {
					JobDataMap jobDataMap = trigger.getJobDataMap();
					if (jobDataMap == null) {
						scheduleJob.setStatus("COMPLETE");
					} else {
						ScheduleJobVo vo = (ScheduleJobVo) jobDataMap.get(ScheduleJobVo.JOB_PARAM_KEY);
						if (!scheduleJobId.equals(vo.getScheduleJobId())) {
							scheduleJob.setStatus("COMPLETE");
						} else {
							Trigger.TriggerState triggerState = scheduler.getTriggerState(trigger.getKey());
							scheduleJob.setStatus(triggerState.name());
						}
					}
				} catch (SchedulerException e) {
					e.printStackTrace();
				}
			}
			updateList.add(scheduleJob);
		}

		/**
		 * 批量更新
		 */
		jdbcTemplate.batchUpdate("UPDATE schedule_job SET status=? WHERE schedule_job_id =?", updateList, 50, new ParameterizedPreparedStatementSetter<ScheduleJob>() {
			@Override
			public void setValues(PreparedStatement ps, ScheduleJob job) throws SQLException {
				if (null != job) {
					ps.setString(1, job.getStatus());
					ps.setLong(2, job.getScheduleJobId());
				}
			}
		});

	}

	public Long insert(ScheduleJobVo scheduleJobVo) {
		Long id = IdWorker.genUniqueId();
		scheduleJobVo.setScheduleJobId(id);
		ScheduleJob scheduleJob = vo2entity(scheduleJobVo);
		Date now = new Date();
		scheduleJob.setCreateTime(now);
		scheduleJob.setModifyTime(now);
		ScheduleUtils.createScheduleJob(scheduler, scheduleJobVo);
		jdbcDao.insert(scheduleJob);
		return id;
	}

	private ScheduleJob vo2entity(ScheduleJobVo scheduleJobVo) {
		ScheduleJob scheduleJob = new ScheduleJob();
		BeanUtils.copyProperties(scheduleJobVo, scheduleJob);
		ScheduleType scheduleType = scheduleJobVo.getScheduleType();
		SimpleTimeUnit scheduleTimeUnit = scheduleJobVo.getScheduleTimeUnit();
		MisfirePocliy misfirePolicy = scheduleJobVo.getMisfirePolicy();
		if (null != scheduleType) {
			scheduleJob.setScheduleType(scheduleType.name());
		}
		if (null != scheduleTimeUnit) {
			scheduleJob.setScheduleTimeUnit(scheduleTimeUnit.name());
		}
		if (null != misfirePolicy) {
			scheduleJob.setMisfirePolicy(misfirePolicy.name());
		}
		return scheduleJob;
	}

	public void update(ScheduleJobVo scheduleJobVo) {
		ScheduleJob scheduleJob = scheduleJobVo.getTargetObject(ScheduleJob.class);
		ScheduleUtils.updateScheduleJob(scheduler, scheduleJobVo);
		jdbcDao.update(scheduleJob);
	}

	public void delUpdate(ScheduleJobVo scheduleJobVo) {
		ScheduleJob scheduleJob = scheduleJobVo.getTargetObject(ScheduleJob.class);
		// 先删除
		ScheduleUtils.deleteScheduleJob(scheduler, scheduleJob.getJobName(), scheduleJob.getJobGroup());
		// 再创建
		ScheduleUtils.createScheduleJob(scheduler, scheduleJobVo);
		// 数据库直接更新即可
		jdbcDao.update(scheduleJob);
	}

	public void delete(Long scheduleJobId) {
		ScheduleJob scheduleJob = jdbcDao.get(ScheduleJob.class, scheduleJobId);
		// 删除运行的任务
		ScheduleUtils.deleteScheduleJob(scheduler, scheduleJob.getJobName(), scheduleJob.getJobGroup());
		// 删除数据
		jdbcDao.delete(ScheduleJob.class, scheduleJobId);
	}

	public void runOnce(Long scheduleJobId) {
		ScheduleJob scheduleJob = jdbcDao.get(ScheduleJob.class, scheduleJobId);
		ScheduleUtils.runOnce(scheduler, scheduleJob.getJobName(), scheduleJob.getJobGroup());
	}

	public void pauseJob(Long scheduleJobId) {
		ScheduleJob scheduleJob = jdbcDao.get(ScheduleJob.class, scheduleJobId);
		ScheduleUtils.pauseJob(scheduler, scheduleJob.getJobName(), scheduleJob.getJobGroup());
		// 演示数据库就不更新了
	}

	public void resumeJob(Long scheduleJobId) {
		ScheduleJob scheduleJob = jdbcDao.get(ScheduleJob.class, scheduleJobId);
		ScheduleUtils.resumeJob(scheduler, scheduleJob.getJobName(), scheduleJob.getJobGroup());
		// 演示数据库就不更新了
	}

	public ScheduleJobVo get(Long scheduleJobId) {
		ScheduleJob scheduleJob = jdbcDao.get(ScheduleJob.class, scheduleJobId);
		return scheduleJob.getTargetObject(ScheduleJobVo.class);
	}

	public List<ScheduleJobVo> queryList(ScheduleJobVo scheduleJobVo) {
		List<ScheduleJob> scheduleJobs = jdbcDao.queryList(vo2entity(scheduleJobVo));
		List<ScheduleJobVo> scheduleJobVoList = new ArrayList<ScheduleJobVo>();
		for (ScheduleJob job : scheduleJobs) {
			ScheduleJobVo vo = new ScheduleJobVo();
			BeanUtils.copyProperties(job, vo);
			vo.setScheduleTimeUnit(SimpleTimeUnit.valueOf(job.getScheduleTimeUnit()));
			scheduleJobVoList.add(vo);
		}

		return scheduleJobVoList;
	}

	/**
	 * 获取运行中的job列表
	 * 
	 * @return
	 */
	public List<ScheduleJobVo> queryExecutingJobList() {
		try {
			// 存放结果集
			List<ScheduleJobVo> jobList = new ArrayList<ScheduleJobVo>();

			// 获取scheduler中的JobGroupName
			for (String group : scheduler.getJobGroupNames()) {
				// 获取JobKey 循环遍历JobKey
				for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.<JobKey> groupEquals(group))) {
					List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobKey);
					Trigger trigger = triggers.iterator().next();
					JobDataMap jobDataMap = trigger.getJobDataMap();
					ScheduleJobVo scheduleJob = (ScheduleJobVo) jobDataMap.get(ScheduleJobVo.JOB_PARAM_KEY);
					Trigger.TriggerState triggerState = scheduler.getTriggerState(trigger.getKey());
					scheduleJob.setStatus(triggerState.name());
					// 获取正常运行的任务列表
					if (triggerState.name().equals("NORMAL")) {
						jobList.add(scheduleJob);
					}
				}
			}

			/** 非集群环境获取正在执行的任务列表 */
			/**
			 * List<JobExecutionContext> executingJobs =
			 * scheduler.getCurrentlyExecutingJobs(); List
			 * <ScheduleJobVo> jobList = new ArrayList
			 * <ScheduleJobVo>(executingJobs.size()); for (JobExecutionContext
			 * executingJob : executingJobs) { ScheduleJobVo job = new
			 * ScheduleJobVo(); JobDetail jobDetail =
			 * executingJob.getJobDetail(); JobKey jobKey = jobDetail.getKey();
			 * Trigger trigger = executingJob.getTrigger();
			 * job.setJobName(jobKey.getName());
			 * job.setJobGroup(jobKey.getGroup());
			 * job.setJobTrigger(trigger.getKey().getName());
			 * Trigger.TriggerState triggerState =
			 * scheduler.getTriggerState(trigger.getKey());
			 * job.setStatus(triggerState.name()); if (trigger instanceof
			 * CronTrigger) { CronTrigger cronTrigger = (CronTrigger) trigger;
			 * String cronExpression = cronTrigger.getCronExpression();
			 * job.setCronExpression(cronExpression); } jobList.add(job); }
			 */

			return jobList;
		} catch (SchedulerException e) {
			return null;
		}

	}
}
