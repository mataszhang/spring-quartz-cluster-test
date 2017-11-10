package com.matas.event;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.matas.enums.ScheduleType;
import com.matas.enums.SimpleTimeUnit;
import com.matas.quartz.SyncJobStatusJobBean;
import com.matas.service.ScheduleJobService;
import com.matas.utils.ScheduleUtils;
import com.matas.vo.ScheduleJobVo;

 
@Component
public class ScheduleJobInit {

	/** 日志对象 */
	private static final Logger LOG = LoggerFactory.getLogger(ScheduleJobInit.class);

	/** 定时任务service */
	@Autowired
	private ScheduleJobService scheduleJobService;
	@Resource
	private Scheduler scheduler;

	/**
	 * 项目启动时初始化
	 */
	@PostConstruct
	public void init() {
		LOG.info("初始化系统定时任务");
		
		ScheduleJobVo vo = new ScheduleJobVo();
		String jobName = SyncJobStatusJobBean.JOB_NAME;
		String jobGroup = SyncJobStatusJobBean.JOB_GROUP;
		
		vo.setJobName(jobName);
		vo.setJobGroup(jobGroup);
		List<ScheduleJobVo> jobList = scheduleJobService.queryList(vo);
		if (CollectionUtils.isEmpty(jobList)) {
			LOG.info("任务状态同步Job不存在，将创建");
			vo.setDescription("任务状态同步");
			vo.setJobBean(SyncJobStatusJobBean.class.getName());
			vo.setJobTrigger(jobName);
			vo.setScheduleTimeUnit(SimpleTimeUnit.MINUTE);
			vo.setScheduleTimeValue(1L);
			vo.setScheduleType(ScheduleType.SIMPLE);
			scheduleJobService.insert(vo);
		} else {
			try {
				Trigger.TriggerState triggerState = scheduler.getTriggerState(TriggerKey.triggerKey(jobName, jobGroup));
				if("NORMAL".equals(triggerState.name())){
					LOG.info("任务状态同步Job正常");
				}else{
					ScheduleUtils.resumeJob(scheduler,jobName,jobGroup);
				}
			} catch (SchedulerException e) {
				e.printStackTrace();
				LOG.info("恢复任务状态同步Job");
				ScheduleUtils.resumeJob(scheduler,jobName,jobGroup);
			}
		}
	}
}
