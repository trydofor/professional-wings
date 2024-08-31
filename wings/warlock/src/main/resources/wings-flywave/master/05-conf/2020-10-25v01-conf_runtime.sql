CREATE TABLE `win_conf_runtime` (
    `key`      VARCHAR(200)  NOT NULL COMMENT 'conf key:Enum|Class|String',
    `enabled`  BOOLEAN       NOT NULL DEFAULT '1' COMMENT 'enabled',
    `current`  TEXT          NOT NULL COMMENT 'current value',
    `previous` TEXT          NOT NULL COMMENT 'previous value',
    `initial`  TEXT          NOT NULL COMMENT 'initial value',
    `outline`  VARCHAR(5000) NOT NULL DEFAULT '' COMMENT 'value ResolvableType',
    `comment`  VARCHAR(5000) NOT NULL DEFAULT '' COMMENT 'usage or purpose',
    `handler`  VARCHAR(200)  NOT NULL DEFAULT 'prop' COMMENT 'data handling:prop|json|kryo',
    PRIMARY KEY (`key`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='110/Runtime Config';
-- -----------

INSERT IGNORE INTO `win_conf_runtime` (`key`, `current`, `previous`, `initial`, `outline`, `comment`, `handler`)
VALUES ('pro.fessional.wings.warlock.service.conf.mode.RunMode', 'Local', 'pro.fessional.wings.warlock.service.conf.mode.RunMode', '', 'Local', 'RunMode', 'prop')
     , ('pro.fessional.wings.warlock.service.conf.mode.ApiMode', 'Nothing', 'pro.fessional.wings.warlock.service.conf.mode.ApiMode', '', 'Nothing', 'ApiMode', 'prop');

-- CALL FLYWAVE('2020-10-25v01-conf_runtime.sql');