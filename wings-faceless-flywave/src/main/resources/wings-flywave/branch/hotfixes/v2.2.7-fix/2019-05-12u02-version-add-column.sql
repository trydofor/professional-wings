ALTER TABLE `sys_schema_version`
    DROP COLUMN `comments`,
    CHANGE COLUMN `apply_dt` `apply_dt` DATETIME NOT NULL DEFAULT '1000-01-01' COMMENT '执行日时' AFTER `undo_sql`;
