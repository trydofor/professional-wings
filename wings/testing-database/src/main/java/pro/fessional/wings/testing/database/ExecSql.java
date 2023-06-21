package pro.fessional.wings.testing.database;

import org.springframework.jdbc.core.JdbcTemplate;
import pro.fessional.mirana.io.InputStreams;

/**
 * @author trydofor
 * @since 2020-12-28
 */
public class ExecSql {

    public static void execWingsSql(JdbcTemplate jdbcTemplate, String path) {
        String sqls = InputStreams.readText(ExecSql.class.getResourceAsStream("/wings-flywave/" + path));
        for (String sql : sqls.split(
                ";+[ \\t]*[\\r\\n]+"
                + "|"
                + ";+[ \\t]*--[^\\r\\n]+[\\r\\n]+"
                + "|"
                + ";+[ \\t]*/\\*[^\\r\\n]+\\*/[ \\t]*[\\r\\n]+"
        )) {
            String s = sql.trim();
            if (!s.isEmpty()) {
                jdbcTemplate.execute(s);
            }
        }
    }
}
