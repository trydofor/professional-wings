CREATE TABLE `tst_user` (
  `id`        BIGINT(20)  NOT NULL COMMENT '主见',
  `create_dt` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建日时',
  `modify_dt` DATETIME    NOT NULL DEFAULT '1000-01-01' ON UPDATE CURRENT_TIMESTAMP COMMENT '修改日时',
  `commit_id` BIGINT(20)  NOT NULL COMMENT '提交ID',
  `name`      VARCHAR(50) NOT NULL COMMENT '更新的跟踪表DDL',
  `role`      TEXT        NOT NULL COMMENT '更新的触发器DDL',
  `pert`      TEXT        NOT NULL COMMENT '删除的跟踪表DDL',
  PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='表结构管理';