CREATE TABLE `sys_light_sequence` (
    `seq_name` VARCHAR(100) NOT NULL COMMENT 'sequence name',
    `block_id` INT(11)      NOT NULL DEFAULT 0 COMMENT 'block',
    `next_val` BIGINT(20)   NOT NULL DEFAULT '1' COMMENT 'next value',
    `step_val` INT(11)      NOT NULL DEFAULT '100' COMMENT 'step of increment',
    `comments` VARCHAR(200) NOT NULL COMMENT 'comments',
    PRIMARY KEY (`seq_name`, `block_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='103/Sequence Generation';

CREATE TABLE `sys_commit_journal` (
    `id`         BIGINT(20)   NOT NULL COMMENT 'primary key',
    `create_dt`  DATETIME(3)  NOT NULL DEFAULT NOW(3) COMMENT 'created datetime',
    `event_name` VARCHAR(200) NOT NULL COMMENT 'event name',
    `target_key` VARCHAR(200) NOT NULL DEFAULT '' COMMENT 'target data',
    `login_info` TEXT COMMENT 'login info: agent, terminal',
    `other_info` TEXT COMMENT 'other info: biz index data',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='104/Data Changeset';

-- sys_light_sequence@plain
INSERT IGNORE INTO `sys_light_sequence` (`seq_name`, `block_id`, `next_val`, `step_val`, `comments`)
VALUES ('singleton_lightid_blockid', 0, 10000000, 100, 'default block_id'),
       ('sys_commit_journal', 0, 1, 100, 'sys_commit_journal');

-- sys_commit_journal@plain
REPLACE INTO `sys_commit_journal` (`id`, `event_name`)
VALUES (0, 'system_manual_init');
