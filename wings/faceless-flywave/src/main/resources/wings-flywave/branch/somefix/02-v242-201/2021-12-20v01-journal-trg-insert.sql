-- 2.4.2.201-SNAPSHOT add insert trigger
ALTER TABLE `sys_schema_journal`
    ADD COLUMN `ddl_instbl` TEXT NOT NULL COMMENT 'trace DDL of insert' AFTER `commit_id`,
    ADD COLUMN `ddl_instrg` TEXT NOT NULL COMMENT 'trigger DDL of insert' AFTER `ddl_instbl`,
    ADD COLUMN `log_insert` DATETIME(3) NOT NULL DEFAULT '1000-01-01 00:00:00.000' COMMENT 'applied datetime of insert' AFTER `ddl_deltrg`;
