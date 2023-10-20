CREATE TABLE `win_perm_entry` (
    `id`        BIGINT(20)   NOT NULL COMMENT 'primary key',
    `create_dt` DATETIME(3)  NOT NULL DEFAULT NOW(3) COMMENT 'created datetime(sys)',
    `modify_dt` DATETIME(3)  NOT NULL DEFAULT '1000-01-01' ON UPDATE NOW(3) COMMENT 'modified datetime(sys)',
    `delete_dt` DATETIME(3)  NOT NULL DEFAULT '1000-01-01' COMMENT 'logic deleted datetime',
    `commit_id` BIGINT(20)   NOT NULL COMMENT 'commit id',
    `scopes`    VARCHAR(200) NOT NULL COMMENT 'all lowercase, period-separated',
    `action`    VARCHAR(50)  NOT NULL COMMENT 'all lowercase',
    `remark`    VARCHAR(500) NOT NULL DEFAULT '' COMMENT 'comment',
    PRIMARY KEY (`id`),
    UNIQUE INDEX (`scopes`, `action`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='130/Perm Entry';

CREATE TABLE `win_role_entry` (
    `id`        BIGINT(20)   NOT NULL COMMENT 'primary key',
    `create_dt` DATETIME(3)  NOT NULL DEFAULT NOW(3) COMMENT 'created datetime(sys)',
    `modify_dt` DATETIME(3)  NOT NULL DEFAULT '1000-01-01' ON UPDATE NOW(3) COMMENT 'modified datetime(sys)',
    `delete_dt` DATETIME(3)  NOT NULL DEFAULT '1000-01-01' COMMENT 'logic deleted datetime',
    `commit_id` BIGINT(20)   NOT NULL COMMENT 'commit id',
    `name`      VARCHAR(50)  NOT NULL COMMENT 'all uppercase, no separated, no ROLE_ prefix',
    `remark`    VARCHAR(500) NOT NULL DEFAULT '' COMMENT 'comment',
    PRIMARY KEY (`id`),
    UNIQUE INDEX (`name`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='131/Role Entry';

CREATE TABLE `win_role_grant` (
    `refer_role`  BIGINT(20)  NOT NULL COMMENT 'current role/win_role_entry.id',
    `grant_type`  INT(11)     NOT NULL COMMENT 'grant type/13301##:Role,Perm',
    `grant_entry` BIGINT(20)  NOT NULL COMMENT 'entry to grant: id/win_role_entry.id, win_perm_entry.id',
    `create_dt`   DATETIME(3) NOT NULL DEFAULT NOW(3) COMMENT 'created datetime(sys)',
    `commit_id`   BIGINT(20)  NOT NULL COMMENT 'commit id',
    PRIMARY KEY (`refer_role`, `grant_type`, `grant_entry`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='134/Role Grant';

CREATE TABLE `win_user_grant` (
    `refer_user`  BIGINT(20)  NOT NULL COMMENT 'current user/win_user_basis.id',
    `grant_type`  INT(11)     NOT NULL COMMENT 'grant type/13301##:Role,Perm',
    `grant_entry` BIGINT(20)  NOT NULL COMMENT 'entry to grant: id/win_role_entry.id, win_perm_entry.id',
    `create_dt`   DATETIME(3) NOT NULL DEFAULT NOW(3) COMMENT 'created datetime(sys)',
    `commit_id`   BIGINT(20)  NOT NULL COMMENT 'commit id',
    PRIMARY KEY (`refer_user`, `grant_type`, `grant_entry`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='135/User Grant';

-- ----
INSERT IGNORE INTO `sys_light_sequence` (`seq_name`, `block_id`, `next_val`, `step_val`, `comments`)
VALUES ('win_perm_entry', 0, 10000, 100, 'dynamic 5+ digits, static 5 digits'),
       ('win_role_entry', 0, 10000, 100, 'dynamic 5+ digits, static 5 digits');

REPLACE INTO `win_perm_entry`(`id`, `create_dt`, `commit_id`, `scopes`, `action`, `remark`)
VALUES (1, NOW(3), 0, '', '*', 'super privilege, NOT for external use'),
       -- User
       (10, NOW(3), 0, 'system.user', '*', 'all'),
       (11, NOW(3), 0, 'system.user', 'create', 'create user'),
       (12, NOW(3), 0, 'system.user', 'update', 'update user'),
       (13, NOW(3), 0, 'system.user', 'delete', 'delete user'),
       -- Perm
       (20, NOW(3), 0, 'system.perm', '*', 'all'),
       (21, NOW(3), 0, 'system.perm', 'create', 'create perm'),
       (22, NOW(3), 0, 'system.perm', 'update', 'update perm'),
       (23, NOW(3), 0, 'system.perm', 'delete', 'delete perm'),
       (24, NOW(3), 0, 'system.perm', 'assign', 'assign perm to user/role'),
       -- Role
       (30, NOW(3), 0, 'system.role', '*', 'all'),
       (31, NOW(3), 0, 'system.role', 'create', 'create role'),
       (32, NOW(3), 0, 'system.role', 'update', 'update role'),
       (33, NOW(3), 0, 'system.role', 'delete', 'delete role'),
       (34, NOW(3), 0, 'system.role', 'assign', 'assign role to user/role');

REPLACE INTO `win_role_entry`(`id`, `create_dt`, `commit_id`, `name`, `remark`)
VALUES (1, NOW(3),  0, 'ROOT', 'Super Admin, full privileges'),
       (9, NOW(3),  0, 'SYSTEM', 'System Admin, system privileges'),
       (10, NOW(3), 0, 'ADMIN', 'Normal Admin, business privileges');

-- Grant root role and perm; admin basic perm;
-- Note that role is not inherited and needs to be specified, ROOT is used by default for SYSTEM and ADMIN
REPLACE INTO `win_role_grant`(`refer_role`, `grant_type`, `grant_entry`, `create_dt`, `commit_id`)
VALUES (1, 1330101, 1, NOW(3), 0),
       (1, 1330102, 9, NOW(3), 0),
       (1, 1330102, 10, NOW(3), 0),
       (10, 1330101, 10, NOW(3), 0),
       (10, 1330101, 20, NOW(3), 0),
       (10, 1330101, 30, NOW(3), 0);

-- Grant super perm to root user
REPLACE INTO `win_user_grant`(`refer_user`, `grant_type`, `grant_entry`, `create_dt`, `commit_id`)
VALUES (1, 1330102, 1, NOW(3), 0);
