-- 时区统一(GMT+8)，编码统一(utf8mb4)
-- CREATE DATABASE `demo_example` /*!40100 DEFAULT CHARACTER SET utf8mb4 */;

CREATE TABLE `demo_user_detail` (
    `id`         BIGINT(20)   NOT NULL COMMENT '主键',
    `create_dt`  DATETIME(3)  NOT NULL DEFAULT NOW(3) COMMENT '创建日时(系统)',
    `modify_dt`  DATETIME(3)  NOT NULL DEFAULT '1000-01-01' ON UPDATE NOW(3) COMMENT '修改日时(系统)',
    `delete_dt`  DATETIME(3)  NOT NULL DEFAULT '1000-01-01' COMMENT '标记删除',
    `commit_id`  BIGINT(20)   NOT NULL COMMENT '提交id',
    `user_id`    BIGINT(20)   NOT NULL COMMENT 'win_user_basis.id',
    `email`      VARCHAR(100) NOT NULL DEFAULT '' COMMENT '用户邮箱',
    PRIMARY KEY (`id`),
    UNIQUE INDEX uk_user_id (`user_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='210/用户详情';
