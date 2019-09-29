ALTER TABLE `tst_auth_role`
  DROP INDEX `ft_auth_set`;

ALTER TABLE `tst_user`
  DROP INDEX `ft_auth_set`,
  DROP INDEX `ft_role_set`;

ALTER TABLE `tst_user_login`
  DROP INDEX `ix_user_id`,
  DROP INDEX `uq_login_type_name`;