ALTER TABLE `win_conf_runtime`
    DROP COLUMN `enabled`,
    DROP COLUMN `outline`,
    CHANGE COLUMN `current` `current` VARCHAR(5000) NOT NULL DEFAULT '' COMMENT 'current value',
    CHANGE COLUMN `previous` `previous` VARCHAR(5000) NOT NULL DEFAULT '' COMMENT 'previous value',
    CHANGE COLUMN `initial` `initial` VARCHAR(5000) NOT NULL DEFAULT '' COMMENT 'initial value',
    CHANGE COLUMN `comment` `comment` VARCHAR(500) NOT NULL DEFAULT '' COMMENT 'comment',
    CHANGE COLUMN `handler` `handler` VARCHAR(200) NOT NULL DEFAULT 'prop' COMMENT 'data handling:prop|json';
