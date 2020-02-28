-- revision=2019052101, apply_dt=2020-02-28 18:18:47

-- sys_light_sequence dmlInsert
INSERT INTO `sys_light_sequence`(`seq_name`,`block_id`,`next_val`,`step_val`,`comments`) VALUES 
('singleton_lightid_blockid',0,10000000,100,'default block_id'),
('sys_commit_journal',0,1,100,'sys_commit_journal');

-- schema

-- sys_schema_journal dmlInsert
INSERT INTO `sys_schema_journal`(`table_name`,`create_dt`,`modify_dt`,`commit_id`,`ddl_updtbl`,`ddl_updtrg`,`ddl_deltbl`,`ddl_deltrg`,`log_update`,`log_delete`) VALUES 
('sys_schema_journal','2020-02-28 18:04:33.000','1000-01-01 00:00:00.000',0,'','','','','1000-01-01 00:00:00.000','1000-01-01 00:00:00.000'),
('sys_schema_version','2020-02-28 18:04:33.000','1000-01-01 00:00:00.000',0,'','','','','1000-01-01 00:00:00.000','1000-01-01 00:00:00.000');

-- sys_schema_version dmlInsert
INSERT INTO `sys_schema_version`(`revision`,`create_dt`,`modify_dt`,`commit_id`,`upto_sql`,`undo_sql`,`apply_dt`) VALUES 
(2019051201,'2020-02-28 18:04:33.000','2020-02-28 18:06:05.000',0,'-- 时区统一(GMT+8)，编码统一(utf8mb4)\n-- CREATE DATABASE `wings_0` /*!40100 DEFAULT CHARACTER SET utf8mb4 */;\n\nCREATE TABLE `sys_schema_version` (\n    `revision`  BIGINT(20) NOT NULL COMMENT \'版本号+修订号\',\n    `create_dt` DATETIME   NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT \'创建日时\',\n    `modify_dt` DATETIME   NOT NULL DEFAULT \'1000-01-01\' ON UPDATE CURRENT_TIMESTAMP COMMENT \'修改日时\',\n    `commit_id` BIGINT(20) NOT NULL COMMENT \'提交ID\',\n    `upto_sql`  TEXT       NOT NULL COMMENT \'升级脚本\',\n    `undo_sql`  TEXT       NOT NULL COMMENT \'降级脚本\',\n    `apply_dt`  DATETIME   NOT NULL DEFAULT \'1000-01-01\' COMMENT \'执行日时\',\n    PRIMARY KEY (`revision`)\n) ENGINE = InnoDB\n  DEFAULT CHARSET = utf8mb4 COMMENT =\'101/表结构版本\';\n\nCREATE TABLE `sys_schema_journal` (\n    `table_name` VARCHAR(100) NOT NULL COMMENT \'主表表名\',\n    `create_dt`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT \'创建日时\',\n    `modify_dt`  DATETIME     NOT NULL DEFAULT \'1000-01-01\' ON UPDATE CURRENT_TIMESTAMP COMMENT \'修改日时\',\n    `commit_id`  BIGINT(20)   NOT NULL COMMENT \'提交ID\',\n    `ddl_updtbl` TEXT         NOT NULL COMMENT \'更新的跟踪表DDL\',\n    `ddl_updtrg` TEXT         NOT NULL COMMENT \'更新的触发器DDL\',\n    `ddl_deltbl` TEXT         NOT NULL COMMENT \'删除的跟踪表DDL\',\n    `ddl_deltrg` TEXT         NOT NULL COMMENT \'删除的触发器DDL\',\n    `log_update` DATETIME     NOT NULL DEFAULT \'1000-01-01\' COMMENT \'开始跟踪更新的日时\',\n    `log_delete` DATETIME     NOT NULL DEFAULT \'1000-01-01\' COMMENT \'开始跟踪删除的日时\',\n    PRIMARY KEY (`table_name`)\n) ENGINE = InnoDB\n  DEFAULT CHARSET = utf8mb4 COMMENT =\'102/数据触发器\';\n\n-- sys_schema_version@plain\nINSERT IGNORE `sys_schema_version`(`revision`, `commit_id`, `upto_sql`, `undo_sql`, `apply_dt`)\nVALUES (2019051201, 0, \'\', \'\', NOW());\n\n-- sys_schema_journal@plain\nREPLACE INTO `sys_schema_journal`(`table_name`, `commit_id`, `ddl_updtbl`, `ddl_updtrg`, `ddl_deltbl`, `ddl_deltrg`)\nVALUES (\'sys_schema_journal\', 0, \'\', \'\', \'\', \'\'),\n       (\'sys_schema_version\', 0, \'\', \'\', \'\', \'\');','DROP TABLE IF EXISTS `sys_schema_version`;\nDROP TABLE IF EXISTS `sys_schema_journal`;\n','2020-02-28 18:04:33.000'),
(2019052001,'2020-02-28 18:06:05.000','2020-02-28 18:18:47.000',0,'CREATE TABLE `sys_light_sequence` (\n    `seq_name` VARCHAR(100) NOT NULL COMMENT \'序列名\',\n    `block_id` INT(11)      NOT NULL DEFAULT 0 COMMENT \'分块序号\',\n    `next_val` BIGINT(20)   NOT NULL DEFAULT \'1\' COMMENT \'下一个序号\',\n    `step_val` INT(11)      NOT NULL DEFAULT \'100\' COMMENT \'序列步长\',\n    `comments` VARCHAR(200) NOT NULL COMMENT \'注释说明\',\n    PRIMARY KEY (`seq_name`, `block_id`)\n) ENGINE = InnoDB\n  DEFAULT CHARSET = utf8mb4 COMMENT =\'103/序号生成器\';\n\nCREATE TABLE `sys_commit_journal` (\n    `id`         BIGINT(20)   NOT NULL COMMENT \'主键\',\n    `create_dt`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT \'创建日时\',\n    `event_name` VARCHAR(200) NOT NULL COMMENT \'事件名称\',\n    `target_key` VARCHAR(200) NOT NULL DEFAULT \'\' COMMENT \'目标数据特征\',\n    `login_info` TEXT COMMENT \'登陆信息，用户，终端等\',\n    `other_info` TEXT COMMENT \'其他信息，业务侧自定义\',\n    PRIMARY KEY (`id`)\n) ENGINE = InnoDB\n  DEFAULT CHARSET = utf8mb4 COMMENT =\'104/数据变更集\';\n\n-- sys_light_sequence@plain\nINSERT IGNORE `sys_light_sequence` (`seq_name`, `block_id`, `next_val`, `step_val`, `comments`)\nVALUES (\'singleton_lightid_blockid\', 0, 10000000, 100, \'default block_id\'),\n       (\'sys_commit_journal\', 0, 1, 100, \'sys_commit_journal\');\n\n-- sys_commit_journal@plain\nREPLACE INTO `sys_commit_journal`(`id`, `event_name`)\nVALUES (0, \'system_manual_init\');\n','DROP TABLE IF EXISTS `sys_light_sequence`;\nDROP TABLE IF EXISTS `sys_commit_journal`;','2020-02-28 18:18:47.000'),
(2019052101,'2020-02-28 18:06:05.000','2020-02-28 18:18:47.000',0,'CREATE TABLE `tst_中文也分表` (\n    `id`         BIGINT(20) NOT NULL COMMENT \'主键\',\n    `create_dt`  DATETIME   NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT \'创建日时\',\n    `modify_dt`  DATETIME   NOT NULL DEFAULT \'1000-01-01\' ON UPDATE CURRENT_TIMESTAMP COMMENT \'修改日时\',\n    `delete_dt`  DATETIME   NOT NULL DEFAULT \'1000-01-01\' COMMENT \'标记删除\',\n    `commit_id`  BIGINT(20) NOT NULL COMMENT \'提交ID\',\n    `login_info` TEXT COMMENT \'登陆信息，用户，终端等\',\n    `other_info` TEXT COMMENT \'其他信息，业务侧自定义\',\n    PRIMARY KEY (`id`)\n) ENGINE = InnoDB\n  DEFAULT CHARSET = utf8mb4 COMMENT =\'变更日志\';\n','DROP TABLE IF EXISTS `tst_中文也分表`;','2020-02-28 18:18:47.000'),
(2019052102,'2020-02-28 18:06:05.000','1000-01-01 00:00:00.000',0,'-- @plain\nREPLACE INTO `tst_中文也分表`(`id`, `commit_id`, `login_info`, `other_info`)\nVALUES (1, -1, \'LOGIN_INFO-1\', \'OTHER_INFO-1\'),\n       (2, -1, \'LOGIN_INFO-2\', \'OTHER_INFO-2\'),\n       (3, -1, \'LOGIN_INFO-3\', \'OTHER_INFO-3\'),\n       (4, -1, \'LOGIN_INFO-4\', \'OTHER_INFO-4\'),\n       (5, -1, \'LOGIN_INFO-5\', \'OTHER_INFO-5\'),\n       (6, -1, \'LOGIN_INFO-6\', \'OTHER_INFO-6\'),\n       (7, -1, \'LOGIN_INFO-7\', \'OTHER_INFO-7\'),\n       (8, -1, \'LOGIN_INFO-8\', \'OTHER_INFO-8\'),\n       (9, -1, \'LOGIN_INFO-9\', \'OTHER_INFO-9\'),\n       (10, -1, \'LOGIN_INFO-10\', \'OTHER_INFO-10\'),\n       (11, -1, \'LOGIN_INFO-11\', \'OTHER_INFO-11\'),\n       (12, -1, \'LOGIN_INFO-12\', \'OTHER_INFO-12\'),\n       (13, -1, \'LOGIN_INFO-13\', \'OTHER_INFO-13\'),\n       (14, -1, \'LOGIN_INFO-14\', \'OTHER_INFO-14\'),\n       (15, -1, \'LOGIN_INFO-15\', \'OTHER_INFO-15\'),\n       (16, -1, \'LOGIN_INFO-16\', \'OTHER_INFO-16\'),\n       (17, -1, \'LOGIN_INFO-17\', \'OTHER_INFO-17\'),\n       (18, -1, \'LOGIN_INFO-18\', \'OTHER_INFO-18\'),\n       (19, -1, \'LOGIN_INFO-19\', \'OTHER_INFO-19\');\n','-- @plain\nTRUNCATE `tst_中文也分表`;','1000-01-01 00:00:00.000'),
(2019061501,'2020-02-28 18:18:42.000','2020-02-28 18:18:43.000',-4,'CREATE TABLE `TEST_TEMP`(\n  `SEQ_NAME` varchar(100) NOT NULL COMMENT \'序列名\'\n) ENGINE = InnoDB\n  DEFAULT CHARSET = utf8mb4 COMMENT =\'TEST_TEMP\';\n\nCREATE TABLE `TEST_TEMP_0`(\n  `SEQ_NAME` varchar(100) NOT NULL COMMENT \'序列名\'\n) ENGINE = InnoDB\n  DEFAULT CHARSET = utf8mb4 COMMENT =\'TEST_TEMP\';\n\nCREATE TABLE `TEST_TEMP_1`(\n  `SEQ_NAME` varchar(100) NOT NULL COMMENT \'序列名\'\n) ENGINE = InnoDB\n  DEFAULT CHARSET = utf8mb4 COMMENT =\'TEST_TEMP\';','DROP TABLE IF EXISTS `TEST_TEMP`','1000-01-01 00:00:00.000');

