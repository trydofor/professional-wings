-- https://github.com/trydofor/professional-wings/issues/256
ALTER TABLE `win_task_define`
    ADD COLUMN `timing_tune` INT NOT NULL DEFAULT '0' COMMENT 'execute before or after tune (seconds)' AFTER `timing_rate`,
    CHANGE COLUMN `last_done` `last_exit` DATETIME(3) NOT NULL DEFAULT '1000-01-01' COMMENT 'previous done/fail (sys)';

UPDATE `win_task_define`
SET last_exit   = last_fail,
    timing_tune = 1
WHERE last_fail > '2000-01-01';

ALTER TABLE `win_task_define`
    DROP COLUMN `last_fail`,
    ADD COLUMN `last_fail` BOOLEAN NOT NULL DEFAULT '0' COMMENT 'whether previous fail' AFTER last_exit;

UPDATE `win_task_define`
SET last_fail   = timing_tune,
    timing_tune = 0
WHERE timing_tune = 1;

-- win_task_result
ALTER TABLE `win_task_result`
    ADD COLUMN `task_key`  VARCHAR(200) NOT NULL DEFAULT '' COMMENT 'conf file key, auto-generated' AFTER `task_id`,
    CHANGE COLUMN `task_msg` `exit_data` TEXT NULL DEFAULT NULL COMMENT 'return (json) or exception (stacktrace)',
    ADD COLUMN `exit_fail` BOOLEAN NOT NULL DEFAULT '0' COMMENT 'whether fail' AFTER `exit_data`,
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

-- https://github.com/trydofor/professional-wings/issues/284
ALTER TABLE `win_task_define`
    CHANGE COLUMN `timing_miss` `timing_miss` BIGINT NOT NULL DEFAULT '0' COMMENT 'within how many seconds of a misfire, default auto calc',
    CHANGE COLUMN `timing_beat` `timing_beat` BIGINT NOT NULL DEFAULT '0' COMMENT 'interval seconds of heartbeat, default auto calc';

-- CALL FLYWAVE('2021-10-26v02-task-tune.sql');