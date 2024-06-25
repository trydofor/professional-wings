ALTER TABLE `win_conf_runtime`
    DROP COLUMN `enabled`,
    CHANGE COLUMN `current` `current` VARCHAR(5000) NOT NULL DEFAULT '' COMMENT 'current value',
    CHANGE COLUMN `previous` `previous` VARCHAR(5000) NOT NULL DEFAULT '' COMMENT 'previous value',
    CHANGE COLUMN `initial` `initial` VARCHAR(5000) NOT NULL DEFAULT '' COMMENT 'initial value';
