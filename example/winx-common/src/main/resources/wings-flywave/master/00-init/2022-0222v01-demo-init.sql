-- Unify timezone (GMT+8), Unify charset (utf8mb4)
-- CREATE DATABASE `wings_example` /*!40100 DEFAULT CHARACTER SET utf8mb4 */;

CREATE TABLE `winx_user_detail` (
    `id`        BIGINT(20)   NOT NULL COMMENT 'primary key',
    `create_dt` DATETIME(3)  NOT NULL DEFAULT NOW(3) COMMENT 'created datetime(sys)',
    `modify_dt` DATETIME(3)  NOT NULL DEFAULT '1000-01-01' ON UPDATE NOW(3) COMMENT 'modified datetime(sys)',
    `delete_dt` DATETIME(3)  NOT NULL DEFAULT '1000-01-01' COMMENT 'logic deleted datetime',
    `commit_id` BIGINT(20)   NOT NULL COMMENT 'commit id',
    `user_id`   BIGINT(20)   NOT NULL COMMENT 'win_user_basis.id',
    `user_type` INT(11)      NOT NULL DEFAULT '0' COMMENT 'user type/21001##:customer|operator|helpdesk',
    `email`     VARCHAR(100) NOT NULL DEFAULT '' COMMENT 'user email',
    PRIMARY KEY (`id`),
    UNIQUE INDEX uq_user_id (`user_id`),
    UNIQUE INDEX ix_email (`email`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='210/User Detail';

-- -----------

INSERT IGNORE INTO `sys_light_sequence` (`seq_name`, `block_id`, `next_val`, `step_val`, `comments`)
VALUES ('winx_user_detail', 0, 1000, 100, 'dynamic 5+ digits, static 5 digits');

REPLACE INTO `sys_constant_enum` (`id`, `type`, `code`, `hint`, `info`)
VALUES (2100100, 'user_type', 'user_type', 'User Type', 'classpath:/wings-tmpl/ConstantEnumTemplate.java'),
       (2100101, 'user_type', 'customer', 'customer', 'customer'),
       (2100102, 'user_type', 'operator', 'operator', 'operator'),
       (2100103, 'user_type', 'helpdesk', 'helpdesk', 'helpdesk');
