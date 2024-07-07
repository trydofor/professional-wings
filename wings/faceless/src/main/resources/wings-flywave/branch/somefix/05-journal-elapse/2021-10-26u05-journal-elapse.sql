ALTER TABLE `sys_commit_journal`
    DROP COLUMN `parent_id`,
    DROP COLUMN `elapse_ms`;

-- CALL FLYWAVE('2021-10-26u05-journal-elapse.sql');