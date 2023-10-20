CREATE TABLE `sys_constant_enum` (
    `id`   INT(11)      NOT NULL COMMENT 'id: 9+ digits for dynamic, 8 digits for static, 3-2-2 (table-column-value) segments, SUPER end with 00',
    `type` VARCHAR(100) NOT NULL COMMENT 'enum group: same type for same enum, auto Pascal naming',
    `code` VARCHAR(100) NOT NULL COMMENT 'enum name: Fixed [code|id] for SUPER, external key, coding friendly',
    `hint` VARCHAR(100) NOT NULL COMMENT 'display message',
    `info` VARCHAR(500) NOT NULL COMMENT 'extended info: category, filter, template path for SUPER',
    PRIMARY KEY (`id`),
    UNIQUE INDEX uq_type_code (`type`, `code`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='105/Enum and Const: auto gen enum code';

CREATE TABLE `sys_standard_i18n` (
    `base` VARCHAR(100)  NOT NULL COMMENT 'table or package name',
    `kind` VARCHAR(100)  NOT NULL COMMENT 'column or class name',
    `ukey` VARCHAR(200)  NOT NULL COMMENT '[id.###|type.code|enum]',
    `lang` CHAR(5)       NOT NULL COMMENT 'lang tag:zh_CN',
    `hint` VARCHAR(3000) NOT NULL COMMENT 'display:Asia/Shanghai',
    PRIMARY KEY (`base`, `kind`, `ukey`, `lang`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='106/I18n Message';

--
INSERT IGNORE INTO `sys_light_sequence`(`seq_name`, `block_id`, `next_val`, `step_val`, `comments`)
VALUES ('sys_constant_enum', 0, 100000000, 100, 'system 9+ digits, manual 8 digits'),
       ('sys_standard_i18n', 0, 100000000, 100, 'system 9+ digits, manual 8 digits');

REPLACE INTO `sys_constant_enum` (`id`, `type`, `code`, `hint`, `info`)
VALUES (0, 'standard_boolean', 'false', 'false', 'false'),
       (1, 'standard_boolean', 'true', 'true', 'true');

-- the enum with the same type, id ending in 00 is the SUPER, code is the name.
REPLACE INTO `sys_constant_enum` (`id`, `type`, `code`, `hint`, `info`)
VALUES (1010100, 'standard_timezone', 'id', 'standard timezone', 'classpath:/wings-tmpl/StandardTimezoneTemplate.java'),
       (1010101, 'standard_timezone', 'GMT', 'Greenwich Mean Time (Zero)', ''),
       (1010201, 'standard_timezone', 'Asia/Shanghai', 'China: BeiJing, ShangHai, HongKong', 'China'),
       (1010301, 'standard_timezone', 'America/Chicago', 'CST: Chicago, Houston', 'USA'),
       (1010302, 'standard_timezone', 'America/Los_Angeles', 'PST: L.A., Seattle', 'USA'),
       (1010303, 'standard_timezone', 'America/New_York', 'EST: NewYork, D.C.', 'USA'),
       (1010304, 'standard_timezone', 'America/Phoenix', 'MST: Denver, Phoenix', 'USA'),
       (1010305, 'standard_timezone', 'US/Alaska', 'AKST: Alaska, Fairbanks', 'USA'),
       (1010306, 'standard_timezone', 'US/Hawaii', 'HST: Hawaii, Honolulu', 'USA'),
       (1010401, 'standard_timezone', 'Asia/Jakarta', 'Indonesia: Jakarta, Surabaya, Medan', 'Indonesia'),
       (1010402, 'standard_timezone', 'Asia/Jayapura', 'Indonesia: Jayapura, Manokwari', 'Indonesia'),
       (1010403, 'standard_timezone', 'Asia/Makassar', 'Indonesia: Makassar, Manado, Balikpapan', 'Indonesia'),
       (1010501, 'standard_timezone', 'Asia/Kuala_Lumpur', 'Malaysia: KualaLumpur', 'Malaysia'),
       (1010601, 'standard_timezone', 'Asia/Seoul', 'Korea: Seoul', 'Korea'),
       (1010701, 'standard_timezone', 'Asia/Singapore', 'Singapore', 'Singapore'),
       (1010801, 'standard_timezone', 'Asia/Tokyo', 'Japan: Tokyo', 'Japan'),
       (1010901, 'standard_timezone', 'Canada/Atlantic', 'AST: Halifax', 'Canada'),
       (1010902, 'standard_timezone', 'Canada/Central', 'CST: Winnipeg', 'Canada'),
       (1010903, 'standard_timezone', 'Canada/Eastern', 'EST: Toronto, Ottawa, Quebec', 'Canada'),
       (1010904, 'standard_timezone', 'Canada/Mountain', 'MST: Edmonton, Calgary', 'Canada'),
       (1010905, 'standard_timezone', 'Canada/Newfoundland', 'NST: St.John', 'Canada'),
       (1010906, 'standard_timezone', 'Canada/Pacific', 'PST: Vancouver', 'Canada'),

       (1020100, 'standard_language', 'code', 'standard language', 'classpath:/wings-tmpl/StandardLanguageTemplate.java'),
       (1020101, 'standard_language', 'ar_AE', 'Arabic', ''),
       (1020102, 'standard_language', 'de_DE', 'German', ''),
       (1020103, 'standard_language', 'en_US', 'English(US)', ''),
       (1020104, 'standard_language', 'es_ES', 'Spanish', ''),
       (1020105, 'standard_language', 'fr_FR', 'French', ''),
       (1020106, 'standard_language', 'it_IT', 'Italian', ''),
       (1020107, 'standard_language', 'ja_JP', 'Japanese', ''),
       (1020108, 'standard_language', 'ko_KR', 'Korean', ''),
       (1020109, 'standard_language', 'ru_RU', 'Russian', ''),
       (1020110, 'standard_language', 'th_TH', 'Thai', ''),
       (1020111, 'standard_language', 'zh_CN', 'Simplified Chinese', ''),
       (1020112, 'standard_language', 'zh_HK', 'Traditional Chinese', '');

-- https://24timezones.com/zh_clock/united_states_time.php
REPLACE INTO `sys_standard_i18n`(`base`, `kind`, `ukey`, `lang`, `hint`)
VALUES ('sys_constant_enum', 'hint', 'id.1010101', 'zh_CN', '格林威治时间(零时区)'),
       ('sys_constant_enum', 'hint', 'id.1010101', 'en_US', 'Greenwich Mean Time'),

       ('sys_constant_enum', 'hint', 'id.1010201', 'zh_CN', '北京时间：北京、上海、香港'),
       ('sys_constant_enum', 'hint', 'id.1010201', 'en_US', 'China: BeiJing, ShangHai, HongKong'),

       ('sys_constant_enum', 'hint', 'id.1010301', 'zh_CN', '中部时(CST)：芝加哥、休斯顿'),
       ('sys_constant_enum', 'hint', 'id.1010301', 'en_US', 'CST: Chicago, Houston'),

       ('sys_constant_enum', 'hint', 'id.1010302', 'zh_CN', '西部时间(PST)：西雅图、洛杉矶'),
       ('sys_constant_enum', 'hint', 'id.1010302', 'en_US', 'PST: L.A., Seattle'),

       ('sys_constant_enum', 'hint', 'id.1010303', 'zh_CN', '东部时(EST)：纽约、华盛顿'),
       ('sys_constant_enum', 'hint', 'id.1010303', 'en_US', 'EST: NewYork, D.C.'),

       ('sys_constant_enum', 'hint', 'id.1010304', 'zh_CN', '山地时(MST)：丹佛、凤凰城'),
       ('sys_constant_enum', 'hint', 'id.1010304', 'en_US', 'MST: Denver, Phoenix'),

       ('sys_constant_enum', 'hint', 'id.1010305', 'zh_CN', '阿拉斯加时间(AKST)：安克雷奇'),
       ('sys_constant_enum', 'hint', 'id.1010305', 'en_US', 'AKST: Alaska, Fairbanks'),

       ('sys_constant_enum', 'hint', 'id.1010306', 'zh_CN', '夏威夷时间(HST)：火鲁奴奴'),
       ('sys_constant_enum', 'hint', 'id.1010306', 'en_US', 'HST: Hawaii, Honolulu'),

       ('sys_constant_enum', 'hint', 'id.1010401', 'zh_CN', '印度尼西亚：雅加达、泗水、棉兰'),
       ('sys_constant_enum', 'hint', 'id.1010401', 'en_US', 'Indonesia: Jakarta, Surabaya, Medan'),

       ('sys_constant_enum', 'hint', 'id.1010402', 'zh_CN', '印度尼西亚：查亚普拉、马诺夸里'),
       ('sys_constant_enum', 'hint', 'id.1010402', 'en_US', 'Indonesia: Jayapura, Manokwari'),

       ('sys_constant_enum', 'hint', 'id.1010403', 'zh_CN', '印度尼西亚：望加锡、万鸦老、阿克'),
       ('sys_constant_enum', 'hint', 'id.1010403', 'en_US', 'Indonesia: Makassar, Manado, Balikpapan'),

       ('sys_constant_enum', 'hint', 'id.1010501', 'zh_CN', '马来西亚：吉隆坡'),
       ('sys_constant_enum', 'hint', 'id.1010501', 'en_US', 'Malaysia: KualaLumpur'),

       ('sys_constant_enum', 'hint', 'id.1010601', 'zh_CN', '韩国时间：首尔'),
       ('sys_constant_enum', 'hint', 'id.1010601', 'en_US', 'Korea: Seoul'),

       ('sys_constant_enum', 'hint', 'id.1010701', 'zh_CN', '新加坡时间'),
       ('sys_constant_enum', 'hint', 'id.1010701', 'en_US', 'Singapore'),

       ('sys_constant_enum', 'hint', 'id.1010801', 'zh_CN', '日本时间：东京'),
       ('sys_constant_enum', 'hint', 'id.1010801', 'en_US', 'Japan: Tokyo'),

       ('sys_constant_enum', 'hint', 'id.1010901', 'zh_CN', '大西洋时(AST)：哈利法克斯'),
       ('sys_constant_enum', 'hint', 'id.1010901', 'en_US', 'AST: Halifax'),

       ('sys_constant_enum', 'hint', 'id.1010902', 'zh_CN', '中部时(CST)：温尼伯'),
       ('sys_constant_enum', 'hint', 'id.1010902', 'en_US', 'CST: Winnipeg'),

       ('sys_constant_enum', 'hint', 'id.1010903', 'zh_CN', '东部时(EST)：多伦多、渥太华、魁北克城'),
       ('sys_constant_enum', 'hint', 'id.1010903', 'en_US', 'EST: Toronto, Ottawa, Quebec'),

       ('sys_constant_enum', 'hint', 'id.1010904', 'zh_CN', '山地时(MST)：埃德蒙顿、卡尔加里'),
       ('sys_constant_enum', 'hint', 'id.1010904', 'en_US', 'MST: Edmonton, Calgary'),

       ('sys_constant_enum', 'hint', 'id.1010905', 'zh_CN', '纽芬兰时(NST)：圣约翰斯'),
       ('sys_constant_enum', 'hint', 'id.1010905', 'en_US', 'NST: St.John'),

       ('sys_constant_enum', 'hint', 'id.1010906', 'zh_CN', '太平洋时(PST)：温哥华'),
       ('sys_constant_enum', 'hint', 'id.1010906', 'en_US', 'PST: Vancouver');

-- java.util.Locale#toLanguageTag
REPLACE INTO `sys_standard_i18n`(`base`, `kind`, `ukey`, `lang`, `hint`)
VALUES ('sys_constant_enum', 'hint', 'standard_language.zh_CN', 'zh_CN', '简体中文'),
       ('sys_constant_enum', 'hint', 'standard_language.zh_CN', 'en_US', 'Simplified Chinese'),

       ('sys_constant_enum', 'hint', 'standard_language.zh_HK', 'zh_HK', '繁體中文'),
       ('sys_constant_enum', 'hint', 'standard_language.zh_HK', 'zh_CN', '繁体中文'),
       ('sys_constant_enum', 'hint', 'standard_language.zh_HK', 'en_US', 'Traditional Chinese'),

       ('sys_constant_enum', 'hint', 'standard_language.ja_JP', 'ja_JP', '日本語'),
       ('sys_constant_enum', 'hint', 'standard_language.ja_JP', 'zh_CN', '日语'),
       ('sys_constant_enum', 'hint', 'standard_language.ja_JP', 'en_US', 'Japanese'),

       ('sys_constant_enum', 'hint', 'standard_language.ko_KR', 'ko_KR', '한국어'),
       ('sys_constant_enum', 'hint', 'standard_language.ko_KR', 'zh_CN', '韩语'),
       ('sys_constant_enum', 'hint', 'standard_language.ko_KR', 'en_US', 'Korean'),

       ('sys_constant_enum', 'hint', 'standard_language.ru_RU', 'ru_RU', 'русский язык'),
       ('sys_constant_enum', 'hint', 'standard_language.ru_RU', 'zh_CN', '俄语'),
       ('sys_constant_enum', 'hint', 'standard_language.ru_RU', 'en_US', 'Russian'),

       ('sys_constant_enum', 'hint', 'standard_language.de_DE', 'de_DE', 'Deutsch'),
       ('sys_constant_enum', 'hint', 'standard_language.de_DE', 'zh_CN', '德语'),
       ('sys_constant_enum', 'hint', 'standard_language.de_DE', 'en_US', 'German'),

       ('sys_constant_enum', 'hint', 'standard_language.es_ES', 'es_ES', 'Español'),
       ('sys_constant_enum', 'hint', 'standard_language.es_ES', 'zh_CN', '西班牙语'),
       ('sys_constant_enum', 'hint', 'standard_language.es_ES', 'en_US', 'Spanish'),

       ('sys_constant_enum', 'hint', 'standard_language.fr_FR', 'fr_FR', 'Français'),
       ('sys_constant_enum', 'hint', 'standard_language.fr_FR', 'zh_CN', '法语'),
       ('sys_constant_enum', 'hint', 'standard_language.fr_FR', 'en_US', 'French'),

       ('sys_constant_enum', 'hint', 'standard_language.it_IT', 'it_IT', 'Italiano'),
       ('sys_constant_enum', 'hint', 'standard_language.it_IT', 'zh_CN', '意大利语'),
       ('sys_constant_enum', 'hint', 'standard_language.it_IT', 'en_US', 'Italian'),

       ('sys_constant_enum', 'hint', 'standard_language.th_TH', 'th_TH', 'ภาษาไทย'),
       ('sys_constant_enum', 'hint', 'standard_language.th_TH', 'zh_CN', '泰国语'),
       ('sys_constant_enum', 'hint', 'standard_language.th_TH', 'en_US', 'Thai'),

       ('sys_constant_enum', 'hint', 'standard_language.ar_AE', 'ar_AE', 'عربي ،'),
       ('sys_constant_enum', 'hint', 'standard_language.ar_AE', 'zh_CN', '阿拉伯联合酋长国'),
       ('sys_constant_enum', 'hint', 'standard_language.ar_AE', 'en_US', 'Arabic'),

       ('sys_constant_enum', 'hint', 'standard_language.en_US', 'en_US', 'English(US)'),
       ('sys_constant_enum', 'hint', 'standard_language.en_US', 'zh_CN', '美国英语');
