CREATE TABLE `win_user_basis` (
    `id`        BIGINT(20)    NOT NULL COMMENT 'primary key/user_id/uid',
    `create_dt` DATETIME(3)   NOT NULL DEFAULT NOW(3) COMMENT 'created datetime(sys)',
    `modify_dt` DATETIME(3)   NOT NULL DEFAULT '1000-01-01' ON UPDATE NOW(3) COMMENT 'modified datetime(sys)',
    `delete_dt` DATETIME(3)   NOT NULL DEFAULT '1000-01-01' COMMENT 'logic deleted datetime',
    `commit_id` BIGINT(20)    NOT NULL COMMENT 'commit id',
    `nickname`  VARCHAR(50)   NOT NULL DEFAULT '' COMMENT 'nickname',
    `passsalt`  VARCHAR(100)  NOT NULL DEFAULT '' COMMENT 'password salt/random, read-only, no external use',
    `gender`    INT(11)       NOT NULL DEFAULT '0' COMMENT 'gender/12001##:unknown|mail|female',
    `avatar`    VARCHAR(1000) NOT NULL DEFAULT '' COMMENT 'avatar url',
    `locale`    CHAR(5)       NOT NULL DEFAULT 'zh_CN' COMMENT 'language/Locale:StandardLanguageEnum',
    `zoneid`    INT(11)       NOT NULL DEFAULT '1010201' COMMENT 'timezone/ZoneId:StandardTimezoneEnum',
    `remark`    VARCHAR(500)  NOT NULL DEFAULT '' COMMENT 'comment',
    `status`    INT(11)       NOT NULL DEFAULT '0' COMMENT 'user status/12002##:',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='120/User Basis';

CREATE TABLE `win_user_authn` (
    `id`         BIGINT(20)    NOT NULL COMMENT 'primary key',
    `create_dt`  DATETIME(3)   NOT NULL DEFAULT NOW(3) COMMENT 'created datetime(sys)',
    `modify_dt`  DATETIME(3)   NOT NULL DEFAULT '1000-01-01' ON UPDATE NOW(3) COMMENT 'modified datetime(sys)',
    `delete_dt`  DATETIME(3)   NOT NULL DEFAULT '1000-01-01' COMMENT 'logic deleted datetime',
    `commit_id`  BIGINT(20)    NOT NULL COMMENT 'commit id',
    `user_id`    BIGINT(20)    NOT NULL DEFAULT '0' COMMENT 'basic user/win_user_basis.id',
    `auth_type`  VARCHAR(10)   NOT NULL COMMENT 'auth type/wings.warlock.security.auth-type.*',
    `username`   VARCHAR(200)  NOT NULL COMMENT 'account/id:email|mobile|union_id|api_key',
    `password`   VARCHAR(200)  NOT NULL DEFAULT '' COMMENT 'password/spring style|api_secret',
    `extra_para` VARCHAR(3000) NOT NULL DEFAULT '' COMMENT 'para for 3rd auth',
    `extra_user` VARCHAR(9000) NOT NULL DEFAULT '' COMMENT 'user info of 3rd',
    `expired_dt` DATETIME(3)   NOT NULL DEFAULT '1000-01-01' COMMENT 'expiration, not for token, empty is disabled',
    `failed_cnt` INT(11)       NOT NULL DEFAULT '0' COMMENT 'continuous error count: clear on success',
    `failed_max` INT(11)       NOT NULL DEFAULT '5' COMMENT 'max continuous error',
    PRIMARY KEY (`id`),
    UNIQUE INDEX uq_uid_type (`user_id`, `auth_type`),
    UNIQUE INDEX uq_type_name (`auth_type`, `username`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='121/User Authn';

CREATE TABLE `win_user_login` (
    `id`        BIGINT(20)    NOT NULL COMMENT 'primary key',
    `user_id`   BIGINT(20)    NOT NULL DEFAULT '0' COMMENT 'basic user/win_user_basis.id',
    `auth_type` VARCHAR(20)   NOT NULL COMMENT 'auth type/wings.warlock.security.auth-type.*',
    `login_ip`  VARCHAR(50)   NOT NULL DEFAULT '' COMMENT 'login IP',
    `login_dt`  DATETIME(3)   NOT NULL DEFAULT NOW(3) COMMENT 'created datetime(sys)',
    `terminal`  VARCHAR(1000) NOT NULL DEFAULT '' COMMENT 'login terminal',
    `details`   VARCHAR(9000) NOT NULL DEFAULT '' COMMENT 'auth details',
    `failed`    TINYINT(1)    NOT NULL DEFAULT '0' COMMENT 'fail or not',
    PRIMARY KEY (`id`),
    INDEX ix_user_id (`user_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='122/User Login: read-only';

-- -----------

INSERT IGNORE INTO `sys_light_sequence` (`seq_name`, `block_id`, `next_val`, `step_val`, `comments`)
VALUES ('win_user_basis', 0, 10000, 100, 'dynamic 5+ digits, static 5 digits'),
       ('win_user_authn', 0, 10000, 100, 'dynamic 5+ digits, static 5 digits');

--
REPLACE INTO `sys_constant_enum` (`id`, `type`, `code`, `hint`, `info`)
VALUES (1200100, 'user_gender', 'user_gender', 'gender', 'classpath:/wings-tmpl/ConstantEnumTemplate.java'),
       (1200101, 'user_gender', 'male', 'male', 'normal'),
       (1200102, 'user_gender', 'female', 'female', 'normal'),
       (1200103, 'user_gender', 'unknown', 'unknown', 'normal');


INSERT IGNORE INTO `win_user_basis` (`id`, `create_dt`, `commit_id`, `nickname`, `passsalt`, `gender`, `avatar`, `locale`, `zoneid`, `remark`, `status`)
VALUES (0, NOW(3), 0, 'nobody', UUID(), 1200103, '', 'zh_CN', 1010201, 'system user without any privileges', 1200207),
       (1, NOW(3), 0, 'root', UUID(), 1200103, '', 'zh_CN', 1010201, 'super user with full privileges', 1200202),
       (2, NOW(3), 0, 'daemon', UUID(), 1200103, '', 'zh_CN', 1010201, 'system user for background tasks', 1200207);

INSERT IGNORE INTO `win_user_authn`(`id`, `create_dt`, `commit_id`, `user_id`, `auth_type`, `username`, `password`, `expired_dt`)
VALUES (1, NOW(3), 0, 1, 'username', 'root', CONCAT('{never}', UUID()), '2999-09-09');
