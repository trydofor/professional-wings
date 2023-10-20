-- Spelling mistakes, very hard to tolerate
ALTER TABLE `win_user_anthn` RENAME TO `win_user_authn`;

update `sys_light_sequence` set `seq_name`='win_user_authn' where `seq_name`='win_user_anthn';
