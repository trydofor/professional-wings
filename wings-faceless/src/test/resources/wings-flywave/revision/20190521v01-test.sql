CREATE TABLE `tst_中文也分表`
(
  `id`         bigint(20) NOT NULL COMMENT '主键',
  `create_dt`  datetime   NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建日时',
  `modify_dt`  datetime   NOT NULL DEFAULT '1000-01-01' ON UPDATE CURRENT_TIMESTAMP COMMENT '修改日时',
  `commit_id`  bigint(20) NOT NULL COMMENT '提交ID',
  `login_info` text COMMENT '登陆信息，用户，终端等',
  `other_info` text COMMENT '其他信息，业务侧自定义',
  PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='变更日志';
