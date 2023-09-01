ALTER TABLE `sys_schema_version`
    DROP COLUMN `comments`,
    CHANGE COLUMN `apply_dt` `apply_dt` DATETIME(3) NOT NULL DEFAULT '1000-01-01' COMMENT 'applied datetime' AFTER `undo_sql`;
