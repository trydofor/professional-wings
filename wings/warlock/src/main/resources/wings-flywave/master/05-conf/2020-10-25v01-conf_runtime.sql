CREATE TABLE `win_conf_runtime` (
    `key`      VARCHAR(200) NOT NULL COMMENT 'conf key:Enum|Class|String',
    `enabled`  TINYINT(1)   NOT NULL DEFAULT '1' COMMENT 'enabled',
    `current`  TEXT         NOT NULL COMMENT 'current value',
    `previous` TEXT         NOT NULL COMMENT 'previous value',
    `initial`  TEXT         NOT NULL COMMENT 'initial value',
    `comment`  VARCHAR(500) NOT NULL DEFAULT '' COMMENT 'comment',
    `handler`  VARCHAR(200) NOT NULL DEFAULT 'prop' COMMENT 'data handling:prop|json',
    PRIMARY KEY (`key`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='110/Runtime Config';
-- -----------

INSERT IGNORE INTO `win_conf_runtime` (`key`, `current`, `previous`, `initial`, `comment`)
VALUES ('pro.fessional.wings.warlock.service.conf.mode.RunMode', 'Local', '', 'Local', 'RunMode')
     , ('pro.fessional.wings.warlock.service.conf.mode.ApiMode', 'Nothing', '', 'Local', 'ApiMode');
