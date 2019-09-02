CREATE TABLE `sys_schema_journal`
(
  `table_name` varchar(100) NOT NULL COMMENT '主表表名',
  `create_dt`  datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建日时',
  `modify_dt`  datetime     NOT NULL DEFAULT '1000-01-01' ON UPDATE CURRENT_TIMESTAMP COMMENT '修改日时',
  `commit_id`  bigint(20)   NOT NULL COMMENT '提交ID',
  `ddl_updtbl` text         NOT NULL COMMENT '更新的跟踪表DDL',
  `ddl_updtrg` text         NOT NULL COMMENT '更新的触发器DDL',
  `ddl_deltbl` text         NOT NULL COMMENT '删除的跟踪表DDL',
  `ddl_deltrg` text         NOT NULL COMMENT '删除的触发器DDL',
  `log_update` datetime     NOT NULL DEFAULT '1000-01-01' COMMENT '开始跟踪更新的日时',
  `log_delete` datetime     NOT NULL DEFAULT '1000-01-01' COMMENT '开始跟踪删除的日时',
  PRIMARY KEY (`table_name`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='表结构管理';

CREATE TABLE `sys_schema_version`
(
  `revision`  bigint(20) NOT NULL COMMENT '版本号+修订号',
  `create_dt` datetime   NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建日时',
  `modify_dt` datetime   NOT NULL DEFAULT '1000-01-01' ON UPDATE CURRENT_TIMESTAMP COMMENT '修改日时',
  `commit_id` bigint(20) NOT NULL COMMENT '提交ID',
  `upto_sql`  text       NOT NULL COMMENT '升级脚本',
  `undo_sql`  text       NOT NULL COMMENT '降级脚本',
  `apply_dt`  datetime   NOT NULL DEFAULT '1000-01-01' COMMENT '执行日时',
  PRIMARY KEY (`revision`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='表结构版本';

-- sys_schema_journal@plain
REPLACE INTO `sys_schema_journal`(`table_name`, `commit_id`, `ddl_updtbl`, `ddl_updtrg`, `ddl_deltbl`, `ddl_deltrg`)
VALUES ('sys_schema_journal', 0, '', '', '', ''),
       ('sys_schema_version', 0, '', '', '', '');

-- sys_schema_version@plain
INSERT IGNORE `sys_schema_version`(`revision`, `commit_id`, `upto_sql`, `undo_sql`, `apply_dt`)
VALUES (2019051201, 0, '', '', NOW());