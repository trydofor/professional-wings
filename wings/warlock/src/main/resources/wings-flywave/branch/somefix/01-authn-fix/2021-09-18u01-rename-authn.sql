-- error@skip
ALTER TABLE `win_user_authn` RENAME TO `win_user_anthn`;

update `sys_light_sequence` set `seq_name`='win_user_anthn' where `seq_name`='win_user_authn';

-- CALL FLYWAVE('2021-09-18u01-rename-authn.sql');