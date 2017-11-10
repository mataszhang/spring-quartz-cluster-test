package com.matas;

import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.matas.enums.ScheduleType;
import com.matas.enums.SimpleTimeUnit;
import com.matas.quartz.AsyncJobBean;
import com.matas.quartz.SyncJobStatusJobBean;
import com.matas.service.ScheduleJobService;
import com.matas.utils.JSONUtil;
import com.matas.vo.ScheduleJobVo;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:applicationContext.xml" })
public class ScheduleJobServiceImplTest {
	@Resource
	private ScheduleJobService service;

	@Test
	public void testInsert() throws Exception {
		ScheduleJobVo vo = new ScheduleJobVo();
		vo.setJobName("test0023");
		vo.setDescription("测试001");
		vo.setJobBean(AsyncJobBean.class.getName());
		vo.setJobGroup("test");
		vo.setJobTrigger("test-triger001");
		vo.setScheduleTimeUnit(SimpleTimeUnit.SECOND);
		vo.setScheduleCount(20);
		vo.setScheduleTimeValue(10L);
		vo.setScheduleType(ScheduleType.SIMPLE);
		service.insert(vo);

		System.in.read();
	}
	
	@Test
	public void testInsert3() throws Exception {
		ScheduleJobVo vo = new ScheduleJobVo();
		vo.setJobName("test004");
		vo.setDescription("测试003");
		vo.setJobBean(AsyncJobBean.class.getName());
		vo.setJobGroup("test");
		vo.setJobTrigger("test-triger001");
		vo.setScheduleTimeUnit(SimpleTimeUnit.MINUTE);
		//vo.setScheduleCount(20);
		vo.setScheduleTimeValue(1L);
		vo.setScheduleType(ScheduleType.SIMPLE);
		service.insert(vo);

		System.in.read();
	}

	@Test
	public void testInsert2() throws Exception {
		ScheduleJobVo vo = new ScheduleJobVo();
		vo.setJobName("test003");
		vo.setDescription("测试003");
		vo.setJobBean(SyncJobStatusJobBean.class.getName());
		vo.setJobGroup("test");
		vo.setJobTrigger("test-triger003");
		vo.setScheduleTimeUnit(SimpleTimeUnit.SECOND);
		vo.setScheduleTimeValue(10L);
		vo.setScheduleType(ScheduleType.SIMPLE);
		service.insert(vo);
		System.in.read();
	}
	
	@Test
	public void testDel() throws Exception{
			service.delete(927136464111448064L);
	}
	
	@Test
	public void testQueryList() throws Exception{
		List<ScheduleJobVo> queryExecutingJobList = service.queryExecutingJobList();
			System.out.println(JSONUtil.objectToJson(queryExecutingJobList));
	}
	
	@Test
	public void testQueryList2() throws Exception{
		ScheduleJobVo scheduleJobVo =new ScheduleJobVo();
		scheduleJobVo.setStatus(null);
		List<ScheduleJobVo> queryExecutingJobList = service.queryList(scheduleJobVo );
			System.out.println(JSONUtil.objectToJson(queryExecutingJobList));
	}

}
