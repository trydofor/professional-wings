ALTER TABLE `win_mail_sender`
    ADD COLUMN `lazy_bean` VARCHAR(300) NOT NULL DEFAULT '' COMMENT 'lazy bean to edit mail if mail_text is null' AFTER `mail_date`,
    ADD COLUMN `lazy_para` TEXT         NULL COMMENT 'lazy para of lazy bean' AFTER `lazy_bean`;

-- CALL FLYWAVE('2021-10-26v06-lazy-mail.sql');