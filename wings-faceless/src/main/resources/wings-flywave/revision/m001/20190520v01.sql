CREATE TABLE `sys_light_sequence` (
    `seq_name` VARCHAR(100) NOT NULL COMMENT '序列名',
    `block_id` INT(11)      NOT NULL DEFAULT 0 COMMENT '分块序号',
    `next_val` BIGINT(20)   NOT NULL DEFAULT '1' COMMENT '下一个序号',
    `step_val` INT(11)      NOT NULL DEFAULT '100' COMMENT '序列步长',
    `comments` VARCHAR(200) NOT NULL COMMENT '注释说明',
    PRIMARY KEY (`seq_name`, `block_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='序号生成器';

CREATE TABLE `sys_commit_journal` (
    `id`         BIGINT(20)   NOT NULL COMMENT '主键',
    `create_dt`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建日时',
    `event_name` VARCHAR(200) NOT NULL COMMENT '事件名称',
    `target_key` VARCHAR(200) NOT NULL DEFAULT '' COMMENT '目标数据特征',
    `login_info` TEXT COMMENT '登陆信息，用户，终端等',
    `other_info` TEXT COMMENT '其他信息，业务侧自定义',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='变更日志';

-- sys_light_sequence@plain
INSERT IGNORE `sys_light_sequence` (`seq_name`, `block_id`, `next_val`, `step_val`, `comments`)
VALUES ('singleton_lightid_blockid', 0, 10000000, 100, 'default block_id'),
       ('sys_commit_journal', 0, 1, 100, 'sys_commit_journal');

-- sys_commit_journal@plain
REPLACE INTO `sys_commit_journal`(`id`, `event_name`)
VALUES (0, 'system_manual_init');
