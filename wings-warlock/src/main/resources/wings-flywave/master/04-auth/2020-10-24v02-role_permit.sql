CREATE TABLE `win_perm_entry` (
    `id`        BIGINT(20)   NOT NULL COMMENT '主键',
    `create_dt` DATETIME(3)  NOT NULL DEFAULT NOW(3) COMMENT '创建日时(系统)',
    `modify_dt` DATETIME(3)  NOT NULL DEFAULT '1000-01-01' ON UPDATE NOW(3) COMMENT '修改日时(系统)',
    `delete_dt` DATETIME(3)  NOT NULL DEFAULT '1000-01-01' COMMENT '标记删除',
    `commit_id` BIGINT(20)   NOT NULL COMMENT '提交id',
    `scopes`    VARCHAR(200) NOT NULL COMMENT '范围:全小写，句号分隔',
    `action`    VARCHAR(50)  NOT NULL COMMENT '动作:全小写',
    `remark`    VARCHAR(500) NOT NULL DEFAULT '' COMMENT '备注',
    PRIMARY KEY (`id`),
    UNIQUE INDEX (`scopes`, `action`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='130/权限条目表';

CREATE TABLE `win_role_entry` (
    `id`        BIGINT(20)   NOT NULL COMMENT '主键',
    `create_dt` DATETIME(3)  NOT NULL DEFAULT NOW(3) COMMENT '创建日时(系统)',
    `modify_dt` DATETIME(3)  NOT NULL DEFAULT '1000-01-01' ON UPDATE NOW(3) COMMENT '修改日时(系统)',
    `delete_dt` DATETIME(3)  NOT NULL DEFAULT '1000-01-01' COMMENT '标记删除',
    `commit_id` BIGINT(20)   NOT NULL COMMENT '提交id',
    `name`      VARCHAR(50)  NOT NULL COMMENT '名称:全小写，句号分隔',
    `remark`    VARCHAR(500) NOT NULL DEFAULT '' COMMENT '备注',
    PRIMARY KEY (`id`),
    UNIQUE INDEX (`name`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='131/角色条目表';

CREATE TABLE `win_role_perm_map` (
    `refer_role` BIGINT(20)  NOT NULL COMMENT '当前角色/win_role_entry.id',
    `grant_perm` BIGINT(20)  NOT NULL COMMENT '授予权限/win_perm_entry.id',
    `create_dt`  DATETIME(3) NOT NULL DEFAULT NOW(3) COMMENT '创建日时(系统)',
    `modify_dt`  DATETIME(3) NOT NULL DEFAULT '1000-01-01' ON UPDATE NOW(3) COMMENT '修改日时(系统)',
    `delete_dt`  DATETIME(3) NOT NULL DEFAULT '1000-01-01' COMMENT '标记删除',
    `commit_id`  BIGINT(20)  NOT NULL COMMENT '提交id',
    PRIMARY KEY (`refer_role`, `grant_perm`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='133/角色权限映射表';

CREATE TABLE `win_role_role_map` (
    `refer_role` BIGINT(20)  NOT NULL COMMENT '当前角色/win_role_entry.id',
    `grant_role` BIGINT(20)  NOT NULL COMMENT '授予角色/win_role_entry.id',
    `create_dt`  DATETIME(3) NOT NULL DEFAULT NOW(3) COMMENT '创建日时(系统)',
    `modify_dt`  DATETIME(3) NOT NULL DEFAULT '1000-01-01' ON UPDATE NOW(3) COMMENT '修改日时(系统)',
    `delete_dt`  DATETIME(3) NOT NULL DEFAULT '1000-01-01' COMMENT '标记删除',
    `commit_id`  BIGINT(20)  NOT NULL COMMENT '提交id',
    PRIMARY KEY (`refer_role`, `grant_role`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='134/角色继承映射表';

CREATE TABLE `win_user_perm_map` (
    `refer_user` BIGINT(20)  NOT NULL COMMENT '当前角色/win_user_basic.id',
    `grant_perm` BIGINT(20)  NOT NULL COMMENT '授予权限/win_perm_entry.id',
    `create_dt`  DATETIME(3) NOT NULL DEFAULT NOW(3) COMMENT '创建日时(系统)',
    `modify_dt`  DATETIME(3) NOT NULL DEFAULT '1000-01-01' ON UPDATE NOW(3) COMMENT '修改日时(系统)',
    `delete_dt`  DATETIME(3) NOT NULL DEFAULT '1000-01-01' COMMENT '标记删除',
    `commit_id`  BIGINT(20)  NOT NULL COMMENT '提交id',
    PRIMARY KEY (`refer_user`, `grant_perm`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='135/角色权限映射表';

CREATE TABLE `win_user_role_map` (
    `refer_user` BIGINT(20)  NOT NULL COMMENT '当前角色/win_user_basic.id',
    `grant_role` BIGINT(20)  NOT NULL COMMENT '授予角色/win_role_entry.id',
    `create_dt`  DATETIME(3) NOT NULL DEFAULT NOW(3) COMMENT '创建日时(系统)',
    `modify_dt`  DATETIME(3) NOT NULL DEFAULT '1000-01-01' ON UPDATE NOW(3) COMMENT '修改日时(系统)',
    `delete_dt`  DATETIME(3) NOT NULL DEFAULT '1000-01-01' COMMENT '标记删除',
    `commit_id`  BIGINT(20)  NOT NULL COMMENT '提交id',
    PRIMARY KEY (`refer_user`, `grant_role`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='136/角色继承映射表';

-- ----
INSERT IGNORE INTO `sys_light_sequence` (`seq_name`, `block_id`, `next_val`, `step_val`, `comments`)
VALUES ('win_perm_entry', 0, 10000, 100, '动态插入5位起，静态5位'),
       ('win_role_entry', 0, 10000, 100, '动态插入5位起，静态5位');

INSERT INTO `win_perm_entry`(`id`, `create_dt`, `commit_id`, `scopes`, `action`, `remark`)
VALUES (1, NOW(3), 0, '', '*', '顶级权限，不对外使用'),
       -- User
       (10, NOW(3), 0, 'system.user', '*', '用户全部'),
       (11, NOW(3), 0, 'system.user', 'read', '用户读取'),
       (12, NOW(3), 0, 'system.user', 'search', '用户搜索'),
       (13, NOW(3), 0, 'system.user', 'create', '用户创建'),
       (14, NOW(3), 0, 'system.user', 'update', '用户编辑'),
       (15, NOW(3), 0, 'system.user', 'delete', '用户删除'),
       -- Perm
       (20, NOW(3), 0, 'system.perm', '*', '权限全部'),
       (21, NOW(3), 0, 'system.perm', 'read', '权限读取'),
       (22, NOW(3), 0, 'system.perm', 'search', '权限搜索'),
       (23, NOW(3), 0, 'system.perm', 'create', '权限创建'),
       (24, NOW(3), 0, 'system.perm', 'update', '权限编辑'),
       (25, NOW(3), 0, 'system.perm', 'delete', '权限删除'),
       -- Role
       (30, NOW(3), 0, 'system.role', '*', '角色全部'),
       (31, NOW(3), 0, 'system.role', 'read', '角色读取'),
       (32, NOW(3), 0, 'system.role', 'search', '角色搜索'),
       (33, NOW(3), 0, 'system.role', 'create', '角色创建'),
       (34, NOW(3), 0, 'system.role', 'update', '角色编辑'),
       (35, NOW(3), 0, 'system.role', 'delete', '角色删除');


INSERT INTO `win_role_entry`(`id`, `create_dt`, `commit_id`, `name`, `remark`)
VALUES (1, NOW(3), 0, 'root', '超级管理员');

INSERT INTO `win_role_perm_map`(`refer_role`, `grant_perm`, `create_dt`, `commit_id`)
VALUES (1, 1, NOW(3), 0);

INSERT INTO `win_user_perm_map`(`refer_user`, `grant_perm`, `create_dt`, `commit_id`)
VALUES (1, 1, NOW(3), 0);
