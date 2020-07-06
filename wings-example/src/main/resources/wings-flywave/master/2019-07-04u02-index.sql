-- @plain apply@nut error@skip
ALTER TABLE `win_auth_role`
  DROP INDEX `ft_auth_set`;

-- @plain apply@nut error@skip
ALTER TABLE `win_user`
  DROP INDEX `ft_auth_set`,
  DROP INDEX `ft_role_set`;

-- @plain apply@nut error@skip
ALTER TABLE `win_user_login`
  DROP INDEX `ix_user_id`,
  DROP INDEX `uq_login_type_name`;