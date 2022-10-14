CREATE TABLE `sys_constant_enum` (
    `id`   INT(11)      NOT NULL COMMENT 'id:动态9位数起，静态8位以下，建议3-2-2分段（表-段-值）,00结尾为SUPER',
    `type` VARCHAR(100) NOT NULL COMMENT 'enum分组:相同type为同一Enum，自动Pascal命名',
    `code` VARCHAR(100) NOT NULL COMMENT 'enum名字:为SUPER时固定code|id，表示对外key,编码友好',
    `hint` VARCHAR(100) NOT NULL COMMENT '显示内容:线上',
    `info` VARCHAR(500) NOT NULL COMMENT '扩展信息:分类，过滤等，如果SUPER时，为模板Resource格式',
    PRIMARY KEY (`id`),
    UNIQUE INDEX uq_type_code (`type`, `code`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='105/常量枚举:自动生成enum类';

CREATE TABLE `sys_standard_i18n` (
    `base` VARCHAR(100)  NOT NULL COMMENT '基点:表名|包名',
    `kind` VARCHAR(100)  NOT NULL COMMENT '种类:列名|类名',
    `ukey` VARCHAR(200)  NOT NULL COMMENT '键值:id.###|type.code|enum',
    `lang` CHAR(5)       NOT NULL COMMENT '语言:下划线分隔:zh_CN',
    `hint` VARCHAR(3000) NOT NULL COMMENT '显示内容:中国/东北三省|攻城狮',
    PRIMARY KEY (`base`, `kind`, `ukey`, `lang`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='106/标准多国语';

--
INSERT IGNORE INTO `sys_light_sequence`(`seq_name`, `block_id`, `next_val`, `step_val`, `comments`)
VALUES ('sys_constant_enum', 0, 100000000, 100, '系统插入9位起，手动8位'),
       ('sys_standard_i18n', 0, 100000000, 100, '系统插入9位起，手动8位');

REPLACE INTO `sys_constant_enum` (`id`, `type`, `code`, `hint`, `info`)
VALUES (0, 'standard_boolean', 'false', 'false', 'false'),
       (1, 'standard_boolean', 'true', 'true', 'true');

-- type相同为同一enum，id以00结尾为SUPER元素，code为enum的name
REPLACE INTO `sys_constant_enum` (`id`, `type`, `code`, `hint`, `info`)
VALUES (1010100, 'standard_timezone', 'id', '标准时区', 'classpath:/wings-tmpl/StandardTimezoneTemplate.java'),
       (1010101, 'standard_timezone', 'GMT', '格林威治时间(零时区)', ''),
       (1010201, 'standard_timezone', 'Asia/Shanghai', '北京时间：北京、上海、香港', '中国'),
       (1010301, 'standard_timezone', 'America/Chicago', '中部时(CST)：芝加哥、休斯顿', '美国'),
       (1010302, 'standard_timezone', 'America/Los_Angeles', '西部时间(PST)：西雅图、洛杉矶', '美国'),
       (1010303, 'standard_timezone', 'America/New_York', '东部时(EST)：纽约、华盛顿', '美国'),
       (1010304, 'standard_timezone', 'America/Phoenix', '山地时(MST)：丹佛、凤凰城', '美国'),
       (1010305, 'standard_timezone', 'US/Alaska', '阿拉斯加时间(AKST)：安克雷奇', '美国'),
       (1010306, 'standard_timezone', 'US/Hawaii', '夏威夷时间(HST)：火鲁奴奴', '美国'),
       (1010401, 'standard_timezone', 'Asia/Jakarta', '雅加达、泗水、棉兰', '印度尼西亚'),
       (1010402, 'standard_timezone', 'Asia/Jayapura', '查亚普拉、马诺夸里', '印度尼西亚'),
       (1010403, 'standard_timezone', 'Asia/Makassar', '望加锡、万鸦老、阿克', '印度尼西亚'),
       (1010501, 'standard_timezone', 'Asia/Kuala_Lumpur', '马来西亚：吉隆坡', '马来西亚'),
       (1010601, 'standard_timezone', 'Asia/Seoul', '韩国时间：首尔', '韩国'),
       (1010701, 'standard_timezone', 'Asia/Singapore', '新加坡时间', '新加坡'),
       (1010801, 'standard_timezone', 'Asia/Tokyo', '日本时间：东京', '日本'),
       (1010901, 'standard_timezone', 'Canada/Atlantic', '大西洋时(AST)：哈利法克斯', '加拿大'),
       (1010902, 'standard_timezone', 'Canada/Central', '中部时(CST)：温尼伯', '加拿大'),
       (1010903, 'standard_timezone', 'Canada/Eastern', '东部时(EST)：多伦多、渥太华、魁北克城', '加拿大'),
       (1010904, 'standard_timezone', 'Canada/Mountain', '山地时(MST)：埃德蒙顿、卡尔加里', '加拿大'),
       (1010905, 'standard_timezone', 'Canada/Newfoundland', '纽芬兰时(NST)：圣约翰斯', '加拿大'),
       (1010906, 'standard_timezone', 'Canada/Pacific', '太平洋时(PST)：温哥华', '加拿大'),

       (1020100, 'standard_language', 'code', '标准语言', 'classpath:/wings-tmpl/StandardLanguageTemplate.java'),
       (1020101, 'standard_language', 'ar_AE', '阿拉伯联合酋长国', ''),
       (1020102, 'standard_language', 'de_DE', '德语', ''),
       (1020103, 'standard_language', 'en_US', '美国英语', ''),
       (1020104, 'standard_language', 'es_ES', '西班牙语', ''),
       (1020105, 'standard_language', 'fr_FR', '法语', ''),
       (1020106, 'standard_language', 'it_IT', '意大利语', ''),
       (1020107, 'standard_language', 'ja_JP', '日语', ''),
       (1020108, 'standard_language', 'ko_KR', '韩语', ''),
       (1020109, 'standard_language', 'ru_RU', '俄语', ''),
       (1020110, 'standard_language', 'th_TH', '泰国语', ''),
       (1020111, 'standard_language', 'zh_CN', '简体中文', ''),
       (1020112, 'standard_language', 'zh_HK', '繁体中文', '');

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
       ('sys_constant_enum', 'hint', 'id.1010401', 'en_US', 'Indonesia：Jakarta, Surabaya、Medan'),

       ('sys_constant_enum', 'hint', 'id.1010402', 'zh_CN', '印度尼西亚：查亚普拉、马诺夸里'),
       ('sys_constant_enum', 'hint', 'id.1010402', 'en_US', 'Indonesia：Jayapura、Manokwari'),

       ('sys_constant_enum', 'hint', 'id.1010403', 'zh_CN', '印度尼西亚：望加锡、万鸦老、阿克'),
       ('sys_constant_enum', 'hint', 'id.1010403', 'en_US', 'Indonesia：Makassar、Manado、Balikpapan'),

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
       ('sys_constant_enum', 'hint', 'standard_language.fr_FR', 'en_US', 'Franch'),

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
