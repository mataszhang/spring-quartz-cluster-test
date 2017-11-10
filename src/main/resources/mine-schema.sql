DROP TABLE IF EXISTS SCHEDULE_JOB ;

CREATE TABLE `SCHEDULE_JOB` (
  `schedule_job_id` BIGINT (20) AUTO_INCREMENT NOT NULL COMMENT '任务Id',
  `job_name` VARCHAR (255) DEFAULT NULL COMMENT '任务名称',
  `alias_name` VARCHAR (255) DEFAULT NULL COMMENT '任务别名',
  `job_group` VARCHAR (255) DEFAULT NULL COMMENT '任务分组',
  `job_trigger` VARCHAR (255) DEFAULT NULL COMMENT '触发器',
  `status` VARCHAR (255) NOT NULL DEFAULT 'NONE' COMMENT '任务状态',
  `schedule_type` VARCHAR (100) DEFAULT NULL COMMENT '调度类型【枚举】',
  `schedule_time_unit` VARCHAR (100) DEFAULT NULL COMMENT '调度时间单位【枚举】IntervalUnit',
  `schedule_time_value` BIGINT DEFAULT 0 COMMENT '调度时间值',
  `schedule_count` INT DEFAULT - 1 COMMENT '执行次数，-1表示永远执行',
  `misfire_policy` VARCHAR (255) DEFAULT NULL COMMENT 'misfire策略【枚举】',
  `cron_expression` VARCHAR (255) DEFAULT NULL COMMENT '任务运行时间表达式',
  `job_bean` VARCHAR (500) DEFAULT NULL COMMENT '任务类',
  `description` VARCHAR (255) DEFAULT NULL COMMENT '任务描述',
  `create_time` TIMESTAMP NULL DEFAULT NULL COMMENT '创建时间',
  `modify_time` TIMESTAMP NULL DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`schedule_job_id`)
) ENGINE = INNODB DEFAULT CHARSET = utf8 ;
 