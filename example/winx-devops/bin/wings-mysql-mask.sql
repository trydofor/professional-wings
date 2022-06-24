-- run mode
UPDATE `win_conf_runtime`
SET `current` = 'Local'
WHERE `key` = 'pro.fessional.wings.warlock.service.conf.mode.RunMode';

UPDATE `win_conf_runtime`
SET `current` = 'Nothing'
WHERE `key` = 'pro.fessional.wings.warlock.service.conf.mode.ApiMode';

-- user
UPDATE `win_user_basis`
SET `nickname` = CONCAT('mask-', `id`)
WHERE `id` > 1000;

-- auth
UPDATE `win_user_authn`
SET `password` = '{noop-md5}Make-DevOps-Great-Again!'
WHERE `user_id` > 1000;
