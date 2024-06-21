-- win_task_define
UPDATE `win_task_define`
SET timing_tune = last_fail
WHERE TRUE;

ALTER TABLE `win_task_define`
    DROP COLUMN `last_fail`,
    CHANGE COLUMN `last_exit` `last_done` DATETIME(3) NOT NULL DEFAULT '1000-01-01' COMMENT 'previous success (sys)',
    ADD COLUMN `last_fail` DATETIME(3) NOT NULL DEFAULT '1000-01-01' COMMENT 'previous fail (sys)' AFTER last_exec;

UPDATE `win_task_define`
SET last_fail = last_done
WHERE timing_tune = 1;

ALTER TABLE `win_task_define`
    DROP COLUMN `timing_tune`;

-- win_task_result
ALTER TABLE `win_task_result`
    DROP COLUMN `task_key`,
    CHANGE COLUMN `exit_data` `task_msg` TEXT NULL DEFAULT NULL COMMENT 'Normal or abnormal messages',
    ADD COLUMN `time_fail` DATETIME(3) NOT NULL DEFAULT '1000-01-01' COMMENT 'datetime of fail (sys)' AFTER `time_exec`,
    CHANGE COLUMN `time_exit` `time_done` DATETIME(3) NOT NULL DEFAULT '1000-01-01' COMMENT 'datetime of done (sys)';

UPDATE win_task_result
SET time_fail = time_done,
    time_done='1000-01-01'
WHERE exit_fail = 1;

ALTER TABLE `win_task_result`
    DROP COLUMN `exit_fail`;
