CREATE TABLE `win_task_define` (
    `id`          BIGINT(20)   NOT NULL COMMENT '主键/task_id',
    `create_dt`   DATETIME(3)  NOT NULL DEFAULT NOW(3) COMMENT '创建日时(系统)',
    `modify_dt`   DATETIME(3)  NOT NULL DEFAULT '1000-01-01' ON UPDATE NOW(3) COMMENT '修改日时(系统)',
    `delete_dt`   DATETIME(3)  NOT NULL DEFAULT '1000-01-01' COMMENT '标记删除',
    `commit_id`   BIGINT(20)   NOT NULL COMMENT '提交id',
    `enabled`     TINYINT(1)   NOT NULL DEFAULT '1' COMMENT '是否可以被注册和执行',
    `autorun`     TINYINT(1)   NOT NULL DEFAULT '1' COMMENT '是否自动注册并启动',
    `version`     INT(11)      NOT NULL DEFAULT '0' COMMENT '版本号，版本高的配置覆盖版本低的',
    `propkey`     VARCHAR(200) NOT NULL DEFAULT '' COMMENT '配置文件的key，自动生成',
    `tasker_bean` VARCHAR(300) NOT NULL DEFAULT '' COMMENT '由TinyTasker注解的Bean，格式为Class#method',
    `tasker_para` TEXT         NULL     DEFAULT NULL COMMENT '任务的参数，对象数组的json格式，默认null无参数',
    `tasker_name` VARCHAR(200) NOT NULL DEFAULT '' COMMENT '任务名字，用于通知和日志，可读性好一些，默认为短Class#method',
    `tasker_fast` TINYINT(1)   NOT NULL DEFAULT '1' COMMENT '是否为轻任务，执行快，秒级完成',
    `tasker_apps` VARCHAR(500) NOT NULL DEFAULT '' COMMENT '所属程序，逗号分隔，推荐一个，使用spring.application.name',
    `tasker_runs` VARCHAR(100) NOT NULL DEFAULT '' COMMENT '执行模式，RunMode(product|test|develop|local)，逗号分隔忽略大小写，默认所有',
    `notice_bean` VARCHAR(200) NOT NULL DEFAULT '' COMMENT '通知Bean，SmallNotice类型，格式为Class，默认无通知',
    `notice_when` VARCHAR(100) NOT NULL DEFAULT 'fail' COMMENT '通知的时机，exec|fail|done，逗号分隔忽略大小写，默认fail',
    `notice_conf` TEXT         NULL     DEFAULT NULL COMMENT '对noticeBean的默认配置的覆盖，默认为json格式，不做补充',
    `timing_zone` VARCHAR(100) NOT NULL DEFAULT '' COMMENT '调度时区的ZoneId格式，默认系统时区',
    `timing_type` VARCHAR(100) NOT NULL DEFAULT 'cron' COMMENT '调度表达式类型，影响timingCron的解析方式，默认为spring cron格式',
    `timing_cron` VARCHAR(100) NOT NULL DEFAULT '' COMMENT '调度表达式内容，最高优先级，受timingType影响，默认spring cron格式（秒分时日月周）',
    `timing_idle` INT(11)      NOT NULL DEFAULT '0' COMMENT '固定空闲相连（秒），优先级次于timingCron，相当于fixedDelay，结束到开始，默认无效',
    `timing_rate` INT(11)      NOT NULL DEFAULT '0' COMMENT '固定频率开始（秒），优先级次于timingIdle，相当于fixedRate，开始到开始，默认无效',
    `timing_miss` INT(11)      NOT NULL DEFAULT '0' COMMENT '错过调度（misfire）多少秒内，需要补救执行，默认不补救',
    `during_from` VARCHAR(20)  NOT NULL DEFAULT '' COMMENT '调度开始的日期时间，timingZone时区，yyyy-MM-dd HH:mm:ss，默认无效',
    `during_stop` VARCHAR(20)  NOT NULL DEFAULT '' COMMENT '调度结束的日期时间，timingZone时区，yyyy-MM-dd HH:mm:ss，默认无效',
    `during_exec` INT(11)      NOT NULL DEFAULT '0' COMMENT '总计初始执行多少次后，结束调度',
    `during_fail` INT(11)      NOT NULL DEFAULT '0' COMMENT '连续失败多少次后，结束调度',
    `during_done` INT(11)      NOT NULL DEFAULT '0' COMMENT '总计成功执行多少次后，结束调度',
    `result_keep` INT(11)      NOT NULL DEFAULT '60' COMMENT '执行结果保存的天数，0为不保存，默认60天',
    `last_exec`   BIGINT(20)   NOT NULL DEFAULT '0' COMMENT '上次执行开始时间（epoch毫秒）',
    `last_fail`   BIGINT(20)   NOT NULL DEFAULT '0' COMMENT '上次执行失败时间（epoch毫秒）',
    `last_done`   BIGINT(20)   NOT NULL DEFAULT '0' COMMENT '上次执行成功时间（epoch毫秒）',
    `next_exec`   BIGINT(20)   NOT NULL DEFAULT '0' COMMENT '下次执行开始时间（epoch毫秒）默认为停止',
    `next_miss`   BIGINT(20)   NOT NULL DEFAULT '0' COMMENT '下次执行补救时间（epoch毫秒）默认已无效',
    `next_lock`   BIGINT(20)   NOT NULL DEFAULT '0' COMMENT '独占锁定期限（epoch毫秒）默认无锁定',
    `core_fail`   BIGINT(20)   NOT NULL DEFAULT '0' COMMENT '连续失败次数',
    `sums_exec`   BIGINT(20)   NOT NULL DEFAULT '0' COMMENT '合计开始次数',
    `sums_fail`   BIGINT(20)   NOT NULL DEFAULT '0' COMMENT '合计失败次数',
    `sums_done`   BIGINT(20)   NOT NULL DEFAULT '0' COMMENT '合计成功次数',
    PRIMARY KEY (`id`),
    UNIQUE INDEX uq_tasker_bean (`tasker_bean`),
    INDEX ix_next_exec (`next_exec`),
    INDEX ix_next_miss (`next_miss`),
    INDEX ix_next_lock (`next_lock`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='120/任务定义';

CREATE TABLE `win_task_result` (
    `id`        BIGINT(20)   NOT NULL COMMENT '主键/task_id',
    `task_id`   BIGINT(20)   NOT NULL DEFAULT '0' COMMENT '任务id，win_task_define.id',
    `task_app`  VARCHAR(300) NOT NULL DEFAULT '' COMMENT '所属程序，逗号分隔，默认使用spring.application.name',
    `task_msg`  TEXT         NULL     DEFAULT NULL COMMENT '正常或异常的信息',
    `time_exec` DATETIME(3)  NOT NULL DEFAULT NOW(3) COMMENT '执行开始时间，服务时间',
    `time_fail` DATETIME(3)  NOT NULL DEFAULT '1000-01-01' COMMENT '执行停止时间，服务时间',
    `time_stop` DATETIME(3)  NOT NULL DEFAULT '1000-01-01' COMMENT '执行停止时间，服务时间',
    `time_cost` BIGINT(20)   NOT NULL DEFAULT '0' COMMENT '耗时毫秒数',
    PRIMARY KEY (`id`),
    INDEX ix_task_id (`task_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='122/任务结果';
