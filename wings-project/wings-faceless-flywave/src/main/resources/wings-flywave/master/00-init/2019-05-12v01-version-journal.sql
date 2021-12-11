-- 时区统一(GMT+8)，编码统一(utf8mb4)
-- CREATE DATABASE `wings_0` /*!40100 DEFAULT CHARACTER SET utf8mb4 */;

CREATE TABLE `sys_schema_version` (
    `revision`  BIGINT(20)   NOT NULL COMMENT '版本号+修订号',
    `create_dt` DATETIME     NOT NULL DEFAULT NOW() COMMENT '创建日时',
    `modify_dt` DATETIME     NOT NULL DEFAULT '1000-01-01' ON UPDATE NOW() COMMENT '修改日时',
    `commit_id` BIGINT(20)   NOT NULL COMMENT '提交ID',
    `apply_dt`  DATETIME     NOT NULL DEFAULT '1000-01-01' COMMENT '执行日时',
    `comments`  VARCHAR(500) NOT NULL DEFAULT '' COMMENT 'sql路径信息',
    `upto_sql`  TEXT         NOT NULL COMMENT '升级脚本',
    `undo_sql`  TEXT         NOT NULL COMMENT '降级脚本',
    PRIMARY KEY (`revision`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='101/表结构版本';

CREATE TABLE `sys_schema_journal` (
    `table_name` VARCHAR(100) NOT NULL COMMENT '主表表名',
    `create_dt`  DATETIME(3)  NOT NULL DEFAULT NOW(3) COMMENT '创建日时',
    `modify_dt`  DATETIME(3)  NOT NULL DEFAULT '1000-01-01' ON UPDATE NOW(3) COMMENT '修改日时',
    `commit_id`  BIGINT(20)   NOT NULL COMMENT '提交ID',
    `ddl_updtbl` TEXT         NOT NULL COMMENT '更新的跟踪表DDL',
    `ddl_updtrg` TEXT         NOT NULL COMMENT '更新的触发器DDL',
    `ddl_deltbl` TEXT         NOT NULL COMMENT '删除的跟踪表DDL',
    `ddl_deltrg` TEXT         NOT NULL COMMENT '删除的触发器DDL',
    `log_update` DATETIME(3)  NOT NULL DEFAULT '1000-01-01' COMMENT '开始跟踪更新的日时',
    `log_delete` DATETIME(3)  NOT NULL DEFAULT '1000-01-01' COMMENT '开始跟踪删除的日时',
    PRIMARY KEY (`table_name`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='102/数据触发器';

-- sys_schema_version@plain
INSERT IGNORE INTO `sys_schema_version` (`revision`, `commit_id`, `upto_sql`, `undo_sql`, `apply_dt`)
VALUES (2019051201, 0, '', '', NOW());

-- sys_schema_journal@plain
REPLACE INTO `sys_schema_journal` (`table_name`, `commit_id`, `ddl_updtbl`, `ddl_updtrg`, `ddl_deltbl`, `ddl_deltrg`)
VALUES ('sys_schema_journal', 0, '', '', '', ''),
       ('sys_schema_version', 0, '', '', '', '');
