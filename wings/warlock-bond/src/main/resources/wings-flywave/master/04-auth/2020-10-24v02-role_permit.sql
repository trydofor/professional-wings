CREATE TABLE `win_perm_entry` (
    `id`        BIGINT(20)   NOT NULL COMMENT '主键',
    `create_dt` DATETIME(3)  NOT NULL DEFAULT NOW(3) COMMENT '创建日时(系统)',
    `modify_dt` DATETIME(3)  NOT NULL DEFAULT '1000-01-01' ON UPDATE NOW(3) COMMENT '修改日时(系统)',
    `delete_dt` DATETIME(3)  NOT NULL DEFAULT '1000-01-01' COMMENT '标记删除',
    `commit_id` BIGINT(20)   NOT NULL COMMENT '提交id',
    `scopes`    VARCHAR(200) NOT NULL COMMENT '范围:全小写，英句号分隔',
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
    `name`      VARCHAR(50)  NOT NULL COMMENT '名称:全大写，不分割，无需ROLE_前缀',
    `remark`    VARCHAR(500) NOT NULL DEFAULT '' COMMENT '备注',
    PRIMARY KEY (`id`),
    UNIQUE INDEX (`name`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='131/角色条目表';

CREATE TABLE `win_role_grant` (
    `refer_role`  BIGINT(20)  NOT NULL COMMENT '当前角色/win_role_entry.id',
    `grant_type`  INT(11)     NOT NULL COMMENT '授权类别/13301##:Role,Perm',
    `grant_entry` BIGINT(20)  NOT NULL COMMENT '授予id/win_role_entry.id或win_perm_entry.id',
    `create_dt`   DATETIME(3) NOT NULL DEFAULT NOW(3) COMMENT '创建日时(系统)',
    `commit_id`   BIGINT(20)  NOT NULL COMMENT '提交id',
    PRIMARY KEY (`refer_role`, `grant_type`, `grant_entry`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='134/角色权限映射表';

CREATE TABLE `win_user_grant` (
    `refer_user`  BIGINT(20)  NOT NULL COMMENT '当前角色/win_user_basis.id',
    `grant_type`  INT(11)     NOT NULL COMMENT '授权类别/13301##:Role,Perm',
    `grant_entry` BIGINT(20)  NOT NULL COMMENT '授予id/win_role_entry.id或win_perm_entry.id',
    `create_dt`   DATETIME(3) NOT NULL DEFAULT NOW(3) COMMENT '创建日时(系统)',
    `commit_id`   BIGINT(20)  NOT NULL COMMENT '提交id',
    PRIMARY KEY (`refer_user`, `grant_type`, `grant_entry`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='135/角色权限映射表';

-- ----
INSERT IGNORE INTO `sys_light_sequence` (`seq_name`, `block_id`, `next_val`, `step_val`, `comments`)
VALUES ('win_perm_entry', 0, 10000, 100, '动态插入5位起，静态5位'),
       ('win_role_entry', 0, 10000, 100, '动态插入5位起，静态5位');

REPLACE INTO `win_perm_entry`(`id`, `create_dt`, `commit_id`, `scopes`, `action`, `remark`)
VALUES (1, NOW(3), 0, '', '*', '顶级权限，不对外使用'),
       -- User
       (10, NOW(3), 0, 'system.user', '*', '用户全部'),
       (11, NOW(3), 0, 'system.user', 'create', '用户创建'),
       (12, NOW(3), 0, 'system.user', 'update', '用户编辑'),
       (13, NOW(3), 0, 'system.user', 'delete', '用户删除'),
       -- Perm
       (20, NOW(3), 0, 'system.perm', '*', '权限全部'),
       (21, NOW(3), 0, 'system.perm', 'create', '权限创建'),
       (22, NOW(3), 0, 'system.perm', 'update', '权限编辑'),
       (23, NOW(3), 0, 'system.perm', 'delete', '权限删除'),
       (24, NOW(3), 0, 'system.perm', 'assign', '角色指派，给用户或角色'),
       -- Role
       (30, NOW(3), 0, 'system.role', '*', '角色全部'),
       (31, NOW(3), 0, 'system.role', 'create', '角色创建'),
       (32, NOW(3), 0, 'system.role', 'update', '角色编辑'),
       (33, NOW(3), 0, 'system.role', 'delete', '角色删除'),
       (34, NOW(3), 0, 'system.role', 'assign', '角色指派，给用户或角色');

REPLACE INTO `win_role_entry`(`id`, `create_dt`, `commit_id`, `name`, `remark`)
VALUES (1, NOW(3),  0, 'ROOT', '超级管理员，全部权限'),
       (9, NOW(3),  0, 'SYSTEM', '系统管理员，系统权限'),
       (10, NOW(3), 0, 'ADMIN', '普通管理员，业务权限');

-- 授予root角色，根权限；admin基本权限；注意role不继承，需要指定，ROOT默认用于SYSTEM和ADMIN
REPLACE INTO `win_role_grant`(`refer_role`, `grant_type`, `grant_entry`, `create_dt`, `commit_id`)
VALUES (1, 1330101, 1, NOW(3), 0),
       (1, 1330102, 9, NOW(3), 0),
       (1, 1330102, 10, NOW(3), 0),
       (10, 1330101, 10, NOW(3), 0),
       (10, 1330101, 20, NOW(3), 0),
       (10, 1330101, 30, NOW(3), 0);

-- 授予root用户，根权限
REPLACE INTO `win_user_grant`(`refer_user`, `grant_type`, `grant_entry`, `create_dt`, `commit_id`)
VALUES (1, 1330102, 1, NOW(3), 0);
