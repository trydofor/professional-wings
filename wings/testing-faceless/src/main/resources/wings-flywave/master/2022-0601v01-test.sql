-- apply@tst_.* error@stop
CREATE TABLE `tst_sharding` (
    `id`         BIGINT      NOT NULL COMMENT 'primary key',
    `create_dt`  DATETIME(3) NOT NULL DEFAULT NOW(3) COMMENT 'created datetime',
    `modify_dt`  DATETIME(3) NOT NULL DEFAULT '1000-01-01' ON UPDATE NOW(3) COMMENT 'modified datetime',
    `delete_dt`  DATETIME(3) NOT NULL DEFAULT '1000-01-01' COMMENT 'logic deleted datetime',
    `commit_id`  BIGINT      NOT NULL COMMENT 'commit id',
    `login_info` TEXT        NULL COMMENT 'login info: agent, terminal',
    `other_info` TEXT        NULL COMMENT 'other info: biz index data',
    `language`   INT         NOT NULL DEFAULT 1020111 COMMENT 'StandardLanguage',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='201/Sharding Test';

CREATE TABLE `tst_sharding_postfix` (
    `id`         BIGINT      NOT NULL COMMENT 'primary key',
    `create_dt`  DATETIME(3) NOT NULL DEFAULT NOW(3) COMMENT 'created datetime',
    `modify_dt`  DATETIME(3) NOT NULL DEFAULT '1000-01-01' ON UPDATE NOW(3) COMMENT 'modified datetime',
    `delete_dt`  DATETIME(3) NOT NULL DEFAULT '1000-01-01' COMMENT 'logic deleted datetime',
    `commit_id`  BIGINT      NOT NULL COMMENT 'commit id',
    `login_info` TEXT        NULL COMMENT 'login info: agent, terminal',
    `other_info` TEXT        NULL COMMENT 'other info: biz index data',
    `language`   INT         NOT NULL DEFAULT 1020111 COMMENT 'StandardLanguage',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='201/Sharding Test';

CREATE TABLE `tst_normal_table` (
    `id`            BIGINT         NOT NULL COMMENT 'primary key',
    `create_dt`     DATETIME(3)    NOT NULL DEFAULT NOW(3) COMMENT 'created datetime',
    `modify_dt`     DATETIME(3)    NOT NULL DEFAULT '1000-01-01' ON UPDATE NOW(3) COMMENT 'modified datetime',
    `delete_dt`     DATETIME(3)    NOT NULL DEFAULT '1000-01-01' COMMENT 'logic deleted datetime',
    `commit_id`     BIGINT         NOT NULL COMMENT 'commit id',
    `value_varchar` VARCHAR(256)   NOT NULL DEFAULT '0' COMMENT 'String',
    `value_decimal` DECIMAL(10, 2) NOT NULL DEFAULT '0' COMMENT 'BigDecimal',
    `value_boolean` BOOLEAN        NOT NULL DEFAULT '0' COMMENT 'Boolean',
    `value_int`     INT            NOT NULL DEFAULT '0' COMMENT 'Integer',
    `value_long`    BIGINT         NOT NULL DEFAULT '0' COMMENT 'Long',
    `value_date`    DATE           NOT NULL DEFAULT '1000-01-01' COMMENT 'LocalDate',
    `value_time`    TIME           NOT NULL DEFAULT '00:00:00' COMMENT 'LocalTime',
    `value_lang`    INT            NOT NULL DEFAULT '1020111' COMMENT 'StandardLanguage',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='202/Normal Test';

REPLACE INTO `sys_light_sequence` (`seq_name`, `block_id`, `next_val`, `step_val`, `comments`)
VALUES ('tst_normal_table', 0, 1000, 1, 'for test step 1');

-- CALL FLYWAVE('2022-0601v01-test.sql');