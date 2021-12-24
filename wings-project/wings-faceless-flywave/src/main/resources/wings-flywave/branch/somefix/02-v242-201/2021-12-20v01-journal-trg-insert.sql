-- 2.4.2.201-SNAPSHOT 增加 insert trigger
ALTER TABLE `sys_schema_journal`
    ADD COLUMN `ddl_instbl` TEXT NOT NULL COMMENT '插入的跟踪表DDL' AFTER `commit_id`,
    ADD COLUMN `ddl_instrg` TEXT NOT NULL COMMENT '插入的触发器DDL' AFTER `ddl_instbl`,
    ADD COLUMN `log_insert` DATETIME(3) NOT NULL DEFAULT '1000-01-01 00:00:00.000' COMMENT '开始跟踪插入的日时' AFTER `ddl_deltrg`;
