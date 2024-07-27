-- Error Code: 1071. Specified key was too long; max key length is 3072 bytes
CREATE TABLE `win_grow_track` (
    `id`        BIGINT       NOT NULL COMMENT 'primary key',
    `create_dt` DATETIME(3)  NOT NULL DEFAULT NOW(3) COMMENT 'created datetime',
    `track_key` VARCHAR(500) NOT NULL DEFAULT '' COMMENT 'track key, method/enum',
    `track_ref` VARCHAR(100) NOT NULL DEFAULT '' COMMENT 'method/enum/url/str',
    `track_app` VARCHAR(100) NOT NULL DEFAULT '' COMMENT 'app name',
    `track_env` TEXT         NULL COMMENT 'env, terminal context, json',
    `track_ins` TEXT         NULL COMMENT 'inputs param, json array',
    `track_out` TEXT         NULL COMMENT 'output return, json',
    `track_err` TEXT         NULL COMMENT 'error exception, json',
    `elapse_ms` BIGINT       NOT NULL DEFAULT '0' COMMENT 'elapse mills',
    `user_key`  BIGINT       NOT NULL DEFAULT '0' COMMENT 'key user, data owner',
    `user_ref`  BIGINT       NOT NULL DEFAULT '0' COMMENT 'ref user, data operator',
    `data_key`  BIGINT       NOT NULL DEFAULT '0' COMMENT 'key data, order id',
    `data_ref`  BIGINT       NOT NULL DEFAULT '0' COMMENT 'ref data, trade id',
    `data_opt`  BIGINT       NOT NULL DEFAULT '0' COMMENT 'optional data, payment id',
    `code_key`  VARCHAR(100) NOT NULL DEFAULT '' COMMENT 'key code, order num',
    `code_ref`  VARCHAR(100) NOT NULL DEFAULT '' COMMENT 'ref code, trade seq',
    `code_opt`  VARCHAR(100) NOT NULL DEFAULT '' COMMENT 'optional code, message id',
    `word_ref`  VARCHAR(800) NOT NULL DEFAULT '' COMMENT 'ref word, mark',
    PRIMARY KEY (`id`),
    INDEX ix_track_key (`track_key`),
    INDEX ix_user_key (`user_key`),
    INDEX ix_data_key (`data_key`),
    INDEX ix_code_key (`code_key`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='145/Grow Tracking';

-- CALL FLYWAVE('2020-10-28v01-tiny_grow.sql');