-- revision=2019051201, apply_dt=2020-05-23 18:04:29

-- schema

-- sys_schema_journal ddlTable
CREATE TABLE `sys_schema_journal` (
  `table_name` varchar(100) NOT NULL COMMENT '主表表名',
  `create_dt` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建日时',
  `modify_dt` datetime NOT NULL DEFAULT '1000-01-01 00:00:00' ON UPDATE CURRENT_TIMESTAMP COMMENT '修改日时',
  `commit_id` bigint(20) NOT NULL COMMENT '提交ID',
  `ddl_updtbl` text NOT NULL COMMENT '更新的跟踪表DDL',
  `ddl_updtrg` text NOT NULL COMMENT '更新的触发器DDL',
  `ddl_deltbl` text NOT NULL COMMENT '删除的跟踪表DDL',
  `ddl_deltrg` text NOT NULL COMMENT '删除的触发器DDL',
  `log_update` datetime NOT NULL DEFAULT '1000-01-01 00:00:00' COMMENT '开始跟踪更新的日时',
  `log_delete` datetime NOT NULL DEFAULT '1000-01-01 00:00:00' COMMENT '开始跟踪删除的日时',
  PRIMARY KEY (`table_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='102/数据触发器';

-- sys_schema_version ddlTable
CREATE TABLE `sys_schema_version` (
  `revision` bigint(20) NOT NULL COMMENT '版本号+修订号',
  `create_dt` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建日时',
  `modify_dt` datetime NOT NULL DEFAULT '1000-01-01 00:00:00' ON UPDATE CURRENT_TIMESTAMP COMMENT '修改日时',
  `commit_id` bigint(20) NOT NULL COMMENT '提交ID',
  `upto_sql` text NOT NULL COMMENT '升级脚本',
  `undo_sql` text NOT NULL COMMENT '降级脚本',
  `apply_dt` datetime NOT NULL DEFAULT '1000-01-01 00:00:00' COMMENT '执行日时',
  PRIMARY KEY (`revision`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='101/表结构版本';

-- wings

