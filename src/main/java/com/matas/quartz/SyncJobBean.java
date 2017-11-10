package com.matas.quartz;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.matas.event.ScheduleJobInit;
import com.matas.utils.SpringUtils;
import com.matas.vo.ScheduleJobVo;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class SyncJobBean extends QuartzJobBean {

	/* 日志对象 */
	private static final Logger LOG = LoggerFactory.getLogger(SyncJobBean.class);
private String a="";
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
		LOG.info("SyncJobFactory execute");
		try {
			context.getScheduler().getContext().get("applicationContextKey");
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
		ScheduleJobInit bean = SpringUtils.getBean(ScheduleJobInit.class);
		JobDataMap mergedJobDataMap = context.getMergedJobDataMap();
		ScheduleJobVo scheduleJob = (ScheduleJobVo) mergedJobDataMap.get(ScheduleJobVo.JOB_PARAM_KEY);
		System.out.println(bean + ",jobName:" + scheduleJob.getJobName());
	}
}
