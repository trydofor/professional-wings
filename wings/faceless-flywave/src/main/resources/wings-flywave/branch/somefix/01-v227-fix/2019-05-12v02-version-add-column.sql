-- Patch before version 2.2.7
ALTER TABLE `sys_schema_version`
    CHANGE COLUMN `apply_dt` `apply_dt` DATETIME(3)     NOT NULL DEFAULT '1000-01-01' COMMENT 'applied datetime' AFTER `commit_id`,
    ADD COLUMN `comments`               VARCHAR(500) NOT NULL DEFAULT '' COMMENT 'sql path' AFTER `apply_dt`;
