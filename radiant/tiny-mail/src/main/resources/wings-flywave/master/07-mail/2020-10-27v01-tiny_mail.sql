CREATE TABLE `win_mail_sender` (
    `id`         BIGINT(20)   NOT NULL COMMENT 'primary key/mail_id',
    `create_dt`  DATETIME(3)  NOT NULL DEFAULT NOW(3) COMMENT 'created datetime(sys)',
    `modify_dt`  DATETIME(3)  NOT NULL DEFAULT '1000-01-01' ON UPDATE NOW(3) COMMENT 'modified datetime(sys)',
    `delete_dt`  DATETIME(3)  NOT NULL DEFAULT '1000-01-01' COMMENT 'logic deleted datetime',
    `commit_id`  BIGINT(20)   NOT NULL COMMENT 'commit id',
    `mail_apps`  VARCHAR(500) NOT NULL DEFAULT '' COMMENT 'belong to applications, comma-separated, default spring.application.name',
    `mail_runs`  VARCHAR(100) NOT NULL DEFAULT '' COMMENT 'RunMode(product|test|develop|local), comma-separated case-insensitive, default all',
    `mail_conf`  VARCHAR(100) NOT NULL DEFAULT '' COMMENT 'config name, default',
    `mail_from`  VARCHAR(200) NOT NULL DEFAULT '' COMMENT 'mail from (sender)',
    `mail_to`    VARCHAR(900) NOT NULL DEFAULT '' COMMENT 'mail to, comma-separated',
    `mail_cc`    VARCHAR(900) NOT NULL DEFAULT '' COMMENT 'mail cc, comma-separated',
    `mail_bcc`   VARCHAR(900) NOT NULL DEFAULT '' COMMENT 'mail bcc, comma-separated',
    `mail_reply` VARCHAR(200) NOT NULL DEFAULT '' COMMENT 'mail reply',
    `mail_subj`  VARCHAR(400) NOT NULL DEFAULT '' COMMENT 'mail subject',
    `mail_text`  TEXT         NULL COMMENT 'mail content',
    `mail_html`  TINYINT(1)   NOT NULL DEFAULT '1' COMMENT 'whether HTML email',
    `mail_file`  TEXT         NULL COMMENT 'attachment name and path map, json format',
    `mail_mark`  VARCHAR(900) NOT NULL DEFAULT '' COMMENT 'business key to lookup',
    `mail_date`  DATETIME(3)  NOT NULL DEFAULT '1000-01-01' COMMENT 'scheduled mail send (sys)',
    `lazy_bean`  VARCHAR(300) NOT NULL DEFAULT '' COMMENT 'lazy bean to edit mail if mail_text is null',
    `lazy_para`  TEXT         NULL COMMENT 'lazy para of lazy bean',
    `last_send`  DATETIME(3)  NOT NULL DEFAULT '1000-01-01' COMMENT 'previous send (sys)',
    `last_fail`  TEXT         NULL COMMENT 'previous fail info',
    `last_done`  DATETIME(3)  NOT NULL DEFAULT '1000-01-01' COMMENT 'previous success (sys)',
    `last_cost`  INT(11)      NOT NULL DEFAULT '0' COMMENT 'mills of previous send cost',
    `next_send`  DATETIME(3)  NOT NULL DEFAULT '1000-01-01' COMMENT 'next send datetime (sys)',
    `next_lock`  INT(11)      NOT NULL DEFAULT '0' COMMENT 'optimistic lock of sending',
    `sum_send`   INT(11)      NOT NULL DEFAULT '0' COMMENT 'total count of send',
    `sum_fail`   INT(11)      NOT NULL DEFAULT '0' COMMENT 'total count of fail',
    `sum_done`   INT(11)      NOT NULL DEFAULT '0' COMMENT 'total count of success',
    `max_fail`   INT(11)      NOT NULL DEFAULT '0' COMMENT 'max count of fail, 0 means use the config',
    `max_done`   INT(11)      NOT NULL DEFAULT '0' COMMENT 'max count of success, 0 means use the config',
    `ref_type`   INT(11)      NOT NULL DEFAULT '0' COMMENT 'ref type indicate key1, key2 usage',
    `ref_key1`   BIGINT(20)   NOT NULL DEFAULT '0' COMMENT 'ref key1, generally the primary key',
    `ref_key2`   VARCHAR(500) NOT NULL DEFAULT '' COMMENT 'ref key2, generally the composite data',
    PRIMARY KEY (`id`),
    INDEX ix_next_send (`next_send`),
    INDEX ix_sum_done (`sum_done`),
    FULLTEXT ft_mail_mark (`mail_mark`),
    INDEX ix_ref_type (`ref_type`),
    INDEX ix_ref_key1 (`ref_key1`),
    INDEX ix_ref_key2 (`ref_key2`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='143/Mail Sending';

-- CALL FLYWAVE('2020-10-27v01-tiny_mail.sql');