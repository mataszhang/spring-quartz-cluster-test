package com.matas.vo;

import java.util.Date;

import com.dexcoder.commons.pager.Pageable;
import com.matas.enums.MisfirePocliy;
import com.matas.enums.ScheduleType;
import com.matas.enums.SimpleTimeUnit;


public class ScheduleJobVo extends Pageable {

	private static final long serialVersionUID = -4216107640768329946L;

	/** 任务调度的参数key */
	public static final String JOB_PARAM_KEY = "jobParam";
	/** 任务id */
	private Long scheduleJobId;

	/** 任务名称 */
	private String jobName;

	/** 任务别名 */
	private String aliasName;

	/** 任务分组 */
	private String jobGroup;

	/** 触发器 */
	private String jobTrigger;

	/** 任务状态 */
	private String status;

	/** 调度类型【枚举】 */
	private ScheduleType scheduleType;

	/** 调度时间单位【枚举】SimpleTimeUnit */
	private SimpleTimeUnit scheduleTimeUnit;

	/** 调度时间值 */
	private Long scheduleTimeValue;

	/** 执行次数，-1表示永远执行 */
	private Integer scheduleCount;

	/** misfire策略【枚举】 */
	private MisfirePocliy misfirePolicy;

	/** 任务运行时间表达式 */
	private String cronExpression;

	/** 任务类 */
	private String jobBean;

	/** 任务描述 */
	private String description;

	/** 创建时间 */
	private Date createTime;

	/** 修改时间 */
	private Date modifyTime;
	
	public Long getScheduleJobId() {
		return scheduleJobId;
	}

	public void setScheduleJobId(Long scheduleJobId) {
		this.scheduleJobId = scheduleJobId;
	}

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	public String getAliasName() {
		return aliasName;
	}

	public void setAliasName(String aliasName) {
		this.aliasName = aliasName;
	}

	public String getJobGroup() {
		return jobGroup;
	}

	public void setJobGroup(String jobGroup) {
		this.jobGroup = jobGroup;
	}

	public String getJobTrigger() {
		return jobTrigger;
	}

	public void setJobTrigger(String jobTrigger) {
		this.jobTrigger = jobTrigger;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public ScheduleType getScheduleType() {
		return scheduleType;
	}

	public void setScheduleType(ScheduleType scheduleType) {
		this.scheduleType = scheduleType;
	}

	public Long getScheduleTimeValue() {
		return scheduleTimeValue;
	}

	public void setScheduleTimeValue(Long scheduleTimeValue) {
		this.scheduleTimeValue = scheduleTimeValue;
	}

	public Integer getScheduleCount() {
		return scheduleCount;
	}

	public void setScheduleCount(Integer scheduleCount) {
		this.scheduleCount = scheduleCount;
	}
 
	public SimpleTimeUnit getScheduleTimeUnit() {
		return scheduleTimeUnit;
	}

	public void setScheduleTimeUnit(SimpleTimeUnit scheduleTimeUnit) {
		this.scheduleTimeUnit = scheduleTimeUnit;
	}

	public MisfirePocliy getMisfirePolicy() {
		return misfirePolicy;
	}

	public void setMisfirePolicy(MisfirePocliy misfirePolicy) {
		this.misfirePolicy = misfirePolicy;
	}

	public String getCronExpression() {
		return cronExpression;
	}

	public void setCronExpression(String cronExpression) {
		this.cronExpression = cronExpression;
	}

	public String getJobBean() {
		return jobBean;
	}

	public void setJobBean(String jobBean) {
		this.jobBean = jobBean;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getModifyTime() {
		return modifyTime;
	}

	public void setModifyTime(Date modifyTime) {
		this.modifyTime = modifyTime;
	}

	@Override
	public String toString() {
		return "ScheduleJobVo [scheduleJobId=" + scheduleJobId + ", jobName=" + jobName + ", aliasName=" + aliasName + ", jobGroup=" + jobGroup + ", jobTrigger=" + jobTrigger
				+ ", status=" + status + ", scheduleType=" + scheduleType + ", scheduleTimeUnit=" + scheduleTimeUnit + ", scheduleTimeValue=" + scheduleTimeValue
				+ ", scheduleCount=" + scheduleCount + ", misfirePolicy=" + misfirePolicy + ", cronExpression=" + cronExpression + ", jobBean=" + jobBean + ", description="
				+ description + ", createTime=" + createTime + ", modifyTime=" + modifyTime + "]";
	}

}
