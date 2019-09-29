ALTER TABLE `tst_auth_role`
  ADD FULLTEXT INDEX `ft_auth_set` (`auth_set`);

ALTER TABLE `tst_user`
  ADD FULLTEXT INDEX `ft_auth_set` (`auth_set`);
ALTER TABLE `tst_user`
  ADD FULLTEXT INDEX `ft_role_set` (`role_set`);

ALTER TABLE `tst_user_login`
  ADD INDEX `ix_user_id` (`user_id`),
  ADD UNIQUE INDEX `uq_login_type_name` (`login_type`, `login_name`);