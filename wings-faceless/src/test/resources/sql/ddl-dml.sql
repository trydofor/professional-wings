-- tx_operator@plain
DROP TRIGGER IF EXISTS `tx_operator$log$bu`;

-- 每个分表单独一个log
DELIMITER $$
CREATE TRIGGER `tx_operator$log$bu` BEFORE UPDATE ON `tx_operator`
  FOR EACH ROW BEGIN
  insert into `tx_operator$log` select *, null, 1, now() from `tx_operator` where id= OLD.id;
END $$
DELIMITER ;

-- 所有分表共用一个log
DELIMITER $$
CREATE TRIGGER `tx_operator$log$bu` BEFORE UPDATE ON `tx_operator`
  FOR EACH ROW BEGIN
  insert into `tx_operator_log` select *, null, 1, now() from `tx_operator` where id= OLD.id;
END $$
DELIMITER ;

ALTER TABLE `tx_operator`
  ADD INDEX `ix_password_operator` (`password` ASC,`operator` ASC);

SELECT max(version) FROM sys_schema_version;

REPLACE INTO sys_schema_version (version, created) VALUES (2019031201, NOW());

INSERT IGNORE INTO `tx_sequence` (`table_name`, `id`) VALUES ('tx_sequence', 1);

ALTER TABLE `tx_wanniansong_record`
  MODIFY airway_bill VARCHAR(500) DEFAULT NULL COMMENT 'tx_wanniansong_record';

-- tx_finance_data_import@plan
DROP TABLE IF EXISTS `tx_finance_data_import$log` ;
