-- apply@tst_.* error@stop
CREATE TABLE `tst_中文也分表` (
    `id`         BIGINT(20)  NOT NULL COMMENT '主键',
    `create_dt`  DATETIME(3) NOT NULL DEFAULT NOW(3) COMMENT '创建日时',
    `modify_dt`  DATETIME(3) NOT NULL DEFAULT '1000-01-01' ON UPDATE NOW(3) COMMENT '修改日时',
    `delete_dt`  DATETIME(3) NOT NULL DEFAULT '1000-01-01' COMMENT '标记删除',
    `commit_id`  BIGINT(20)  NOT NULL COMMENT '提交ID',
    `login_info` TEXT COMMENT '登陆信息，用户，终端等',
    `other_info` TEXT COMMENT '其他信息，业务侧自定义',
    `language`   INT(11) NOT NULL DEFAULT 1020111 COMMENT 'StandardLanguage',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='201/奇葩测试';

CREATE TABLE `tst_中文也分表_postfix` (
    `id`         BIGINT(20)  NOT NULL COMMENT '主键',
    `create_dt`  DATETIME(3) NOT NULL DEFAULT NOW(3) COMMENT '创建日时',
    `modify_dt`  DATETIME(3) NOT NULL DEFAULT '1000-01-01' ON UPDATE NOW(3) COMMENT '修改日时',
    `delete_dt`  DATETIME(3) NOT NULL DEFAULT '1000-01-01' COMMENT '标记删除',
    `commit_id`  BIGINT(20)  NOT NULL COMMENT '提交ID',
    `login_info` TEXT COMMENT '登陆信息，用户，终端等',
    `other_info` TEXT COMMENT '其他信息，业务侧自定义',
    `language`   INT(11) NOT NULL DEFAULT 1020111 COMMENT 'StandardLanguage',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='201/奇葩测试';

