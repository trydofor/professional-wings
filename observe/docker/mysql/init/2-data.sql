-- MySQL dump 10.13  Distrib 8.0.34, for macos13 (x86_64)
--
-- Host: 127.0.0.1    Database: wings_shard_0
-- ------------------------------------------------------
-- Server version	8.0.35

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Current Database: `wings_shard_0`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `wings_shard_0` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `wings_shard_0`;

--
-- Table structure for table `sys_commit_journal`
--

DROP TABLE IF EXISTS `sys_commit_journal`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_commit_journal` (
    `id` bigint NOT NULL COMMENT 'primary key',
    `create_dt` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT 'created datetime',
    `event_name` varchar(200) NOT NULL COMMENT 'event name',
    `target_key` varchar(200) NOT NULL DEFAULT '' COMMENT 'target data',
    `login_info` text COMMENT 'login info: agent, terminal',
    `other_info` text COMMENT 'other info: biz index data',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='104/Data Changeset';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_commit_journal`
--

LOCK TABLES `sys_commit_journal` WRITE;
/*!40000 ALTER TABLE `sys_commit_journal` DISABLE KEYS */;
INSERT INTO `sys_commit_journal` VALUES (0,'2024-01-10 16:38:34.702','system_manual_init','',NULL,NULL);
/*!40000 ALTER TABLE `sys_commit_journal` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_light_sequence`
--

DROP TABLE IF EXISTS `sys_light_sequence`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_light_sequence` (
    `seq_name` varchar(100) NOT NULL COMMENT 'sequence name',
    `block_id` int NOT NULL DEFAULT '0' COMMENT 'block',
    `next_val` bigint NOT NULL DEFAULT '1' COMMENT 'next value',
    `step_val` int NOT NULL DEFAULT '100' COMMENT 'step of increment',
    `comments` varchar(200) NOT NULL COMMENT 'comments',
    PRIMARY KEY (`seq_name`,`block_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='103/Sequence Generation';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_light_sequence`
--

LOCK TABLES `sys_light_sequence` WRITE;
/*!40000 ALTER TABLE `sys_light_sequence` DISABLE KEYS */;
INSERT INTO `sys_light_sequence` VALUES ('singleton_lightid_blockid',0,10000000,100,'default block_id'),('sys_commit_journal',0,1,100,'sys_commit_journal'),('tst_normal_table',0,1000,1,'for test step 1');
/*!40000 ALTER TABLE `sys_light_sequence` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_schema_journal`
--

DROP TABLE IF EXISTS `sys_schema_journal`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_schema_journal` (
    `table_name` varchar(100) NOT NULL COMMENT 'plain table name',
    `create_dt` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT 'created datetime',
    `modify_dt` datetime(3) NOT NULL DEFAULT '1000-01-01 00:00:00.000' ON UPDATE CURRENT_TIMESTAMP(3) COMMENT 'modified datetime',
    `commit_id` bigint NOT NULL COMMENT 'commit id',
    `ddl_instbl` text NOT NULL COMMENT 'trace DDL of insert',
    `ddl_instrg` text NOT NULL COMMENT 'trigger DDL of insert',
    `ddl_updtbl` text NOT NULL COMMENT 'trace DDL of update',
    `ddl_updtrg` text NOT NULL COMMENT 'trigger DDL of update',
    `ddl_deltbl` text NOT NULL COMMENT 'trace DDL of delete',
    `ddl_deltrg` text NOT NULL COMMENT 'trigger DDL of delete',
    `log_insert` datetime(3) NOT NULL DEFAULT '1000-01-01 00:00:00.000' COMMENT 'applied datetime of insert',
    `log_update` datetime(3) NOT NULL DEFAULT '1000-01-01 00:00:00.000' COMMENT 'applied datetime of update',
    `log_delete` datetime(3) NOT NULL DEFAULT '1000-01-01 00:00:00.000' COMMENT 'applied datetime of delete',
    PRIMARY KEY (`table_name`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='102/Table Trigger';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_schema_journal`
--

LOCK TABLES `sys_schema_journal` WRITE;
/*!40000 ALTER TABLE `sys_schema_journal` DISABLE KEYS */;
INSERT INTO `sys_schema_journal` VALUES ('sys_schema_journal','2024-01-10 16:38:34.425','1000-01-01 00:00:00.000',0,'','','','','','','1000-01-01 00:00:00.000','1000-01-01 00:00:00.000','1000-01-01 00:00:00.000'),('sys_schema_version','2024-01-10 16:38:34.425','1000-01-01 00:00:00.000',0,'','','','','','','1000-01-01 00:00:00.000','1000-01-01 00:00:00.000','1000-01-01 00:00:00.000');
/*!40000 ALTER TABLE `sys_schema_journal` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_schema_version`
--

DROP TABLE IF EXISTS `sys_schema_version`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_schema_version` (
    `revision` bigint NOT NULL COMMENT 'version + build',
    `create_dt` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT 'created datetime',
    `modify_dt` datetime(3) NOT NULL DEFAULT '1000-01-01 00:00:00.000' ON UPDATE CURRENT_TIMESTAMP(3) COMMENT 'modified datetime',
    `commit_id` bigint NOT NULL COMMENT 'commit id',
    `apply_dt` datetime(3) NOT NULL DEFAULT '1000-01-01 00:00:00.000' COMMENT 'applied datetime',
    `comments` varchar(500) NOT NULL DEFAULT '' COMMENT 'sql path',
    `upto_sql` text NOT NULL COMMENT 'upgrade script',
    `undo_sql` text NOT NULL COMMENT 'downgrade script',
    PRIMARY KEY (`revision`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='101/Table Structure';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_schema_version`
--

LOCK TABLES `sys_schema_version` WRITE;
/*!40000 ALTER TABLE `sys_schema_version` DISABLE KEYS */;
INSERT INTO `sys_schema_version` VALUES (2019051201,'2024-01-10 16:38:34.421','2024-01-10 16:38:34.435',0,'2024-01-10 16:38:34.430','master/00-init/2019-0512_01-version-journal.sql','-- Unify timezone (GMT+8), Unify charset (utf8mb4)\n-- CREATE DATABASE `wings` /*!40100 DEFAULT CHARACTER SET utf8mb4 */;\n\nCREATE TABLE `sys_schema_version` (\n    `revision`  BIGINT(20)   NOT NULL COMMENT \'version + build\',\n    `create_dt` DATETIME(3)  NOT NULL DEFAULT NOW(3) COMMENT \'created datetime\',\n    `modify_dt` DATETIME(3)  NOT NULL DEFAULT \'1000-01-01 00:00:00.000\' ON UPDATE NOW(3) COMMENT \'modified datetime\',\n    `commit_id` BIGINT(20)   NOT NULL COMMENT \'commit id\',\n    `apply_dt`  DATETIME(3)  NOT NULL DEFAULT \'1000-01-01 00:00:00.000\' COMMENT \'applied datetime\',\n    `comments`  VARCHAR(500) NOT NULL DEFAULT \'\' COMMENT \'sql path\',\n    `upto_sql`  TEXT         NOT NULL COMMENT \'upgrade script\',\n    `undo_sql`  TEXT         NOT NULL COMMENT \'downgrade script\',\n    PRIMARY KEY (`revision`)\n) ENGINE = InnoDB\n  DEFAULT CHARSET = utf8mb4 COMMENT =\'101/Table Structure\';\n\nCREATE TABLE `sys_schema_journal` (\n    `table_name` VARCHAR(100) NOT NULL COMMENT \'plain table name\',\n    `create_dt`  DATETIME(3)  NOT NULL DEFAULT NOW(3) COMMENT \'created datetime\',\n    `modify_dt`  DATETIME(3)  NOT NULL DEFAULT \'1000-01-01 00:00:00.000\' ON UPDATE NOW(3) COMMENT \'modified datetime\',\n    `commit_id`  BIGINT(20)   NOT NULL COMMENT \'commit id\',\n    `ddl_instbl` TEXT         NOT NULL COMMENT \'trace DDL of insert\',\n    `ddl_instrg` TEXT         NOT NULL COMMENT \'trigger DDL of insert\',\n    `ddl_updtbl` TEXT         NOT NULL COMMENT \'trace DDL of update\',\n    `ddl_updtrg` TEXT         NOT NULL COMMENT \'trigger DDL of update\',\n    `ddl_deltbl` TEXT         NOT NULL COMMENT \'trace DDL of delete\',\n    `ddl_deltrg` TEXT         NOT NULL COMMENT \'trigger DDL of delete\',\n    `log_insert` DATETIME(3)  NOT NULL DEFAULT \'1000-01-01 00:00:00.000\' COMMENT \'applied datetime of insert\',\n    `log_update` DATETIME(3)  NOT NULL DEFAULT \'1000-01-01 00:00:00.000\' COMMENT \'applied datetime of update\',\n    `log_delete` DATETIME(3)  NOT NULL DEFAULT \'1000-01-01 00:00:00.000\' COMMENT \'applied datetime of delete\',\n    PRIMARY KEY (`table_name`) USING BTREE\n) ENGINE = InnoDB\n  DEFAULT CHARSET = utf8mb4 COMMENT =\'102/Table Trigger\';\n\n-- sys_schema_version@plain\nINSERT IGNORE INTO `sys_schema_version` (`revision`, `commit_id`, `upto_sql`, `undo_sql`, `apply_dt`)\nVALUES (2019051201, 0, \'\', \'\', NOW(3));\n\n-- sys_schema_journal@plain\nREPLACE INTO `sys_schema_journal` (`table_name`, `commit_id`, `ddl_instbl`, `ddl_instrg`, `ddl_updtbl`, `ddl_updtrg`, `ddl_deltbl`, `ddl_deltrg`)\nVALUES (\'sys_schema_journal\', 0, \'\', \'\', \'\', \'\', \'\', \'\'),\n       (\'sys_schema_version\', 0, \'\', \'\', \'\', \'\', \'\', \'\');\n','DROP TABLE IF EXISTS `sys_schema_version`; -- 101/Table Structure;\nDROP TABLE IF EXISTS `sys_schema_journal`; -- 102/Table Trigger;\n'),(2019052001,'2024-01-10 16:38:34.448','2024-01-10 16:38:34.706',0,'2024-01-10 16:38:34.706','master/01-light/2019-0520_01-light-commit.sql','CREATE TABLE `sys_light_sequence` (\n    `seq_name` VARCHAR(100) NOT NULL COMMENT \'sequence name\',\n    `block_id` INT(11)      NOT NULL DEFAULT 0 COMMENT \'block\',\n    `next_val` BIGINT(20)   NOT NULL DEFAULT \'1\' COMMENT \'next value\',\n    `step_val` INT(11)      NOT NULL DEFAULT \'100\' COMMENT \'step of increment\',\n    `comments` VARCHAR(200) NOT NULL COMMENT \'comments\',\n    PRIMARY KEY (`seq_name`, `block_id`)\n) ENGINE = InnoDB\n  DEFAULT CHARSET = utf8mb4 COMMENT =\'103/Sequence Generation\';\n\nCREATE TABLE `sys_commit_journal` (\n    `id`         BIGINT(20)   NOT NULL COMMENT \'primary key\',\n    `create_dt`  DATETIME(3)  NOT NULL DEFAULT NOW(3) COMMENT \'created datetime\',\n    `event_name` VARCHAR(200) NOT NULL COMMENT \'event name\',\n    `target_key` VARCHAR(200) NOT NULL DEFAULT \'\' COMMENT \'target data\',\n    `login_info` TEXT COMMENT \'login info: agent, terminal\',\n    `other_info` TEXT COMMENT \'other info: biz index data\',\n    PRIMARY KEY (`id`)\n) ENGINE = InnoDB\n  DEFAULT CHARSET = utf8mb4 COMMENT =\'104/Data Changeset\';\n\n-- sys_light_sequence@plain\nINSERT IGNORE INTO `sys_light_sequence` (`seq_name`, `block_id`, `next_val`, `step_val`, `comments`)\nVALUES (\'singleton_lightid_blockid\', 0, 10000000, 100, \'default block_id\'),\n       (\'sys_commit_journal\', 0, 1, 100, \'sys_commit_journal\');\n\n-- sys_commit_journal@plain\nREPLACE INTO `sys_commit_journal` (`id`, `event_name`)\nVALUES (0, \'system_manual_init\');\n','-- ask@danger\nDROP TABLE IF EXISTS `sys_light_sequence`; -- 103/Sequence Generation;\nDROP TABLE IF EXISTS `sys_commit_journal`; -- 104/Data Changeset;\n'),(2019060101,'2024-01-10 16:38:34.464','2024-01-10 16:38:34.793',0,'2024-01-10 16:38:34.793','master/2019-0601_01-test.sql','-- apply@tst_.* error@stop\nCREATE TABLE `tst_sharding` (\n    `id`         BIGINT(20)  NOT NULL COMMENT \'primary key\',\n    `create_dt`  DATETIME(3) NOT NULL DEFAULT NOW(3) COMMENT \'created datetime\',\n    `modify_dt`  DATETIME(3) NOT NULL DEFAULT \'1000-01-01\' ON UPDATE NOW(3) COMMENT \'modified datetime\',\n    `delete_dt`  DATETIME(3) NOT NULL DEFAULT \'1000-01-01\' COMMENT \'logic deleted datetime\',\n    `commit_id`  BIGINT(20)  NOT NULL COMMENT \'commit id\',\n    `login_info` TEXT COMMENT \'login info: agent, terminal\',\n    `other_info` TEXT COMMENT \'other info: biz index data\',\n    `language`   INT(11)     NOT NULL DEFAULT 1020111 COMMENT \'StandardLanguage\',\n    PRIMARY KEY (`id`)\n) ENGINE = InnoDB\n  DEFAULT CHARSET = utf8mb4 COMMENT =\'201/Sharding Test\';\n\nCREATE TABLE `tst_sharding_postfix` (\n    `id`         BIGINT(20)  NOT NULL COMMENT \'primary key\',\n    `create_dt`  DATETIME(3) NOT NULL DEFAULT NOW(3) COMMENT \'created datetime\',\n    `modify_dt`  DATETIME(3) NOT NULL DEFAULT \'1000-01-01\' ON UPDATE NOW(3) COMMENT \'modified datetime\',\n    `delete_dt`  DATETIME(3) NOT NULL DEFAULT \'1000-01-01\' COMMENT \'logic deleted datetime\',\n    `commit_id`  BIGINT(20)  NOT NULL COMMENT \'commit id\',\n    `login_info` TEXT COMMENT \'login info: agent, terminal\',\n    `other_info` TEXT COMMENT \'other info: biz index data\',\n    `language`   INT(11)     NOT NULL DEFAULT 1020111 COMMENT \'StandardLanguage\',\n    PRIMARY KEY (`id`)\n) ENGINE = InnoDB\n  DEFAULT CHARSET = utf8mb4 COMMENT =\'201/Sharding Test\';\n\nCREATE TABLE `tst_normal_table` (\n    `id`            BIGINT(20)     NOT NULL COMMENT \'primary key\',\n    `create_dt`     DATETIME(3)    NOT NULL DEFAULT NOW(3) COMMENT \'created datetime\',\n    `modify_dt`     DATETIME(3)    NOT NULL DEFAULT \'1000-01-01\' ON UPDATE NOW(3) COMMENT \'modified datetime\',\n    `delete_dt`     DATETIME(3)    NOT NULL DEFAULT \'1000-01-01\' COMMENT \'logic deleted datetime\',\n    `commit_id`     BIGINT(20)     NOT NULL COMMENT \'commit id\',\n    `value_varchar` VARCHAR(256)   NOT NULL DEFAULT \'0\' COMMENT \'String\',\n    `value_decimal` DECIMAL(10, 2) NOT NULL DEFAULT \'0\' COMMENT \'BigDecimal\',\n    `value_boolean` TINYINT(1)     NOT NULL DEFAULT \'0\' COMMENT \'Boolean\',\n    `value_int`     INT(11)        NOT NULL DEFAULT \'0\' COMMENT \'Integer\',\n    `value_long`    BIGINT(20)     NOT NULL DEFAULT \'0\' COMMENT \'Long\',\n    `value_date`    DATE           NOT NULL DEFAULT \'1000-01-01\' COMMENT \'LocalDate\',\n    `value_time`    TIME           NOT NULL DEFAULT \'00:00:00\' COMMENT \'LocalTime\',\n    `value_lang`    INT(11)        NOT NULL DEFAULT \'1020111\' COMMENT \'StandardLanguage\',\n    PRIMARY KEY (`id`)\n) ENGINE = InnoDB\n  DEFAULT CHARSET = utf8mb4 COMMENT =\'202/Normal Test\';\n\nREPLACE INTO `sys_light_sequence` (`seq_name`, `block_id`, `next_val`, `step_val`, `comments`)\nVALUES (\'tst_normal_table\', 0, 1000, 1, \'for test step 1\');\n','DROP TABLE IF EXISTS `tst_sharding`; -- 201/sharding;\nDROP TABLE IF EXISTS `tst_sharding_postfix`; -- 201/sharding;\nDROP TABLE IF EXISTS `tst_normal_table`; -- 202/normal;\n'),(2019060102,'2024-01-10 16:38:34.479','2024-01-10 16:38:34.830',2,'2024-01-10 16:38:34.830','master/2019-0601_02-test.sql','-- @plain error@skip\nREPLACE INTO `tst_sharding`(`id`, `commit_id`, `login_info`, `other_info`)\nVALUES (100, -1, \'LOGIN_INFO-00\', \'OTHER_INFO-00\'),\n       (101, -1, \'LOGIN_INFO-01\', \'OTHER_INFO-01\'),\n       (102, -1, \'LOGIN_INFO-02\', \'OTHER_INFO-02\'),\n       (103, -1, \'LOGIN_INFO-03\', \'OTHER_INFO-03\'),\n       (104, -1, \'LOGIN_INFO-04\', \'OTHER_INFO-04\'),\n       (105, -1, \'LOGIN_INFO-05\', \'OTHER_INFO-05\'),\n       (106, -1, \'LOGIN_INFO-06\', \'OTHER_INFO-06\'),\n       (107, -1, \'LOGIN_INFO-07\', \'OTHER_INFO-07\'),\n       (108, -1, \'LOGIN_INFO-08\', \'OTHER_INFO-08\'),\n       (109, -1, \'LOGIN_INFO-09\', \'OTHER_INFO-09\'),\n       (110, -1, \'LOGIN_INFO-10\', \'OTHER_INFO-10\'),\n       (111, -1, \'LOGIN_INFO-11\', \'OTHER_INFO-11\'),\n       (112, -1, \'LOGIN_INFO-12\', \'OTHER_INFO-12\'),\n       (113, -1, \'LOGIN_INFO-13\', \'OTHER_INFO-13\'),\n       (114, -1, \'LOGIN_INFO-14\', \'OTHER_INFO-14\'),\n       (115, -1, \'LOGIN_INFO-15\', \'OTHER_INFO-15\'),\n       (116, -1, \'LOGIN_INFO-16\', \'OTHER_INFO-16\'),\n       (117, -1, \'LOGIN_INFO-17\', \'OTHER_INFO-17\'),\n       (118, -1, \'LOGIN_INFO-18\', \'OTHER_INFO-18\'),\n       (119, -1, \'LOGIN_INFO-19\', \'OTHER_INFO-19\');\n','-- @plain\nTRUNCATE `tst_sharding`;\n');
/*!40000 ALTER TABLE `sys_schema_version` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tst_normal_table`
--

DROP TABLE IF EXISTS `tst_normal_table`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tst_normal_table` (
    `id` bigint NOT NULL COMMENT 'primary key',
    `create_dt` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT 'created datetime',
    `modify_dt` datetime(3) NOT NULL DEFAULT '1000-01-01 00:00:00.000' ON UPDATE CURRENT_TIMESTAMP(3) COMMENT 'modified datetime',
    `delete_dt` datetime(3) NOT NULL DEFAULT '1000-01-01 00:00:00.000' COMMENT 'logic deleted datetime',
    `commit_id` bigint NOT NULL COMMENT 'commit id',
    `value_varchar` varchar(256) NOT NULL DEFAULT '0' COMMENT 'String',
    `value_decimal` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT 'BigDecimal',
    `value_boolean` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Boolean',
    `value_int` int NOT NULL DEFAULT '0' COMMENT 'Integer',
    `value_long` bigint NOT NULL DEFAULT '0' COMMENT 'Long',
    `value_date` date NOT NULL DEFAULT '1000-01-01' COMMENT 'LocalDate',
    `value_time` time NOT NULL DEFAULT '00:00:00' COMMENT 'LocalTime',
    `value_lang` int NOT NULL DEFAULT '1020111' COMMENT 'StandardLanguage',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='202/Normal Test';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tst_normal_table`
--

LOCK TABLES `tst_normal_table` WRITE;
/*!40000 ALTER TABLE `tst_normal_table` DISABLE KEYS */;
/*!40000 ALTER TABLE `tst_normal_table` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tst_sharding`
--

DROP TABLE IF EXISTS `tst_sharding`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tst_sharding` (
    `id` bigint NOT NULL COMMENT 'primary key',
    `create_dt` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT 'created datetime',
    `modify_dt` datetime(3) NOT NULL DEFAULT '1000-01-01 00:00:00.000' ON UPDATE CURRENT_TIMESTAMP(3) COMMENT 'modified datetime',
    `delete_dt` datetime(3) NOT NULL DEFAULT '1000-01-01 00:00:00.000' COMMENT 'logic deleted datetime',
    `commit_id` bigint NOT NULL COMMENT 'commit id',
    `login_info` text COMMENT 'login info: agent, terminal',
    `other_info` text COMMENT 'other info: biz index data',
    `language` int NOT NULL DEFAULT '1020111' COMMENT 'StandardLanguage',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='201/Sharding Test';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tst_sharding`
--

LOCK TABLES `tst_sharding` WRITE;
/*!40000 ALTER TABLE `tst_sharding` DISABLE KEYS */;
/*!40000 ALTER TABLE `tst_sharding` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tst_sharding_0`
--

DROP TABLE IF EXISTS `tst_sharding_0`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tst_sharding_0` (
    `id` bigint NOT NULL COMMENT 'primary key',
    `create_dt` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT 'created datetime',
    `modify_dt` datetime(3) NOT NULL DEFAULT '1000-01-01 00:00:00.000' ON UPDATE CURRENT_TIMESTAMP(3) COMMENT 'modified datetime',
    `delete_dt` datetime(3) NOT NULL DEFAULT '1000-01-01 00:00:00.000' COMMENT 'logic deleted datetime',
    `commit_id` bigint NOT NULL COMMENT 'commit id',
    `login_info` text COMMENT 'login info: agent, terminal',
    `other_info` text COMMENT 'other info: biz index data',
    `language` int NOT NULL DEFAULT '1020111' COMMENT 'StandardLanguage',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='201/Sharding Test';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tst_sharding_0`
--

LOCK TABLES `tst_sharding_0` WRITE;
/*!40000 ALTER TABLE `tst_sharding_0` DISABLE KEYS */;
INSERT INTO `tst_sharding_0` VALUES (100,'2024-01-10 16:38:34.824','1000-01-01 00:00:00.000','1000-01-01 00:00:00.000',-1,'LOGIN_INFO-00','OTHER_INFO-00',1020111),(105,'2024-01-10 16:38:34.824','1000-01-01 00:00:00.000','1000-01-01 00:00:00.000',-1,'LOGIN_INFO-05','OTHER_INFO-05',1020111),(110,'2024-01-10 16:38:34.824','1000-01-01 00:00:00.000','1000-01-01 00:00:00.000',-1,'LOGIN_INFO-10','OTHER_INFO-10',1020111),(115,'2024-01-10 16:38:34.824','1000-01-01 00:00:00.000','1000-01-01 00:00:00.000',-1,'LOGIN_INFO-15','OTHER_INFO-15',1020111);
/*!40000 ALTER TABLE `tst_sharding_0` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tst_sharding_1`
--

DROP TABLE IF EXISTS `tst_sharding_1`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tst_sharding_1` (
    `id` bigint NOT NULL COMMENT 'primary key',
    `create_dt` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT 'created datetime',
    `modify_dt` datetime(3) NOT NULL DEFAULT '1000-01-01 00:00:00.000' ON UPDATE CURRENT_TIMESTAMP(3) COMMENT 'modified datetime',
    `delete_dt` datetime(3) NOT NULL DEFAULT '1000-01-01 00:00:00.000' COMMENT 'logic deleted datetime',
    `commit_id` bigint NOT NULL COMMENT 'commit id',
    `login_info` text COMMENT 'login info: agent, terminal',
    `other_info` text COMMENT 'other info: biz index data',
    `language` int NOT NULL DEFAULT '1020111' COMMENT 'StandardLanguage',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='201/Sharding Test';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tst_sharding_1`
--

LOCK TABLES `tst_sharding_1` WRITE;
/*!40000 ALTER TABLE `tst_sharding_1` DISABLE KEYS */;
INSERT INTO `tst_sharding_1` VALUES (101,'2024-01-10 16:38:34.824','1000-01-01 00:00:00.000','1000-01-01 00:00:00.000',-1,'LOGIN_INFO-01','OTHER_INFO-01',1020111),(106,'2024-01-10 16:38:34.824','1000-01-01 00:00:00.000','1000-01-01 00:00:00.000',-1,'LOGIN_INFO-06','OTHER_INFO-06',1020111),(111,'2024-01-10 16:38:34.824','1000-01-01 00:00:00.000','1000-01-01 00:00:00.000',-1,'LOGIN_INFO-11','OTHER_INFO-11',1020111),(116,'2024-01-10 16:38:34.824','1000-01-01 00:00:00.000','1000-01-01 00:00:00.000',-1,'LOGIN_INFO-16','OTHER_INFO-16',1020111);
/*!40000 ALTER TABLE `tst_sharding_1` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tst_sharding_2`
--

DROP TABLE IF EXISTS `tst_sharding_2`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tst_sharding_2` (
    `id` bigint NOT NULL COMMENT 'primary key',
    `create_dt` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT 'created datetime',
    `modify_dt` datetime(3) NOT NULL DEFAULT '1000-01-01 00:00:00.000' ON UPDATE CURRENT_TIMESTAMP(3) COMMENT 'modified datetime',
    `delete_dt` datetime(3) NOT NULL DEFAULT '1000-01-01 00:00:00.000' COMMENT 'logic deleted datetime',
    `commit_id` bigint NOT NULL COMMENT 'commit id',
    `login_info` text COMMENT 'login info: agent, terminal',
    `other_info` text COMMENT 'other info: biz index data',
    `language` int NOT NULL DEFAULT '1020111' COMMENT 'StandardLanguage',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='201/Sharding Test';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tst_sharding_2`
--

LOCK TABLES `tst_sharding_2` WRITE;
/*!40000 ALTER TABLE `tst_sharding_2` DISABLE KEYS */;
INSERT INTO `tst_sharding_2` VALUES (102,'2024-01-10 16:38:34.824','1000-01-01 00:00:00.000','1000-01-01 00:00:00.000',-1,'LOGIN_INFO-02','OTHER_INFO-02',1020111),(107,'2024-01-10 16:38:34.824','1000-01-01 00:00:00.000','1000-01-01 00:00:00.000',-1,'LOGIN_INFO-07','OTHER_INFO-07',1020111),(112,'2024-01-10 16:38:34.824','1000-01-01 00:00:00.000','1000-01-01 00:00:00.000',-1,'LOGIN_INFO-12','OTHER_INFO-12',1020111),(117,'2024-01-10 16:38:34.824','1000-01-01 00:00:00.000','1000-01-01 00:00:00.000',-1,'LOGIN_INFO-17','OTHER_INFO-17',1020111);
/*!40000 ALTER TABLE `tst_sharding_2` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tst_sharding_3`
--

DROP TABLE IF EXISTS `tst_sharding_3`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tst_sharding_3` (
    `id` bigint NOT NULL COMMENT 'primary key',
    `create_dt` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT 'created datetime',
    `modify_dt` datetime(3) NOT NULL DEFAULT '1000-01-01 00:00:00.000' ON UPDATE CURRENT_TIMESTAMP(3) COMMENT 'modified datetime',
    `delete_dt` datetime(3) NOT NULL DEFAULT '1000-01-01 00:00:00.000' COMMENT 'logic deleted datetime',
    `commit_id` bigint NOT NULL COMMENT 'commit id',
    `login_info` text COMMENT 'login info: agent, terminal',
    `other_info` text COMMENT 'other info: biz index data',
    `language` int NOT NULL DEFAULT '1020111' COMMENT 'StandardLanguage',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='201/Sharding Test';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tst_sharding_3`
--

LOCK TABLES `tst_sharding_3` WRITE;
/*!40000 ALTER TABLE `tst_sharding_3` DISABLE KEYS */;
INSERT INTO `tst_sharding_3` VALUES (103,'2024-01-10 16:38:34.824','1000-01-01 00:00:00.000','1000-01-01 00:00:00.000',-1,'LOGIN_INFO-03','OTHER_INFO-03',1020111),(108,'2024-01-10 16:38:34.824','1000-01-01 00:00:00.000','1000-01-01 00:00:00.000',-1,'LOGIN_INFO-08','OTHER_INFO-08',1020111),(113,'2024-01-10 16:38:34.824','1000-01-01 00:00:00.000','1000-01-01 00:00:00.000',-1,'LOGIN_INFO-13','OTHER_INFO-13',1020111),(118,'2024-01-10 16:38:34.824','1000-01-01 00:00:00.000','1000-01-01 00:00:00.000',-1,'LOGIN_INFO-18','OTHER_INFO-18',1020111);
/*!40000 ALTER TABLE `tst_sharding_3` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tst_sharding_4`
--

DROP TABLE IF EXISTS `tst_sharding_4`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tst_sharding_4` (
    `id` bigint NOT NULL COMMENT 'primary key',
    `create_dt` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT 'created datetime',
    `modify_dt` datetime(3) NOT NULL DEFAULT '1000-01-01 00:00:00.000' ON UPDATE CURRENT_TIMESTAMP(3) COMMENT 'modified datetime',
    `delete_dt` datetime(3) NOT NULL DEFAULT '1000-01-01 00:00:00.000' COMMENT 'logic deleted datetime',
    `commit_id` bigint NOT NULL COMMENT 'commit id',
    `login_info` text COMMENT 'login info: agent, terminal',
    `other_info` text COMMENT 'other info: biz index data',
    `language` int NOT NULL DEFAULT '1020111' COMMENT 'StandardLanguage',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='201/Sharding Test';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tst_sharding_4`
--

LOCK TABLES `tst_sharding_4` WRITE;
/*!40000 ALTER TABLE `tst_sharding_4` DISABLE KEYS */;
INSERT INTO `tst_sharding_4` VALUES (104,'2024-01-10 16:38:34.824','1000-01-01 00:00:00.000','1000-01-01 00:00:00.000',-1,'LOGIN_INFO-04','OTHER_INFO-04',1020111),(109,'2024-01-10 16:38:34.824','1000-01-01 00:00:00.000','1000-01-01 00:00:00.000',-1,'LOGIN_INFO-09','OTHER_INFO-09',1020111),(114,'2024-01-10 16:38:34.824','1000-01-01 00:00:00.000','1000-01-01 00:00:00.000',-1,'LOGIN_INFO-14','OTHER_INFO-14',1020111),(119,'2024-01-10 16:38:34.824','1000-01-01 00:00:00.000','1000-01-01 00:00:00.000',-1,'LOGIN_INFO-19','OTHER_INFO-19',1020111);
/*!40000 ALTER TABLE `tst_sharding_4` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tst_sharding_postfix`
--

DROP TABLE IF EXISTS `tst_sharding_postfix`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tst_sharding_postfix` (
    `id` bigint NOT NULL COMMENT 'primary key',
    `create_dt` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT 'created datetime',
    `modify_dt` datetime(3) NOT NULL DEFAULT '1000-01-01 00:00:00.000' ON UPDATE CURRENT_TIMESTAMP(3) COMMENT 'modified datetime',
    `delete_dt` datetime(3) NOT NULL DEFAULT '1000-01-01 00:00:00.000' COMMENT 'logic deleted datetime',
    `commit_id` bigint NOT NULL COMMENT 'commit id',
    `login_info` text COMMENT 'login info: agent, terminal',
    `other_info` text COMMENT 'other info: biz index data',
    `language` int NOT NULL DEFAULT '1020111' COMMENT 'StandardLanguage',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='201/Sharding Test';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tst_sharding_postfix`
--

LOCK TABLES `tst_sharding_postfix` WRITE;
/*!40000 ALTER TABLE `tst_sharding_postfix` DISABLE KEYS */;
/*!40000 ALTER TABLE `tst_sharding_postfix` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Current Database: `wings_tiny`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `wings_tiny` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `wings_tiny`;

--
-- Table structure for table `sys_commit_journal`
--

DROP TABLE IF EXISTS `sys_commit_journal`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_commit_journal` (
    `id` bigint NOT NULL COMMENT 'primary key',
    `create_dt` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT 'created datetime',
    `event_name` varchar(200) NOT NULL COMMENT 'event name',
    `target_key` varchar(200) NOT NULL DEFAULT '' COMMENT 'target data',
    `login_info` text COMMENT 'login info: agent, terminal',
    `other_info` text COMMENT 'other info: biz index data',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='104/Data Changeset';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_commit_journal`
--

LOCK TABLES `sys_commit_journal` WRITE;
/*!40000 ALTER TABLE `sys_commit_journal` DISABLE KEYS */;
INSERT INTO `sys_commit_journal` VALUES (0,'2024-01-10 16:04:34.064','system_manual_init','',NULL,NULL),(1,'2024-01-10 16:04:49.662','pro.fessional.wings.tiny.task.service.impl.TinyTaskConfServiceImpl$Jane#Insert','','',''),(2,'2024-01-10 16:04:49.776','pro.fessional.wings.tiny.task.service.impl.TinyTaskConfServiceImpl$Jane#Insert','','',''),(3,'2024-01-10 16:04:49.819','pro.fessional.wings.tiny.task.service.impl.TinyTaskConfServiceImpl$Jane#Insert','','',''),(4,'2024-01-10 16:04:49.851','pro.fessional.wings.tiny.task.service.impl.TinyTaskConfServiceImpl$Jane#Insert','','',''),(5,'2024-01-10 16:04:49.898','pro.fessional.wings.tiny.task.service.impl.TinyTaskExecServiceImpl$Jane#SaveNextExec','','',''),(6,'2024-01-10 16:04:49.936','pro.fessional.wings.tiny.task.service.impl.TinyTaskExecServiceImpl$Jane#SaveNextExec','','',''),(7,'2024-01-10 16:04:49.966','pro.fessional.wings.tiny.task.service.impl.TinyTaskExecServiceImpl$Jane#SaveNextExec','','',''),(8,'2024-01-10 16:04:49.993','pro.fessional.wings.tiny.task.service.impl.TinyTaskConfServiceImpl$Jane#Insert','','',''),(9,'2024-01-10 16:04:50.051','pro.fessional.wings.tiny.task.service.impl.TinyTaskConfServiceImpl$Jane#Insert','','',''),(10,'2024-01-10 16:04:50.091','pro.fessional.wings.tiny.task.service.impl.TinyTaskExecServiceImpl$Jane#SaveNextExec','','',''),(11,'2024-01-10 16:04:50.111','pro.fessional.wings.tiny.task.service.impl.TinyTaskExecServiceImpl$Jane#SaveNextExec','','',''),(12,'2024-01-10 16:04:50.147','pro.fessional.wings.tiny.task.service.impl.TinyTaskExecServiceImpl$Jane#SaveResult','','',''),(13,'2024-01-10 16:04:50.223','pro.fessional.wings.tiny.task.service.impl.TinyTaskExecServiceImpl$Jane#SaveNextExec','','',''),(14,'2024-01-10 16:04:51.378','pro.fessional.wings.tiny.task.service.impl.TinyTaskConfServiceImpl$Jane#Insert','','',''),(15,'2024-01-10 16:04:51.465','pro.fessional.wings.tiny.task.service.impl.TinyTaskExecServiceImpl$Jane#SaveResult','','',''),(16,'2024-01-10 16:04:51.492','pro.fessional.wings.tiny.task.service.impl.TinyTaskExecServiceImpl$Jane#SaveNextExec','','',''),(17,'2024-01-10 16:04:51.494','pro.fessional.wings.tiny.task.service.impl.TinyTaskExecServiceImpl$Jane#SaveResult','','',''),(18,'2024-01-10 16:04:51.522','pro.fessional.wings.tiny.task.service.impl.TinyTaskExecServiceImpl$Jane#SaveNextExec','','',''),(19,'2024-01-10 16:05:01.518','pro.fessional.wings.tiny.task.service.impl.TinyTaskExecServiceImpl$Jane#SaveResult','','',''),(20,'2024-01-10 16:05:01.547','pro.fessional.wings.tiny.task.service.impl.TinyTaskExecServiceImpl$Jane#SaveNextExec','','',''),(101,'2024-01-10 16:42:04.585','pro.fessional.wings.tiny.task.service.impl.TinyTaskExecServiceImpl$Jane#SaveNextExec','','',''),(102,'2024-01-10 16:42:04.640','pro.fessional.wings.tiny.task.service.impl.TinyTaskExecServiceImpl$Jane#SaveNextExec','','',''),(103,'2024-01-10 16:42:04.661','pro.fessional.wings.tiny.task.service.impl.TinyTaskExecServiceImpl$Jane#SaveNextExec','','',''),(104,'2024-01-10 16:42:04.702','pro.fessional.wings.tiny.task.service.impl.TinyTaskExecServiceImpl$Jane#SaveNextExec','','',''),(105,'2024-01-10 16:42:04.725','pro.fessional.wings.tiny.task.service.impl.TinyTaskExecServiceImpl$Jane#SaveNextExec','','',''),(106,'2024-01-10 16:42:21.529','pro.fessional.wings.tiny.task.service.impl.TinyTaskExecServiceImpl$Jane#SaveResult','','',''),(107,'2024-01-10 16:42:21.590','pro.fessional.wings.tiny.task.service.impl.TinyTaskExecServiceImpl$Jane#SaveNextExec','','','');
/*!40000 ALTER TABLE `sys_commit_journal` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_light_sequence`
--

DROP TABLE IF EXISTS `sys_light_sequence`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_light_sequence` (
    `seq_name` varchar(100) NOT NULL COMMENT 'sequence name',
    `block_id` int NOT NULL DEFAULT '0' COMMENT 'block',
    `next_val` bigint NOT NULL DEFAULT '1' COMMENT 'next value',
    `step_val` int NOT NULL DEFAULT '100' COMMENT 'step of increment',
    `comments` varchar(200) NOT NULL COMMENT 'comments',
    PRIMARY KEY (`seq_name`,`block_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='103/Sequence Generation';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_light_sequence`
--

LOCK TABLES `sys_light_sequence` WRITE;
/*!40000 ALTER TABLE `sys_light_sequence` DISABLE KEYS */;
INSERT INTO `sys_light_sequence` VALUES ('singleton_lightid_blockid',0,10000000,100,'default block_id'),('sys_commit_journal',0,201,100,'sys_commit_journal'),('win_task_define',0,1100,100,'Auto insert if Not found'),('win_task_result',0,1200,100,'Auto insert if Not found');
/*!40000 ALTER TABLE `sys_light_sequence` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_schema_journal`
--

DROP TABLE IF EXISTS `sys_schema_journal`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_schema_journal` (
    `table_name` varchar(100) NOT NULL COMMENT 'plain table name',
    `create_dt` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT 'created datetime',
    `modify_dt` datetime(3) NOT NULL DEFAULT '1000-01-01 00:00:00.000' ON UPDATE CURRENT_TIMESTAMP(3) COMMENT 'modified datetime',
    `commit_id` bigint NOT NULL COMMENT 'commit id',
    `ddl_instbl` text NOT NULL COMMENT 'trace DDL of insert',
    `ddl_instrg` text NOT NULL COMMENT 'trigger DDL of insert',
    `ddl_updtbl` text NOT NULL COMMENT 'trace DDL of update',
    `ddl_updtrg` text NOT NULL COMMENT 'trigger DDL of update',
    `ddl_deltbl` text NOT NULL COMMENT 'trace DDL of delete',
    `ddl_deltrg` text NOT NULL COMMENT 'trigger DDL of delete',
    `log_insert` datetime(3) NOT NULL DEFAULT '1000-01-01 00:00:00.000' COMMENT 'applied datetime of insert',
    `log_update` datetime(3) NOT NULL DEFAULT '1000-01-01 00:00:00.000' COMMENT 'applied datetime of update',
    `log_delete` datetime(3) NOT NULL DEFAULT '1000-01-01 00:00:00.000' COMMENT 'applied datetime of delete',
    PRIMARY KEY (`table_name`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='102/Table Trigger';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_schema_journal`
--

LOCK TABLES `sys_schema_journal` WRITE;
/*!40000 ALTER TABLE `sys_schema_journal` DISABLE KEYS */;
INSERT INTO `sys_schema_journal` VALUES ('sys_schema_journal','2024-01-10 16:04:33.881','1000-01-01 00:00:00.000',0,'','','','','','','1000-01-01 00:00:00.000','1000-01-01 00:00:00.000','1000-01-01 00:00:00.000'),('sys_schema_version','2024-01-10 16:04:33.881','1000-01-01 00:00:00.000',0,'','','','','','','1000-01-01 00:00:00.000','1000-01-01 00:00:00.000','1000-01-01 00:00:00.000');
/*!40000 ALTER TABLE `sys_schema_journal` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_schema_version`
--

DROP TABLE IF EXISTS `sys_schema_version`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_schema_version` (
    `revision` bigint NOT NULL COMMENT 'version + build',
    `create_dt` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT 'created datetime',
    `modify_dt` datetime(3) NOT NULL DEFAULT '1000-01-01 00:00:00.000' ON UPDATE CURRENT_TIMESTAMP(3) COMMENT 'modified datetime',
    `commit_id` bigint NOT NULL COMMENT 'commit id',
    `apply_dt` datetime(3) NOT NULL DEFAULT '1000-01-01 00:00:00.000' COMMENT 'applied datetime',
    `comments` varchar(500) NOT NULL DEFAULT '' COMMENT 'sql path',
    `upto_sql` text NOT NULL COMMENT 'upgrade script',
    `undo_sql` text NOT NULL COMMENT 'downgrade script',
    PRIMARY KEY (`revision`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='101/Table Structure';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_schema_version`
--

LOCK TABLES `sys_schema_version` WRITE;
/*!40000 ALTER TABLE `sys_schema_version` DISABLE KEYS */;
INSERT INTO `sys_schema_version` VALUES (2019051201,'2024-01-10 16:04:33.872','2024-01-10 16:04:33.911',-1704873873688,'2024-01-10 16:04:33.892','master/00-init/2019-0512_01-version-journal.sql','-- Unify timezone (GMT+8), Unify charset (utf8mb4)\n-- CREATE DATABASE `wings` /*!40100 DEFAULT CHARACTER SET utf8mb4 */;\n\nCREATE TABLE `sys_schema_version` (\n    `revision`  BIGINT(20)   NOT NULL COMMENT \'version + build\',\n    `create_dt` DATETIME(3)  NOT NULL DEFAULT NOW(3) COMMENT \'created datetime\',\n    `modify_dt` DATETIME(3)  NOT NULL DEFAULT \'1000-01-01 00:00:00.000\' ON UPDATE NOW(3) COMMENT \'modified datetime\',\n    `commit_id` BIGINT(20)   NOT NULL COMMENT \'commit id\',\n    `apply_dt`  DATETIME(3)  NOT NULL DEFAULT \'1000-01-01 00:00:00.000\' COMMENT \'applied datetime\',\n    `comments`  VARCHAR(500) NOT NULL DEFAULT \'\' COMMENT \'sql path\',\n    `upto_sql`  TEXT         NOT NULL COMMENT \'upgrade script\',\n    `undo_sql`  TEXT         NOT NULL COMMENT \'downgrade script\',\n    PRIMARY KEY (`revision`)\n) ENGINE = InnoDB\n  DEFAULT CHARSET = utf8mb4 COMMENT =\'101/Table Structure\';\n\nCREATE TABLE `sys_schema_journal` (\n    `table_name` VARCHAR(100) NOT NULL COMMENT \'plain table name\',\n    `create_dt`  DATETIME(3)  NOT NULL DEFAULT NOW(3) COMMENT \'created datetime\',\n    `modify_dt`  DATETIME(3)  NOT NULL DEFAULT \'1000-01-01 00:00:00.000\' ON UPDATE NOW(3) COMMENT \'modified datetime\',\n    `commit_id`  BIGINT(20)   NOT NULL COMMENT \'commit id\',\n    `ddl_instbl` TEXT         NOT NULL COMMENT \'trace DDL of insert\',\n    `ddl_instrg` TEXT         NOT NULL COMMENT \'trigger DDL of insert\',\n    `ddl_updtbl` TEXT         NOT NULL COMMENT \'trace DDL of update\',\n    `ddl_updtrg` TEXT         NOT NULL COMMENT \'trigger DDL of update\',\n    `ddl_deltbl` TEXT         NOT NULL COMMENT \'trace DDL of delete\',\n    `ddl_deltrg` TEXT         NOT NULL COMMENT \'trigger DDL of delete\',\n    `log_insert` DATETIME(3)  NOT NULL DEFAULT \'1000-01-01 00:00:00.000\' COMMENT \'applied datetime of insert\',\n    `log_update` DATETIME(3)  NOT NULL DEFAULT \'1000-01-01 00:00:00.000\' COMMENT \'applied datetime of update\',\n    `log_delete` DATETIME(3)  NOT NULL DEFAULT \'1000-01-01 00:00:00.000\' COMMENT \'applied datetime of delete\',\n    PRIMARY KEY (`table_name`) USING BTREE\n) ENGINE = InnoDB\n  DEFAULT CHARSET = utf8mb4 COMMENT =\'102/Table Trigger\';\n\n-- sys_schema_version@plain\nINSERT IGNORE INTO `sys_schema_version` (`revision`, `commit_id`, `upto_sql`, `undo_sql`, `apply_dt`)\nVALUES (2019051201, 0, \'\', \'\', NOW(3));\n\n-- sys_schema_journal@plain\nREPLACE INTO `sys_schema_journal` (`table_name`, `commit_id`, `ddl_instbl`, `ddl_instrg`, `ddl_updtbl`, `ddl_updtrg`, `ddl_deltbl`, `ddl_deltrg`)\nVALUES (\'sys_schema_journal\', 0, \'\', \'\', \'\', \'\', \'\', \'\'),\n       (\'sys_schema_version\', 0, \'\', \'\', \'\', \'\', \'\', \'\');\n','DROP TABLE IF EXISTS `sys_schema_version`; -- 101/Table Structure;\nDROP TABLE IF EXISTS `sys_schema_journal`; -- 102/Table Trigger;\n'),(2019052001,'2024-01-10 16:04:33.938','2024-01-10 16:04:34.070',-1704873873688,'2024-01-10 16:04:34.070','','CREATE TABLE `sys_light_sequence` (\n    `seq_name` VARCHAR(100) NOT NULL COMMENT \'sequence name\',\n    `block_id` INT(11)      NOT NULL DEFAULT 0 COMMENT \'block\',\n    `next_val` BIGINT(20)   NOT NULL DEFAULT \'1\' COMMENT \'next value\',\n    `step_val` INT(11)      NOT NULL DEFAULT \'100\' COMMENT \'step of increment\',\n    `comments` VARCHAR(200) NOT NULL COMMENT \'comments\',\n    PRIMARY KEY (`seq_name`, `block_id`)\n) ENGINE = InnoDB\n  DEFAULT CHARSET = utf8mb4 COMMENT =\'103/Sequence Generation\';\n\nCREATE TABLE `sys_commit_journal` (\n    `id`         BIGINT(20)   NOT NULL COMMENT \'primary key\',\n    `create_dt`  DATETIME(3)  NOT NULL DEFAULT NOW(3) COMMENT \'created datetime\',\n    `event_name` VARCHAR(200) NOT NULL COMMENT \'event name\',\n    `target_key` VARCHAR(200) NOT NULL DEFAULT \'\' COMMENT \'target data\',\n    `login_info` TEXT COMMENT \'login info: agent, terminal\',\n    `other_info` TEXT COMMENT \'other info: biz index data\',\n    PRIMARY KEY (`id`)\n) ENGINE = InnoDB\n  DEFAULT CHARSET = utf8mb4 COMMENT =\'104/Data Changeset\';\n\n-- sys_light_sequence@plain\nINSERT IGNORE INTO `sys_light_sequence` (`seq_name`, `block_id`, `next_val`, `step_val`, `comments`)\nVALUES (\'singleton_lightid_blockid\', 0, 10000000, 100, \'default block_id\'),\n       (\'sys_commit_journal\', 0, 1, 100, \'sys_commit_journal\');\n\n-- sys_commit_journal@plain\nREPLACE INTO `sys_commit_journal` (`id`, `event_name`)\nVALUES (0, \'system_manual_init\');\n','-- ask@danger\nDROP TABLE IF EXISTS `sys_light_sequence`; -- 103/Sequence Generation;\nDROP TABLE IF EXISTS `sys_commit_journal`; -- 104/Data Changeset;\n'),(2020102601,'2024-01-10 16:04:48.869','2024-01-10 16:04:49.111',-1704873888717,'2024-01-10 16:04:49.111','','CREATE TABLE `win_task_define` (\n    `id`          BIGINT(20)   NOT NULL COMMENT \'primary key/task_id\',\n    `create_dt`   DATETIME(3)  NOT NULL DEFAULT NOW(3) COMMENT \'created datetime(sys)\',\n    `modify_dt`   DATETIME(3)  NOT NULL DEFAULT \'1000-01-01\' ON UPDATE NOW(3) COMMENT \'modified datetime(sys)\',\n    `delete_dt`   DATETIME(3)  NOT NULL DEFAULT \'1000-01-01\' COMMENT \'logic deleted datetime\',\n    `commit_id`   BIGINT(20)   NOT NULL COMMENT \'commit id\',\n    `propkey`     VARCHAR(200) NOT NULL DEFAULT \'\' COMMENT \'conf file key, auto-generated\',\n    `enabled`     TINYINT(1)   NOT NULL DEFAULT \'1\' COMMENT \'whether to register and execute\',\n    `autorun`     TINYINT(1)   NOT NULL DEFAULT \'1\' COMMENT \'whether to auto register and start\',\n    `version`     INT(11)      NOT NULL DEFAULT \'0\' COMMENT \'version number, higher overrides lower one\',\n    `tasker_bean` VARCHAR(300) NOT NULL DEFAULT \'\' COMMENT \'beans annotated by TinyTasker, formatted as Class#method\',\n    `tasker_para` TEXT         NULL     DEFAULT NULL COMMENT \'parameters of the task, object array in json format\',\n    `tasker_name` VARCHAR(200) NOT NULL DEFAULT \'\' COMMENT \'task name, used for notice and log, shortClassName#method\',\n    `tasker_fast` TINYINT(1)   NOT NULL DEFAULT \'1\' COMMENT \'whether light task, fast execution, completed in seconds\',\n    `tasker_apps` VARCHAR(500) NOT NULL DEFAULT \'\' COMMENT \'belong to applications, comma-separated, default spring.application.name\',\n    `tasker_runs` VARCHAR(100) NOT NULL DEFAULT \'\' COMMENT \'RunMode(product|test|develop|local), comma-separated case-insensitive\',\n    `notice_bean` VARCHAR(200) NOT NULL DEFAULT \'\' COMMENT \'notice bean, SmallNotice type, fullpath of Class\',\n    `notice_when` VARCHAR(100) NOT NULL DEFAULT \'fail\' COMMENT \'timing of notice, exec|fail|done|feed, comma-separated case-insensitive\',\n    `notice_conf` VARCHAR(200) NOT NULL DEFAULT \'\' COMMENT \'conf name of notice bean\',\n    `timing_zone` VARCHAR(100) NOT NULL DEFAULT \'\' COMMENT \'timezone of scheduling , default system timezone\',\n    `timing_type` VARCHAR(100) NOT NULL DEFAULT \'cron\' COMMENT \'scheduling expression type\',\n    `timing_cron` VARCHAR(100) NOT NULL DEFAULT \'\' COMMENT \'scheduling expression content\',\n    `timing_idle` INT(11)      NOT NULL DEFAULT \'0\' COMMENT \'fixed idle interval (seconds)\',\n    `timing_rate` INT(11)      NOT NULL DEFAULT \'0\' COMMENT \'fixed frequency interval (seconds)\',\n    `timing_miss` INT(11)      NOT NULL DEFAULT \'0\' COMMENT \'within how many seconds of a misfire\',\n    `timing_beat` INT(11)      NOT NULL DEFAULT \'0\' COMMENT \'interval seconds of heartbeat\',\n    `during_from` VARCHAR(20)  NOT NULL DEFAULT \'\' COMMENT \'schedule start datetime at timingZone, yyyy-MM-dd HH:mm:ss\',\n    `during_stop` VARCHAR(20)  NOT NULL DEFAULT \'\' COMMENT \'schedule stop datetime at timingZone, yyyy-MM-dd HH:mm:ss\',\n    `during_exec` INT(11)      NOT NULL DEFAULT \'0\' COMMENT \'stop schedule after how many total executions\',\n    `during_fail` INT(11)      NOT NULL DEFAULT \'0\' COMMENT \'stop schedule after how many consecutive failures\',\n    `during_done` INT(11)      NOT NULL DEFAULT \'0\' COMMENT \'stop schedule after how many successful executions\',\n    `during_boot` INT(11)      NOT NULL DEFAULT \'0\' COMMENT \'recount each time the app is started, and stop schedule after how many successful executions\',\n    `result_keep` INT(11)      NOT NULL DEFAULT \'60\' COMMENT \'how many days to save the execution results\',\n    `last_exec`   DATETIME(3)  NOT NULL DEFAULT \'1000-01-01\' COMMENT \'previous exec (sys)\',\n    `last_fail`   DATETIME(3)  NOT NULL DEFAULT \'1000-01-01\' COMMENT \'previous fail (sys)\',\n    `last_done`   DATETIME(3)  NOT NULL DEFAULT \'1000-01-01\' COMMENT \'previous success (sys)\',\n    `next_exec`   DATETIME(3)  NOT NULL DEFAULT \'1000-01-01\' COMMENT \'next exec (sys), default stop\',\n    `next_lock`   INT(11)      NOT NULL DEFAULT \'0\' COMMENT \'optimistic lock of exec\',\n    `dur_fail`    INT(11)      NOT NULL DEFAULT \'0\' COMMENT \'total count of consecutive fail\',\n    `sum_exec`    INT(11)      NOT NULL DEFAULT \'0\' COMMENT \'total count of exec\',\n    `sum_fail`    INT(11)      NOT NULL DEFAULT \'0\' COMMENT \'total count of fail\',\n    `sum_done`    INT(11)      NOT NULL DEFAULT \'0\' COMMENT \'total count of done\',\n    PRIMARY KEY (`id`),\n    UNIQUE INDEX uq_tasker_bean (`tasker_bean`)\n) ENGINE = InnoDB\n  DEFAULT CHARSET = utf8mb4 COMMENT =\'120/Task Define\';\n\nCREATE TABLE `win_task_result` (\n    `id`        BIGINT(20)   NOT NULL COMMENT \'primary key\',\n    `task_id`   BIGINT(20)   NOT NULL DEFAULT \'0\' COMMENT \'task id, win_task_define.id\',\n    `task_app`  VARCHAR(300) NOT NULL DEFAULT \'\' COMMENT \'belong to applications, comma-separated\',\n    `task_pid`  INT(11)      NOT NULL DEFAULT \'0\' COMMENT \'belong to jvm pid\',\n    `task_msg`  TEXT         NULL     DEFAULT NULL COMMENT \'Normal or abnormal messages\',\n    `time_exec` DATETIME(3)  NOT NULL DEFAULT \'1000-01-01\' COMMENT \'datetime of exec (sys)\',\n    `time_fail` DATETIME(3)  NOT NULL DEFAULT \'1000-01-01\' COMMENT \'datetime of fail (sys)\',\n    `time_done` DATETIME(3)  NOT NULL DEFAULT \'1000-01-01\' COMMENT \'datetime of done (sys)\',\n    `time_cost` INT(11)      NOT NULL DEFAULT \'0\' COMMENT \'mills cost of task\',\n    PRIMARY KEY (`id`),\n    INDEX ix_task_id (`task_id`)\n) ENGINE = InnoDB\n  DEFAULT CHARSET = utf8mb4 COMMENT =\'122/Task Result\';\n','DROP TABLE IF EXISTS `win_task_define`; -- 120/Task Define;\nDROP TABLE IF EXISTS `win_task_result`; -- 122/Task Result;\n\n'),(2020102701,'2024-01-10 16:04:34.082','2024-01-10 16:04:34.417',-1704873873688,'2024-01-10 16:04:34.417','','CREATE TABLE `win_mail_sender` (\n    `id`         BIGINT(20)    NOT NULL COMMENT \'primary key/mail_id\',\n    `create_dt`  DATETIME(3)   NOT NULL DEFAULT NOW(3) COMMENT \'created datetime(sys)\',\n    `modify_dt`  DATETIME(3)   NOT NULL DEFAULT \'1000-01-01\' ON UPDATE NOW(3) COMMENT \'modified datetime(sys)\',\n    `delete_dt`  DATETIME(3)   NOT NULL DEFAULT \'1000-01-01\' COMMENT \'logic deleted datetime\',\n    `commit_id`  BIGINT(20)    NOT NULL COMMENT \'commit id\',\n    `mail_apps`  VARCHAR(500)  NOT NULL DEFAULT \'\' COMMENT \'belong to applications, comma-separated, default spring.application.name\',\n    `mail_runs`  VARCHAR(100)  NOT NULL DEFAULT \'\' COMMENT \'RunMode(product|test|develop|local), comma-separated case-insensitive, default all\',\n    `mail_conf`  VARCHAR(100)  NOT NULL DEFAULT \'\' COMMENT \'config name, default\',\n    `mail_from`  VARCHAR(200)  NOT NULL DEFAULT \'\' COMMENT \'mail from (sender)\',\n    `mail_to`    VARCHAR(500)  NOT NULL DEFAULT \'\' COMMENT \'mail to, comma-separated\',\n    `mail_cc`    VARCHAR(500)  NOT NULL DEFAULT \'\' COMMENT \'mail cc, comma-separated\',\n    `mail_bcc`   VARCHAR(500)  NOT NULL DEFAULT \'\' COMMENT \'mail bcc, comma-separated\',\n    `mail_reply` VARCHAR(200)  NOT NULL DEFAULT \'\' COMMENT \'mail reply\',\n    `mail_subj`  VARCHAR(400)  NOT NULL DEFAULT \'\' COMMENT \'mail subject\',\n    `mail_text`  TEXT          NULL     DEFAULT NULL COMMENT \'mail content\',\n    `mail_html`  TINYINT(1)    NOT NULL DEFAULT \'1\' COMMENT \'whether HTML email\',\n    `mail_file`  VARCHAR(9000) NOT NULL DEFAULT \'\' COMMENT \'attachment name and path map, json format\',\n    `mail_mark`  VARCHAR(200)  NOT NULL DEFAULT \'\' COMMENT \'space-separated business key\',\n    `mail_date`  DATETIME(3)   NOT NULL DEFAULT \'1000-01-01\' COMMENT \'scheduled mail send (sys)\',\n    `last_send`  DATETIME(3)   NOT NULL DEFAULT \'1000-01-01\' COMMENT \'previous send (sys)\',\n    `last_fail`  TEXT          NULL     DEFAULT NULL COMMENT \'previous fail info\',\n    `last_done`  DATETIME(3)   NOT NULL DEFAULT \'1000-01-01\' COMMENT \'previous success (sys)\',\n    `last_cost`  INT(11)       NOT NULL DEFAULT \'0\' COMMENT \'mills of previous send cost\',\n    `next_send`  DATETIME(3)   NOT NULL DEFAULT \'1000-01-01\' COMMENT \'next send datetime (sys)\',\n    `next_lock`  INT(11)       NOT NULL DEFAULT \'0\' COMMENT \'optimistic lock of sending\',\n    `sum_send`   INT(11)       NOT NULL DEFAULT \'0\' COMMENT \'total count of send\',\n    `sum_fail`   INT(11)       NOT NULL DEFAULT \'0\' COMMENT \'total count of fail\',\n    `sum_done`   INT(11)       NOT NULL DEFAULT \'0\' COMMENT \'total count of success\',\n    `max_fail`   INT(11)       NOT NULL DEFAULT \'0\' COMMENT \'max count of fail, 0 means use the config\',\n    `max_done`   INT(11)       NOT NULL DEFAULT \'0\' COMMENT \'max count of success, 0 means use the config\',\n    `ref_type`   INT(11)       NOT NULL DEFAULT \'0\' COMMENT \'ref type to mark key1, key2 use\',\n    `ref_key1`   BIGINT(20)    NOT NULL DEFAULT \'0\' COMMENT \'ref key1, generally the primary key\',\n    `ref_key2`   VARCHAR(500)  NOT NULL DEFAULT \'\' COMMENT \'ref key2, generally the composite data\',\n    PRIMARY KEY (`id`),\n    INDEX ix_next_send (`next_send`),\n    INDEX ix_sum_done (`sum_done`),\n    FULLTEXT ft_mail_mark (`mail_mark`),\n    INDEX ix_ref_type (`ref_type`),\n    INDEX ix_ref_key1 (`ref_key1`),\n    INDEX ix_ref_key2 (`ref_key2`)\n) ENGINE = InnoDB\n  DEFAULT CHARSET = utf8mb4 COMMENT =\'124/Mail Sending\';\n','DROP TABLE IF EXISTS `win_mail_sender`; -- 124/Mail Sending;\n\n');
/*!40000 ALTER TABLE `sys_schema_version` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `win_mail_sender`
--

DROP TABLE IF EXISTS `win_mail_sender`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `win_mail_sender` (
    `id` bigint NOT NULL COMMENT 'primary key/mail_id',
    `create_dt` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT 'created datetime(sys)',
    `modify_dt` datetime(3) NOT NULL DEFAULT '1000-01-01 00:00:00.000' ON UPDATE CURRENT_TIMESTAMP(3) COMMENT 'modified datetime(sys)',
    `delete_dt` datetime(3) NOT NULL DEFAULT '1000-01-01 00:00:00.000' COMMENT 'logic deleted datetime',
    `commit_id` bigint NOT NULL COMMENT 'commit id',
    `mail_apps` varchar(500) NOT NULL DEFAULT '' COMMENT 'belong to applications, comma-separated, default spring.application.name',
    `mail_runs` varchar(100) NOT NULL DEFAULT '' COMMENT 'RunMode(product|test|develop|local), comma-separated case-insensitive, default all',
    `mail_conf` varchar(100) NOT NULL DEFAULT '' COMMENT 'config name, default',
    `mail_from` varchar(200) NOT NULL DEFAULT '' COMMENT 'mail from (sender)',
    `mail_to` varchar(500) NOT NULL DEFAULT '' COMMENT 'mail to, comma-separated',
    `mail_cc` varchar(500) NOT NULL DEFAULT '' COMMENT 'mail cc, comma-separated',
    `mail_bcc` varchar(500) NOT NULL DEFAULT '' COMMENT 'mail bcc, comma-separated',
    `mail_reply` varchar(200) NOT NULL DEFAULT '' COMMENT 'mail reply',
    `mail_subj` varchar(400) NOT NULL DEFAULT '' COMMENT 'mail subject',
    `mail_text` text COMMENT 'mail content',
    `mail_html` tinyint(1) NOT NULL DEFAULT '1' COMMENT 'whether HTML email',
    `mail_file` varchar(9000) NOT NULL DEFAULT '' COMMENT 'attachment name and path map, json format',
    `mail_mark` varchar(200) NOT NULL DEFAULT '' COMMENT 'space-separated business key',
    `mail_date` datetime(3) NOT NULL DEFAULT '1000-01-01 00:00:00.000' COMMENT 'scheduled mail send (sys)',
    `last_send` datetime(3) NOT NULL DEFAULT '1000-01-01 00:00:00.000' COMMENT 'previous send (sys)',
    `last_fail` text COMMENT 'previous fail info',
    `last_done` datetime(3) NOT NULL DEFAULT '1000-01-01 00:00:00.000' COMMENT 'previous success (sys)',
    `last_cost` int NOT NULL DEFAULT '0' COMMENT 'mills of previous send cost',
    `next_send` datetime(3) NOT NULL DEFAULT '1000-01-01 00:00:00.000' COMMENT 'next send datetime (sys)',
    `next_lock` int NOT NULL DEFAULT '0' COMMENT 'optimistic lock of sending',
    `sum_send` int NOT NULL DEFAULT '0' COMMENT 'total count of send',
    `sum_fail` int NOT NULL DEFAULT '0' COMMENT 'total count of fail',
    `sum_done` int NOT NULL DEFAULT '0' COMMENT 'total count of success',
    `max_fail` int NOT NULL DEFAULT '0' COMMENT 'max count of fail, 0 means use the config',
    `max_done` int NOT NULL DEFAULT '0' COMMENT 'max count of success, 0 means use the config',
    `ref_type` int NOT NULL DEFAULT '0' COMMENT 'ref type to mark key1, key2 use',
    `ref_key1` bigint NOT NULL DEFAULT '0' COMMENT 'ref key1, generally the primary key',
    `ref_key2` varchar(500) NOT NULL DEFAULT '' COMMENT 'ref key2, generally the composite data',
    PRIMARY KEY (`id`),
    KEY `ix_next_send` (`next_send`),
    KEY `ix_sum_done` (`sum_done`),
    KEY `ix_ref_type` (`ref_type`),
    KEY `ix_ref_key1` (`ref_key1`),
    KEY `ix_ref_key2` (`ref_key2`),
    FULLTEXT KEY `ft_mail_mark` (`mail_mark`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='124/Mail Sending';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `win_mail_sender`
--

LOCK TABLES `win_mail_sender` WRITE;
/*!40000 ALTER TABLE `win_mail_sender` DISABLE KEYS */;
/*!40000 ALTER TABLE `win_mail_sender` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `win_task_define`
--

DROP TABLE IF EXISTS `win_task_define`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `win_task_define` (
    `id` bigint NOT NULL COMMENT 'primary key/task_id',
    `create_dt` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT 'created datetime(sys)',
    `modify_dt` datetime(3) NOT NULL DEFAULT '1000-01-01 00:00:00.000' ON UPDATE CURRENT_TIMESTAMP(3) COMMENT 'modified datetime(sys)',
    `delete_dt` datetime(3) NOT NULL DEFAULT '1000-01-01 00:00:00.000' COMMENT 'logic deleted datetime',
    `commit_id` bigint NOT NULL COMMENT 'commit id',
    `propkey` varchar(200) NOT NULL DEFAULT '' COMMENT 'conf file key, auto-generated',
    `enabled` tinyint(1) NOT NULL DEFAULT '1' COMMENT 'whether to register and execute',
    `autorun` tinyint(1) NOT NULL DEFAULT '1' COMMENT 'whether to auto register and start',
    `version` int NOT NULL DEFAULT '0' COMMENT 'version number, higher overrides lower one',
    `tasker_bean` varchar(300) NOT NULL DEFAULT '' COMMENT 'beans annotated by TinyTasker, formatted as Class#method',
    `tasker_para` text COMMENT 'parameters of the task, object array in json format',
    `tasker_name` varchar(200) NOT NULL DEFAULT '' COMMENT 'task name, used for notice and log, shortClassName#method',
    `tasker_fast` tinyint(1) NOT NULL DEFAULT '1' COMMENT 'whether light task, fast execution, completed in seconds',
    `tasker_apps` varchar(500) NOT NULL DEFAULT '' COMMENT 'belong to applications, comma-separated, default spring.application.name',
    `tasker_runs` varchar(100) NOT NULL DEFAULT '' COMMENT 'RunMode(product|test|develop|local), comma-separated case-insensitive',
    `notice_bean` varchar(200) NOT NULL DEFAULT '' COMMENT 'notice bean, SmallNotice type, fullpath of Class',
    `notice_when` varchar(100) NOT NULL DEFAULT 'fail' COMMENT 'timing of notice, exec|fail|done|feed, comma-separated case-insensitive',
    `notice_conf` varchar(200) NOT NULL DEFAULT '' COMMENT 'conf name of notice bean',
    `timing_zone` varchar(100) NOT NULL DEFAULT '' COMMENT 'timezone of scheduling , default system timezone',
    `timing_type` varchar(100) NOT NULL DEFAULT 'cron' COMMENT 'scheduling expression type',
    `timing_cron` varchar(100) NOT NULL DEFAULT '' COMMENT 'scheduling expression content',
    `timing_idle` int NOT NULL DEFAULT '0' COMMENT 'fixed idle interval (seconds)',
    `timing_rate` int NOT NULL DEFAULT '0' COMMENT 'fixed frequency interval (seconds)',
    `timing_miss` int NOT NULL DEFAULT '0' COMMENT 'within how many seconds of a misfire',
    `timing_beat` int NOT NULL DEFAULT '0' COMMENT 'interval seconds of heartbeat',
    `during_from` varchar(20) NOT NULL DEFAULT '' COMMENT 'schedule start datetime at timingZone, yyyy-MM-dd HH:mm:ss',
    `during_stop` varchar(20) NOT NULL DEFAULT '' COMMENT 'schedule stop datetime at timingZone, yyyy-MM-dd HH:mm:ss',
    `during_exec` int NOT NULL DEFAULT '0' COMMENT 'stop schedule after how many total executions',
    `during_fail` int NOT NULL DEFAULT '0' COMMENT 'stop schedule after how many consecutive failures',
    `during_done` int NOT NULL DEFAULT '0' COMMENT 'stop schedule after how many successful executions',
    `during_boot` int NOT NULL DEFAULT '0' COMMENT 'recount each time the app is started, and stop schedule after how many successful executions',
    `result_keep` int NOT NULL DEFAULT '60' COMMENT 'how many days to save the execution results',
    `last_exec` datetime(3) NOT NULL DEFAULT '1000-01-01 00:00:00.000' COMMENT 'previous exec (sys)',
    `last_fail` datetime(3) NOT NULL DEFAULT '1000-01-01 00:00:00.000' COMMENT 'previous fail (sys)',
    `last_done` datetime(3) NOT NULL DEFAULT '1000-01-01 00:00:00.000' COMMENT 'previous success (sys)',
    `next_exec` datetime(3) NOT NULL DEFAULT '1000-01-01 00:00:00.000' COMMENT 'next exec (sys), default stop',
    `next_lock` int NOT NULL DEFAULT '0' COMMENT 'optimistic lock of exec',
    `dur_fail` int NOT NULL DEFAULT '0' COMMENT 'total count of consecutive fail',
    `sum_exec` int NOT NULL DEFAULT '0' COMMENT 'total count of exec',
    `sum_fail` int NOT NULL DEFAULT '0' COMMENT 'total count of fail',
    `sum_done` int NOT NULL DEFAULT '0' COMMENT 'total count of done',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uq_tasker_bean` (`tasker_bean`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='120/Task Define';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `win_task_define`
--

LOCK TABLES `win_task_define` WRITE;
/*!40000 ALTER TABLE `win_task_define` DISABLE KEYS */;
INSERT INTO `win_task_define` VALUES (1000,'2024-01-10 16:04:49.662','2024-01-10 16:42:04.640','1000-01-01 00:00:00.000',102,'pro.fessional.wings.tiny.app.service.TestServiceAuto#strStr',1,1,0,'pro.fessional.wings.tiny.app.service.TestServiceAuto#strStr',NULL,'TestServiceAuto#strStr',1,'tiny-task-test','local','pro.fessional.wings.slardar.notice.DingTalkNotice','fail','','','cron','0 * * * * *',0,0,0,0,'','',0,0,0,0,60,'2024-01-10 16:05:00.001','1000-01-01 00:00:00.000','2024-01-10 16:05:01.517','2024-01-10 16:43:00.000',1,0,1,0,1),(1001,'2024-01-10 16:04:49.776','2024-01-10 16:42:21.590','1000-01-01 00:00:00.000',107,'pro.fessional.wings.tiny.app.service.TestServiceAuto#strVoid',1,1,0,'pro.fessional.wings.tiny.app.service.TestServiceAuto#strVoid',NULL,'TestServiceAuto#strVoid',1,'tiny-task-test','local','pro.fessional.wings.slardar.notice.DingTalkNotice','fail','','','cron','',0,30,0,0,'','',0,0,0,0,60,'2024-01-10 16:42:19.930','1000-01-01 00:00:00.000','2024-01-10 16:42:21.477','2024-01-10 16:42:49.930',2,0,2,0,2),(1002,'2024-01-10 16:04:49.819','2024-01-10 16:42:04.661','1000-01-01 00:00:00.000',103,'voidStrAuto',1,1,0,'pro.fessional.wings.tiny.app.service.TestServiceAuto#voidStr',NULL,'TestServiceAuto#voidStr',1,'tiny-task-test','local','pro.fessional.wings.slardar.notice.DingTalkNotice','fail','','','cron','',60,0,0,0,'','',0,0,0,0,60,'2024-01-10 16:04:49.981','1000-01-01 00:00:00.000','2024-01-10 16:04:51.493','2024-01-10 16:42:51.493',1,0,1,0,1),(1003,'2024-01-10 16:04:49.851','1000-01-01 00:00:00.000','1000-01-01 00:00:00.000',4,'voidVoidAuto',0,1,0,'pro.fessional.wings.tiny.app.service.TestServiceAuto#voidVoid',NULL,'TestServiceAuto#voidVoid',1,'tiny-task-test','local','pro.fessional.wings.slardar.notice.DingTalkNotice','fail','','','cron','',0,30,0,0,'','',0,0,0,0,60,'1000-01-01 00:00:00.000','1000-01-01 00:00:00.000','1000-01-01 00:00:00.000','1000-01-01 00:00:00.000',0,0,0,0,0),(1004,'2024-01-10 16:04:49.993','2024-01-10 16:42:04.725','1000-01-01 00:00:00.000',105,'TinyTaskCleanResult',1,1,0,'pro.fessional.wings.tiny.task.service.impl.TinyTaskBeatServiceImpl#cleanResult',NULL,'TinyTaskBeatServiceImpl#cleanResult',1,'tiny-task-test','local','pro.fessional.wings.slardar.notice.DingTalkNotice','fail','','','cron','0 1 2 * * *',0,0,0,0,'','',0,0,0,0,60,'1000-01-01 00:00:00.000','1000-01-01 00:00:00.000','1000-01-01 00:00:00.000','2024-01-11 02:01:00.000',0,0,0,0,0),(1005,'2024-01-10 16:04:50.051','2024-01-10 16:42:04.702','1000-01-01 00:00:00.000',104,'TinyTaskCheckHealth',1,1,0,'pro.fessional.wings.tiny.task.service.impl.TinyTaskBeatServiceImpl#checkHealth',NULL,'TinyTaskBeatServiceImpl#checkHealth',1,'tiny-task-test','local','pro.fessional.wings.slardar.notice.DingTalkNotice','fail,feed','','','cron','',300,0,0,0,'','',0,0,0,0,60,'2024-01-10 16:04:50.103','1000-01-01 00:00:00.000','2024-01-10 16:04:50.126','2024-01-10 16:44:50.126',1,0,1,0,1),(1006,'2024-01-10 16:04:51.378','1000-01-01 00:00:00.000','1000-01-01 00:00:00.000',14,'pro.fessional.wings.tiny.app.service.TestServiceManual#strStr',0,0,1,'pro.fessional.wings.tiny.app.service.TestServiceManual#strStr','\"trydofor test string\"','TestServiceManual#strStr',1,'tiny-task-test','local','pro.fessional.wings.slardar.notice.DingTalkNotice','fail','','','cron','0 1 2 3 4 5',0,0,0,0,'','',0,0,0,0,60,'1000-01-01 00:00:00.000','1000-01-01 00:00:00.000','1000-01-01 00:00:00.000','1000-01-01 00:00:00.000',0,0,0,0,0);
/*!40000 ALTER TABLE `win_task_define` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `win_task_result`
--

DROP TABLE IF EXISTS `win_task_result`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `win_task_result` (
    `id` bigint NOT NULL COMMENT 'primary key',
    `task_id` bigint NOT NULL DEFAULT '0' COMMENT 'task id, win_task_define.id',
    `task_app` varchar(300) NOT NULL DEFAULT '' COMMENT 'belong to applications, comma-separated',
    `task_pid` int NOT NULL DEFAULT '0' COMMENT 'belong to jvm pid',
    `task_msg` text COMMENT 'Normal or abnormal messages',
    `time_exec` datetime(3) NOT NULL DEFAULT '1000-01-01 00:00:00.000' COMMENT 'datetime of exec (sys)',
    `time_fail` datetime(3) NOT NULL DEFAULT '1000-01-01 00:00:00.000' COMMENT 'datetime of fail (sys)',
    `time_done` datetime(3) NOT NULL DEFAULT '1000-01-01 00:00:00.000' COMMENT 'datetime of done (sys)',
    `time_cost` int NOT NULL DEFAULT '0' COMMENT 'mills cost of task',
    PRIMARY KEY (`id`),
    KEY `ix_task_id` (`task_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='122/Task Result';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `win_task_result`
--

LOCK TABLES `win_task_result` WRITE;
/*!40000 ALTER TABLE `win_task_result` DISABLE KEYS */;
INSERT INTO `win_task_result` VALUES (1000,1005,'tiny-task-test',48247,NULL,'2024-01-10 16:04:50.103','1000-01-01 00:00:00.000','2024-01-10 16:04:50.126',23),(1001,1001,'tiny-task-test',48247,NULL,'2024-01-10 16:04:49.925','1000-01-01 00:00:00.000','2024-01-10 16:04:51.464',1539),(1002,1002,'tiny-task-test',48247,NULL,'2024-01-10 16:04:49.981','1000-01-01 00:00:00.000','2024-01-10 16:04:51.493',1512),(1003,1000,'tiny-task-test',48247,NULL,'2024-01-10 16:05:00.001','1000-01-01 00:00:00.000','2024-01-10 16:05:01.517',1516),(1100,1001,'tiny-task-test',52564,NULL,'2024-01-10 16:42:19.930','1000-01-01 00:00:00.000','2024-01-10 16:42:21.477',1547);
/*!40000 ALTER TABLE `win_task_result` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Current Database: `wings_shard_1`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `wings_shard_1` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `wings_shard_1`;

--
-- Table structure for table `sys_commit_journal`
--

DROP TABLE IF EXISTS `sys_commit_journal`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_commit_journal` (
    `id` bigint NOT NULL COMMENT 'primary key',
    `create_dt` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT 'created datetime',
    `event_name` varchar(200) NOT NULL COMMENT 'event name',
    `target_key` varchar(200) NOT NULL DEFAULT '' COMMENT 'target data',
    `login_info` text COMMENT 'login info: agent, terminal',
    `other_info` text COMMENT 'other info: biz index data',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='104/Data Changeset';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_commit_journal`
--

LOCK TABLES `sys_commit_journal` WRITE;
/*!40000 ALTER TABLE `sys_commit_journal` DISABLE KEYS */;
INSERT INTO `sys_commit_journal` VALUES (0,'2024-01-10 16:38:34.557','system_manual_init','',NULL,NULL);
/*!40000 ALTER TABLE `sys_commit_journal` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_light_sequence`
--

DROP TABLE IF EXISTS `sys_light_sequence`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_light_sequence` (
    `seq_name` varchar(100) NOT NULL COMMENT 'sequence name',
    `block_id` int NOT NULL DEFAULT '0' COMMENT 'block',
    `next_val` bigint NOT NULL DEFAULT '1' COMMENT 'next value',
    `step_val` int NOT NULL DEFAULT '100' COMMENT 'step of increment',
    `comments` varchar(200) NOT NULL COMMENT 'comments',
    PRIMARY KEY (`seq_name`,`block_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='103/Sequence Generation';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_light_sequence`
--

LOCK TABLES `sys_light_sequence` WRITE;
/*!40000 ALTER TABLE `sys_light_sequence` DISABLE KEYS */;
INSERT INTO `sys_light_sequence` VALUES ('singleton_lightid_blockid',0,10000000,100,'default block_id'),('sys_commit_journal',0,1,100,'sys_commit_journal'),('tst_normal_table',0,1000,1,'for test step 1');
/*!40000 ALTER TABLE `sys_light_sequence` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_schema_journal`
--

DROP TABLE IF EXISTS `sys_schema_journal`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_schema_journal` (
    `table_name` varchar(100) NOT NULL COMMENT 'plain table name',
    `create_dt` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT 'created datetime',
    `modify_dt` datetime(3) NOT NULL DEFAULT '1000-01-01 00:00:00.000' ON UPDATE CURRENT_TIMESTAMP(3) COMMENT 'modified datetime',
    `commit_id` bigint NOT NULL COMMENT 'commit id',
    `ddl_instbl` text NOT NULL COMMENT 'trace DDL of insert',
    `ddl_instrg` text NOT NULL COMMENT 'trigger DDL of insert',
    `ddl_updtbl` text NOT NULL COMMENT 'trace DDL of update',
    `ddl_updtrg` text NOT NULL COMMENT 'trigger DDL of update',
    `ddl_deltbl` text NOT NULL COMMENT 'trace DDL of delete',
    `ddl_deltrg` text NOT NULL COMMENT 'trigger DDL of delete',
    `log_insert` datetime(3) NOT NULL DEFAULT '1000-01-01 00:00:00.000' COMMENT 'applied datetime of insert',
    `log_update` datetime(3) NOT NULL DEFAULT '1000-01-01 00:00:00.000' COMMENT 'applied datetime of update',
    `log_delete` datetime(3) NOT NULL DEFAULT '1000-01-01 00:00:00.000' COMMENT 'applied datetime of delete',
    PRIMARY KEY (`table_name`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='102/Table Trigger';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_schema_journal`
--

LOCK TABLES `sys_schema_journal` WRITE;
/*!40000 ALTER TABLE `sys_schema_journal` DISABLE KEYS */;
INSERT INTO `sys_schema_journal` VALUES ('sys_schema_journal','2024-01-10 16:38:34.359','1000-01-01 00:00:00.000',0,'','','','','','','1000-01-01 00:00:00.000','1000-01-01 00:00:00.000','1000-01-01 00:00:00.000'),('sys_schema_version','2024-01-10 16:38:34.359','1000-01-01 00:00:00.000',0,'','','','','','','1000-01-01 00:00:00.000','1000-01-01 00:00:00.000','1000-01-01 00:00:00.000');
/*!40000 ALTER TABLE `sys_schema_journal` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_schema_version`
--

DROP TABLE IF EXISTS `sys_schema_version`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_schema_version` (
    `revision` bigint NOT NULL COMMENT 'version + build',
    `create_dt` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT 'created datetime',
    `modify_dt` datetime(3) NOT NULL DEFAULT '1000-01-01 00:00:00.000' ON UPDATE CURRENT_TIMESTAMP(3) COMMENT 'modified datetime',
    `commit_id` bigint NOT NULL COMMENT 'commit id',
    `apply_dt` datetime(3) NOT NULL DEFAULT '1000-01-01 00:00:00.000' COMMENT 'applied datetime',
    `comments` varchar(500) NOT NULL DEFAULT '' COMMENT 'sql path',
    `upto_sql` text NOT NULL COMMENT 'upgrade script',
    `undo_sql` text NOT NULL COMMENT 'downgrade script',
    PRIMARY KEY (`revision`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='101/Table Structure';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_schema_version`
--

LOCK TABLES `sys_schema_version` WRITE;
/*!40000 ALTER TABLE `sys_schema_version` DISABLE KEYS */;
INSERT INTO `sys_schema_version` VALUES (2019051201,'2024-01-10 16:38:34.356','2024-01-10 16:38:34.369',0,'2024-01-10 16:38:34.365','master/00-init/2019-0512_01-version-journal.sql','-- Unify timezone (GMT+8), Unify charset (utf8mb4)\n-- CREATE DATABASE `wings` /*!40100 DEFAULT CHARACTER SET utf8mb4 */;\n\nCREATE TABLE `sys_schema_version` (\n    `revision`  BIGINT(20)   NOT NULL COMMENT \'version + build\',\n    `create_dt` DATETIME(3)  NOT NULL DEFAULT NOW(3) COMMENT \'created datetime\',\n    `modify_dt` DATETIME(3)  NOT NULL DEFAULT \'1000-01-01 00:00:00.000\' ON UPDATE NOW(3) COMMENT \'modified datetime\',\n    `commit_id` BIGINT(20)   NOT NULL COMMENT \'commit id\',\n    `apply_dt`  DATETIME(3)  NOT NULL DEFAULT \'1000-01-01 00:00:00.000\' COMMENT \'applied datetime\',\n    `comments`  VARCHAR(500) NOT NULL DEFAULT \'\' COMMENT \'sql path\',\n    `upto_sql`  TEXT         NOT NULL COMMENT \'upgrade script\',\n    `undo_sql`  TEXT         NOT NULL COMMENT \'downgrade script\',\n    PRIMARY KEY (`revision`)\n) ENGINE = InnoDB\n  DEFAULT CHARSET = utf8mb4 COMMENT =\'101/Table Structure\';\n\nCREATE TABLE `sys_schema_journal` (\n    `table_name` VARCHAR(100) NOT NULL COMMENT \'plain table name\',\n    `create_dt`  DATETIME(3)  NOT NULL DEFAULT NOW(3) COMMENT \'created datetime\',\n    `modify_dt`  DATETIME(3)  NOT NULL DEFAULT \'1000-01-01 00:00:00.000\' ON UPDATE NOW(3) COMMENT \'modified datetime\',\n    `commit_id`  BIGINT(20)   NOT NULL COMMENT \'commit id\',\n    `ddl_instbl` TEXT         NOT NULL COMMENT \'trace DDL of insert\',\n    `ddl_instrg` TEXT         NOT NULL COMMENT \'trigger DDL of insert\',\n    `ddl_updtbl` TEXT         NOT NULL COMMENT \'trace DDL of update\',\n    `ddl_updtrg` TEXT         NOT NULL COMMENT \'trigger DDL of update\',\n    `ddl_deltbl` TEXT         NOT NULL COMMENT \'trace DDL of delete\',\n    `ddl_deltrg` TEXT         NOT NULL COMMENT \'trigger DDL of delete\',\n    `log_insert` DATETIME(3)  NOT NULL DEFAULT \'1000-01-01 00:00:00.000\' COMMENT \'applied datetime of insert\',\n    `log_update` DATETIME(3)  NOT NULL DEFAULT \'1000-01-01 00:00:00.000\' COMMENT \'applied datetime of update\',\n    `log_delete` DATETIME(3)  NOT NULL DEFAULT \'1000-01-01 00:00:00.000\' COMMENT \'applied datetime of delete\',\n    PRIMARY KEY (`table_name`) USING BTREE\n) ENGINE = InnoDB\n  DEFAULT CHARSET = utf8mb4 COMMENT =\'102/Table Trigger\';\n\n-- sys_schema_version@plain\nINSERT IGNORE INTO `sys_schema_version` (`revision`, `commit_id`, `upto_sql`, `undo_sql`, `apply_dt`)\nVALUES (2019051201, 0, \'\', \'\', NOW(3));\n\n-- sys_schema_journal@plain\nREPLACE INTO `sys_schema_journal` (`table_name`, `commit_id`, `ddl_instbl`, `ddl_instrg`, `ddl_updtbl`, `ddl_updtrg`, `ddl_deltbl`, `ddl_deltrg`)\nVALUES (\'sys_schema_journal\', 0, \'\', \'\', \'\', \'\', \'\', \'\'),\n       (\'sys_schema_version\', 0, \'\', \'\', \'\', \'\', \'\', \'\');\n','DROP TABLE IF EXISTS `sys_schema_version`; -- 101/Table Structure;\nDROP TABLE IF EXISTS `sys_schema_journal`; -- 102/Table Trigger;\n'),(2019052001,'2024-01-10 16:38:34.441','2024-01-10 16:38:34.561',0,'2024-01-10 16:38:34.561','master/01-light/2019-0520_01-light-commit.sql','CREATE TABLE `sys_light_sequence` (\n    `seq_name` VARCHAR(100) NOT NULL COMMENT \'sequence name\',\n    `block_id` INT(11)      NOT NULL DEFAULT 0 COMMENT \'block\',\n    `next_val` BIGINT(20)   NOT NULL DEFAULT \'1\' COMMENT \'next value\',\n    `step_val` INT(11)      NOT NULL DEFAULT \'100\' COMMENT \'step of increment\',\n    `comments` VARCHAR(200) NOT NULL COMMENT \'comments\',\n    PRIMARY KEY (`seq_name`, `block_id`)\n) ENGINE = InnoDB\n  DEFAULT CHARSET = utf8mb4 COMMENT =\'103/Sequence Generation\';\n\nCREATE TABLE `sys_commit_journal` (\n    `id`         BIGINT(20)   NOT NULL COMMENT \'primary key\',\n    `create_dt`  DATETIME(3)  NOT NULL DEFAULT NOW(3) COMMENT \'created datetime\',\n    `event_name` VARCHAR(200) NOT NULL COMMENT \'event name\',\n    `target_key` VARCHAR(200) NOT NULL DEFAULT \'\' COMMENT \'target data\',\n    `login_info` TEXT COMMENT \'login info: agent, terminal\',\n    `other_info` TEXT COMMENT \'other info: biz index data\',\n    PRIMARY KEY (`id`)\n) ENGINE = InnoDB\n  DEFAULT CHARSET = utf8mb4 COMMENT =\'104/Data Changeset\';\n\n-- sys_light_sequence@plain\nINSERT IGNORE INTO `sys_light_sequence` (`seq_name`, `block_id`, `next_val`, `step_val`, `comments`)\nVALUES (\'singleton_lightid_blockid\', 0, 10000000, 100, \'default block_id\'),\n       (\'sys_commit_journal\', 0, 1, 100, \'sys_commit_journal\');\n\n-- sys_commit_journal@plain\nREPLACE INTO `sys_commit_journal` (`id`, `event_name`)\nVALUES (0, \'system_manual_init\');\n','-- ask@danger\nDROP TABLE IF EXISTS `sys_light_sequence`; -- 103/Sequence Generation;\nDROP TABLE IF EXISTS `sys_commit_journal`; -- 104/Data Changeset;\n'),(2019060101,'2024-01-10 16:38:34.458','2024-01-10 16:38:34.641',0,'2024-01-10 16:38:34.641','master/2019-0601_01-test.sql','-- apply@tst_.* error@stop\nCREATE TABLE `tst_sharding` (\n    `id`         BIGINT(20)  NOT NULL COMMENT \'primary key\',\n    `create_dt`  DATETIME(3) NOT NULL DEFAULT NOW(3) COMMENT \'created datetime\',\n    `modify_dt`  DATETIME(3) NOT NULL DEFAULT \'1000-01-01\' ON UPDATE NOW(3) COMMENT \'modified datetime\',\n    `delete_dt`  DATETIME(3) NOT NULL DEFAULT \'1000-01-01\' COMMENT \'logic deleted datetime\',\n    `commit_id`  BIGINT(20)  NOT NULL COMMENT \'commit id\',\n    `login_info` TEXT COMMENT \'login info: agent, terminal\',\n    `other_info` TEXT COMMENT \'other info: biz index data\',\n    `language`   INT(11)     NOT NULL DEFAULT 1020111 COMMENT \'StandardLanguage\',\n    PRIMARY KEY (`id`)\n) ENGINE = InnoDB\n  DEFAULT CHARSET = utf8mb4 COMMENT =\'201/Sharding Test\';\n\nCREATE TABLE `tst_sharding_postfix` (\n    `id`         BIGINT(20)  NOT NULL COMMENT \'primary key\',\n    `create_dt`  DATETIME(3) NOT NULL DEFAULT NOW(3) COMMENT \'created datetime\',\n    `modify_dt`  DATETIME(3) NOT NULL DEFAULT \'1000-01-01\' ON UPDATE NOW(3) COMMENT \'modified datetime\',\n    `delete_dt`  DATETIME(3) NOT NULL DEFAULT \'1000-01-01\' COMMENT \'logic deleted datetime\',\n    `commit_id`  BIGINT(20)  NOT NULL COMMENT \'commit id\',\n    `login_info` TEXT COMMENT \'login info: agent, terminal\',\n    `other_info` TEXT COMMENT \'other info: biz index data\',\n    `language`   INT(11)     NOT NULL DEFAULT 1020111 COMMENT \'StandardLanguage\',\n    PRIMARY KEY (`id`)\n) ENGINE = InnoDB\n  DEFAULT CHARSET = utf8mb4 COMMENT =\'201/Sharding Test\';\n\nCREATE TABLE `tst_normal_table` (\n    `id`            BIGINT(20)     NOT NULL COMMENT \'primary key\',\n    `create_dt`     DATETIME(3)    NOT NULL DEFAULT NOW(3) COMMENT \'created datetime\',\n    `modify_dt`     DATETIME(3)    NOT NULL DEFAULT \'1000-01-01\' ON UPDATE NOW(3) COMMENT \'modified datetime\',\n    `delete_dt`     DATETIME(3)    NOT NULL DEFAULT \'1000-01-01\' COMMENT \'logic deleted datetime\',\n    `commit_id`     BIGINT(20)     NOT NULL COMMENT \'commit id\',\n    `value_varchar` VARCHAR(256)   NOT NULL DEFAULT \'0\' COMMENT \'String\',\n    `value_decimal` DECIMAL(10, 2) NOT NULL DEFAULT \'0\' COMMENT \'BigDecimal\',\n    `value_boolean` TINYINT(1)     NOT NULL DEFAULT \'0\' COMMENT \'Boolean\',\n    `value_int`     INT(11)        NOT NULL DEFAULT \'0\' COMMENT \'Integer\',\n    `value_long`    BIGINT(20)     NOT NULL DEFAULT \'0\' COMMENT \'Long\',\n    `value_date`    DATE           NOT NULL DEFAULT \'1000-01-01\' COMMENT \'LocalDate\',\n    `value_time`    TIME           NOT NULL DEFAULT \'00:00:00\' COMMENT \'LocalTime\',\n    `value_lang`    INT(11)        NOT NULL DEFAULT \'1020111\' COMMENT \'StandardLanguage\',\n    PRIMARY KEY (`id`)\n) ENGINE = InnoDB\n  DEFAULT CHARSET = utf8mb4 COMMENT =\'202/Normal Test\';\n\nREPLACE INTO `sys_light_sequence` (`seq_name`, `block_id`, `next_val`, `step_val`, `comments`)\nVALUES (\'tst_normal_table\', 0, 1000, 1, \'for test step 1\');\n','DROP TABLE IF EXISTS `tst_sharding`; -- 201/sharding;\nDROP TABLE IF EXISTS `tst_sharding_postfix`; -- 201/sharding;\nDROP TABLE IF EXISTS `tst_normal_table`; -- 202/normal;\n'),(2019060102,'2024-01-10 16:38:34.472','1000-01-01 00:00:00.000',0,'1000-01-01 00:00:00.000','master/2019-0601_02-test.sql','-- @plain error@skip\nREPLACE INTO `tst_sharding`(`id`, `commit_id`, `login_info`, `other_info`)\nVALUES (100, -1, \'LOGIN_INFO-00\', \'OTHER_INFO-00\'),\n       (101, -1, \'LOGIN_INFO-01\', \'OTHER_INFO-01\'),\n       (102, -1, \'LOGIN_INFO-02\', \'OTHER_INFO-02\'),\n       (103, -1, \'LOGIN_INFO-03\', \'OTHER_INFO-03\'),\n       (104, -1, \'LOGIN_INFO-04\', \'OTHER_INFO-04\'),\n       (105, -1, \'LOGIN_INFO-05\', \'OTHER_INFO-05\'),\n       (106, -1, \'LOGIN_INFO-06\', \'OTHER_INFO-06\'),\n       (107, -1, \'LOGIN_INFO-07\', \'OTHER_INFO-07\'),\n       (108, -1, \'LOGIN_INFO-08\', \'OTHER_INFO-08\'),\n       (109, -1, \'LOGIN_INFO-09\', \'OTHER_INFO-09\'),\n       (110, -1, \'LOGIN_INFO-10\', \'OTHER_INFO-10\'),\n       (111, -1, \'LOGIN_INFO-11\', \'OTHER_INFO-11\'),\n       (112, -1, \'LOGIN_INFO-12\', \'OTHER_INFO-12\'),\n       (113, -1, \'LOGIN_INFO-13\', \'OTHER_INFO-13\'),\n       (114, -1, \'LOGIN_INFO-14\', \'OTHER_INFO-14\'),\n       (115, -1, \'LOGIN_INFO-15\', \'OTHER_INFO-15\'),\n       (116, -1, \'LOGIN_INFO-16\', \'OTHER_INFO-16\'),\n       (117, -1, \'LOGIN_INFO-17\', \'OTHER_INFO-17\'),\n       (118, -1, \'LOGIN_INFO-18\', \'OTHER_INFO-18\'),\n       (119, -1, \'LOGIN_INFO-19\', \'OTHER_INFO-19\');\n','-- @plain\nTRUNCATE `tst_sharding`;\n');
/*!40000 ALTER TABLE `sys_schema_version` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tst_normal_table`
--

DROP TABLE IF EXISTS `tst_normal_table`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tst_normal_table` (
    `id` bigint NOT NULL COMMENT 'primary key',
    `create_dt` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT 'created datetime',
    `modify_dt` datetime(3) NOT NULL DEFAULT '1000-01-01 00:00:00.000' ON UPDATE CURRENT_TIMESTAMP(3) COMMENT 'modified datetime',
    `delete_dt` datetime(3) NOT NULL DEFAULT '1000-01-01 00:00:00.000' COMMENT 'logic deleted datetime',
    `commit_id` bigint NOT NULL COMMENT 'commit id',
    `value_varchar` varchar(256) NOT NULL DEFAULT '0' COMMENT 'String',
    `value_decimal` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT 'BigDecimal',
    `value_boolean` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Boolean',
    `value_int` int NOT NULL DEFAULT '0' COMMENT 'Integer',
    `value_long` bigint NOT NULL DEFAULT '0' COMMENT 'Long',
    `value_date` date NOT NULL DEFAULT '1000-01-01' COMMENT 'LocalDate',
    `value_time` time NOT NULL DEFAULT '00:00:00' COMMENT 'LocalTime',
    `value_lang` int NOT NULL DEFAULT '1020111' COMMENT 'StandardLanguage',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='202/Normal Test';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tst_normal_table`
--

LOCK TABLES `tst_normal_table` WRITE;
/*!40000 ALTER TABLE `tst_normal_table` DISABLE KEYS */;
/*!40000 ALTER TABLE `tst_normal_table` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tst_sharding`
--

DROP TABLE IF EXISTS `tst_sharding`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tst_sharding` (
    `id` bigint NOT NULL COMMENT 'primary key',
    `create_dt` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT 'created datetime',
    `modify_dt` datetime(3) NOT NULL DEFAULT '1000-01-01 00:00:00.000' ON UPDATE CURRENT_TIMESTAMP(3) COMMENT 'modified datetime',
    `delete_dt` datetime(3) NOT NULL DEFAULT '1000-01-01 00:00:00.000' COMMENT 'logic deleted datetime',
    `commit_id` bigint NOT NULL COMMENT 'commit id',
    `login_info` text COMMENT 'login info: agent, terminal',
    `other_info` text COMMENT 'other info: biz index data',
    `language` int NOT NULL DEFAULT '1020111' COMMENT 'StandardLanguage',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='201/Sharding Test';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tst_sharding`
--

LOCK TABLES `tst_sharding` WRITE;
/*!40000 ALTER TABLE `tst_sharding` DISABLE KEYS */;
/*!40000 ALTER TABLE `tst_sharding` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tst_sharding_0`
--

DROP TABLE IF EXISTS `tst_sharding_0`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tst_sharding_0` (
    `id` bigint NOT NULL COMMENT 'primary key',
    `create_dt` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT 'created datetime',
    `modify_dt` datetime(3) NOT NULL DEFAULT '1000-01-01 00:00:00.000' ON UPDATE CURRENT_TIMESTAMP(3) COMMENT 'modified datetime',
    `delete_dt` datetime(3) NOT NULL DEFAULT '1000-01-01 00:00:00.000' COMMENT 'logic deleted datetime',
    `commit_id` bigint NOT NULL COMMENT 'commit id',
    `login_info` text COMMENT 'login info: agent, terminal',
    `other_info` text COMMENT 'other info: biz index data',
    `language` int NOT NULL DEFAULT '1020111' COMMENT 'StandardLanguage',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='201/Sharding Test';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tst_sharding_0`
--

LOCK TABLES `tst_sharding_0` WRITE;
/*!40000 ALTER TABLE `tst_sharding_0` DISABLE KEYS */;
/*!40000 ALTER TABLE `tst_sharding_0` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tst_sharding_1`
--

DROP TABLE IF EXISTS `tst_sharding_1`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tst_sharding_1` (
    `id` bigint NOT NULL COMMENT 'primary key',
    `create_dt` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT 'created datetime',
    `modify_dt` datetime(3) NOT NULL DEFAULT '1000-01-01 00:00:00.000' ON UPDATE CURRENT_TIMESTAMP(3) COMMENT 'modified datetime',
    `delete_dt` datetime(3) NOT NULL DEFAULT '1000-01-01 00:00:00.000' COMMENT 'logic deleted datetime',
    `commit_id` bigint NOT NULL COMMENT 'commit id',
    `login_info` text COMMENT 'login info: agent, terminal',
    `other_info` text COMMENT 'other info: biz index data',
    `language` int NOT NULL DEFAULT '1020111' COMMENT 'StandardLanguage',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='201/Sharding Test';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tst_sharding_1`
--

LOCK TABLES `tst_sharding_1` WRITE;
/*!40000 ALTER TABLE `tst_sharding_1` DISABLE KEYS */;
/*!40000 ALTER TABLE `tst_sharding_1` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tst_sharding_2`
--

DROP TABLE IF EXISTS `tst_sharding_2`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tst_sharding_2` (
    `id` bigint NOT NULL COMMENT 'primary key',
    `create_dt` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT 'created datetime',
    `modify_dt` datetime(3) NOT NULL DEFAULT '1000-01-01 00:00:00.000' ON UPDATE CURRENT_TIMESTAMP(3) COMMENT 'modified datetime',
    `delete_dt` datetime(3) NOT NULL DEFAULT '1000-01-01 00:00:00.000' COMMENT 'logic deleted datetime',
    `commit_id` bigint NOT NULL COMMENT 'commit id',
    `login_info` text COMMENT 'login info: agent, terminal',
    `other_info` text COMMENT 'other info: biz index data',
    `language` int NOT NULL DEFAULT '1020111' COMMENT 'StandardLanguage',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='201/Sharding Test';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tst_sharding_2`
--

LOCK TABLES `tst_sharding_2` WRITE;
/*!40000 ALTER TABLE `tst_sharding_2` DISABLE KEYS */;
/*!40000 ALTER TABLE `tst_sharding_2` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tst_sharding_3`
--

DROP TABLE IF EXISTS `tst_sharding_3`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tst_sharding_3` (
    `id` bigint NOT NULL COMMENT 'primary key',
    `create_dt` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT 'created datetime',
    `modify_dt` datetime(3) NOT NULL DEFAULT '1000-01-01 00:00:00.000' ON UPDATE CURRENT_TIMESTAMP(3) COMMENT 'modified datetime',
    `delete_dt` datetime(3) NOT NULL DEFAULT '1000-01-01 00:00:00.000' COMMENT 'logic deleted datetime',
    `commit_id` bigint NOT NULL COMMENT 'commit id',
    `login_info` text COMMENT 'login info: agent, terminal',
    `other_info` text COMMENT 'other info: biz index data',
    `language` int NOT NULL DEFAULT '1020111' COMMENT 'StandardLanguage',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='201/Sharding Test';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tst_sharding_3`
--

LOCK TABLES `tst_sharding_3` WRITE;
/*!40000 ALTER TABLE `tst_sharding_3` DISABLE KEYS */;
/*!40000 ALTER TABLE `tst_sharding_3` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tst_sharding_4`
--

DROP TABLE IF EXISTS `tst_sharding_4`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tst_sharding_4` (
    `id` bigint NOT NULL COMMENT 'primary key',
    `create_dt` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT 'created datetime',
    `modify_dt` datetime(3) NOT NULL DEFAULT '1000-01-01 00:00:00.000' ON UPDATE CURRENT_TIMESTAMP(3) COMMENT 'modified datetime',
    `delete_dt` datetime(3) NOT NULL DEFAULT '1000-01-01 00:00:00.000' COMMENT 'logic deleted datetime',
    `commit_id` bigint NOT NULL COMMENT 'commit id',
    `login_info` text COMMENT 'login info: agent, terminal',
    `other_info` text COMMENT 'other info: biz index data',
    `language` int NOT NULL DEFAULT '1020111' COMMENT 'StandardLanguage',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='201/Sharding Test';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tst_sharding_4`
--

LOCK TABLES `tst_sharding_4` WRITE;
/*!40000 ALTER TABLE `tst_sharding_4` DISABLE KEYS */;
/*!40000 ALTER TABLE `tst_sharding_4` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tst_sharding_postfix`
--

DROP TABLE IF EXISTS `tst_sharding_postfix`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tst_sharding_postfix` (
    `id` bigint NOT NULL COMMENT 'primary key',
    `create_dt` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT 'created datetime',
    `modify_dt` datetime(3) NOT NULL DEFAULT '1000-01-01 00:00:00.000' ON UPDATE CURRENT_TIMESTAMP(3) COMMENT 'modified datetime',
    `delete_dt` datetime(3) NOT NULL DEFAULT '1000-01-01 00:00:00.000' COMMENT 'logic deleted datetime',
    `commit_id` bigint NOT NULL COMMENT 'commit id',
    `login_info` text COMMENT 'login info: agent, terminal',
    `other_info` text COMMENT 'other info: biz index data',
    `language` int NOT NULL DEFAULT '1020111' COMMENT 'StandardLanguage',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='201/Sharding Test';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tst_sharding_postfix`
--

LOCK TABLES `tst_sharding_postfix` WRITE;
/*!40000 ALTER TABLE `tst_sharding_postfix` DISABLE KEYS */;
/*!40000 ALTER TABLE `tst_sharding_postfix` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Current Database: `wings_faceless`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `wings_faceless` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `wings_faceless`;

--
-- Table structure for table `sys_commit_journal`
--

DROP TABLE IF EXISTS `sys_commit_journal`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_commit_journal` (
    `id` bigint NOT NULL COMMENT 'primary key',
    `create_dt` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT 'created datetime',
    `event_name` varchar(200) NOT NULL COMMENT 'event name',
    `target_key` varchar(200) NOT NULL DEFAULT '' COMMENT 'target data',
    `login_info` text COMMENT 'login info: agent, terminal',
    `other_info` text COMMENT 'other info: biz index data',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='104/Data Changeset';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_commit_journal`
--

LOCK TABLES `sys_commit_journal` WRITE;
/*!40000 ALTER TABLE `sys_commit_journal` DISABLE KEYS */;
INSERT INTO `sys_commit_journal` VALUES (0,'2024-01-10 16:38:17.223','system_manual_init','',NULL,NULL);
/*!40000 ALTER TABLE `sys_commit_journal` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_light_sequence`
--

DROP TABLE IF EXISTS `sys_light_sequence`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_light_sequence` (
    `seq_name` varchar(100) NOT NULL COMMENT 'sequence name',
    `block_id` int NOT NULL DEFAULT '0' COMMENT 'block',
    `next_val` bigint NOT NULL DEFAULT '1' COMMENT 'next value',
    `step_val` int NOT NULL DEFAULT '100' COMMENT 'step of increment',
    `comments` varchar(200) NOT NULL COMMENT 'comments',
    PRIMARY KEY (`seq_name`,`block_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='103/Sequence Generation';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_light_sequence`
--

LOCK TABLES `sys_light_sequence` WRITE;
/*!40000 ALTER TABLE `sys_light_sequence` DISABLE KEYS */;
INSERT INTO `sys_light_sequence` VALUES ('singleton_lightid_blockid',0,10000000,100,'default block_id'),('sys_commit_journal',0,1,100,'sys_commit_journal'),('tst_normal_table',0,1000,1,'for test step 1');
/*!40000 ALTER TABLE `sys_light_sequence` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_schema_journal`
--

DROP TABLE IF EXISTS `sys_schema_journal`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_schema_journal` (
    `table_name` varchar(100) NOT NULL COMMENT 'plain table name',
    `create_dt` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT 'created datetime',
    `modify_dt` datetime(3) NOT NULL DEFAULT '1000-01-01 00:00:00.000' ON UPDATE CURRENT_TIMESTAMP(3) COMMENT 'modified datetime',
    `commit_id` bigint NOT NULL COMMENT 'commit id',
    `ddl_instbl` text NOT NULL COMMENT 'trace DDL of insert',
    `ddl_instrg` text NOT NULL COMMENT 'trigger DDL of insert',
    `ddl_updtbl` text NOT NULL COMMENT 'trace DDL of update',
    `ddl_updtrg` text NOT NULL COMMENT 'trigger DDL of update',
    `ddl_deltbl` text NOT NULL COMMENT 'trace DDL of delete',
    `ddl_deltrg` text NOT NULL COMMENT 'trigger DDL of delete',
    `log_insert` datetime(3) NOT NULL DEFAULT '1000-01-01 00:00:00.000' COMMENT 'applied datetime of insert',
    `log_update` datetime(3) NOT NULL DEFAULT '1000-01-01 00:00:00.000' COMMENT 'applied datetime of update',
    `log_delete` datetime(3) NOT NULL DEFAULT '1000-01-01 00:00:00.000' COMMENT 'applied datetime of delete',
    PRIMARY KEY (`table_name`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='102/Table Trigger';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_schema_journal`
--

LOCK TABLES `sys_schema_journal` WRITE;
/*!40000 ALTER TABLE `sys_schema_journal` DISABLE KEYS */;
INSERT INTO `sys_schema_journal` VALUES ('sys_schema_journal','2024-01-10 16:38:17.101','1000-01-01 00:00:00.000',0,'','','','','','','1000-01-01 00:00:00.000','1000-01-01 00:00:00.000','1000-01-01 00:00:00.000'),('sys_schema_version','2024-01-10 16:38:17.101','1000-01-01 00:00:00.000',0,'','','','','','','1000-01-01 00:00:00.000','1000-01-01 00:00:00.000','1000-01-01 00:00:00.000');
/*!40000 ALTER TABLE `sys_schema_journal` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_schema_version`
--

DROP TABLE IF EXISTS `sys_schema_version`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_schema_version` (
    `revision` bigint NOT NULL COMMENT 'version + build',
    `create_dt` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT 'created datetime',
    `modify_dt` datetime(3) NOT NULL DEFAULT '1000-01-01 00:00:00.000' ON UPDATE CURRENT_TIMESTAMP(3) COMMENT 'modified datetime',
    `commit_id` bigint NOT NULL COMMENT 'commit id',
    `apply_dt` datetime(3) NOT NULL DEFAULT '1000-01-01 00:00:00.000' COMMENT 'applied datetime',
    `comments` varchar(500) NOT NULL DEFAULT '' COMMENT 'sql path',
    `upto_sql` text NOT NULL COMMENT 'upgrade script',
    `undo_sql` text NOT NULL COMMENT 'downgrade script',
    PRIMARY KEY (`revision`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='101/Table Structure';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_schema_version`
--

LOCK TABLES `sys_schema_version` WRITE;
/*!40000 ALTER TABLE `sys_schema_version` DISABLE KEYS */;
INSERT INTO `sys_schema_version` VALUES (2019051201,'2024-01-10 16:38:17.094','2024-01-10 16:38:17.110',0,'2024-01-10 16:38:17.106','master/00-init/2019-0512_01-version-journal.sql','-- Unify timezone (GMT+8), Unify charset (utf8mb4)\n-- CREATE DATABASE `wings` /*!40100 DEFAULT CHARACTER SET utf8mb4 */;\n\nCREATE TABLE `sys_schema_version` (\n    `revision`  BIGINT(20)   NOT NULL COMMENT \'version + build\',\n    `create_dt` DATETIME(3)  NOT NULL DEFAULT NOW(3) COMMENT \'created datetime\',\n    `modify_dt` DATETIME(3)  NOT NULL DEFAULT \'1000-01-01 00:00:00.000\' ON UPDATE NOW(3) COMMENT \'modified datetime\',\n    `commit_id` BIGINT(20)   NOT NULL COMMENT \'commit id\',\n    `apply_dt`  DATETIME(3)  NOT NULL DEFAULT \'1000-01-01 00:00:00.000\' COMMENT \'applied datetime\',\n    `comments`  VARCHAR(500) NOT NULL DEFAULT \'\' COMMENT \'sql path\',\n    `upto_sql`  TEXT         NOT NULL COMMENT \'upgrade script\',\n    `undo_sql`  TEXT         NOT NULL COMMENT \'downgrade script\',\n    PRIMARY KEY (`revision`)\n) ENGINE = InnoDB\n  DEFAULT CHARSET = utf8mb4 COMMENT =\'101/Table Structure\';\n\nCREATE TABLE `sys_schema_journal` (\n    `table_name` VARCHAR(100) NOT NULL COMMENT \'plain table name\',\n    `create_dt`  DATETIME(3)  NOT NULL DEFAULT NOW(3) COMMENT \'created datetime\',\n    `modify_dt`  DATETIME(3)  NOT NULL DEFAULT \'1000-01-01 00:00:00.000\' ON UPDATE NOW(3) COMMENT \'modified datetime\',\n    `commit_id`  BIGINT(20)   NOT NULL COMMENT \'commit id\',\n    `ddl_instbl` TEXT         NOT NULL COMMENT \'trace DDL of insert\',\n    `ddl_instrg` TEXT         NOT NULL COMMENT \'trigger DDL of insert\',\n    `ddl_updtbl` TEXT         NOT NULL COMMENT \'trace DDL of update\',\n    `ddl_updtrg` TEXT         NOT NULL COMMENT \'trigger DDL of update\',\n    `ddl_deltbl` TEXT         NOT NULL COMMENT \'trace DDL of delete\',\n    `ddl_deltrg` TEXT         NOT NULL COMMENT \'trigger DDL of delete\',\n    `log_insert` DATETIME(3)  NOT NULL DEFAULT \'1000-01-01 00:00:00.000\' COMMENT \'applied datetime of insert\',\n    `log_update` DATETIME(3)  NOT NULL DEFAULT \'1000-01-01 00:00:00.000\' COMMENT \'applied datetime of update\',\n    `log_delete` DATETIME(3)  NOT NULL DEFAULT \'1000-01-01 00:00:00.000\' COMMENT \'applied datetime of delete\',\n    PRIMARY KEY (`table_name`) USING BTREE\n) ENGINE = InnoDB\n  DEFAULT CHARSET = utf8mb4 COMMENT =\'102/Table Trigger\';\n\n-- sys_schema_version@plain\nINSERT IGNORE INTO `sys_schema_version` (`revision`, `commit_id`, `upto_sql`, `undo_sql`, `apply_dt`)\nVALUES (2019051201, 0, \'\', \'\', NOW(3));\n\n-- sys_schema_journal@plain\nREPLACE INTO `sys_schema_journal` (`table_name`, `commit_id`, `ddl_instbl`, `ddl_instrg`, `ddl_updtbl`, `ddl_updtrg`, `ddl_deltbl`, `ddl_deltrg`)\nVALUES (\'sys_schema_journal\', 0, \'\', \'\', \'\', \'\', \'\', \'\'),\n       (\'sys_schema_version\', 0, \'\', \'\', \'\', \'\', \'\', \'\');\n','DROP TABLE IF EXISTS `sys_schema_version`; -- 101/Table Structure;\nDROP TABLE IF EXISTS `sys_schema_journal`; -- 102/Table Trigger;\n'),(2019052001,'2024-01-10 16:38:17.117','2024-01-10 16:38:17.229',-1,'2024-01-10 16:38:17.229','master/01-light/2019-0520_01-light-commit.sql','CREATE TABLE `sys_light_sequence` (\n    `seq_name` VARCHAR(100) NOT NULL COMMENT \'sequence name\',\n    `block_id` INT(11)      NOT NULL DEFAULT 0 COMMENT \'block\',\n    `next_val` BIGINT(20)   NOT NULL DEFAULT \'1\' COMMENT \'next value\',\n    `step_val` INT(11)      NOT NULL DEFAULT \'100\' COMMENT \'step of increment\',\n    `comments` VARCHAR(200) NOT NULL COMMENT \'comments\',\n    PRIMARY KEY (`seq_name`, `block_id`)\n) ENGINE = InnoDB\n  DEFAULT CHARSET = utf8mb4 COMMENT =\'103/Sequence Generation\';\n\nCREATE TABLE `sys_commit_journal` (\n    `id`         BIGINT(20)   NOT NULL COMMENT \'primary key\',\n    `create_dt`  DATETIME(3)  NOT NULL DEFAULT NOW(3) COMMENT \'created datetime\',\n    `event_name` VARCHAR(200) NOT NULL COMMENT \'event name\',\n    `target_key` VARCHAR(200) NOT NULL DEFAULT \'\' COMMENT \'target data\',\n    `login_info` TEXT COMMENT \'login info: agent, terminal\',\n    `other_info` TEXT COMMENT \'other info: biz index data\',\n    PRIMARY KEY (`id`)\n) ENGINE = InnoDB\n  DEFAULT CHARSET = utf8mb4 COMMENT =\'104/Data Changeset\';\n\n-- sys_light_sequence@plain\nINSERT IGNORE INTO `sys_light_sequence` (`seq_name`, `block_id`, `next_val`, `step_val`, `comments`)\nVALUES (\'singleton_lightid_blockid\', 0, 10000000, 100, \'default block_id\'),\n       (\'sys_commit_journal\', 0, 1, 100, \'sys_commit_journal\');\n\n-- sys_commit_journal@plain\nREPLACE INTO `sys_commit_journal` (`id`, `event_name`)\nVALUES (0, \'system_manual_init\');\n','-- ask@danger\nDROP TABLE IF EXISTS `sys_light_sequence`; -- 103/Sequence Generation;\nDROP TABLE IF EXISTS `sys_commit_journal`; -- 104/Data Changeset;\n'),(2019060101,'2024-01-10 16:38:17.124','2024-01-10 16:38:17.336',-1,'2024-01-10 16:38:17.336','master/2019-0601_01-test.sql','-- apply@tst_.* error@stop\nCREATE TABLE `tst_sharding` (\n    `id`         BIGINT(20)  NOT NULL COMMENT \'primary key\',\n    `create_dt`  DATETIME(3) NOT NULL DEFAULT NOW(3) COMMENT \'created datetime\',\n    `modify_dt`  DATETIME(3) NOT NULL DEFAULT \'1000-01-01\' ON UPDATE NOW(3) COMMENT \'modified datetime\',\n    `delete_dt`  DATETIME(3) NOT NULL DEFAULT \'1000-01-01\' COMMENT \'logic deleted datetime\',\n    `commit_id`  BIGINT(20)  NOT NULL COMMENT \'commit id\',\n    `login_info` TEXT COMMENT \'login info: agent, terminal\',\n    `other_info` TEXT COMMENT \'other info: biz index data\',\n    `language`   INT(11)     NOT NULL DEFAULT 1020111 COMMENT \'StandardLanguage\',\n    PRIMARY KEY (`id`)\n) ENGINE = InnoDB\n  DEFAULT CHARSET = utf8mb4 COMMENT =\'201/Sharding Test\';\n\nCREATE TABLE `tst_sharding_postfix` (\n    `id`         BIGINT(20)  NOT NULL COMMENT \'primary key\',\n    `create_dt`  DATETIME(3) NOT NULL DEFAULT NOW(3) COMMENT \'created datetime\',\n    `modify_dt`  DATETIME(3) NOT NULL DEFAULT \'1000-01-01\' ON UPDATE NOW(3) COMMENT \'modified datetime\',\n    `delete_dt`  DATETIME(3) NOT NULL DEFAULT \'1000-01-01\' COMMENT \'logic deleted datetime\',\n    `commit_id`  BIGINT(20)  NOT NULL COMMENT \'commit id\',\n    `login_info` TEXT COMMENT \'login info: agent, terminal\',\n    `other_info` TEXT COMMENT \'other info: biz index data\',\n    `language`   INT(11)     NOT NULL DEFAULT 1020111 COMMENT \'StandardLanguage\',\n    PRIMARY KEY (`id`)\n) ENGINE = InnoDB\n  DEFAULT CHARSET = utf8mb4 COMMENT =\'201/Sharding Test\';\n\nCREATE TABLE `tst_normal_table` (\n    `id`            BIGINT(20)     NOT NULL COMMENT \'primary key\',\n    `create_dt`     DATETIME(3)    NOT NULL DEFAULT NOW(3) COMMENT \'created datetime\',\n    `modify_dt`     DATETIME(3)    NOT NULL DEFAULT \'1000-01-01\' ON UPDATE NOW(3) COMMENT \'modified datetime\',\n    `delete_dt`     DATETIME(3)    NOT NULL DEFAULT \'1000-01-01\' COMMENT \'logic deleted datetime\',\n    `commit_id`     BIGINT(20)     NOT NULL COMMENT \'commit id\',\n    `value_varchar` VARCHAR(256)   NOT NULL DEFAULT \'0\' COMMENT \'String\',\n    `value_decimal` DECIMAL(10, 2) NOT NULL DEFAULT \'0\' COMMENT \'BigDecimal\',\n    `value_boolean` TINYINT(1)     NOT NULL DEFAULT \'0\' COMMENT \'Boolean\',\n    `value_int`     INT(11)        NOT NULL DEFAULT \'0\' COMMENT \'Integer\',\n    `value_long`    BIGINT(20)     NOT NULL DEFAULT \'0\' COMMENT \'Long\',\n    `value_date`    DATE           NOT NULL DEFAULT \'1000-01-01\' COMMENT \'LocalDate\',\n    `value_time`    TIME           NOT NULL DEFAULT \'00:00:00\' COMMENT \'LocalTime\',\n    `value_lang`    INT(11)        NOT NULL DEFAULT \'1020111\' COMMENT \'StandardLanguage\',\n    PRIMARY KEY (`id`)\n) ENGINE = InnoDB\n  DEFAULT CHARSET = utf8mb4 COMMENT =\'202/Normal Test\';\n\nREPLACE INTO `sys_light_sequence` (`seq_name`, `block_id`, `next_val`, `step_val`, `comments`)\nVALUES (\'tst_normal_table\', 0, 1000, 1, \'for test step 1\');\n','DROP TABLE IF EXISTS `tst_sharding`; -- 201/sharding;\nDROP TABLE IF EXISTS `tst_sharding_postfix`; -- 201/sharding;\nDROP TABLE IF EXISTS `tst_normal_table`; -- 202/normal;\n'),(2019060102,'2024-01-10 16:38:17.131','2024-01-10 16:38:17.352',-1,'2024-01-10 16:38:17.352','master/2019-0601_02-test.sql','-- @plain error@skip\nREPLACE INTO `tst_sharding`(`id`, `commit_id`, `login_info`, `other_info`)\nVALUES (100, -1, \'LOGIN_INFO-00\', \'OTHER_INFO-00\'),\n       (101, -1, \'LOGIN_INFO-01\', \'OTHER_INFO-01\'),\n       (102, -1, \'LOGIN_INFO-02\', \'OTHER_INFO-02\'),\n       (103, -1, \'LOGIN_INFO-03\', \'OTHER_INFO-03\'),\n       (104, -1, \'LOGIN_INFO-04\', \'OTHER_INFO-04\'),\n       (105, -1, \'LOGIN_INFO-05\', \'OTHER_INFO-05\'),\n       (106, -1, \'LOGIN_INFO-06\', \'OTHER_INFO-06\'),\n       (107, -1, \'LOGIN_INFO-07\', \'OTHER_INFO-07\'),\n       (108, -1, \'LOGIN_INFO-08\', \'OTHER_INFO-08\'),\n       (109, -1, \'LOGIN_INFO-09\', \'OTHER_INFO-09\'),\n       (110, -1, \'LOGIN_INFO-10\', \'OTHER_INFO-10\'),\n       (111, -1, \'LOGIN_INFO-11\', \'OTHER_INFO-11\'),\n       (112, -1, \'LOGIN_INFO-12\', \'OTHER_INFO-12\'),\n       (113, -1, \'LOGIN_INFO-13\', \'OTHER_INFO-13\'),\n       (114, -1, \'LOGIN_INFO-14\', \'OTHER_INFO-14\'),\n       (115, -1, \'LOGIN_INFO-15\', \'OTHER_INFO-15\'),\n       (116, -1, \'LOGIN_INFO-16\', \'OTHER_INFO-16\'),\n       (117, -1, \'LOGIN_INFO-17\', \'OTHER_INFO-17\'),\n       (118, -1, \'LOGIN_INFO-18\', \'OTHER_INFO-18\'),\n       (119, -1, \'LOGIN_INFO-19\', \'OTHER_INFO-19\');\n','-- @plain\nTRUNCATE `tst_sharding`;\n');
/*!40000 ALTER TABLE `sys_schema_version` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tst_normal_table`
--

DROP TABLE IF EXISTS `tst_normal_table`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tst_normal_table` (
    `id` bigint NOT NULL COMMENT 'primary key',
    `create_dt` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT 'created datetime',
    `modify_dt` datetime(3) NOT NULL DEFAULT '1000-01-01 00:00:00.000' ON UPDATE CURRENT_TIMESTAMP(3) COMMENT 'modified datetime',
    `delete_dt` datetime(3) NOT NULL DEFAULT '1000-01-01 00:00:00.000' COMMENT 'logic deleted datetime',
    `commit_id` bigint NOT NULL COMMENT 'commit id',
    `value_varchar` varchar(256) NOT NULL DEFAULT '0' COMMENT 'String',
    `value_decimal` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT 'BigDecimal',
    `value_boolean` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Boolean',
    `value_int` int NOT NULL DEFAULT '0' COMMENT 'Integer',
    `value_long` bigint NOT NULL DEFAULT '0' COMMENT 'Long',
    `value_date` date NOT NULL DEFAULT '1000-01-01' COMMENT 'LocalDate',
    `value_time` time NOT NULL DEFAULT '00:00:00' COMMENT 'LocalTime',
    `value_lang` int NOT NULL DEFAULT '1020111' COMMENT 'StandardLanguage',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='202/Normal Test';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tst_normal_table`
--

LOCK TABLES `tst_normal_table` WRITE;
/*!40000 ALTER TABLE `tst_normal_table` DISABLE KEYS */;
/*!40000 ALTER TABLE `tst_normal_table` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tst_sharding`
--

DROP TABLE IF EXISTS `tst_sharding`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tst_sharding` (
    `id` bigint NOT NULL COMMENT 'primary key',
    `create_dt` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT 'created datetime',
    `modify_dt` datetime(3) NOT NULL DEFAULT '1000-01-01 00:00:00.000' ON UPDATE CURRENT_TIMESTAMP(3) COMMENT 'modified datetime',
    `delete_dt` datetime(3) NOT NULL DEFAULT '1000-01-01 00:00:00.000' COMMENT 'logic deleted datetime',
    `commit_id` bigint NOT NULL COMMENT 'commit id',
    `login_info` text COMMENT 'login info: agent, terminal',
    `other_info` text COMMENT 'other info: biz index data',
    `language` int NOT NULL DEFAULT '1020111' COMMENT 'StandardLanguage',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='201/Sharding Test';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tst_sharding`
--

LOCK TABLES `tst_sharding` WRITE;
/*!40000 ALTER TABLE `tst_sharding` DISABLE KEYS */;
INSERT INTO `tst_sharding` VALUES (100,'2024-01-10 16:38:17.347','1000-01-01 00:00:00.000','1000-01-01 00:00:00.000',-1,'LOGIN_INFO-00','OTHER_INFO-00',1020111),(101,'2024-01-10 16:38:17.347','1000-01-01 00:00:00.000','1000-01-01 00:00:00.000',-1,'LOGIN_INFO-01','OTHER_INFO-01',1020111),(102,'2024-01-10 16:38:17.347','1000-01-01 00:00:00.000','1000-01-01 00:00:00.000',-1,'LOGIN_INFO-02','OTHER_INFO-02',1020111),(103,'2024-01-10 16:38:17.347','1000-01-01 00:00:00.000','1000-01-01 00:00:00.000',-1,'LOGIN_INFO-03','OTHER_INFO-03',1020111),(104,'2024-01-10 16:38:17.347','1000-01-01 00:00:00.000','1000-01-01 00:00:00.000',-1,'LOGIN_INFO-04','OTHER_INFO-04',1020111),(105,'2024-01-10 16:38:17.347','1000-01-01 00:00:00.000','1000-01-01 00:00:00.000',-1,'LOGIN_INFO-05','OTHER_INFO-05',1020111),(106,'2024-01-10 16:38:17.347','1000-01-01 00:00:00.000','1000-01-01 00:00:00.000',-1,'LOGIN_INFO-06','OTHER_INFO-06',1020111),(107,'2024-01-10 16:38:17.347','1000-01-01 00:00:00.000','1000-01-01 00:00:00.000',-1,'LOGIN_INFO-07','OTHER_INFO-07',1020111),(108,'2024-01-10 16:38:17.347','1000-01-01 00:00:00.000','1000-01-01 00:00:00.000',-1,'LOGIN_INFO-08','OTHER_INFO-08',1020111),(109,'2024-01-10 16:38:17.347','1000-01-01 00:00:00.000','1000-01-01 00:00:00.000',-1,'LOGIN_INFO-09','OTHER_INFO-09',1020111),(110,'2024-01-10 16:38:17.347','1000-01-01 00:00:00.000','1000-01-01 00:00:00.000',-1,'LOGIN_INFO-10','OTHER_INFO-10',1020111),(111,'2024-01-10 16:38:17.347','1000-01-01 00:00:00.000','1000-01-01 00:00:00.000',-1,'LOGIN_INFO-11','OTHER_INFO-11',1020111),(112,'2024-01-10 16:38:17.347','1000-01-01 00:00:00.000','1000-01-01 00:00:00.000',-1,'LOGIN_INFO-12','OTHER_INFO-12',1020111),(113,'2024-01-10 16:38:17.347','1000-01-01 00:00:00.000','1000-01-01 00:00:00.000',-1,'LOGIN_INFO-13','OTHER_INFO-13',1020111),(114,'2024-01-10 16:38:17.347','1000-01-01 00:00:00.000','1000-01-01 00:00:00.000',-1,'LOGIN_INFO-14','OTHER_INFO-14',1020111),(115,'2024-01-10 16:38:17.347','1000-01-01 00:00:00.000','1000-01-01 00:00:00.000',-1,'LOGIN_INFO-15','OTHER_INFO-15',1020111),(116,'2024-01-10 16:38:17.347','1000-01-01 00:00:00.000','1000-01-01 00:00:00.000',-1,'LOGIN_INFO-16','OTHER_INFO-16',1020111),(117,'2024-01-10 16:38:17.347','1000-01-01 00:00:00.000','1000-01-01 00:00:00.000',-1,'LOGIN_INFO-17','OTHER_INFO-17',1020111),(118,'2024-01-10 16:38:17.347','1000-01-01 00:00:00.000','1000-01-01 00:00:00.000',-1,'LOGIN_INFO-18','OTHER_INFO-18',1020111),(119,'2024-01-10 16:38:17.347','1000-01-01 00:00:00.000','1000-01-01 00:00:00.000',-1,'LOGIN_INFO-19','OTHER_INFO-19',1020111);
/*!40000 ALTER TABLE `tst_sharding` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tst_sharding_postfix`
--

DROP TABLE IF EXISTS `tst_sharding_postfix`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tst_sharding_postfix` (
    `id` bigint NOT NULL COMMENT 'primary key',
    `create_dt` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT 'created datetime',
    `modify_dt` datetime(3) NOT NULL DEFAULT '1000-01-01 00:00:00.000' ON UPDATE CURRENT_TIMESTAMP(3) COMMENT 'modified datetime',
    `delete_dt` datetime(3) NOT NULL DEFAULT '1000-01-01 00:00:00.000' COMMENT 'logic deleted datetime',
    `commit_id` bigint NOT NULL COMMENT 'commit id',
    `login_info` text COMMENT 'login info: agent, terminal',
    `other_info` text COMMENT 'other info: biz index data',
    `language` int NOT NULL DEFAULT '1020111' COMMENT 'StandardLanguage',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='201/Sharding Test';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tst_sharding_postfix`
--

LOCK TABLES `tst_sharding_postfix` WRITE;
/*!40000 ALTER TABLE `tst_sharding_postfix` DISABLE KEYS */;
/*!40000 ALTER TABLE `tst_sharding_postfix` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Current Database: `wings_warlock`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `wings_warlock` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `wings_warlock`;

--
-- Table structure for table `sys_commit_journal`
--

DROP TABLE IF EXISTS `sys_commit_journal`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_commit_journal` (
    `id` bigint NOT NULL COMMENT 'primary key',
    `create_dt` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT 'created datetime',
    `event_name` varchar(200) NOT NULL COMMENT 'event name',
    `target_key` varchar(200) NOT NULL DEFAULT '' COMMENT 'target data',
    `login_info` text COMMENT 'login info: agent, terminal',
    `other_info` text COMMENT 'other info: biz index data',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='104/Data Changeset';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_commit_journal`
--

LOCK TABLES `sys_commit_journal` WRITE;
/*!40000 ALTER TABLE `sys_commit_journal` DISABLE KEYS */;
INSERT INTO `sys_commit_journal` VALUES (0,'2024-01-10 16:05:35.466','system_manual_init','',NULL,NULL),(1,'2024-01-10 16:06:02.140','pro.fessional.wings.warlock.service.perm.WarlockRoleService$Jane#Modify','1','','super user'),(101,'2024-01-10 16:06:15.803','pro.fessional.wings.warlock.service.perm.WarlockPermService$Jane#Modify','1','',''),(201,'2024-01-10 16:06:28.568','pro.fessional.wings.warlock.service.perm.WarlockPermService$Jane#Modify','1','',''),(301,'2024-01-10 16:06:43.073','pro.fessional.wings.warlock.service.user.WarlockUserAuthnService$Jane#Danger','1','{\"userId\":-1,\"locale\":\"en-CN\",\"zoneid\":\"Asia/Shanghai\",\"authType\":\"Null\",\"username\":\"\"}','false'),(302,'2024-01-10 16:06:45.431','pro.fessional.wings.warlock.service.auth.WarlockAuthnService$Jane#Success','1','{\"userId\":1,\"locale\":\"zh-CN\",\"zoneid\":\"Asia/Shanghai\",\"authType\":\"USERNAME\",\"username\":\"root\"}','success login auth-type=username'),(303,'2024-01-10 16:06:45.799','pro.fessional.wings.warlock.service.user.WarlockUserAuthnService$Jane#Danger','1','{\"userId\":1,\"locale\":\"zh-CN\",\"zoneid\":\"Asia/Shanghai\",\"authType\":\"USERNAME\",\"username\":\"root\"}','true'),(304,'2024-01-10 16:06:45.916','pro.fessional.wings.warlock.service.user.WarlockUserAuthnService$Jane#Danger','1','{\"userId\":1,\"locale\":\"zh-CN\",\"zoneid\":\"Asia/Shanghai\",\"authType\":\"USERNAME\",\"username\":\"root\"}','false'),(305,'2024-01-10 16:06:47.062','pro.fessional.wings.warlock.service.auth.WarlockAuthnService$Jane#Success','1','{\"userId\":1,\"locale\":\"zh-CN\",\"zoneid\":\"Asia/Shanghai\",\"authType\":\"USERNAME\",\"username\":\"root\"}','success login auth-type=username'),(306,'2024-01-10 16:06:47.134','pro.fessional.wings.warlock.service.auth.WarlockAuthnService$Jane#Failure','1','{\"userId\":-1,\"locale\":\"en\",\"zoneid\":\"Asia/Shanghai\",\"authType\":\"USERNAME\",\"username\":\"root\"}','failed login auth-id=1'),(307,'2024-01-10 16:06:47.259','pro.fessional.wings.warlock.service.auth.WarlockAuthnService$Jane#Failure','1','{\"userId\":-1,\"locale\":\"en\",\"zoneid\":\"Asia/Shanghai\",\"authType\":\"USERNAME\",\"username\":\"root\"}','failed login auth-id=1'),(308,'2024-01-10 16:06:47.390','pro.fessional.wings.warlock.service.user.WarlockUserAuthnService$Jane#Danger','1','{\"userId\":1,\"locale\":\"zh-CN\",\"zoneid\":\"Asia/Shanghai\",\"authType\":\"USERNAME\",\"username\":\"root\"}','false'),(309,'2024-01-10 16:06:48.502','pro.fessional.wings.warlock.service.auth.WarlockAuthnService$Jane#Success','1','{\"userId\":1,\"locale\":\"zh-CN\",\"zoneid\":\"Asia/Shanghai\",\"authType\":\"USERNAME\",\"username\":\"root\"}','success login auth-type=username'),(401,'2024-01-10 16:43:19.500','pro.fessional.wings.warlock.service.perm.WarlockRoleService$Jane#Modify','1','','super user'),(501,'2024-01-10 16:43:34.039','pro.fessional.wings.warlock.service.perm.WarlockPermService$Jane#Modify','1','',''),(601,'2024-01-10 16:43:47.929','pro.fessional.wings.warlock.service.perm.WarlockPermService$Jane#Modify','1','',''),(701,'2024-01-10 16:44:01.215','pro.fessional.wings.warlock.service.user.WarlockUserAuthnService$Jane#Danger','1','{\"userId\":-1,\"locale\":\"en-CN\",\"zoneid\":\"Asia/Shanghai\",\"authType\":\"Null\",\"username\":\"\"}','false'),(702,'2024-01-10 16:44:03.399','pro.fessional.wings.warlock.service.auth.WarlockAuthnService$Jane#Success','1','{\"userId\":1,\"locale\":\"zh-CN\",\"zoneid\":\"Asia/Shanghai\",\"authType\":\"USERNAME\",\"username\":\"root\"}','success login auth-type=username'),(703,'2024-01-10 16:44:03.600','pro.fessional.wings.warlock.service.user.WarlockUserAuthnService$Jane#Danger','1','{\"userId\":1,\"locale\":\"zh-CN\",\"zoneid\":\"Asia/Shanghai\",\"authType\":\"USERNAME\",\"username\":\"root\"}','true'),(704,'2024-01-10 16:44:03.740','pro.fessional.wings.warlock.service.user.WarlockUserAuthnService$Jane#Danger','1','{\"userId\":1,\"locale\":\"zh-CN\",\"zoneid\":\"Asia/Shanghai\",\"authType\":\"USERNAME\",\"username\":\"root\"}','false'),(705,'2024-01-10 16:44:04.847','pro.fessional.wings.warlock.service.auth.WarlockAuthnService$Jane#Success','1','{\"userId\":1,\"locale\":\"zh-CN\",\"zoneid\":\"Asia/Shanghai\",\"authType\":\"USERNAME\",\"username\":\"root\"}','success login auth-type=username'),(706,'2024-01-10 16:44:04.914','pro.fessional.wings.warlock.service.auth.WarlockAuthnService$Jane#Failure','1','{\"userId\":-1,\"locale\":\"en\",\"zoneid\":\"Asia/Shanghai\",\"authType\":\"USERNAME\",\"username\":\"root\"}','failed login auth-id=1'),(707,'2024-01-10 16:44:04.983','pro.fessional.wings.warlock.service.auth.WarlockAuthnService$Jane#Failure','1','{\"userId\":-1,\"locale\":\"en\",\"zoneid\":\"Asia/Shanghai\",\"authType\":\"USERNAME\",\"username\":\"root\"}','failed login auth-id=1'),(708,'2024-01-10 16:44:05.073','pro.fessional.wings.warlock.service.user.WarlockUserAuthnService$Jane#Danger','1','{\"userId\":1,\"locale\":\"zh-CN\",\"zoneid\":\"Asia/Shanghai\",\"authType\":\"USERNAME\",\"username\":\"root\"}','false'),(709,'2024-01-10 16:44:06.195','pro.fessional.wings.warlock.service.auth.WarlockAuthnService$Jane#Success','1','{\"userId\":1,\"locale\":\"zh-CN\",\"zoneid\":\"Asia/Shanghai\",\"authType\":\"USERNAME\",\"username\":\"root\"}','success login auth-type=username');
/*!40000 ALTER TABLE `sys_commit_journal` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_constant_enum`
--

DROP TABLE IF EXISTS `sys_constant_enum`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_constant_enum` (
    `id` int NOT NULL COMMENT 'id: 9+ digits for dynamic, 8 digits for static, 3-2-2 (table-column-value) segments, SUPER end with 00',
    `type` varchar(100) NOT NULL COMMENT 'enum group: same type for same enum, auto Pascal naming',
    `code` varchar(100) NOT NULL COMMENT 'enum name: Fixed [code|id] for SUPER, external key, coding friendly',
    `hint` varchar(100) NOT NULL COMMENT 'display message',
    `info` varchar(500) NOT NULL COMMENT 'extended info: category, filter, template path for SUPER',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uq_type_code` (`type`,`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='105/Enum and Const: auto gen enum code';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_constant_enum`
--

LOCK TABLES `sys_constant_enum` WRITE;
/*!40000 ALTER TABLE `sys_constant_enum` DISABLE KEYS */;
INSERT INTO `sys_constant_enum` VALUES (0,'standard_boolean','false','false','false'),(1,'standard_boolean','true','true','true'),(1010100,'standard_timezone','id','standard timezone','classpath:/wings-tmpl/StandardTimezoneTemplate.java'),(1010101,'standard_timezone','GMT','Greenwich Mean Time (Zero)',''),(1010201,'standard_timezone','Asia/Shanghai','China: BeiJing, ShangHai, HongKong','China'),(1010301,'standard_timezone','America/Chicago','CST: Chicago, Houston','USA'),(1010302,'standard_timezone','America/Los_Angeles','PST: L.A., Seattle','USA'),(1010303,'standard_timezone','America/New_York','EST: NewYork, D.C.','USA'),(1010304,'standard_timezone','America/Phoenix','MST: Denver, Phoenix','USA'),(1010305,'standard_timezone','US/Alaska','AKST: Alaska, Fairbanks','USA'),(1010306,'standard_timezone','US/Hawaii','HST: Hawaii, Honolulu','USA'),(1010401,'standard_timezone','Asia/Jakarta','Indonesia: Jakarta, Surabaya, Medan','Indonesia'),(1010402,'standard_timezone','Asia/Jayapura','Indonesia: Jayapura, Manokwari','Indonesia'),(1010403,'standard_timezone','Asia/Makassar','Indonesia: Makassar, Manado, Balikpapan','Indonesia'),(1010501,'standard_timezone','Asia/Kuala_Lumpur','Malaysia: KualaLumpur','Malaysia'),(1010601,'standard_timezone','Asia/Seoul','Korea: Seoul','Korea'),(1010701,'standard_timezone','Asia/Singapore','Singapore','Singapore'),(1010801,'standard_timezone','Asia/Tokyo','Japan: Tokyo','Japan'),(1010901,'standard_timezone','Canada/Atlantic','AST: Halifax','Canada'),(1010902,'standard_timezone','Canada/Central','CST: Winnipeg','Canada'),(1010903,'standard_timezone','Canada/Eastern','EST: Toronto, Ottawa, Quebec','Canada'),(1010904,'standard_timezone','Canada/Mountain','MST: Edmonton, Calgary','Canada'),(1010905,'standard_timezone','Canada/Newfoundland','NST: St.John','Canada'),(1010906,'standard_timezone','Canada/Pacific','PST: Vancouver','Canada'),(1020100,'standard_language','code','standard language','classpath:/wings-tmpl/StandardLanguageTemplate.java'),(1020101,'standard_language','ar_AE','Arabic',''),(1020102,'standard_language','de_DE','German',''),(1020103,'standard_language','en_US','English(US)',''),(1020104,'standard_language','es_ES','Spanish',''),(1020105,'standard_language','fr_FR','French',''),(1020106,'standard_language','it_IT','Italian',''),(1020107,'standard_language','ja_JP','Japanese',''),(1020108,'standard_language','ko_KR','Korean',''),(1020109,'standard_language','ru_RU','Russian',''),(1020110,'standard_language','th_TH','Thai',''),(1020111,'standard_language','zh_CN','Simplified Chinese',''),(1020112,'standard_language','zh_HK','Traditional Chinese',''),(1200100,'user_gender','user_gender','gender','classpath:/wings-tmpl/ConstantEnumTemplate.java'),(1200101,'user_gender','male','male','normal'),(1200102,'user_gender','female','female','normal'),(1200103,'user_gender','unknown','unknown','normal'),(1200200,'user_status','user_status','user status','classpath:/wings-tmpl/ConstantEnumTemplate.java'),(1200201,'user_status','uninit','uninitialized','created but incomplete'),(1200202,'user_status','active','normal account','normal account'),(1200203,'user_status','infirm','weak account','weak or expired password'),(1200204,'user_status','unsafe','unsafe account','suspicious or frequent operation'),(1200205,'user_status','danger','danger account','deny login, eg. too much fail'),(1200206,'user_status','frozen','frozen account','deny login, eg. funds in danger'),(1200207,'user_status','locked','locked account','deny login, manually locked'),(1200208,'user_status','closed','closed account','deny login, manually closed'),(1200299,'user_status','hidden','hidden account','deny login, special purpose'),(1330100,'grant_type','grant_type','grant type','classpath:/wings-tmpl/ConstantEnumTemplate.java'),(1330101,'grant_type','perm','permit','permit'),(1330102,'grant_type','role','role','role');
/*!40000 ALTER TABLE `sys_constant_enum` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_light_sequence`
--

DROP TABLE IF EXISTS `sys_light_sequence`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_light_sequence` (
    `seq_name` varchar(100) NOT NULL COMMENT 'sequence name',
    `block_id` int NOT NULL DEFAULT '0' COMMENT 'block',
    `next_val` bigint NOT NULL DEFAULT '1' COMMENT 'next value',
    `step_val` int NOT NULL DEFAULT '100' COMMENT 'step of increment',
    `comments` varchar(200) NOT NULL COMMENT 'comments',
    PRIMARY KEY (`seq_name`,`block_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='103/Sequence Generation';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_light_sequence`
--

LOCK TABLES `sys_light_sequence` WRITE;
/*!40000 ALTER TABLE `sys_light_sequence` DISABLE KEYS */;
INSERT INTO `sys_light_sequence` VALUES ('singleton_lightid_blockid',0,10000000,100,'default block_id'),('sys_commit_journal',0,801,100,'sys_commit_journal'),('sys_constant_enum',0,100000000,100,'system 9+ digits, manual 8 digits'),('sys_standard_i18n',0,100000000,100,'system 9+ digits, manual 8 digits'),('testMultiInstance',0,32870,10,'Auto insert if Not found'),('win_perm_entry',0,10000,100,'dynamic 5+ digits, static 5 digits'),('win_role_entry',0,10000,100,'dynamic 5+ digits, static 5 digits'),('win_user_authn',0,10000,100,'dynamic 5+ digits, static 5 digits'),('win_user_basis',0,10000,100,'dynamic 5+ digits, static 5 digits'),('win_user_login',0,1200,100,'Auto insert if Not found');
/*!40000 ALTER TABLE `sys_light_sequence` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_schema_journal`
--

DROP TABLE IF EXISTS `sys_schema_journal`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_schema_journal` (
    `table_name` varchar(100) NOT NULL COMMENT 'plain table name',
    `create_dt` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT 'created datetime',
    `modify_dt` datetime(3) NOT NULL DEFAULT '1000-01-01 00:00:00.000' ON UPDATE CURRENT_TIMESTAMP(3) COMMENT 'modified datetime',
    `commit_id` bigint NOT NULL COMMENT 'commit id',
    `ddl_instbl` text NOT NULL COMMENT 'trace DDL of insert',
    `ddl_instrg` text NOT NULL COMMENT 'trigger DDL of insert',
    `ddl_updtbl` text NOT NULL COMMENT 'trace DDL of update',
    `ddl_updtrg` text NOT NULL COMMENT 'trigger DDL of update',
    `ddl_deltbl` text NOT NULL COMMENT 'trace DDL of delete',
    `ddl_deltrg` text NOT NULL COMMENT 'trigger DDL of delete',
    `log_insert` datetime(3) NOT NULL DEFAULT '1000-01-01 00:00:00.000' COMMENT 'applied datetime of insert',
    `log_update` datetime(3) NOT NULL DEFAULT '1000-01-01 00:00:00.000' COMMENT 'applied datetime of update',
    `log_delete` datetime(3) NOT NULL DEFAULT '1000-01-01 00:00:00.000' COMMENT 'applied datetime of delete',
    PRIMARY KEY (`table_name`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='102/Table Trigger';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_schema_journal`
--

LOCK TABLES `sys_schema_journal` WRITE;
/*!40000 ALTER TABLE `sys_schema_journal` DISABLE KEYS */;
INSERT INTO `sys_schema_journal` VALUES ('sys_schema_journal','2024-01-10 16:05:35.286','1000-01-01 00:00:00.000',0,'','','','','','','1000-01-01 00:00:00.000','1000-01-01 00:00:00.000','1000-01-01 00:00:00.000'),('sys_schema_version','2024-01-10 16:05:35.286','1000-01-01 00:00:00.000',0,'','','','','','','1000-01-01 00:00:00.000','1000-01-01 00:00:00.000','1000-01-01 00:00:00.000');
/*!40000 ALTER TABLE `sys_schema_journal` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_schema_version`
--

DROP TABLE IF EXISTS `sys_schema_version`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_schema_version` (
    `revision` bigint NOT NULL COMMENT 'version + build',
    `create_dt` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT 'created datetime',
    `modify_dt` datetime(3) NOT NULL DEFAULT '1000-01-01 00:00:00.000' ON UPDATE CURRENT_TIMESTAMP(3) COMMENT 'modified datetime',
    `commit_id` bigint NOT NULL COMMENT 'commit id',
    `apply_dt` datetime(3) NOT NULL DEFAULT '1000-01-01 00:00:00.000' COMMENT 'applied datetime',
    `comments` varchar(500) NOT NULL DEFAULT '' COMMENT 'sql path',
    `upto_sql` text NOT NULL COMMENT 'upgrade script',
    `undo_sql` text NOT NULL COMMENT 'downgrade script',
    PRIMARY KEY (`revision`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='101/Table Structure';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_schema_version`
--

LOCK TABLES `sys_schema_version` WRITE;
/*!40000 ALTER TABLE `sys_schema_version` DISABLE KEYS */;
INSERT INTO `sys_schema_version` VALUES (2019051201,'2024-01-10 16:05:35.277','2024-01-10 16:05:35.312',-1704873935092,'2024-01-10 16:05:35.295','master/00-init/2019-0512_01-version-journal.sql','-- Unify timezone (GMT+8), Unify charset (utf8mb4)\n-- CREATE DATABASE `wings` /*!40100 DEFAULT CHARACTER SET utf8mb4 */;\n\nCREATE TABLE `sys_schema_version` (\n    `revision`  BIGINT(20)   NOT NULL COMMENT \'version + build\',\n    `create_dt` DATETIME(3)  NOT NULL DEFAULT NOW(3) COMMENT \'created datetime\',\n    `modify_dt` DATETIME(3)  NOT NULL DEFAULT \'1000-01-01 00:00:00.000\' ON UPDATE NOW(3) COMMENT \'modified datetime\',\n    `commit_id` BIGINT(20)   NOT NULL COMMENT \'commit id\',\n    `apply_dt`  DATETIME(3)  NOT NULL DEFAULT \'1000-01-01 00:00:00.000\' COMMENT \'applied datetime\',\n    `comments`  VARCHAR(500) NOT NULL DEFAULT \'\' COMMENT \'sql path\',\n    `upto_sql`  TEXT         NOT NULL COMMENT \'upgrade script\',\n    `undo_sql`  TEXT         NOT NULL COMMENT \'downgrade script\',\n    PRIMARY KEY (`revision`)\n) ENGINE = InnoDB\n  DEFAULT CHARSET = utf8mb4 COMMENT =\'101/Table Structure\';\n\nCREATE TABLE `sys_schema_journal` (\n    `table_name` VARCHAR(100) NOT NULL COMMENT \'plain table name\',\n    `create_dt`  DATETIME(3)  NOT NULL DEFAULT NOW(3) COMMENT \'created datetime\',\n    `modify_dt`  DATETIME(3)  NOT NULL DEFAULT \'1000-01-01 00:00:00.000\' ON UPDATE NOW(3) COMMENT \'modified datetime\',\n    `commit_id`  BIGINT(20)   NOT NULL COMMENT \'commit id\',\n    `ddl_instbl` TEXT         NOT NULL COMMENT \'trace DDL of insert\',\n    `ddl_instrg` TEXT         NOT NULL COMMENT \'trigger DDL of insert\',\n    `ddl_updtbl` TEXT         NOT NULL COMMENT \'trace DDL of update\',\n    `ddl_updtrg` TEXT         NOT NULL COMMENT \'trigger DDL of update\',\n    `ddl_deltbl` TEXT         NOT NULL COMMENT \'trace DDL of delete\',\n    `ddl_deltrg` TEXT         NOT NULL COMMENT \'trigger DDL of delete\',\n    `log_insert` DATETIME(3)  NOT NULL DEFAULT \'1000-01-01 00:00:00.000\' COMMENT \'applied datetime of insert\',\n    `log_update` DATETIME(3)  NOT NULL DEFAULT \'1000-01-01 00:00:00.000\' COMMENT \'applied datetime of update\',\n    `log_delete` DATETIME(3)  NOT NULL DEFAULT \'1000-01-01 00:00:00.000\' COMMENT \'applied datetime of delete\',\n    PRIMARY KEY (`table_name`) USING BTREE\n) ENGINE = InnoDB\n  DEFAULT CHARSET = utf8mb4 COMMENT =\'102/Table Trigger\';\n\n-- sys_schema_version@plain\nINSERT IGNORE INTO `sys_schema_version` (`revision`, `commit_id`, `upto_sql`, `undo_sql`, `apply_dt`)\nVALUES (2019051201, 0, \'\', \'\', NOW(3));\n\n-- sys_schema_journal@plain\nREPLACE INTO `sys_schema_journal` (`table_name`, `commit_id`, `ddl_instbl`, `ddl_instrg`, `ddl_updtbl`, `ddl_updtrg`, `ddl_deltbl`, `ddl_deltrg`)\nVALUES (\'sys_schema_journal\', 0, \'\', \'\', \'\', \'\', \'\', \'\'),\n       (\'sys_schema_version\', 0, \'\', \'\', \'\', \'\', \'\', \'\');\n','DROP TABLE IF EXISTS `sys_schema_version`; -- 101/Table Structure;\nDROP TABLE IF EXISTS `sys_schema_journal`; -- 102/Table Trigger;\n'),(2019052001,'2024-01-10 16:05:35.334','2024-01-10 16:05:35.472',-1704873935092,'2024-01-10 16:05:35.472','','CREATE TABLE `sys_light_sequence` (\n    `seq_name` VARCHAR(100) NOT NULL COMMENT \'sequence name\',\n    `block_id` INT(11)      NOT NULL DEFAULT 0 COMMENT \'block\',\n    `next_val` BIGINT(20)   NOT NULL DEFAULT \'1\' COMMENT \'next value\',\n    `step_val` INT(11)      NOT NULL DEFAULT \'100\' COMMENT \'step of increment\',\n    `comments` VARCHAR(200) NOT NULL COMMENT \'comments\',\n    PRIMARY KEY (`seq_name`, `block_id`)\n) ENGINE = InnoDB\n  DEFAULT CHARSET = utf8mb4 COMMENT =\'103/Sequence Generation\';\n\nCREATE TABLE `sys_commit_journal` (\n    `id`         BIGINT(20)   NOT NULL COMMENT \'primary key\',\n    `create_dt`  DATETIME(3)  NOT NULL DEFAULT NOW(3) COMMENT \'created datetime\',\n    `event_name` VARCHAR(200) NOT NULL COMMENT \'event name\',\n    `target_key` VARCHAR(200) NOT NULL DEFAULT \'\' COMMENT \'target data\',\n    `login_info` TEXT COMMENT \'login info: agent, terminal\',\n    `other_info` TEXT COMMENT \'other info: biz index data\',\n    PRIMARY KEY (`id`)\n) ENGINE = InnoDB\n  DEFAULT CHARSET = utf8mb4 COMMENT =\'104/Data Changeset\';\n\n-- sys_light_sequence@plain\nINSERT IGNORE INTO `sys_light_sequence` (`seq_name`, `block_id`, `next_val`, `step_val`, `comments`)\nVALUES (\'singleton_lightid_blockid\', 0, 10000000, 100, \'default block_id\'),\n       (\'sys_commit_journal\', 0, 1, 100, \'sys_commit_journal\');\n\n-- sys_commit_journal@plain\nREPLACE INTO `sys_commit_journal` (`id`, `event_name`)\nVALUES (0, \'system_manual_init\');\n','-- ask@danger\nDROP TABLE IF EXISTS `sys_light_sequence`; -- 103/Sequence Generation;\nDROP TABLE IF EXISTS `sys_commit_journal`; -- 104/Data Changeset;\n'),(2019052101,'2024-01-10 16:05:35.485','2024-01-10 16:05:35.661',-1704873935092,'2024-01-10 16:05:35.661','','CREATE TABLE `sys_constant_enum` (\n    `id`   INT(11)      NOT NULL COMMENT \'id: 9+ digits for dynamic, 8 digits for static, 3-2-2 (table-column-value) segments, SUPER end with 00\',\n    `type` VARCHAR(100) NOT NULL COMMENT \'enum group: same type for same enum, auto Pascal naming\',\n    `code` VARCHAR(100) NOT NULL COMMENT \'enum name: Fixed [code|id] for SUPER, external key, coding friendly\',\n    `hint` VARCHAR(100) NOT NULL COMMENT \'display message\',\n    `info` VARCHAR(500) NOT NULL COMMENT \'extended info: category, filter, template path for SUPER\',\n    PRIMARY KEY (`id`),\n    UNIQUE INDEX uq_type_code (`type`, `code`)\n) ENGINE = InnoDB\n  DEFAULT CHARSET = utf8mb4 COMMENT =\'105/Enum and Const: auto gen enum code\';\n\nCREATE TABLE `sys_standard_i18n` (\n    `base` VARCHAR(100)  NOT NULL COMMENT \'table or package name\',\n    `kind` VARCHAR(100)  NOT NULL COMMENT \'column or class name\',\n    `ukey` VARCHAR(200)  NOT NULL COMMENT \'[id.###|type.code|enum]\',\n    `lang` CHAR(5)       NOT NULL COMMENT \'lang tag:zh_CN\',\n    `hint` VARCHAR(3000) NOT NULL COMMENT \'display:Asia/Shanghai\',\n    PRIMARY KEY (`base`, `kind`, `ukey`, `lang`)\n) ENGINE = InnoDB\n  DEFAULT CHARSET = utf8mb4 COMMENT =\'106/I18n Message\';\n\n--\nINSERT IGNORE INTO `sys_light_sequence`(`seq_name`, `block_id`, `next_val`, `step_val`, `comments`)\nVALUES (\'sys_constant_enum\', 0, 100000000, 100, \'system 9+ digits, manual 8 digits\'),\n       (\'sys_standard_i18n\', 0, 100000000, 100, \'system 9+ digits, manual 8 digits\');\n\nREPLACE INTO `sys_constant_enum` (`id`, `type`, `code`, `hint`, `info`)\nVALUES (0, \'standard_boolean\', \'false\', \'false\', \'false\'),\n       (1, \'standard_boolean\', \'true\', \'true\', \'true\');\n\n-- the enum with the same type, id ending in 00 is the SUPER, code is the name.\nREPLACE INTO `sys_constant_enum` (`id`, `type`, `code`, `hint`, `info`)\nVALUES (1010100, \'standard_timezone\', \'id\', \'standard timezone\', \'classpath:/wings-tmpl/StandardTimezoneTemplate.java\'),\n       (1010101, \'standard_timezone\', \'GMT\', \'Greenwich Mean Time (Zero)\', \'\'),\n       (1010201, \'standard_timezone\', \'Asia/Shanghai\', \'China: BeiJing, ShangHai, HongKong\', \'China\'),\n       (1010301, \'standard_timezone\', \'America/Chicago\', \'CST: Chicago, Houston\', \'USA\'),\n       (1010302, \'standard_timezone\', \'America/Los_Angeles\', \'PST: L.A., Seattle\', \'USA\'),\n       (1010303, \'standard_timezone\', \'America/New_York\', \'EST: NewYork, D.C.\', \'USA\'),\n       (1010304, \'standard_timezone\', \'America/Phoenix\', \'MST: Denver, Phoenix\', \'USA\'),\n       (1010305, \'standard_timezone\', \'US/Alaska\', \'AKST: Alaska, Fairbanks\', \'USA\'),\n       (1010306, \'standard_timezone\', \'US/Hawaii\', \'HST: Hawaii, Honolulu\', \'USA\'),\n       (1010401, \'standard_timezone\', \'Asia/Jakarta\', \'Indonesia: Jakarta, Surabaya, Medan\', \'Indonesia\'),\n       (1010402, \'standard_timezone\', \'Asia/Jayapura\', \'Indonesia: Jayapura, Manokwari\', \'Indonesia\'),\n       (1010403, \'standard_timezone\', \'Asia/Makassar\', \'Indonesia: Makassar, Manado, Balikpapan\', \'Indonesia\'),\n       (1010501, \'standard_timezone\', \'Asia/Kuala_Lumpur\', \'Malaysia: KualaLumpur\', \'Malaysia\'),\n       (1010601, \'standard_timezone\', \'Asia/Seoul\', \'Korea: Seoul\', \'Korea\'),\n       (1010701, \'standard_timezone\', \'Asia/Singapore\', \'Singapore\', \'Singapore\'),\n       (1010801, \'standard_timezone\', \'Asia/Tokyo\', \'Japan: Tokyo\', \'Japan\'),\n       (1010901, \'standard_timezone\', \'Canada/Atlantic\', \'AST: Halifax\', \'Canada\'),\n       (1010902, \'standard_timezone\', \'Canada/Central\', \'CST: Winnipeg\', \'Canada\'),\n       (1010903, \'standard_timezone\', \'Canada/Eastern\', \'EST: Toronto, Ottawa, Quebec\', \'Canada\'),\n       (1010904, \'standard_timezone\', \'Canada/Mountain\', \'MST: Edmonton, Calgary\', \'Canada\'),\n       (1010905, \'standard_timezone\', \'Canada/Newfoundland\', \'NST: St.John\', \'Canada\'),\n       (1010906, \'standard_timezone\', \'Canada/Pacific\', \'PST: Vancouver\', \'Canada\'),\n\n       (1020100, \'standard_language\', \'code\', \'standard language\', \'classpath:/wings-tmpl/StandardLanguageTemplate.java\'),\n       (1020101, \'standard_language\', \'ar_AE\', \'Arabic\', \'\'),\n       (1020102, \'standard_language\', \'de_DE\', \'German\', \'\'),\n       (1020103, \'standard_language\', \'en_US\', \'English(US)\', \'\'),\n       (1020104, \'standard_language\', \'es_ES\', \'Spanish\', \'\'),\n       (1020105, \'standard_language\', \'fr_FR\', \'French\', \'\'),\n       (1020106, \'standard_language\', \'it_IT\', \'Italian\', \'\'),\n       (1020107, \'standard_language\', \'ja_JP\', \'Japanese\', \'\'),\n       (1020108, \'standard_language\', \'ko_KR\', \'Korean\', \'\'),\n       (1020109, \'standard_language\', \'ru_RU\', \'Russian\', \'\'),\n       (1020110, \'standard_language\', \'th_TH\', \'Thai\', \'\'),\n       (1020111, \'standard_language\', \'zh_CN\', \'Simplified Chinese\', \'\'),\n       (1020112, \'standard_language\', \'zh_HK\', \'Traditional Chinese\', \'\');\n\n-- https://24timezones.com/zh_clock/united_states_time.php\nREPLACE INTO `sys_standard_i18n`(`base`, `kind`, `ukey`, `lang`, `hint`)\nVALUES (\'sys_constant_enum\', \'hint\', \'id.1010101\', \'zh_CN\', \'格林威治时间(零时区)\'),\n       (\'sys_constant_enum\', \'hint\', \'id.1010101\', \'en_US\', \'Greenwich Mean Time\'),\n\n       (\'sys_constant_enum\', \'hint\', \'id.1010201\', \'zh_CN\', \'北京时间：北京、上海、香港\'),\n       (\'sys_constant_enum\', \'hint\', \'id.1010201\', \'en_US\', \'China: BeiJing, ShangHai, HongKong\'),\n\n       (\'sys_constant_enum\', \'hint\', \'id.1010301\', \'zh_CN\', \'中部时(CST)：芝加哥、休斯顿\'),\n       (\'sys_constant_enum\', \'hint\', \'id.1010301\', \'en_US\', \'CST: Chicago, Houston\'),\n\n       (\'sys_constant_enum\', \'hint\', \'id.1010302\', \'zh_CN\', \'西部时间(PST)：西雅图、洛杉矶\'),\n       (\'sys_constant_enum\', \'hint\', \'id.1010302\', \'en_US\', \'PST: L.A., Seattle\'),\n\n       (\'sys_constant_enum\', \'hint\', \'id.1010303\', \'zh_CN\', \'东部时(EST)：纽约、华盛顿\'),\n       (\'sys_constant_enum\', \'hint\', \'id.1010303\', \'en_US\', \'EST: NewYork, D.C.\'),\n\n       (\'sys_constant_enum\', \'hint\', \'id.1010304\', \'zh_CN\', \'山地时(MST)：丹佛、凤凰城\'),\n       (\'sys_constant_enum\', \'hint\', \'id.1010304\', \'en_US\', \'MST: Denver, Phoenix\'),\n\n       (\'sys_constant_enum\', \'hint\', \'id.1010305\', \'zh_CN\', \'阿拉斯加时间(AKST)：安克雷奇\'),\n       (\'sys_constant_enum\', \'hint\', \'id.1010305\', \'en_US\', \'AKST: Alaska, Fairbanks\'),\n\n       (\'sys_constant_enum\', \'hint\', \'id.1010306\', \'zh_CN\', \'夏威夷时间(HST)：火鲁奴奴\'),\n       (\'sys_constant_enum\', \'hint\', \'id.1010306\', \'en_US\', \'HST: Hawaii, Honolulu\'),\n\n       (\'sys_constant_enum\', \'hint\', \'id.1010401\', \'zh_CN\', \'印度尼西亚：雅加达、泗水、棉兰\'),\n       (\'sys_constant_enum\', \'hint\', \'id.1010401\', \'en_US\', \'Indonesia: Jakarta, Surabaya, Medan\'),\n\n       (\'sys_constant_enum\', \'hint\', \'id.1010402\', \'zh_CN\', \'印度尼西亚：查亚普拉、马诺夸里\'),\n       (\'sys_constant_enum\', \'hint\', \'id.1010402\', \'en_US\', \'Indonesia: Jayapura, Manokwari\'),\n\n       (\'sys_constant_enum\', \'hint\', \'id.1010403\', \'zh_CN\', \'印度尼西亚：望加锡、万鸦老、阿克\'),\n       (\'sys_constant_enum\', \'hint\', \'id.1010403\', \'en_US\', \'Indonesia: Makassar, Manado, Balikpapan\'),\n\n       (\'sys_constant_enum\', \'hint\', \'id.1010501\', \'zh_CN\', \'马来西亚：吉隆坡\'),\n       (\'sys_constant_enum\', \'hint\', \'id.1010501\', \'en_US\', \'Malaysia: KualaLumpur\'),\n\n       (\'sys_constant_enum\', \'hint\', \'id.1010601\', \'zh_CN\', \'韩国时间：首尔\'),\n       (\'sys_constant_enum\', \'hint\', \'id.1010601\', \'en_US\', \'Korea: Seoul\'),\n\n       (\'sys_constant_enum\', \'hint\', \'id.1010701\', \'zh_CN\', \'新加坡时间\'),\n       (\'sys_constant_enum\', \'hint\', \'id.1010701\', \'en_US\', \'Singapore\'),\n\n       (\'sys_constant_enum\', \'hint\', \'id.1010801\', \'zh_CN\', \'日本时间：东京\'),\n       (\'sys_constant_enum\', \'hint\', \'id.1010801\', \'en_US\', \'Japan: Tokyo\'),\n\n       (\'sys_constant_enum\', \'hint\', \'id.1010901\', \'zh_CN\', \'大西洋时(AST)：哈利法克斯\'),\n       (\'sys_constant_enum\', \'hint\', \'id.1010901\', \'en_US\', \'AST: Halifax\'),\n\n       (\'sys_constant_enum\', \'hint\', \'id.1010902\', \'zh_CN\', \'中部时(CST)：温尼伯\'),\n       (\'sys_constant_enum\', \'hint\', \'id.1010902\', \'en_US\', \'CST: Winnipeg\'),\n\n       (\'sys_constant_enum\', \'hint\', \'id.1010903\', \'zh_CN\', \'东部时(EST)：多伦多、渥太华、魁北克城\'),\n       (\'sys_constant_enum\', \'hint\', \'id.1010903\', \'en_US\', \'EST: Toronto, Ottawa, Quebec\'),\n\n       (\'sys_constant_enum\', \'hint\', \'id.1010904\', \'zh_CN\', \'山地时(MST)：埃德蒙顿、卡尔加里\'),\n       (\'sys_constant_enum\', \'hint\', \'id.1010904\', \'en_US\', \'MST: Edmonton, Calgary\'),\n\n       (\'sys_constant_enum\', \'hint\', \'id.1010905\', \'zh_CN\', \'纽芬兰时(NST)：圣约翰斯\'),\n       (\'sys_constant_enum\', \'hint\', \'id.1010905\', \'en_US\', \'NST: St.John\'),\n\n       (\'sys_constant_enum\', \'hint\', \'id.1010906\', \'zh_CN\', \'太平洋时(PST)：温哥华\'),\n       (\'sys_constant_enum\', \'hint\', \'id.1010906\', \'en_US\', \'PST: Vancouver\');\n\n-- java.util.Locale#toLanguageTag\nREPLACE INTO `sys_standard_i18n`(`base`, `kind`, `ukey`, `lang`, `hint`)\nVALUES (\'sys_constant_enum\', \'hint\', \'standard_language.zh_CN\', \'zh_CN\', \'简体中文\'),\n       (\'sys_constant_enum\', \'hint\', \'standard_language.zh_CN\', \'en_US\', \'Simplified Chinese\'),\n\n       (\'sys_constant_enum\', \'hint\', \'standard_language.zh_HK\', \'zh_HK\', \'繁體中文\'),\n       (\'sys_constant_enum\', \'hint\', \'standard_language.zh_HK\', \'zh_CN\', \'繁体中文\'),\n       (\'sys_constant_enum\', \'hint\', \'standard_language.zh_HK\', \'en_US\', \'Traditional Chinese\'),\n\n       (\'sys_constant_enum\', \'hint\', \'standard_language.ja_JP\', \'ja_JP\', \'日本語\'),\n       (\'sys_constant_enum\', \'hint\', \'standard_language.ja_JP\', \'zh_CN\', \'日语\'),\n       (\'sys_constant_enum\', \'hint\', \'standard_language.ja_JP\', \'en_US\', \'Japanese\'),\n\n       (\'sys_constant_enum\', \'hint\', \'standard_language.ko_KR\', \'ko_KR\', \'한국어\'),\n       (\'sys_constant_enum\', \'hint\', \'standard_language.ko_KR\', \'zh_CN\', \'韩语\'),\n       (\'sys_constant_enum\', \'hint\', \'standard_language.ko_KR\', \'en_US\', \'Korean\'),\n\n       (\'sys_constant_enum\', \'hint\', \'standard_language.ru_RU\', \'ru_RU\', \'русский язык\'),\n       (\'sys_constant_enum\', \'hint\', \'standard_language.ru_RU\', \'zh_CN\', \'俄语\'),\n       (\'sys_constant_enum\', \'hint\', \'standard_language.ru_RU\', \'en_US\', \'Russian\'),\n\n       (\'sys_constant_enum\', \'hint\', \'standard_language.de_DE\', \'de_DE\', \'Deutsch\'),\n       (\'sys_constant_enum\', \'hint\', \'standard_language.de_DE\', \'zh_CN\', \'德语\'),\n       (\'sys_constant_enum\', \'hint\', \'standard_language.de_DE\', \'en_US\', \'German\'),\n\n       (\'sys_constant_enum\', \'hint\', \'standard_language.es_ES\', \'es_ES\', \'Español\'),\n       (\'sys_constant_enum\', \'hint\', \'standard_language.es_ES\', \'zh_CN\', \'西班牙语\'),\n       (\'sys_constant_enum\', \'hint\', \'standard_language.es_ES\', \'en_US\', \'Spanish\'),\n\n       (\'sys_constant_enum\', \'hint\', \'standard_language.fr_FR\', \'fr_FR\', \'Français\'),\n       (\'sys_constant_enum\', \'hint\', \'standard_language.fr_FR\', \'zh_CN\', \'法语\'),\n       (\'sys_constant_enum\', \'hint\', \'standard_language.fr_FR\', \'en_US\', \'French\'),\n\n       (\'sys_constant_enum\', \'hint\', \'standard_language.it_IT\', \'it_IT\', \'Italiano\'),\n       (\'sys_constant_enum\', \'hint\', \'standard_language.it_IT\', \'zh_CN\', \'意大利语\'),\n       (\'sys_constant_enum\', \'hint\', \'standard_language.it_IT\', \'en_US\', \'Italian\'),\n\n       (\'sys_constant_enum\', \'hint\', \'standard_language.th_TH\', \'th_TH\', \'ภาษาไทย\'),\n       (\'sys_constant_enum\', \'hint\', \'standard_language.th_TH\', \'zh_CN\', \'泰国语\'),\n       (\'sys_constant_enum\', \'hint\', \'standard_language.th_TH\', \'en_US\', \'Thai\'),\n\n       (\'sys_constant_enum\', \'hint\', \'standard_language.ar_AE\', \'ar_AE\', \'عربي ،\'),\n       (\'sys_constant_enum\', \'hint\', \'standard_language.ar_AE\', \'zh_CN\', \'阿拉伯联合酋长国\'),\n       (\'sys_constant_enum\', \'hint\', \'standard_language.ar_AE\', \'en_US\', \'Arabic\'),\n\n       (\'sys_constant_enum\', \'hint\', \'standard_language.en_US\', \'en_US\', \'English(US)\'),\n       (\'sys_constant_enum\', \'hint\', \'standard_language.en_US\', \'zh_CN\', \'美国英语\');\n','DROP TABLE IF EXISTS `sys_constant_enum`; -- 105/Enum and Const;\nDROP TABLE IF EXISTS `sys_standard_i18n`; -- 106/I18n Message;\n'),(2020102301,'2024-01-10 16:05:35.671','2024-01-10 16:05:35.697',-1704873935092,'2024-01-10 16:05:35.697','','REPLACE INTO `sys_constant_enum` (`id`, `type`, `code`, `hint`, `info`)\nVALUES (1200200, \'user_status\', \'user_status\', \'user status\', \'classpath:/wings-tmpl/ConstantEnumTemplate.java\'),\n       (1200201, \'user_status\', \'uninit\', \'uninitialized\', \'created but incomplete\'),\n       (1200202, \'user_status\', \'active\', \'normal account\', \'normal account\'),\n       (1200203, \'user_status\', \'infirm\', \'weak account\', \'weak or expired password\'),\n       (1200204, \'user_status\', \'unsafe\', \'unsafe account\', \'suspicious or frequent operation\'),\n       (1200205, \'user_status\', \'danger\', \'danger account\', \'deny login, eg. too much fail\'),\n       (1200206, \'user_status\', \'frozen\', \'frozen account\', \'deny login, eg. funds in danger\'),\n       (1200207, \'user_status\', \'locked\', \'locked account\', \'deny login, manually locked\'),\n       (1200208, \'user_status\', \'closed\', \'closed account\', \'deny login, manually closed\'),\n       (1200299, \'user_status\', \'hidden\', \'hidden account\', \'deny login, special purpose\'),\n\n       (1330100, \'grant_type\', \'grant_type\', \'grant type\', \'classpath:/wings-tmpl/ConstantEnumTemplate.java\'),\n       (1330101, \'grant_type\', \'perm\', \'permit\', \'permit\'),\n       (1330102, \'grant_type\', \'role\', \'role\', \'role\');\n',''),(2020102401,'2024-01-10 16:05:59.901','2024-01-10 16:06:00.125',-1704873959869,'2024-01-10 16:06:00.125','','CREATE TABLE `win_user_basis` (\n    `id`        BIGINT(20)    NOT NULL COMMENT \'primary key/user_id/uid\',\n    `create_dt` DATETIME(3)   NOT NULL DEFAULT NOW(3) COMMENT \'created datetime(sys)\',\n    `modify_dt` DATETIME(3)   NOT NULL DEFAULT \'1000-01-01\' ON UPDATE NOW(3) COMMENT \'modified datetime(sys)\',\n    `delete_dt` DATETIME(3)   NOT NULL DEFAULT \'1000-01-01\' COMMENT \'logic deleted datetime\',\n    `commit_id` BIGINT(20)    NOT NULL COMMENT \'commit id\',\n    `nickname`  VARCHAR(50)   NOT NULL DEFAULT \'\' COMMENT \'nickname\',\n    `passsalt`  VARCHAR(100)  NOT NULL DEFAULT \'\' COMMENT \'password salt/random, read-only, no external use\',\n    `gender`    INT(11)       NOT NULL DEFAULT \'0\' COMMENT \'gender/12001##:unknown|mail|female\',\n    `avatar`    VARCHAR(1000) NOT NULL DEFAULT \'\' COMMENT \'avatar url\',\n    `locale`    CHAR(5)       NOT NULL DEFAULT \'zh_CN\' COMMENT \'language/Locale:StandardLanguageEnum\',\n    `zoneid`    INT(11)       NOT NULL DEFAULT \'1010201\' COMMENT \'timezone/ZoneId:StandardTimezoneEnum\',\n    `remark`    VARCHAR(500)  NOT NULL DEFAULT \'\' COMMENT \'comment\',\n    `status`    INT(11)       NOT NULL DEFAULT \'0\' COMMENT \'user status/12002##:\',\n    PRIMARY KEY (`id`)\n) ENGINE = InnoDB\n  DEFAULT CHARSET = utf8mb4 COMMENT =\'120/User Basis\';\n\nCREATE TABLE `win_user_authn` (\n    `id`         BIGINT(20)    NOT NULL COMMENT \'primary key\',\n    `create_dt`  DATETIME(3)   NOT NULL DEFAULT NOW(3) COMMENT \'created datetime(sys)\',\n    `modify_dt`  DATETIME(3)   NOT NULL DEFAULT \'1000-01-01\' ON UPDATE NOW(3) COMMENT \'modified datetime(sys)\',\n    `delete_dt`  DATETIME(3)   NOT NULL DEFAULT \'1000-01-01\' COMMENT \'logic deleted datetime\',\n    `commit_id`  BIGINT(20)    NOT NULL COMMENT \'commit id\',\n    `user_id`    BIGINT(20)    NOT NULL DEFAULT \'0\' COMMENT \'basic user/win_user_basis.id\',\n    `auth_type`  VARCHAR(10)   NOT NULL COMMENT \'auth type/wings.warlock.security.auth-type.*\',\n    `username`   VARCHAR(200)  NOT NULL COMMENT \'account/id:email|mobile|union_id|api_key\',\n    `password`   VARCHAR(200)  NOT NULL DEFAULT \'\' COMMENT \'password/spring style|api_secret\',\n    `extra_para` VARCHAR(3000) NOT NULL DEFAULT \'\' COMMENT \'para for 3rd auth\',\n    `extra_user` VARCHAR(9000) NOT NULL DEFAULT \'\' COMMENT \'user info of 3rd\',\n    `expired_dt` DATETIME(3)   NOT NULL DEFAULT \'1000-01-01\' COMMENT \'expiration, not for token, empty is disabled\',\n    `failed_cnt` INT(11)       NOT NULL DEFAULT \'0\' COMMENT \'continuous error count: clear on success\',\n    `failed_max` INT(11)       NOT NULL DEFAULT \'5\' COMMENT \'max continuous error\',\n    PRIMARY KEY (`id`),\n    UNIQUE INDEX uq_uid_type (`user_id`, `auth_type`),\n    UNIQUE INDEX uq_type_name (`auth_type`, `username`)\n) ENGINE = InnoDB\n  DEFAULT CHARSET = utf8mb4 COMMENT =\'121/User Authn\';\n\nCREATE TABLE `win_user_login` (\n    `id`        BIGINT(20)    NOT NULL COMMENT \'primary key\',\n    `user_id`   BIGINT(20)    NOT NULL DEFAULT \'0\' COMMENT \'basic user/win_user_basis.id\',\n    `auth_type` VARCHAR(20)   NOT NULL COMMENT \'auth type/wings.warlock.security.auth-type.*\',\n    `login_ip`  VARCHAR(50)   NOT NULL DEFAULT \'\' COMMENT \'login IP\',\n    `login_dt`  DATETIME(3)   NOT NULL DEFAULT NOW(3) COMMENT \'created datetime(sys)\',\n    `terminal`  VARCHAR(1000) NOT NULL DEFAULT \'\' COMMENT \'login terminal\',\n    `details`   VARCHAR(9000) NOT NULL DEFAULT \'\' COMMENT \'auth details\',\n    `failed`    TINYINT(1)    NOT NULL DEFAULT \'0\' COMMENT \'fail or not\',\n    PRIMARY KEY (`id`),\n    INDEX ix_user_id (`user_id`)\n) ENGINE = InnoDB\n  DEFAULT CHARSET = utf8mb4 COMMENT =\'122/User Login: read-only\';\n\n-- -----------\n\nINSERT IGNORE INTO `sys_light_sequence` (`seq_name`, `block_id`, `next_val`, `step_val`, `comments`)\nVALUES (\'win_user_basis\', 0, 10000, 100, \'dynamic 5+ digits, static 5 digits\'),\n       (\'win_user_authn\', 0, 10000, 100, \'dynamic 5+ digits, static 5 digits\');\n\n--\nREPLACE INTO `sys_constant_enum` (`id`, `type`, `code`, `hint`, `info`)\nVALUES (1200100, \'user_gender\', \'user_gender\', \'gender\', \'classpath:/wings-tmpl/ConstantEnumTemplate.java\'),\n       (1200101, \'user_gender\', \'male\', \'male\', \'normal\'),\n       (1200102, \'user_gender\', \'female\', \'female\', \'normal\'),\n       (1200103, \'user_gender\', \'unknown\', \'unknown\', \'normal\');\n\n\nINSERT IGNORE INTO `win_user_basis` (`id`, `create_dt`, `commit_id`, `nickname`, `passsalt`, `gender`, `avatar`, `locale`, `zoneid`, `remark`, `status`)\nVALUES (0, NOW(3), 0, \'nobody\', UUID(), 1200103, \'\', \'zh_CN\', 1010201, \'system user without any privileges\', 1200207),\n       (1, NOW(3), 0, \'root\', UUID(), 1200103, \'\', \'zh_CN\', 1010201, \'super user with full privileges\', 1200202),\n       (2, NOW(3), 0, \'daemon\', UUID(), 1200103, \'\', \'zh_CN\', 1010201, \'system user for background tasks\', 1200207);\n\nINSERT IGNORE INTO `win_user_authn`(`id`, `create_dt`, `commit_id`, `user_id`, `auth_type`, `username`, `password`, `expired_dt`)\nVALUES (1, NOW(3), 0, 1, \'username\', \'root\', CONCAT(\'{never}\', UUID()), \'2999-09-09\');\n','DROP TABLE IF EXISTS `win_user_basis`; -- 120/User Basis;\nDROP TABLE IF EXISTS `win_user_authn`; -- 121/User Authn;\nDROP TABLE IF EXISTS `win_user_login`; -- 122/User Login;\n'),(2020102402,'2024-01-10 16:06:00.139','2024-01-10 16:06:00.312',-1704873959869,'2024-01-10 16:06:00.312','','CREATE TABLE `win_perm_entry` (\n    `id`        BIGINT(20)   NOT NULL COMMENT \'primary key\',\n    `create_dt` DATETIME(3)  NOT NULL DEFAULT NOW(3) COMMENT \'created datetime(sys)\',\n    `modify_dt` DATETIME(3)  NOT NULL DEFAULT \'1000-01-01\' ON UPDATE NOW(3) COMMENT \'modified datetime(sys)\',\n    `delete_dt` DATETIME(3)  NOT NULL DEFAULT \'1000-01-01\' COMMENT \'logic deleted datetime\',\n    `commit_id` BIGINT(20)   NOT NULL COMMENT \'commit id\',\n    `scopes`    VARCHAR(200) NOT NULL COMMENT \'all lowercase, period-separated\',\n    `action`    VARCHAR(50)  NOT NULL COMMENT \'all lowercase\',\n    `remark`    VARCHAR(500) NOT NULL DEFAULT \'\' COMMENT \'comment\',\n    PRIMARY KEY (`id`),\n    UNIQUE INDEX (`scopes`, `action`)\n) ENGINE = InnoDB\n  DEFAULT CHARSET = utf8mb4 COMMENT =\'130/Perm Entry\';\n\nCREATE TABLE `win_role_entry` (\n    `id`        BIGINT(20)   NOT NULL COMMENT \'primary key\',\n    `create_dt` DATETIME(3)  NOT NULL DEFAULT NOW(3) COMMENT \'created datetime(sys)\',\n    `modify_dt` DATETIME(3)  NOT NULL DEFAULT \'1000-01-01\' ON UPDATE NOW(3) COMMENT \'modified datetime(sys)\',\n    `delete_dt` DATETIME(3)  NOT NULL DEFAULT \'1000-01-01\' COMMENT \'logic deleted datetime\',\n    `commit_id` BIGINT(20)   NOT NULL COMMENT \'commit id\',\n    `name`      VARCHAR(50)  NOT NULL COMMENT \'all uppercase, no separated, no ROLE_ prefix\',\n    `remark`    VARCHAR(500) NOT NULL DEFAULT \'\' COMMENT \'comment\',\n    PRIMARY KEY (`id`),\n    UNIQUE INDEX (`name`)\n) ENGINE = InnoDB\n  DEFAULT CHARSET = utf8mb4 COMMENT =\'131/Role Entry\';\n\nCREATE TABLE `win_role_grant` (\n    `refer_role`  BIGINT(20)  NOT NULL COMMENT \'current role/win_role_entry.id\',\n    `grant_type`  INT(11)     NOT NULL COMMENT \'grant type/13301##:Role,Perm\',\n    `grant_entry` BIGINT(20)  NOT NULL COMMENT \'entry to grant: id/win_role_entry.id, win_perm_entry.id\',\n    `create_dt`   DATETIME(3) NOT NULL DEFAULT NOW(3) COMMENT \'created datetime(sys)\',\n    `commit_id`   BIGINT(20)  NOT NULL COMMENT \'commit id\',\n    PRIMARY KEY (`refer_role`, `grant_type`, `grant_entry`)\n) ENGINE = InnoDB\n  DEFAULT CHARSET = utf8mb4 COMMENT =\'134/Role Grant\';\n\nCREATE TABLE `win_user_grant` (\n    `refer_user`  BIGINT(20)  NOT NULL COMMENT \'current user/win_user_basis.id\',\n    `grant_type`  INT(11)     NOT NULL COMMENT \'grant type/13301##:Role,Perm\',\n    `grant_entry` BIGINT(20)  NOT NULL COMMENT \'entry to grant: id/win_role_entry.id, win_perm_entry.id\',\n    `create_dt`   DATETIME(3) NOT NULL DEFAULT NOW(3) COMMENT \'created datetime(sys)\',\n    `commit_id`   BIGINT(20)  NOT NULL COMMENT \'commit id\',\n    PRIMARY KEY (`refer_user`, `grant_type`, `grant_entry`)\n) ENGINE = InnoDB\n  DEFAULT CHARSET = utf8mb4 COMMENT =\'135/User Grant\';\n\n-- ----\nINSERT IGNORE INTO `sys_light_sequence` (`seq_name`, `block_id`, `next_val`, `step_val`, `comments`)\nVALUES (\'win_perm_entry\', 0, 10000, 100, \'dynamic 5+ digits, static 5 digits\'),\n       (\'win_role_entry\', 0, 10000, 100, \'dynamic 5+ digits, static 5 digits\');\n\nREPLACE INTO `win_perm_entry`(`id`, `create_dt`, `commit_id`, `scopes`, `action`, `remark`)\nVALUES (1, NOW(3), 0, \'\', \'*\', \'super privilege, NOT for external use\'),\n       -- User\n       (10, NOW(3), 0, \'system.user\', \'*\', \'all\'),\n       (11, NOW(3), 0, \'system.user\', \'create\', \'create user\'),\n       (12, NOW(3), 0, \'system.user\', \'update\', \'update user\'),\n       (13, NOW(3), 0, \'system.user\', \'delete\', \'delete user\'),\n       -- Perm\n       (20, NOW(3), 0, \'system.perm\', \'*\', \'all\'),\n       (21, NOW(3), 0, \'system.perm\', \'create\', \'create perm\'),\n       (22, NOW(3), 0, \'system.perm\', \'update\', \'update perm\'),\n       (23, NOW(3), 0, \'system.perm\', \'delete\', \'delete perm\'),\n       (24, NOW(3), 0, \'system.perm\', \'assign\', \'assign perm to user/role\'),\n       -- Role\n       (30, NOW(3), 0, \'system.role\', \'*\', \'all\'),\n       (31, NOW(3), 0, \'system.role\', \'create\', \'create role\'),\n       (32, NOW(3), 0, \'system.role\', \'update\', \'update role\'),\n       (33, NOW(3), 0, \'system.role\', \'delete\', \'delete role\'),\n       (34, NOW(3), 0, \'system.role\', \'assign\', \'assign role to user/role\');\n\nREPLACE INTO `win_role_entry`(`id`, `create_dt`, `commit_id`, `name`, `remark`)\nVALUES (1, NOW(3),  0, \'ROOT\', \'Super Admin, full privileges\'),\n       (9, NOW(3),  0, \'SYSTEM\', \'System Admin, system privileges\'),\n       (10, NOW(3), 0, \'ADMIN\', \'Normal Admin, business privileges\');\n\n-- Grant root role and perm; admin basic perm;\n-- Note that role is not inherited and needs to be specified, ROOT is used by default for SYSTEM and ADMIN\nREPLACE INTO `win_role_grant`(`refer_role`, `grant_type`, `grant_entry`, `create_dt`, `commit_id`)\nVALUES (1, 1330101, 1, NOW(3), 0),\n       (1, 1330102, 9, NOW(3), 0),\n       (1, 1330102, 10, NOW(3), 0),\n       (10, 1330101, 10, NOW(3), 0),\n       (10, 1330101, 20, NOW(3), 0),\n       (10, 1330101, 30, NOW(3), 0);\n\n-- Grant super perm to root user\nREPLACE INTO `win_user_grant`(`refer_user`, `grant_type`, `grant_entry`, `create_dt`, `commit_id`)\nVALUES (1, 1330102, 1, NOW(3), 0);\n','DROP TABLE IF EXISTS `win_perm_entry`; -- 130/Perm Entry;\nDROP TABLE IF EXISTS `win_role_entry`; -- 131/Role Entry;\nDROP TABLE IF EXISTS `win_role_grant`; -- 134/Role Grant;\nDROP TABLE IF EXISTS `win_user_grant`; -- 135/User Grant;\n'),(2020102501,'2024-01-10 16:05:35.704','2024-01-10 16:05:35.771',-1704873935092,'2024-01-10 16:05:35.771','','CREATE TABLE `win_conf_runtime` (\n    `key`      VARCHAR(200)  NOT NULL COMMENT \'conf key:Enum|Class|String\',\n    `current`  VARCHAR(5000) NOT NULL DEFAULT \'\' COMMENT \'current value\',\n    `previous` VARCHAR(5000) NOT NULL DEFAULT \'\' COMMENT \'previous value\',\n    `initial`  VARCHAR(5000) NOT NULL DEFAULT \'\' COMMENT \'initial value\',\n    `comment`  VARCHAR(500)  NOT NULL DEFAULT \'\' COMMENT \'comment\',\n    `handler`  VARCHAR(200)  NOT NULL DEFAULT \'prop\' COMMENT \'data handling:prop|json\',\n    PRIMARY KEY (`key`)\n) ENGINE = InnoDB\n  DEFAULT CHARSET = utf8mb4 COMMENT =\'110/Runtime Config\';\n-- -----------\n\nINSERT IGNORE INTO `win_conf_runtime` (`key`, `current`, `previous`, `initial`, `comment`)\nVALUES (\'pro.fessional.wings.warlock.service.conf.mode.RunMode\', \'Local\', \'\', \'Local\', \'RunMode\')\n     , (\'pro.fessional.wings.warlock.service.conf.mode.ApiMode\', \'Nothing\', \'\', \'Local\', \'ApiMode\');\n','DROP TABLE IF EXISTS `win_conf_runtime`; -- 110/Runtime Config;\n\n');
/*!40000 ALTER TABLE `sys_schema_version` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_standard_i18n`
--

DROP TABLE IF EXISTS `sys_standard_i18n`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_standard_i18n` (
    `base` varchar(100) NOT NULL COMMENT 'table or package name',
    `kind` varchar(100) NOT NULL COMMENT 'column or class name',
    `ukey` varchar(200) NOT NULL COMMENT '[id.###|type.code|enum]',
    `lang` char(5) NOT NULL COMMENT 'lang tag:zh_CN',
    `hint` varchar(3000) NOT NULL COMMENT 'display:Asia/Shanghai',
    PRIMARY KEY (`base`,`kind`,`ukey`,`lang`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='106/I18n Message';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_standard_i18n`
--

LOCK TABLES `sys_standard_i18n` WRITE;
/*!40000 ALTER TABLE `sys_standard_i18n` DISABLE KEYS */;
INSERT INTO `sys_standard_i18n` VALUES ('sys_constant_enum','hint','id.1010101','en_US','Greenwich Mean Time'),('sys_constant_enum','hint','id.1010101','zh_CN','格林威治时间(零时区)'),('sys_constant_enum','hint','id.1010201','en_US','China: BeiJing, ShangHai, HongKong'),('sys_constant_enum','hint','id.1010201','zh_CN','北京时间：北京、上海、香港'),('sys_constant_enum','hint','id.1010301','en_US','CST: Chicago, Houston'),('sys_constant_enum','hint','id.1010301','zh_CN','中部时(CST)：芝加哥、休斯顿'),('sys_constant_enum','hint','id.1010302','en_US','PST: L.A., Seattle'),('sys_constant_enum','hint','id.1010302','zh_CN','西部时间(PST)：西雅图、洛杉矶'),('sys_constant_enum','hint','id.1010303','en_US','EST: NewYork, D.C.'),('sys_constant_enum','hint','id.1010303','zh_CN','东部时(EST)：纽约、华盛顿'),('sys_constant_enum','hint','id.1010304','en_US','MST: Denver, Phoenix'),('sys_constant_enum','hint','id.1010304','zh_CN','山地时(MST)：丹佛、凤凰城'),('sys_constant_enum','hint','id.1010305','en_US','AKST: Alaska, Fairbanks'),('sys_constant_enum','hint','id.1010305','zh_CN','阿拉斯加时间(AKST)：安克雷奇'),('sys_constant_enum','hint','id.1010306','en_US','HST: Hawaii, Honolulu'),('sys_constant_enum','hint','id.1010306','zh_CN','夏威夷时间(HST)：火鲁奴奴'),('sys_constant_enum','hint','id.1010401','en_US','Indonesia: Jakarta, Surabaya, Medan'),('sys_constant_enum','hint','id.1010401','zh_CN','印度尼西亚：雅加达、泗水、棉兰'),('sys_constant_enum','hint','id.1010402','en_US','Indonesia: Jayapura, Manokwari'),('sys_constant_enum','hint','id.1010402','zh_CN','印度尼西亚：查亚普拉、马诺夸里'),('sys_constant_enum','hint','id.1010403','en_US','Indonesia: Makassar, Manado, Balikpapan'),('sys_constant_enum','hint','id.1010403','zh_CN','印度尼西亚：望加锡、万鸦老、阿克'),('sys_constant_enum','hint','id.1010501','en_US','Malaysia: KualaLumpur'),('sys_constant_enum','hint','id.1010501','zh_CN','马来西亚：吉隆坡'),('sys_constant_enum','hint','id.1010601','en_US','Korea: Seoul'),('sys_constant_enum','hint','id.1010601','zh_CN','韩国时间：首尔'),('sys_constant_enum','hint','id.1010701','en_US','Singapore'),('sys_constant_enum','hint','id.1010701','zh_CN','新加坡时间'),('sys_constant_enum','hint','id.1010801','en_US','Japan: Tokyo'),('sys_constant_enum','hint','id.1010801','zh_CN','日本时间：东京'),('sys_constant_enum','hint','id.1010901','en_US','AST: Halifax'),('sys_constant_enum','hint','id.1010901','zh_CN','大西洋时(AST)：哈利法克斯'),('sys_constant_enum','hint','id.1010902','en_US','CST: Winnipeg'),('sys_constant_enum','hint','id.1010902','zh_CN','中部时(CST)：温尼伯'),('sys_constant_enum','hint','id.1010903','en_US','EST: Toronto, Ottawa, Quebec'),('sys_constant_enum','hint','id.1010903','zh_CN','东部时(EST)：多伦多、渥太华、魁北克城'),('sys_constant_enum','hint','id.1010904','en_US','MST: Edmonton, Calgary'),('sys_constant_enum','hint','id.1010904','zh_CN','山地时(MST)：埃德蒙顿、卡尔加里'),('sys_constant_enum','hint','id.1010905','en_US','NST: St.John'),('sys_constant_enum','hint','id.1010905','zh_CN','纽芬兰时(NST)：圣约翰斯'),('sys_constant_enum','hint','id.1010906','en_US','PST: Vancouver'),('sys_constant_enum','hint','id.1010906','zh_CN','太平洋时(PST)：温哥华'),('sys_constant_enum','hint','standard_language.ar_AE','ar_AE','عربي ،'),('sys_constant_enum','hint','standard_language.ar_AE','en_US','Arabic'),('sys_constant_enum','hint','standard_language.ar_AE','zh_CN','阿拉伯联合酋长国'),('sys_constant_enum','hint','standard_language.de_DE','de_DE','Deutsch'),('sys_constant_enum','hint','standard_language.de_DE','en_US','German'),('sys_constant_enum','hint','standard_language.de_DE','zh_CN','德语'),('sys_constant_enum','hint','standard_language.en_US','en_US','English(US)'),('sys_constant_enum','hint','standard_language.en_US','zh_CN','美国英语'),('sys_constant_enum','hint','standard_language.es_ES','en_US','Spanish'),('sys_constant_enum','hint','standard_language.es_ES','es_ES','Español'),('sys_constant_enum','hint','standard_language.es_ES','zh_CN','西班牙语'),('sys_constant_enum','hint','standard_language.fr_FR','en_US','French'),('sys_constant_enum','hint','standard_language.fr_FR','fr_FR','Français'),('sys_constant_enum','hint','standard_language.fr_FR','zh_CN','法语'),('sys_constant_enum','hint','standard_language.it_IT','en_US','Italian'),('sys_constant_enum','hint','standard_language.it_IT','it_IT','Italiano'),('sys_constant_enum','hint','standard_language.it_IT','zh_CN','意大利语'),('sys_constant_enum','hint','standard_language.ja_JP','en_US','Japanese'),('sys_constant_enum','hint','standard_language.ja_JP','ja_JP','日本語'),('sys_constant_enum','hint','standard_language.ja_JP','zh_CN','日语'),('sys_constant_enum','hint','standard_language.ko_KR','en_US','Korean'),('sys_constant_enum','hint','standard_language.ko_KR','ko_KR','한국어'),('sys_constant_enum','hint','standard_language.ko_KR','zh_CN','韩语'),('sys_constant_enum','hint','standard_language.ru_RU','en_US','Russian'),('sys_constant_enum','hint','standard_language.ru_RU','ru_RU','русский язык'),('sys_constant_enum','hint','standard_language.ru_RU','zh_CN','俄语'),('sys_constant_enum','hint','standard_language.th_TH','en_US','Thai'),('sys_constant_enum','hint','standard_language.th_TH','th_TH','ภาษาไทย'),('sys_constant_enum','hint','standard_language.th_TH','zh_CN','泰国语'),('sys_constant_enum','hint','standard_language.zh_CN','en_US','Simplified Chinese'),('sys_constant_enum','hint','standard_language.zh_CN','zh_CN','简体中文'),('sys_constant_enum','hint','standard_language.zh_HK','en_US','Traditional Chinese'),('sys_constant_enum','hint','standard_language.zh_HK','zh_CN','繁体中文'),('sys_constant_enum','hint','standard_language.zh_HK','zh_HK','繁體中文');
/*!40000 ALTER TABLE `sys_standard_i18n` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `win_conf_runtime`
--

DROP TABLE IF EXISTS `win_conf_runtime`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `win_conf_runtime` (
    `key` varchar(200) NOT NULL COMMENT 'conf key:Enum|Class|String',
    `current` varchar(5000) NOT NULL DEFAULT '' COMMENT 'current value',
    `previous` varchar(5000) NOT NULL DEFAULT '' COMMENT 'previous value',
    `initial` varchar(5000) NOT NULL DEFAULT '' COMMENT 'initial value',
    `comment` varchar(500) NOT NULL DEFAULT '' COMMENT 'comment',
    `handler` varchar(200) NOT NULL DEFAULT 'prop' COMMENT 'data handling:prop|json',
    PRIMARY KEY (`key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='110/Runtime Config';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `win_conf_runtime`
--

LOCK TABLES `win_conf_runtime` WRITE;
/*!40000 ALTER TABLE `win_conf_runtime` DISABLE KEYS */;
INSERT INTO `win_conf_runtime` VALUES ('java.lang.Integer','\"10\"','','\"10\"','test Integer','json'),('java.lang.Long','\"1023\"','','\"1023\"','test Long','json'),('java.lang.String','string','','string','test String','json'),('java.math.BigDecimal','\"10.00\"','','\"10.00\"','test BigDecimal','json'),('java.time.LocalDateTime','\"2022-02-01 12:34:56\"','','\"2022-02-01 12:34:56\"','test LocalDateTime','json'),('java.time.ZonedDateTime','\"2022-02-01T12:34:56[Asia/Shanghai]\"','','\"2022-02-01T12:34:56[Asia/Shanghai]\"','test ZonedDateTime','json'),('java.util.List','[\"Jan\",\"Fer\"]','','[\"Jan\",\"Fer\"]','test list','json'),('java.util.Map','{\"Fer\":false,\"Jan\":true}','','{\"Fer\":false,\"Jan\":true}','test map','json'),('pro.fessional.wings.warlock.service.conf.mode.ApiMode','Nothing','','Local','ApiMode','prop'),('pro.fessional.wings.warlock.service.conf.mode.RunMode','Local','','Local','RunMode','prop'),('pro.fessional.wings.warlock.service.conf.RuntimeConfServiceTest$Dto','AQBwcm8uZmVzc2lvbmFsLndpbmdzLndhcmxvY2suc2VydmljZS5jb25mLlJ1bnRpbWVDb25mU2VydmljZVRlc3QkRHTvATAB6A8BChAqNLDXjYYCSmFja3Nv7g','','AQBwcm8uZmVzc2lvbmFsLndpbmdzLndhcmxvY2suc2VydmljZS5jb25mLlJ1bnRpbWVDb25mU2VydmljZVRlc3QkRHTvATAB6A8BChAqNLDXjYYCSmFja3Nv7g','test dto','kryo'),('RuntimeConfCacheTest.testCache','[\"Develop\",\"Local\"]','[\"Develop\",\"Local\"]','[\"Develop\",\"Local\"]','test RunMode','json'),('RuntimeConfServiceTest.testMode','\"Develop\"','\"Develop\"','[\"Develop\",\"Local\"]','test RunMode','json');
/*!40000 ALTER TABLE `win_conf_runtime` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `win_perm_entry`
--

DROP TABLE IF EXISTS `win_perm_entry`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `win_perm_entry` (
    `id` bigint NOT NULL COMMENT 'primary key',
    `create_dt` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT 'created datetime(sys)',
    `modify_dt` datetime(3) NOT NULL DEFAULT '1000-01-01 00:00:00.000' ON UPDATE CURRENT_TIMESTAMP(3) COMMENT 'modified datetime(sys)',
    `delete_dt` datetime(3) NOT NULL DEFAULT '1000-01-01 00:00:00.000' COMMENT 'logic deleted datetime',
    `commit_id` bigint NOT NULL COMMENT 'commit id',
    `scopes` varchar(200) NOT NULL COMMENT 'all lowercase, period-separated',
    `action` varchar(50) NOT NULL COMMENT 'all lowercase',
    `remark` varchar(500) NOT NULL DEFAULT '' COMMENT 'comment',
    PRIMARY KEY (`id`),
    UNIQUE KEY `scopes` (`scopes`,`action`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='130/Perm Entry';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `win_perm_entry`
--

LOCK TABLES `win_perm_entry` WRITE;
/*!40000 ALTER TABLE `win_perm_entry` DISABLE KEYS */;
INSERT INTO `win_perm_entry` VALUES (1,'2024-01-10 16:06:00.283','2024-01-10 16:43:47.929','1000-01-01 00:00:00.000',601,'','*','super user'),(10,'2024-01-10 16:06:00.283','1000-01-01 00:00:00.000','1000-01-01 00:00:00.000',0,'system.user','*','all'),(11,'2024-01-10 16:06:00.283','1000-01-01 00:00:00.000','1000-01-01 00:00:00.000',0,'system.user','create','create user'),(12,'2024-01-10 16:06:00.283','1000-01-01 00:00:00.000','1000-01-01 00:00:00.000',0,'system.user','update','update user'),(13,'2024-01-10 16:06:00.283','1000-01-01 00:00:00.000','1000-01-01 00:00:00.000',0,'system.user','delete','delete user'),(20,'2024-01-10 16:06:00.283','1000-01-01 00:00:00.000','1000-01-01 00:00:00.000',0,'system.perm','*','all'),(21,'2024-01-10 16:06:00.283','1000-01-01 00:00:00.000','1000-01-01 00:00:00.000',0,'system.perm','create','create perm'),(22,'2024-01-10 16:06:00.283','1000-01-01 00:00:00.000','1000-01-01 00:00:00.000',0,'system.perm','update','update perm'),(23,'2024-01-10 16:06:00.283','1000-01-01 00:00:00.000','1000-01-01 00:00:00.000',0,'system.perm','delete','delete perm'),(24,'2024-01-10 16:06:00.283','1000-01-01 00:00:00.000','1000-01-01 00:00:00.000',0,'system.perm','assign','assign perm to user/role'),(30,'2024-01-10 16:06:00.283','1000-01-01 00:00:00.000','1000-01-01 00:00:00.000',0,'system.role','*','all'),(31,'2024-01-10 16:06:00.283','1000-01-01 00:00:00.000','1000-01-01 00:00:00.000',0,'system.role','create','create role'),(32,'2024-01-10 16:06:00.283','1000-01-01 00:00:00.000','1000-01-01 00:00:00.000',0,'system.role','update','update role'),(33,'2024-01-10 16:06:00.283','1000-01-01 00:00:00.000','1000-01-01 00:00:00.000',0,'system.role','delete','delete role'),(34,'2024-01-10 16:06:00.283','1000-01-01 00:00:00.000','1000-01-01 00:00:00.000',0,'system.role','assign','assign role to user/role');
/*!40000 ALTER TABLE `win_perm_entry` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `win_role_entry`
--

DROP TABLE IF EXISTS `win_role_entry`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `win_role_entry` (
    `id` bigint NOT NULL COMMENT 'primary key',
    `create_dt` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT 'created datetime(sys)',
    `modify_dt` datetime(3) NOT NULL DEFAULT '1000-01-01 00:00:00.000' ON UPDATE CURRENT_TIMESTAMP(3) COMMENT 'modified datetime(sys)',
    `delete_dt` datetime(3) NOT NULL DEFAULT '1000-01-01 00:00:00.000' COMMENT 'logic deleted datetime',
    `commit_id` bigint NOT NULL COMMENT 'commit id',
    `name` varchar(50) NOT NULL COMMENT 'all uppercase, no separated, no ROLE_ prefix',
    `remark` varchar(500) NOT NULL DEFAULT '' COMMENT 'comment',
    PRIMARY KEY (`id`),
    UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='131/Role Entry';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `win_role_entry`
--

LOCK TABLES `win_role_entry` WRITE;
/*!40000 ALTER TABLE `win_role_entry` DISABLE KEYS */;
INSERT INTO `win_role_entry` VALUES (1,'2024-01-10 16:06:00.291','2024-01-10 16:43:19.500','1000-01-01 00:00:00.000',401,'ROOT','super user'),(9,'2024-01-10 16:06:00.291','1000-01-01 00:00:00.000','1000-01-01 00:00:00.000',0,'SYSTEM','System Admin, system privileges'),(10,'2024-01-10 16:06:00.291','1000-01-01 00:00:00.000','1000-01-01 00:00:00.000',0,'ADMIN','Normal Admin, business privileges');
/*!40000 ALTER TABLE `win_role_entry` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `win_role_grant`
--

DROP TABLE IF EXISTS `win_role_grant`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `win_role_grant` (
    `refer_role` bigint NOT NULL COMMENT 'current role/win_role_entry.id',
    `grant_type` int NOT NULL COMMENT 'grant type/13301##:Role,Perm',
    `grant_entry` bigint NOT NULL COMMENT 'entry to grant: id/win_role_entry.id, win_perm_entry.id',
    `create_dt` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT 'created datetime(sys)',
    `commit_id` bigint NOT NULL COMMENT 'commit id',
    PRIMARY KEY (`refer_role`,`grant_type`,`grant_entry`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='134/Role Grant';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `win_role_grant`
--

LOCK TABLES `win_role_grant` WRITE;
/*!40000 ALTER TABLE `win_role_grant` DISABLE KEYS */;
INSERT INTO `win_role_grant` VALUES (1,1330101,1,'2024-01-10 16:06:00.299',0),(1,1330102,9,'2024-01-10 16:06:00.299',0),(1,1330102,10,'2024-01-10 16:06:00.299',0),(10,1330101,10,'2024-01-10 16:06:00.299',0),(10,1330101,20,'2024-01-10 16:06:00.299',0),(10,1330101,30,'2024-01-10 16:06:00.299',0);
/*!40000 ALTER TABLE `win_role_grant` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `win_user_authn`
--

DROP TABLE IF EXISTS `win_user_authn`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `win_user_authn` (
    `id` bigint NOT NULL COMMENT 'primary key',
    `create_dt` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT 'created datetime(sys)',
    `modify_dt` datetime(3) NOT NULL DEFAULT '1000-01-01 00:00:00.000' ON UPDATE CURRENT_TIMESTAMP(3) COMMENT 'modified datetime(sys)',
    `delete_dt` datetime(3) NOT NULL DEFAULT '1000-01-01 00:00:00.000' COMMENT 'logic deleted datetime',
    `commit_id` bigint NOT NULL COMMENT 'commit id',
    `user_id` bigint NOT NULL DEFAULT '0' COMMENT 'basic user/win_user_basis.id',
    `auth_type` varchar(10) NOT NULL COMMENT 'auth type/wings.warlock.security.auth-type.*',
    `username` varchar(200) NOT NULL COMMENT 'account/id:email|mobile|union_id|api_key',
    `password` varchar(200) NOT NULL DEFAULT '' COMMENT 'password/spring style|api_secret',
    `extra_para` varchar(3000) NOT NULL DEFAULT '' COMMENT 'para for 3rd auth',
    `extra_user` varchar(9000) NOT NULL DEFAULT '' COMMENT 'user info of 3rd',
    `expired_dt` datetime(3) NOT NULL DEFAULT '1000-01-01 00:00:00.000' COMMENT 'expiration, not for token, empty is disabled',
    `failed_cnt` int NOT NULL DEFAULT '0' COMMENT 'continuous error count: clear on success',
    `failed_max` int NOT NULL DEFAULT '5' COMMENT 'max continuous error',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uq_uid_type` (`user_id`,`auth_type`),
    UNIQUE KEY `uq_type_name` (`auth_type`,`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='121/User Authn';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `win_user_authn`
--

LOCK TABLES `win_user_authn` WRITE;
/*!40000 ALTER TABLE `win_user_authn` DISABLE KEYS */;
INSERT INTO `win_user_authn` VALUES (1,'2024-01-10 16:06:00.118','2024-01-10 16:44:06.195','1000-01-01 00:00:00.000',709,1,'username','root','{never}174d8919-af8f-11ee-8e53-0242c0a8d002','','','2999-09-09 00:00:00.000',0,5);
/*!40000 ALTER TABLE `win_user_authn` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `win_user_basis`
--

DROP TABLE IF EXISTS `win_user_basis`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `win_user_basis` (
    `id` bigint NOT NULL COMMENT 'primary key/user_id/uid',
    `create_dt` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT 'created datetime(sys)',
    `modify_dt` datetime(3) NOT NULL DEFAULT '1000-01-01 00:00:00.000' ON UPDATE CURRENT_TIMESTAMP(3) COMMENT 'modified datetime(sys)',
    `delete_dt` datetime(3) NOT NULL DEFAULT '1000-01-01 00:00:00.000' COMMENT 'logic deleted datetime',
    `commit_id` bigint NOT NULL COMMENT 'commit id',
    `nickname` varchar(50) NOT NULL DEFAULT '' COMMENT 'nickname',
    `passsalt` varchar(100) NOT NULL DEFAULT '' COMMENT 'password salt/random, read-only, no external use',
    `gender` int NOT NULL DEFAULT '0' COMMENT 'gender/12001##:unknown|mail|female',
    `avatar` varchar(1000) NOT NULL DEFAULT '' COMMENT 'avatar url',
    `locale` char(5) NOT NULL DEFAULT 'zh_CN' COMMENT 'language/Locale:StandardLanguageEnum',
    `zoneid` int NOT NULL DEFAULT '1010201' COMMENT 'timezone/ZoneId:StandardTimezoneEnum',
    `remark` varchar(500) NOT NULL DEFAULT '' COMMENT 'comment',
    `status` int NOT NULL DEFAULT '0' COMMENT 'user status/12002##:',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='120/User Basis';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `win_user_basis`
--

LOCK TABLES `win_user_basis` WRITE;
/*!40000 ALTER TABLE `win_user_basis` DISABLE KEYS */;
INSERT INTO `win_user_basis` VALUES (0,'2024-01-10 16:06:00.105','1000-01-01 00:00:00.000','1000-01-01 00:00:00.000',0,'nobody','174c0082-af8f-11ee-8e53-0242c0a8d002',1200103,'','zh_CN',1010201,'system user without any privileges',1200207),(1,'2024-01-10 16:06:00.105','2024-01-10 16:44:05.073','1000-01-01 00:00:00.000',708,'root','174c0581-af8f-11ee-8e53-0242c0a8d002',1200103,'','zh_CN',1010201,'super user with full privileges',1200202),(2,'2024-01-10 16:06:00.105','1000-01-01 00:00:00.000','1000-01-01 00:00:00.000',0,'daemon','174c075f-af8f-11ee-8e53-0242c0a8d002',1200103,'','zh_CN',1010201,'system user for background tasks',1200207);
/*!40000 ALTER TABLE `win_user_basis` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `win_user_grant`
--

DROP TABLE IF EXISTS `win_user_grant`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `win_user_grant` (
    `refer_user` bigint NOT NULL COMMENT 'current user/win_user_basis.id',
    `grant_type` int NOT NULL COMMENT 'grant type/13301##:Role,Perm',
    `grant_entry` bigint NOT NULL COMMENT 'entry to grant: id/win_role_entry.id, win_perm_entry.id',
    `create_dt` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT 'created datetime(sys)',
    `commit_id` bigint NOT NULL COMMENT 'commit id',
    PRIMARY KEY (`refer_user`,`grant_type`,`grant_entry`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='135/User Grant';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `win_user_grant`
--

LOCK TABLES `win_user_grant` WRITE;
/*!40000 ALTER TABLE `win_user_grant` DISABLE KEYS */;
INSERT INTO `win_user_grant` VALUES (1,1330102,1,'2024-01-10 16:06:00.307',0);
/*!40000 ALTER TABLE `win_user_grant` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `win_user_login`
--

DROP TABLE IF EXISTS `win_user_login`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `win_user_login` (
    `id` bigint NOT NULL COMMENT 'primary key',
    `user_id` bigint NOT NULL DEFAULT '0' COMMENT 'basic user/win_user_basis.id',
    `auth_type` varchar(20) NOT NULL COMMENT 'auth type/wings.warlock.security.auth-type.*',
    `login_ip` varchar(50) NOT NULL DEFAULT '' COMMENT 'login IP',
    `login_dt` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT 'created datetime(sys)',
    `terminal` varchar(1000) NOT NULL DEFAULT '' COMMENT 'login terminal',
    `details` varchar(9000) NOT NULL DEFAULT '' COMMENT 'auth details',
    `failed` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'fail or not',
    PRIMARY KEY (`id`),
    KEY `ix_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='122/User Login: read-only';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `win_user_login`
--

LOCK TABLES `win_user_login` WRITE;
/*!40000 ALTER TABLE `win_user_login` DISABLE KEYS */;
INSERT INTO `win_user_login` VALUES (1000,1,'username','127.0.0.1','2024-01-10 16:06:45.454','okhttp/4.12.0;','{\"AuthAgent\":\"okhttp/4.12.0;\",\"nickname\":\"root\",\"zoneid\":\"Asia/Shanghai\",\"AuthAddr\":\"127.0.0.1\",\"authType\":\"USERNAME\",\"locale\":\"zh_CN\",\"username\":\"root\"}',0),(1001,1,'username','127.0.0.1','2024-01-10 16:06:47.067','okhttp/4.12.0;','{\"AuthAgent\":\"okhttp/4.12.0;\",\"nickname\":\"root\",\"zoneid\":\"Asia/Shanghai\",\"AuthAddr\":\"127.0.0.1\",\"authType\":\"USERNAME\",\"locale\":\"zh_CN\",\"username\":\"root\"}',0),(1002,1,'username','127.0.0.1','2024-01-10 16:06:47.154','okhttp/4.12.0;','{\"AuthAgent\":\"okhttp/4.12.0;\",\"AuthAddr\":\"127.0.0.1\",\"authType\":\"USERNAME\",\"locale\":\"en\",\"username\":\"root\"}',1),(1003,1,'username','127.0.0.1','2024-01-10 16:06:47.309','okhttp/4.12.0;','{\"AuthAgent\":\"okhttp/4.12.0;\",\"AuthAddr\":\"127.0.0.1\",\"authType\":\"USERNAME\",\"locale\":\"en\",\"username\":\"root\"}',1),(1004,1,'username','127.0.0.1','2024-01-10 16:06:48.506','okhttp/4.12.0;','{\"AuthAgent\":\"okhttp/4.12.0;\",\"nickname\":\"root\",\"zoneid\":\"Asia/Shanghai\",\"AuthAddr\":\"127.0.0.1\",\"authType\":\"USERNAME\",\"locale\":\"zh_CN\",\"username\":\"root\"}',0),(1100,1,'username','127.0.0.1','2024-01-10 16:44:03.423','okhttp/4.12.0;','{\"AuthAgent\":\"okhttp/4.12.0;\",\"nickname\":\"root\",\"zoneid\":\"Asia/Shanghai\",\"AuthAddr\":\"127.0.0.1\",\"authType\":\"USERNAME\",\"locale\":\"zh_CN\",\"username\":\"root\"}',0),(1101,1,'username','127.0.0.1','2024-01-10 16:44:04.851','okhttp/4.12.0;','{\"AuthAgent\":\"okhttp/4.12.0;\",\"nickname\":\"root\",\"zoneid\":\"Asia/Shanghai\",\"AuthAddr\":\"127.0.0.1\",\"authType\":\"USERNAME\",\"locale\":\"zh_CN\",\"username\":\"root\"}',0),(1102,1,'username','127.0.0.1','2024-01-10 16:44:04.931','okhttp/4.12.0;','{\"AuthAgent\":\"okhttp/4.12.0;\",\"AuthAddr\":\"127.0.0.1\",\"authType\":\"USERNAME\",\"locale\":\"en\",\"username\":\"root\"}',1),(1103,1,'username','127.0.0.1','2024-01-10 16:44:04.994','okhttp/4.12.0;','{\"AuthAgent\":\"okhttp/4.12.0;\",\"AuthAddr\":\"127.0.0.1\",\"authType\":\"USERNAME\",\"locale\":\"en\",\"username\":\"root\"}',1),(1104,1,'username','127.0.0.1','2024-01-10 16:44:06.200','okhttp/4.12.0;','{\"AuthAgent\":\"okhttp/4.12.0;\",\"nickname\":\"root\",\"zoneid\":\"Asia/Shanghai\",\"AuthAddr\":\"127.0.0.1\",\"authType\":\"USERNAME\",\"locale\":\"zh_CN\",\"username\":\"root\"}',0);
/*!40000 ALTER TABLE `win_user_login` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2024-01-10 17:10:41
