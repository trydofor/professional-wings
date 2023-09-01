REPLACE INTO `sys_constant_enum` (`id`, `type`, `code`, `hint`, `info`)
VALUES (1200200, 'user_status', 'user_status', 'user status', 'classpath:/wings-tmpl/ConstantEnumTemplate.java'),
       (1200201, 'user_status', 'uninit', 'uninitialized', 'created but incomplete'),
       (1200202, 'user_status', 'active', 'normal account', 'normal account'),
       (1200203, 'user_status', 'infirm', 'weak account', 'weak or expired password'),
       (1200204, 'user_status', 'unsafe', 'unsafe account', 'suspicious or frequent operation'),
       (1200205, 'user_status', 'danger', 'danger account', 'deny login, eg. too much fail'),
       (1200206, 'user_status', 'frozen', 'frozen account', 'deny login, eg. funds in danger'),
       (1200207, 'user_status', 'locked', 'locked account', 'deny login, manually locked'),
       (1200208, 'user_status', 'closed', 'closed account', 'deny login, manually closed'),
       (1200299, 'user_status', 'hidden', 'hidden account', 'deny login, special purpose'),

       (1330100, 'grant_type', 'grant_type', 'grant type', 'classpath:/wings-tmpl/ConstantEnumTemplate.java'),
       (1330101, 'grant_type', 'perm', 'permit', 'permit'),
       (1330102, 'grant_type', 'role', 'role', 'role');
