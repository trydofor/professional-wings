-- 时区统一(GMT+8)，编码统一(utf8mb4)
-- CREATE DATABASE `wings_example` /*!40100 DEFAULT CHARACTER SET utf8mb4 */;

CREATE TABLE `winx_user_detail` (
    `id`        BIGINT(20)   NOT NULL COMMENT '主键',
    `create_dt` DATETIME(3)  NOT NULL DEFAULT NOW(3) COMMENT '创建日时(系统)',
    `modify_dt` DATETIME(3)  NOT NULL DEFAULT '1000-01-01' ON UPDATE NOW(3) COMMENT '修改日时(系统)',
    `delete_dt` DATETIME(3)  NOT NULL DEFAULT '1000-01-01' COMMENT '标记删除',
    `commit_id` BIGINT(20)   NOT NULL COMMENT '提交id',
    `user_id`   BIGINT(20)   NOT NULL COMMENT 'win_user_basis.id',
    `user_type` INT(11)      NOT NULL DEFAULT '0' COMMENT '用户类型/21001##:用户|运营|客服',
    `email`     VARCHAR(100) NOT NULL DEFAULT '' COMMENT '用户邮箱',
    PRIMARY KEY (`id`),
    UNIQUE INDEX uq_user_id (`user_id`),
    UNIQUE INDEX ix_email (`email`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='210/用户详情';

-- -----------

INSERT IGNORE INTO `sys_light_sequence` (`seq_name`, `block_id`, `next_val`, `step_val`, `comments`)
VALUES ('winx_user_detail', 0, 1000, 100, '动态插入5位起，静态5位');

REPLACE INTO `sys_constant_enum` (`id`, `type`, `code`, `hint`, `info`)
VALUES (2100100, 'user_type', 'user_type', '用户类型', 'classpath:/wings-tmpl/ConstantEnumTemplate.java'),
       (2100101, 'user_type', 'customer', '用户', '用户'),
       (2100102, 'user_type', 'operator', '运营', '运营'),
       (2100103, 'user_type', 'helpdesk', '客服', '客服');
