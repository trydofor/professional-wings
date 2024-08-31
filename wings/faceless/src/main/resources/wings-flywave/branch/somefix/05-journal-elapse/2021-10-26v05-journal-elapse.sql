ALTER TABLE `sys_commit_journal`
    ADD COLUMN `parent_id` BIGINT NOT NULL DEFAULT '0' COMMENT 'parent id if renew' AFTER `create_dt`,
    ADD COLUMN `elapse_ms` BIGINT NOT NULL DEFAULT '0' COMMENT 'elapse mills' AFTER `parent_id`;

-- CALL FLYWAVE('2021-10-26v05-journal-elapse.sql');