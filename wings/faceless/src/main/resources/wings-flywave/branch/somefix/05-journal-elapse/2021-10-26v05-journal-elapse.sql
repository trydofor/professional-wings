ALTER TABLE `sys_commit_journal`
    ADD COLUMN `parent_id` BIGINT(20) NOT NULL DEFAULT '0' COMMENT 'parent id if renew' AFTER `create_dt`,
    ADD COLUMN `elapse_ms` BIGINT(20) NOT NULL DEFAULT '0' COMMENT 'elapse mills' AFTER `parent_id`;