INSERT IGNORE INTO `win_user_basis` (`id`, `create_dt`, `commit_id`, `nickname`, `passsalt`, `gender`, `avatar`, `locale`, `zoneid`, `remark`, `status`)
VALUES (5, NOW(3), 0, 'test_ny', UUID(), 1200103, '', 'en_US', 1010303, 'NY用户', 1200202);

INSERT IGNORE INTO `win_user_anthn`(`id`, `create_dt`, `commit_id`, `user_id`, `auth_type`, `username`, `password`, `expired_dt`)
VALUES (5, NOW(3), 0, 5, 'username', 'test_ny', CONCAT('{never}', UUID()), '2999-09-09');
