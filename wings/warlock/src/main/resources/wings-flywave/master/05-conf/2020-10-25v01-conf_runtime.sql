CREATE TABLE `win_conf_runtime` (
    `key`      VARCHAR(200)  NOT NULL COMMENT '配置key:Enum|Class|String',
    `current`  VARCHAR(5000) NOT NULL DEFAULT '' COMMENT '当前值',
    `previous` VARCHAR(5000) NOT NULL DEFAULT '' COMMENT '前次值',
    `initial`  VARCHAR(5000) NOT NULL DEFAULT '' COMMENT '初始值',
    `comment`  VARCHAR(500)  NOT NULL DEFAULT '' COMMENT '注释',
    `handler`  VARCHAR(200)  NOT NULL DEFAULT 'prop' COMMENT '处理:prop|json',
    PRIMARY KEY (`key`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='110/运行时配置';
-- -----------

INSERT IGNORE INTO `win_conf_runtime` (`key`, `current`, `previous`, `initial`, `comment`)
VALUES ('pro.fessional.wings.warlock.service.conf.mode.RunMode', 'Local', '', 'Local', 'wings原型模式:RunMode')
     , ('pro.fessional.wings.warlock.service.conf.mode.ApiMode', 'Nothing', '', 'Local', 'wings原型模式:ApiMode');
