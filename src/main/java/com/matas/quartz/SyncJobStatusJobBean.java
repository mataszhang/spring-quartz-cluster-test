package com.matas.quartz;

import javax.annotation.Resource;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.matas.service.ScheduleJobService;
import com.matas.utils.SpringUtils;

/**
 * 状态同步
 * @author MATAS
 *
 */
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class SyncJobStatusJobBean extends QuartzJobBean {
	public static final String JOB_NAME="SYNC-JOB-STATUS";
	public static final String JOB_GROUP="SYSTEM-JOB";

    /* 日志对象 */
    private static final Logger LOG = LoggerFactory.getLogger(SyncJobStatusJobBean.class);
    @Resource
    private ScheduleJobService service;
    
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        LOG.info("执行任务状态同步任务");
        service.syncStatus();
    }
}
