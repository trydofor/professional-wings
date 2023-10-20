-- Unify timezone (GMT+8), Unify charset (utf8mb4)
-- CREATE DATABASE `wings` /*!40100 DEFAULT CHARACTER SET utf8mb4 */;

CREATE TABLE `sys_schema_version` (
    `revision`  BIGINT(20)   NOT NULL COMMENT 'version + build',
    `create_dt` DATETIME(3)  NOT NULL DEFAULT NOW(3) COMMENT 'created datetime',
    `modify_dt` DATETIME(3)  NOT NULL DEFAULT '1000-01-01 00:00:00.000' ON UPDATE NOW(3) COMMENT 'modified datetime',
    `commit_id` BIGINT(20)   NOT NULL COMMENT 'commit id',
    `apply_dt`  DATETIME(3)  NOT NULL DEFAULT '1000-01-01 00:00:00.000' COMMENT 'applied datetime',
    `comments`  VARCHAR(500) NOT NULL DEFAULT '' COMMENT 'sql path',
    `upto_sql`  TEXT         NOT NULL COMMENT 'upgrade script',
    `undo_sql`  TEXT         NOT NULL COMMENT 'downgrade script',
    PRIMARY KEY (`revision`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='101/Table Structure';

CREATE TABLE `sys_schema_journal` (
    `table_name` VARCHAR(100) NOT NULL COMMENT 'plain table name',
    `create_dt`  DATETIME(3)  NOT NULL DEFAULT NOW(3) COMMENT 'created datetime',
    `modify_dt`  DATETIME(3)  NOT NULL DEFAULT '1000-01-01 00:00:00.000' ON UPDATE NOW(3) COMMENT 'modified datetime',
    `commit_id`  BIGINT(20)   NOT NULL COMMENT 'commit id',
    `ddl_instbl` TEXT         NOT NULL COMMENT 'trace DDL of insert',
    `ddl_instrg` TEXT         NOT NULL COMMENT 'trigger DDL of insert',
    `ddl_updtbl` TEXT         NOT NULL COMMENT 'trace DDL of update',
    `ddl_updtrg` TEXT         NOT NULL COMMENT 'trigger DDL of update',
    `ddl_deltbl` TEXT         NOT NULL COMMENT 'trace DDL of delete',
    `ddl_deltrg` TEXT         NOT NULL COMMENT 'trigger DDL of delete',
    `log_insert` DATETIME(3)  NOT NULL DEFAULT '1000-01-01 00:00:00.000' COMMENT 'applied datetime of insert',
    `log_update` DATETIME(3)  NOT NULL DEFAULT '1000-01-01 00:00:00.000' COMMENT 'applied datetime of update',
    `log_delete` DATETIME(3)  NOT NULL DEFAULT '1000-01-01 00:00:00.000' COMMENT 'applied datetime of delete',
    PRIMARY KEY (`table_name`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='102/Table Trigger';

-- sys_schema_version@plain
INSERT IGNORE INTO `sys_schema_version` (`revision`, `commit_id`, `upto_sql`, `undo_sql`, `apply_dt`)
VALUES (2019051201, 0, '', '', NOW(3));

-- sys_schema_journal@plain
REPLACE INTO `sys_schema_journal` (`table_name`, `commit_id`, `ddl_instbl`, `ddl_instrg`, `ddl_updtbl`, `ddl_updtrg`, `ddl_deltbl`, `ddl_deltrg`)
VALUES ('sys_schema_journal', 0, '', '', '', '', '', ''),
       ('sys_schema_version', 0, '', '', '', '', '', '');
