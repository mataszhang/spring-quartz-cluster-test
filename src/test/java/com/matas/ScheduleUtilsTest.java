package com.matas;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.matas.enums.ScheduleType;
import com.matas.enums.SimpleTimeUnit;
import com.matas.quartz.AsyncJobBean;
import com.matas.quartz.SyncJobBean;
import com.matas.quartz.SyncJobStatusJobBean;
import com.matas.utils.ScheduleUtils;
import com.matas.vo.ScheduleJobVo;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:applicationContext.xml" })
public class ScheduleUtilsTest {
	@Resource
	private Scheduler scheduler;
	@Test
	public void testStart() throws Exception{
		System.in.read();
	}
	

	@Test
	public void testCreate() throws Exception {
		ScheduleJobVo vo = new ScheduleJobVo();
		vo.setJobName("test001");
		vo.setDescription("测试001");
		vo.setJobBean(AsyncJobBean.class.getName());
		vo.setJobGroup("test");
		vo.setJobTrigger("test-triger001");
		vo.setScheduleTimeUnit(SimpleTimeUnit.SECOND);
		vo.setScheduleCount(1);
		vo.setScheduleTimeValue(10L);
		vo.setScheduleType(ScheduleType.SIMPLE);
		ScheduleUtils.createScheduleJob(scheduler, vo);
		System.in.read();
	}
	
	
	@Test
	public void testCreate3() throws Exception {
		ScheduleJobVo vo = new ScheduleJobVo();
		vo.setJobName("test003");
		vo.setDescription("测试003");
		vo.setJobBean(SyncJobStatusJobBean.class.getName());
		vo.setJobGroup("test");
		vo.setJobTrigger("test-triger003");
		vo.setScheduleTimeUnit(SimpleTimeUnit.SECOND);
		vo.setScheduleTimeValue(10L);
		vo.setScheduleType(ScheduleType.SIMPLE);
		ScheduleUtils.createScheduleJob(scheduler, vo);
		System.in.read();
	}
	
	
	@Test
	public void testCreate2() throws Exception {
		ScheduleJobVo vo = new ScheduleJobVo();
		vo.setJobName("test002");
		vo.setDescription("测试002");
		vo.setJobBean(AsyncJobBean.class.getName());
		vo.setJobGroup("test");
		vo.setJobTrigger("test-triger002");
		vo.setScheduleTimeUnit(SimpleTimeUnit.SECOND);
		vo.setScheduleType(ScheduleType.CRON);
		vo.setCronExpression("0/2 * * * * ?");
		ScheduleUtils.createScheduleJob(scheduler, vo);
		System.in.read();
	}
	

	@Test
	public void testGetJobDetail() throws Exception {
		JobDetail jobDetail = ScheduleUtils.getJobDetail(scheduler, "test001", "test");
		System.out.println(jobDetail.getJobClass());
	}

	@Test
	public void testDelJob() throws Exception {
		ScheduleUtils.deleteScheduleJob(scheduler, "test001", "test");
	}
	
	@Test
	public void testDelJob2() throws Exception {
		ScheduleUtils.deleteScheduleJob(scheduler, "test002", "test");
	}

	@Test
	public void testRunOnce() throws Exception {
		ScheduleUtils.runOnce(scheduler, "test001", "test");
	}

	@Test
	public void testPause() throws Exception {
		ScheduleUtils.pauseJob(scheduler, "test001", "test");
	}
	
	@Test
	public void testResume() throws Exception {
		ScheduleUtils.resumeJob(scheduler, "test001", "test");
	}

	@Test
	public void testUpdateJob() throws Exception {
		ScheduleJobVo vo = new ScheduleJobVo();
		vo.setJobName("test001");
		vo.setDescription("测试001");
		vo.setJobBean(AsyncJobBean.class.getName());
		vo.setJobGroup("test");
		vo.setJobTrigger("test-triger001");
		vo.setScheduleTimeUnit(SimpleTimeUnit.SECOND);
		vo.setScheduleTimeValue(3L);
		vo.setScheduleType(ScheduleType.SIMPLE);
		ScheduleUtils.updateScheduleJob(scheduler, vo);
		System.in.read();
	}
	
	
	@Test
	public void testUpdate2() throws Exception {
		ScheduleJobVo vo = new ScheduleJobVo();
		vo.setJobName("test002");
		vo.setDescription("测试002");
		vo.setJobBean(SyncJobBean.class.getName());
		vo.setJobGroup("test");
		vo.setJobTrigger("test-triger002");
		vo.setScheduleTimeUnit(SimpleTimeUnit.SECOND);
		vo.setScheduleType(ScheduleType.CRON);
		vo.setCronExpression("0/1 * * * * ?");
		ScheduleUtils.updateScheduleJob(scheduler, vo);
		System.in.read();
	}

}
