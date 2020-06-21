CREATE TABLE `win_auth_role` (
    `id`        BIGINT(20)    NOT NULL COMMENT '主键',
    `create_dt` DATETIME(3)   NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建日时(系统)',
    `modify_dt` DATETIME(3)   NOT NULL DEFAULT '1000-01-01' ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '修改日时(系统)',
    `delete_dt` DATETIME(3)   NOT NULL DEFAULT '1000-01-01' COMMENT '标记删除',
    `commit_id` BIGINT(20)    NOT NULL COMMENT '提交id',
    `role_type` INT(11)       NOT NULL COMMENT '类型/40201##:financial|operation',
    `role_name` VARCHAR(100)  NOT NULL COMMENT '名字',
    `desc`      VARCHAR(200)  NOT NULL DEFAULT '' COMMENT '描述',
    `auth_set`  VARCHAR(3000) NOT NULL COMMENT '权限集:authority#401####，逗号分割',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='402/权限组(角色)';

CREATE TABLE `win_user` (
    `id`        BIGINT(20)    NOT NULL COMMENT '主键',
    `create_dt` DATETIME(3)   NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建日时(系统)',
    `modify_dt` DATETIME(3)   NOT NULL DEFAULT '1000-01-01' ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '修改日时(系统)',
    `delete_dt` DATETIME(3)   NOT NULL DEFAULT '1000-01-01' COMMENT '标记删除',
    `commit_id` BIGINT(20)    NOT NULL COMMENT '提交id',
    `name`      VARCHAR(50)   NOT NULL COMMENT '名字',
    `gender`    INT(11)       NOT NULL COMMENT '性别/20101##',
    `birth`     DATE          NOT NULL COMMENT '生日',
    `avatar`    VARCHAR(200)  NOT NULL COMMENT '头像',
    `country`   CHAR(2)       NOT NULL COMMENT '国家/地区',
    `language`  CHAR(5)       NOT NULL COMMENT '使用语言',
    `timezone`  INT(11)   NOT NULL COMMENT '所在时区',
    `auth_set`  VARCHAR(2000) NOT NULL DEFAULT '' COMMENT '权限集:authority#401####，逗号分割，临时增减，负数表减',
    `role_set`  VARCHAR(1000) NOT NULL DEFAULT '' COMMENT '角色集:win_auth_role.id，逗号分割',
    `status`    INT(11)       NOT NULL COMMENT '用户状态/41101##',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='411/用户';

CREATE TABLE `win_user_login` (
    `id`         BIGINT(20)    NOT NULL COMMENT '主键',
    `create_dt`  DATETIME(3)   NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建日时(系统)',
    `modify_dt`  DATETIME(3)   NOT NULL DEFAULT '1000-01-01' ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '修改日时(系统)',
    `delete_dt`  DATETIME(3)   NOT NULL DEFAULT '1000-01-01' COMMENT '标记删除',
    `commit_id`  BIGINT(20)    NOT NULL COMMENT '提交id',
    `user_id`    BIGINT(20)    NOT NULL COMMENT '客户id:win_user.id',
    `login_type` INT(11)       NOT NULL COMMENT '登录类别/41201##:邮件|手机|微信|facebook',
    `login_name` VARCHAR(200)  NOT NULL COMMENT '登录名称',
    `login_pass` VARCHAR(200)  NOT NULL COMMENT '登录密码及算法，参考SpringSecurity格式',
    `login_salt` VARCHAR(100)  NOT NULL DEFAULT '' COMMENT '密码加盐',
    `login_para` VARCHAR(2000) NOT NULL COMMENT '登录参数:json格式的第三方参数',
    `auth_code`  VARCHAR(50)   NOT NULL DEFAULT '' COMMENT '长期的识别码',
    `bad_count`  INT(11)       NOT NULL DEFAULT 0 COMMENT '错误次数',
    `status`     INT(11)       NOT NULL COMMENT '鉴权状态/41101##:同用户状态',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='412/用户登录';
