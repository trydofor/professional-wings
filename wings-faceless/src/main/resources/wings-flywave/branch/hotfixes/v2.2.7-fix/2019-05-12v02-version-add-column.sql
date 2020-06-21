-- 2.2.7版本之前的补丁
ALTER TABLE `sys_schema_version`
    CHANGE COLUMN `apply_dt` `apply_dt` DATETIME(3)  NOT NULL DEFAULT '1000-01-01 00:00:00' COMMENT '执行日时' AFTER `commit_id`,
    ADD COLUMN `comments`               VARCHAR(500) NOT NULL DEFAULT '' COMMENT 'sql路径信息' AFTER `apply_dt`;