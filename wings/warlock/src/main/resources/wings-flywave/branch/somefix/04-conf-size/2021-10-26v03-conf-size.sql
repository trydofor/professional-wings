ALTER TABLE `win_conf_runtime`
    ADD COLUMN `enabled` BOOLEAN       NOT NULL DEFAULT '1' COMMENT 'enabled' AFTER `key`,
    ADD COLUMN `outline` VARCHAR(5000) NOT NULL DEFAULT '' COMMENT 'value ResolvableType' AFTER `initial`,
    CHANGE COLUMN `current` `current` TEXT NOT NULL COMMENT 'current value',
    CHANGE COLUMN `previous` `previous` TEXT NOT NULL COMMENT 'previous value',
    CHANGE COLUMN `initial` `initial` TEXT NOT NULL COMMENT 'initial value',
    CHANGE COLUMN `comment` `comment` VARCHAR(5000) NOT NULL DEFAULT '' COMMENT 'usage or purpose',
    CHANGE COLUMN `handler` `handler` VARCHAR(200) NOT NULL DEFAULT 'prop' COMMENT 'data handling:prop|json|kryo';

UPDATE `win_conf_runtime`
SET outline = `key`
WHERE `key` = 'pro.fessional.wings.warlock.service.conf.mode.RunMode';

UPDATE `win_conf_runtime`
SET outline   = `key`,
    `initial` = 'Nothing'
WHERE `key` = 'pro.fessional.wings.warlock.service.conf.mode.ApiMode';

-- CALL FLYWAVE('2021-10-26v03-conf-size.sql');