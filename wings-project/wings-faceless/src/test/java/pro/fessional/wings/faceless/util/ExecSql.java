package pro.fessional.wings.faceless.util;

import org.springframework.jdbc.core.JdbcTemplate;
import pro.fessional.mirana.io.InputStreams;

/**
 * @author trydofor
 * @since 2020-12-28
 */
public class ExecSql {

    public static void execWingsSql(JdbcTemplate jdbcTemplate, String path) {
        String sqls = InputStreams.readText(ExecSql.class.getResourceAsStream("/wings-flywave/"+path));
        for (String sql : sqls.split(";[ \t]*[\r\n]")) {
            jdbcTemplate.execute(sql);
        }
    }
}
