CREATE TABLE `win_user_basis` (
    `id`        BIGINT(20)   NOT NULL COMMENT '主键/user_id/uid',
    `create_dt` DATETIME(3)  NOT NULL DEFAULT NOW(3) COMMENT '创建日时(系统)',
    `modify_dt` DATETIME(3)  NOT NULL DEFAULT '1000-01-01' ON UPDATE NOW(3) COMMENT '修改日时(系统)',
    `delete_dt` DATETIME(3)  NOT NULL DEFAULT '1000-01-01' COMMENT '标记删除',
    `commit_id` BIGINT(20)   NOT NULL COMMENT '提交id',
    `nickname`  VARCHAR(50)  NOT NULL DEFAULT '' COMMENT '用户昵称',
    `gender`    INT(11)      NOT NULL DEFAULT '0' COMMENT '用户性别/12001##:未知|先生|女士',
    `avatar`    VARCHAR(200) NOT NULL DEFAULT '' COMMENT '头像地址',
    `locale`    CHAR(5)      NOT NULL DEFAULT 'zh_CN' COMMENT '使用语言/Locale:StandardLanguageEnum',
    `zoneid`    INT(11)      NOT NULL DEFAULT '1010201' COMMENT '所在时区/ZoneId:StandardTimezoneEnum',
    `remark`    VARCHAR(500) NOT NULL DEFAULT '' COMMENT '备注',
    `status`    INT(11)      NOT NULL DEFAULT '0' COMMENT '用户状态/12002##:',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='120/用户基本表';

CREATE TABLE `win_user_anthn` (
    `id`         BIGINT(20)    NOT NULL COMMENT '主键',
    `create_dt`  DATETIME(3)   NOT NULL DEFAULT NOW(3) COMMENT '创建日时(系统)',
    `modify_dt`  DATETIME(3)   NOT NULL DEFAULT '1000-01-01' ON UPDATE NOW(3) COMMENT '修改日时(系统)',
    `delete_dt`  DATETIME(3)   NOT NULL DEFAULT '1000-01-01' COMMENT '标记删除',
    `commit_id`  BIGINT(20)    NOT NULL COMMENT '提交id',
    `user_id`    BIGINT(20)    NOT NULL DEFAULT '0' COMMENT '绑定用户/win_user_basis.id',
    `auth_type`  VARCHAR(10)   NOT NULL COMMENT '验证类型/wings.warlock.security.auth-type.*',
    `username`   VARCHAR(200)  NOT NULL COMMENT '验证账号/身份辨识:邮箱|手机|union_id|api_key',
    `password`   VARCHAR(200)  NOT NULL DEFAULT '' COMMENT '验证密码/spring格式|api_secret',
    `passsalt`   VARCHAR(100)  NOT NULL DEFAULT '' COMMENT '验证加盐/随机数',
    `extra_para` VARCHAR(3000) NOT NULL DEFAULT '' COMMENT '第三方验证参数',
    `extra_user` VARCHAR(9000) NOT NULL DEFAULT '' COMMENT '第三方用户信息',
    `expired_dt` DATETIME(3)   NOT NULL DEFAULT '1000-01-01' COMMENT '到期时间，非token到期时间',
    `failed_cnt` INT(11)       NOT NULL DEFAULT '0' COMMENT '连错计数:成功后清零',
    `failed_max` INT(11)       NOT NULL DEFAULT '5' COMMENT '连错上限',
    PRIMARY KEY (`id`),
    UNIQUE INDEX uq_uid_type (`user_id`, `auth_type`),
    UNIQUE INDEX uq_type_name (`auth_type`, `username`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='121/用户验证表';

CREATE TABLE `win_user_login` (
    `id`        BIGINT(20)    NOT NULL COMMENT '主键',
    `user_id`   BIGINT(20)    NOT NULL DEFAULT '0' COMMENT '绑定用户/win_user_basis.id',
    `auth_type` VARCHAR(20)   NOT NULL COMMENT '验证类型/wings.warlock.security.auth-type.*',
    `login_ip`  VARCHAR(50)   NOT NULL DEFAULT '' COMMENT '登录IP',
    `login_dt`  DATETIME(3)   NOT NULL DEFAULT NOW(3) COMMENT '创建日时(系统)',
    `terminal`  VARCHAR(1000) NOT NULL DEFAULT '' COMMENT '登录终端',
    `details`   VARCHAR(9000) NOT NULL DEFAULT '' COMMENT '验证或登录信息',
    `failed`    TINYINT(1)    NOT NULL DEFAULT '0' COMMENT '是否失败',
    PRIMARY KEY (`id`),
    INDEX ix_user_id (`user_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='122/用户登录表/只读，超过最大失败次数不记录';

-- -----------

INSERT IGNORE INTO `sys_light_sequence` (`seq_name`, `block_id`, `next_val`, `step_val`, `comments`)
VALUES ('win_user_basis', 0, 10000, 100, '动态插入5位起，静态5位'),
       ('win_user_anthn', 0, 10000, 100, '动态插入5位起，静态5位');

--
REPLACE INTO `sys_constant_enum` (`id`, `type`, `code`, `hint`, `info`)
VALUES (1200100, 'user_gender', 'user_gender', '性别', 'classpath:/wings-tmpl/ConstantEnumTemplate.java'),
       (1200101, 'user_gender', 'male', '男', '通常'),
       (1200102, 'user_gender', 'female', '女', '通常'),
       (1200103, 'user_gender', 'unknown', '未知', '通常'),

       (1200200, 'user_status', 'user_status', '用户状态', 'classpath:/wings-tmpl/ConstantEnumTemplate.java'),
       (1200201, 'user_status', 'uninit', '新建', '未初始化'),
       (1200202, 'user_status', 'active', '正常', '正常活动'),
       (1200203, 'user_status', 'infirm', '薄弱', '薄弱账户'),
       (1200204, 'user_status', 'unsafe', '异动', '异动账户'),
       (1200205, 'user_status', 'danger', '危险', '危险账户'),
       (1200206, 'user_status', 'frozen', '冻结', '冻结账户'),
       (1200207, 'user_status', 'hidden', '隐藏', '隐藏账户');


INSERT INTO `win_user_basis` (`id`, `create_dt`, `commit_id`, `nickname`, `gender`, `avatar`, `locale`, `zoneid`, `remark`, `status`)
VALUES (0, NOW(3), 0, 'nobody', 1200103, '', 'zh_CN', 1010201, '系统用户，无任何权限', 1200207),
       (1, NOW(3), 0, 'root', 1200103, '', 'zh_CN', 1010201, '超级用户，拥有所以权限', 1200202),
       (2, NOW(3), 0, 'daemon', 1200103, '', 'zh_CN', 1010201, '系统用户，执行后台任务', 1200207);

INSERT INTO `win_user_anthn`(`id`, `create_dt`, `commit_id`, `user_id`, `auth_type`, `username`, `password`, `passsalt`, `expired_dt`)
VALUES (1, NOW(3), 0, 1, 'username', 'root', '{never}ruDz1R0qKfa4wnlGye9Axngpa5O2wLSVd2O1McAtRJ6TfhisMcbeonEorm9V', 'xaUcoFCyLLnkaimmySpVB7KK6xxE8BFi1xrLafaD', '2999-09-09');
