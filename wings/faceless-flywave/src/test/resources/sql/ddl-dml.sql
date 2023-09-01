-- tx_operator@plain
DROP TRIGGER IF EXISTS `bu__tx_operator__`; -- 909/Test ending comments

-- separate logs for each shard table
DELIMITER ;;
CREATE TRIGGER `bu__tx_operator__`
  BEFORE UPDATE
  ON `tx_operator`
  FOR EACH ROW
BEGIN
  INSERT INTO `tx_operator__` SELECT *, NULL, 1, now() FROM `tx_operator` WHERE id = OLD.id;
END
;;
DELIMITER ;

-- share the same log for all shard table
DELIMITER ;;
CREATE TRIGGER `bu__tx_operator`
  BEFORE UPDATE
  ON `tx_operator`
  FOR EACH ROW
BEGIN
  INSERT INTO `tx_operator_log` SELECT *, NULL, 1, now() FROM `tx_operator` WHERE id = OLD.id;
END;;
DELIMITER ;

ALTER TABLE `tx_operator`
  ADD INDEX `ix_password_operator` (`password` ASC, `operator` ASC);

SELECT max(version)
FROM sys_schema_version; /* test comments */

REPLACE INTO sys_schema_version (version, created)
VALUES (2019031201, NOW(3));  /* test comments */;

INSERT IGNORE INTO `tx_sequence` (`table_name`, `id`)
VALUES ('tx_sequence', 1);

ALTER TABLE `tx_wanniansong_record`
  MODIFY airway_bill VARCHAR(500) DEFAULT NULL COMMENT 'delimiter ;;; in comments';

-- tx_finance_data_import@plan
DROP TABLE IF EXISTS `tx_finance_data_import__`;
