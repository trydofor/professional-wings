-- https://github.com/trydofor/professional-wings/issues/256
UPDATE `win_task_define`
SET timing_tune = last_fail
WHERE TRUE;

ALTER TABLE `win_task_define`
    DROP COLUMN `last_fail`,
    CHANGE COLUMN `last_exit` `last_done` DATETIME(3) NOT NULL DEFAULT '1000-01-01' COMMENT 'previous success (sys)',
    CHANGE COLUMN `timing_miss` `timing_miss` INT NOT NULL DEFAULT '0' COMMENT 'within how many seconds of a misfire',
    CHANGE COLUMN `timing_beat` `timing_beat` INT NOT NULL DEFAULT '0' COMMENT 'interval seconds of heartbeat, default auto calc',
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

-- https://github.com/trydofor/professional-wings/issues/284
ALTER TABLE `win_task_define`
    CHANGE COLUMN `timing_miss` `timing_miss` INT NOT NULL DEFAULT '0' COMMENT 'within how many seconds of a misfire',
    CHANGE COLUMN `timing_beat` `timing_beat` INT NOT NULL DEFAULT '0' COMMENT 'interval seconds of heartbeat, default auto calc';

-- CALL FLYWAVE('2021-10-26u02-task-tune.sql');