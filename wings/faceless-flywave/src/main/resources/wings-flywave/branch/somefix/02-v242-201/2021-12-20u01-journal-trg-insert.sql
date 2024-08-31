ALTER TABLE `sys_schema_journal`
    DROP COLUMN `ddl_instrg`,
    DROP COLUMN `ddl_instbl`,
    DROP COLUMN `log_insert`;

-- CALL FLYWAVE('2021-12-20u01-journal-trg-insert.sql');