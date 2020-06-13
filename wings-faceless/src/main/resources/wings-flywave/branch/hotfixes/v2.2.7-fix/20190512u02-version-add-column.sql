ALTER TABLE `sys_schema_version`
    DROP COLUMN `comments`,
    CHANGE COLUMN `apply_dt` `apply_dt` DATETIME(3) NOT NULL DEFAULT '1000-01-01 00:00:00' COMMENT '执行日时' AFTER `undo_sql`;
