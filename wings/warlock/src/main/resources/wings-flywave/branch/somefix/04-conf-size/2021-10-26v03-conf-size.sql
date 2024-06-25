ALTER TABLE `win_conf_runtime`
    ADD COLUMN `enabled` TINYINT(1) NOT NULL DEFAULT '1' COMMENT 'enabled' AFTER `key`,
    CHANGE COLUMN `current` `current` TEXT NOT NULL COMMENT 'current value',
    CHANGE COLUMN `previous` `previous` TEXT NOT NULL COMMENT 'previous value',
    CHANGE COLUMN `initial` `initial` TEXT NOT NULL COMMENT 'initial value';
