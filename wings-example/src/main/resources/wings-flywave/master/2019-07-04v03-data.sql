INSERT IGNORE INTO `sys_light_sequence` (`seq_name`, `block_id`, `next_val`, `step_val`, `comments`)
VALUES ('win_authority', 0, 100000000, 100, '系统插入9位起，手动8位'),
       ('win_auth_role', 0, 100000000, 100, '系统插入9位起，手动8位'),
       ('win_user', 0, 1000, 100, '系统插入4位起，手动3位'),
       ('win_user_login', 0, 1000, 100, '系统插入4位起，手动3位');

-- 从200#### 开始编号
REPLACE INTO `sys_constant_enum` (`id`, `type`, `code`, `desc`, `info`)
VALUES (2010100, 'common_gender', 'common_gender', '性别', 'classpath:/wings-tmpl/ConstantEnumTemplate.java'),
       (2010101, 'common_gender', 'male', '男', '通常'),
       (2010102, 'common_gender', 'female', '女', '通常'),
       (2010103, 'common_gender', 'unknown', '未知', '通常'),

       (2020100, 'user_type', 'user_type', '用户类别', 'classpath:/wings-tmpl/ConstantEnumTemplate.java'),
       (2020101, 'user_type', 'admin', '管理', ''),
       (2020102, 'user_type', 'staff', '员工', ''),
       (2020103, 'user_type', 'guest', '客户', ''),

       (2030100, 'terminal_type', 'terminal_type', '用户类别', 'classpath:/wings-tmpl/ConstantEnumTemplate.java'),
       (2030101, 'terminal_type', 'web_admin', 'WEB管理端', ''),
       (2030102, 'terminal_type', 'app_android', '安卓app', ''),
       (2030103, 'terminal_type', 'app_ios', '苹果app', ''),
       (2030103, 'terminal_type', 'wx_mapp', '微信小程序', ''),
       (2030103, 'terminal_type', 'wx_mp', '微信公众号', ''),
       (2030103, 'terminal_type', 'exe_pc', 'pc端exe', ''),

       (4010100, 'authority', 'authority', '权限定义', 'classpath:/wings-tmpl/ConstantEnumTemplate.java'),
       (4010101, 'authority', 'CREATE_USER', '创建用户', '用户:'),
       (4010102, 'authority', 'DELETE_USER', '删除用户', '用户:'),
       (4010203, 'authority', 'CREATE_ROLE', '删除角色', '角色:'),
       (4010204, 'authority', 'DELETE_ROLE', '删除角色', '角色:'),

       (4020100, 'role_type', 'role_type', '角色类别', 'classpath:/wings-tmpl/ConstantEnumTemplate.java'),
       (4020101, 'role_type', 'financial', '财务', '财务总部'),
       (4020102, 'role_type', 'operation', '运营', '财务总部'),

       (4110100, 'user_status', 'user_status', '用户状态', 'classpath:/wings-tmpl/ConstantEnumTemplate.java'),
       (4110101, 'user_status', 'uninit', '新建', '建立未登陆'),
       (4110102, 'user_status', 'active', '正常', '正常活动'),
       (4110103, 'user_status', 'unsafe', '异动', '异动账户'),
       (4110104, 'user_status', 'danger', '危险', '危险账户'),
       (4110105, 'user_status', 'frozen', '冻结', '冻结账户'),

       (4120100, 'login_type', 'login_type', '用户登录类型', 'classpath:/wings-tmpl/ConstantEnumTemplate.java'),
       (4120101, 'login_type', 'email_pass', '邮件', '邮件登录'),
       (4120102, 'login_type', 'name_pass', '用户名', '用户名密码'),
       (4120103, 'login_type', 'mobile_sms', '手机号', '手机号'),
       (4120104, 'login_type', 'weixin_oauth', '微信登录', '微信登录');

REPLACE INTO `win_auth_role` (`id`, `commit_id`, `role_type`, `role_name`, `desc`, `auth_set`)
VALUES (10010101, 202006061234, 4020101, 'user.manager', '财务用户管理', '4010101,4010102'),
       (10010102, 202006061234, 4020102, 'user.manager', '运营用户管理', '4010204');

REPLACE INTO `win_user` (`id`, `commit_id`, `name`, `gender`, `birth`, `avatar`, `country`, `language`, `timezone`, `auth_set`, `role_set`, `status`)
VALUES (1001, 202006061234, '臭豆腐', 2010101, '1993-09-03', 'http://www.trydofor.com/images/post/057.jpg', 'CN', 'zh_CN', 1010201, '4010203,-4010204', '10010101,10010102', 4110101);

-- 密码 moilioncircle
REPLACE INTO `win_user_login` (`id`, `commit_id`, `user_id`, `login_type`, `login_name`, `login_pass`, `login_salt`, `login_para`, `auth_code`, `bad_count`, `status`)
VALUES (1001, 202006061234, 1001, 4120102, 'trydofor', '{bcrypt}$2a$10$aV.rTLLnOdgDc2jRBFf0Ee70tm33vsYWX5RAasTjJ5mxAmGwrQhtm', '', '', '', 0, 4110101);
