REPLACE INTO `sys_constant_enum` (`id`, `type`, `code`, `hint`, `info`)
VALUES (1200200, 'user_status', 'user_status', '用户状态', 'classpath:/wings-tmpl/ConstantEnumTemplate.java'),
       (1200201, 'user_status', 'uninit', '新建', '未初始化，有信息不完善'),
       (1200202, 'user_status', 'active', '正常', '正常活动，通常的账号状态'),
       (1200203, 'user_status', 'infirm', '薄弱', '薄弱账户，弱密码，密码临期'),
       (1200204, 'user_status', 'unsafe', '异动', '异动账户，有可疑迹象，如频繁操作'),
       (1200205, 'user_status', 'danger', '危险', '危险账户，不可登录，如失败过多'),
       (1200206, 'user_status', 'frozen', '冻结', '冻结账户，不可登录，如资金危险'),
       (1200207, 'user_status', 'locked', '锁定', '锁定账户，不可登录，人为锁定'),
       (1200208, 'user_status', 'closed', '关闭', '关闭账户，不可登录，人为关闭'),
       (1200299, 'user_status', 'hidden', '隐藏', '隐藏账户，不可登录，特殊用途'),

       (1330100, 'grant_type', 'grant_type', '授权类别', 'classpath:/wings-tmpl/ConstantEnumTemplate.java'),
       (1330101, 'grant_type', 'perm', '权限', '权限'),
       (1330102, 'grant_type', 'role', '角色', '角色');
