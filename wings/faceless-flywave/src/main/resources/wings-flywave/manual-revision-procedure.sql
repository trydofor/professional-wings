-- drop
DROP PROCEDURE IF EXISTS FLYWAVE;

-- create
DELIMITER $$
CREATE PROCEDURE FLYWAVE(IN filename VARCHAR(50))
BEGIN
    DECLARE revi VARCHAR(20);
    SET revi = REGEXP_REPLACE(REGEXP_SUBSTR(filename, '[-_0-9]{8,}[uv][0-9]{2,}', 1, 1, 'i'), '[^0-9]', '');
    IF REGEXP_LIKE(filename, '[-_0-9]{8,}[v][0-9]{2,}','i') = 1 THEN
        INSERT INTO `sys_schema_version` (`revision`, `apply_dt`, `comments`, `commit_id`, `upto_sql`, `undo_sql`)
        VALUES (revi, NOW(3), filename, 0, '', '')
        ON DUPLICATE KEY UPDATE `apply_dt` = NOW(3);
    ELSE
        UPDATE `sys_schema_version`
        SET `apply_dt` = '1000-01-01',
            `modify_dt`= NOW(3)
        WHERE `revision` = revi;
    END IF;
END$$
DELIMITER ;
