-- @plain apply@win_auth_role[_0-9]* error@skip
ALTER TABLE `win_auth_role`
  DROP INDEX `ft_auth_set`;

-- @plain apply@win_user[_0-9]* error@skip
ALTER TABLE `win_user`
  DROP INDEX `ft_auth_set`,
  DROP INDEX `ft_role_set`;

-- @plain apply@win_user_login[_0-9]* error@skip
ALTER TABLE `win_user_login`
  DROP INDEX `ix_user_id`,
  DROP INDEX `uq_login_type_name`;