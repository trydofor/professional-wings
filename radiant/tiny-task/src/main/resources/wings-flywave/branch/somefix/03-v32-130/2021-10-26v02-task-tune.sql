-- win_task_define
ALTER TABLE `win_task_define`
    ADD COLUMN `timing_tune` INT(11) NOT NULL DEFAULT '0' COMMENT 'execute before or after tune (seconds)' AFTER `timing_rate`,
    CHANGE COLUMN `last_done` `last_exit` DATETIME(3) NOT NULL DEFAULT '1000-01-01' COMMENT 'previous done/fail (sys)';

UPDATE `win_task_define`
SET last_exit   = last_fail,
    timing_tune = 1
WHERE last_fail > '2000-01-01';

ALTER TABLE `win_task_define`
    DROP COLUMN `last_fail`,
    ADD COLUMN `last_fail` TINYINT(1) NOT NULL DEFAULT '0' COMMENT 'whether previous fail' AFTER last_exit;

UPDATE `win_task_define`
SET last_fail   = timing_tune,
    timing_tune = 0
WHERE timing_tune = 1;

-- win_task_result
ALTER TABLE `win_task_result`
    ADD COLUMN `task_key`  VARCHAR(200) NOT NULL DEFAULT '' COMMENT 'conf file key, auto-generated' AFTER `task_id`,
    CHANGE COLUMN `task_msg` `exit_data` TEXT NULL DEFAULT NULL COMMENT 'return (json) or exception (stacktrace)',
    ADD COLUMN `exit_fail` TINYINT(1) NOT NULL DEFAULT '0' COMMENT 'whether fail' AFTER `exit_data`,
    CHANGE COLUMN `time_done` `time_exit` DATETIME(3) NOT NULL DEFAULT '1000-01-01' COMMENT 'datetime of done/fail (sys)';

UPDATE `win_task_result`
SET task_key = (select propkey from win_task_define WHERE id = task_id)
WHERE task_key = '';

UPDATE `win_task_result`
SET time_exit = time_fail,
    exit_fail = 1
WHERE time_fail > '2000-01-01';

ALTER TABLE `win_task_result`
    DROP COLUMN `time_fail`;
