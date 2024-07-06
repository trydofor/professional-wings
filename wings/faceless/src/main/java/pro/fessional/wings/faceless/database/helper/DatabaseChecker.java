package pro.fessional.wings.faceless.database.helper;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.JdbcUtils;
import pro.fessional.mirana.best.DummyBlock;
import pro.fessional.mirana.time.DateFormatter;
import pro.fessional.mirana.time.DateParser;
import pro.fessional.wings.faceless.database.DataSourceContext;
import pro.fessional.wings.silencer.spring.help.ApplicationContextHelper;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.TimeZone;

import static java.time.ZoneOffset.UTC;

/**
 * check the timezone between mysql server, application, and jdbc session.
 * see <a href="https://dev.mysql.com/doc/refman/8.0/en/time-zone-support.html#time-zone-variables">time-zone-variables</a>
 *
 * @author trydofor
 * @since 2021-04-14
 */
@Slf4j
public class DatabaseChecker {

    /**
     * Whether in H2database
     */
    public static boolean isH2(DataSource ds) {
        return extractJdbcUrl(ds).contains(":h2:");
    }

    /**
     * Extract the jdbc-url from the datasource
     */
    @SneakyThrows
    @NotNull
    public static String extractJdbcUrl(DataSource ds) {
        return JdbcUtils.extractDatabaseMetaData(ds, it -> {
            try {
                return (String) DatabaseMetaData.class.getMethod("getURL").invoke(it);
            }
            catch (Exception e) {
                log.warn("failed to get jdbcUrl", e);
                return "";
            }
        });
    }

    /**
     * Check timezone with off=5, fail=true
     *
     * @see #timezone(DataSource, int, boolean)
     */
    public static void timezone(DataSource ds) {
        timezone(ds, 5, true);
    }

    /**
     * Check the database and jvm time difference,
     * in the absolute `off` second, throw or log as ERROR if `fail`.
     *
     * @param ds   datasource
     * @param off  max abs time tolerance in second
     * @param fail throw IllegalStateException or log.ERROR
     */
    public static void timezone(DataSource ds, int off, boolean fail) {
        if (isH2(ds)) {
            log.info("skip timezone check for h2database");
            return;
        }

        final String epo = "1979-01-01 00:00:00";
        final LocalDateTime ldt = DateParser.parseDateTime(epo);
        final String sql = "SELECT @@system_time_zone,  @@global.time_zone, @@session.time_zone,"
                           + " NOW()," // db now vs jvm now
                           + " TIMESTAMPDIFF(SECOND, ?, '" + epo + "')" // sql-string vs prepared
                           + " FROM dual";

        final JdbcTemplate tmpl = new JdbcTemplate(ds);

        tmpl.query(sql, rs -> {
            final LocalDateTime jvNow = LocalDateTime.now();
            final StringBuilder sb = new StringBuilder();
            final String dbStz = rs.getString(1);
            final String dbGtz = rs.getString(2);
            final String dbNtz = rs.getString(3);
            final LocalDateTime dbNow = DateParser.parseDateTime(rs.getString(4));
            final int dbOff = rs.getInt(5);
            final int jvOff = (int) (jvNow.toEpochSecond(UTC) - dbNow.toEpochSecond(UTC));

            sb.append("\ndb-system_time_zone=").append(dbStz);
            sb.append("\ndb-global.time_zone=").append(dbGtz);

            sb.append("\ndb-session.time_zone=").append(dbNtz);
            sb.append("\njvm-default-timezone=").append(ZoneOffset.ofTotalSeconds(TimeZone.getDefault().getRawOffset() / 1000))
              .append(", ").append(ZoneId.systemDefault().getId());

            sb.append("\ndb-session.now=").append(DateFormatter.full19(dbNow));
            sb.append("\njvm-local.now =").append(DateFormatter.full19(jvNow));

            sb.append("\ndb-zone-offset =").append(ZoneOffset.ofTotalSeconds(dbOff));
            sb.append("\njvm-zone-offset=").append(ZoneOffset.ofTotalSeconds(jvOff));

            if (Math.abs(jvOff) <= Math.abs(off) && Math.abs(dbOff) <= Math.abs(off)) {
                for (String s : sb.substring(1).split("\n")) {
                    log.info(s);
                }
                return;
            }

            sb.append("\n=== DIFF TIMEZONE ===");
            sb.append("\nthe flowing can make session at same zone. be careful of Daylight Saving Time");
            sb.append("\n- mysql server `default-time-zone = '+08:00'`");
            sb.append("\n- jdbc url `?connectionTimeZone=%2B08:00&forceConnectionTimeZoneToSession=true`");
            sb.append("\n- wings conf `wings.silencer.i18n.zoneid=Asia/Shanghai`");
            sb.append("\n- wings stater.sh `TIME_ZID=Asia/Shanghai`");
            sb.append("\n- java args `-Duser.timezone=Asia/Shanghai`");
            sb.append("\n- java code `TimeZone.setDefault(TimeZone.getTimeZone(\"Asia/Shanghai\"));`");
            sb.append("\n- https://dev.mysql.com/doc/connector-j/8.0/en/connector-j-reference-configuration-properties.html");

            if (fail) {
                throw new IllegalStateException(sb.toString());
            }
            else {
                log.error(sb.toString());
            }
        }, Timestamp.valueOf(ldt));
    }

    /**
     * output the database version in the log, return flywave revision if found
     */
    public static long version(DataSource ds) {
        final JdbcTemplate tmpl = new JdbcTemplate(ds);

        log.info("jdbcurl={}", DataSourceContext.extractUrl(ds));
        final String ver = isH2(ds) ? "H2VERSION()" : "VERSION()";
        tmpl.query("SELECT " + ver + " FROM dual", rs -> {
            log.info("database {}={}", ver, rs.getString(1));
        });

        String reviTable = null;
        try {
            reviTable = ApplicationContextHelper.getProperties("wings.faceless.flywave.ver.schema-version-table");
        }
        catch (Exception e) {
            DummyBlock.ignore(e);
        }
        if (StringUtils.isBlank(reviTable)) {
            reviTable = "sys_schema_version";
        }

        String sql = "SELECT MAX(revision) FROM " + JdbcTemplateHelper.safeName(reviTable) + " WHERE apply_dt > '1111-11-11'";
        Long rev = null;
        try {
            rev = tmpl.query(sql, JdbcTemplateHelper.FirstLongOrNull);
        }
        catch (DataAccessException e) {
            log.info("flywave revision is unknown, for no sys_schema_version");
        }
        log.info("flywave revision={}", rev);
        return rev == null ? -1 : rev;
    }

    /**
     * Whether the table exist, need close the `conn` manually.
     * `SELECT 1 FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME=? AND TABLE_SCHEMA=SCHEMA()`
     * <a href="https://www.jooq.org/doc/latest/manual/sql-building/column-expressions/system-functions/current-schema-function/">current-schema-function</a>
     */
    @SneakyThrows
    public static boolean existTable(Connection conn, String table) {
        final String sql = "SELECT 1 FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME=? AND TABLE_SCHEMA=SCHEMA()";
        try (PreparedStatement stm = conn.prepareStatement(sql)) {
            stm.setString(1, table);
            final ResultSet rs = stm.executeQuery();
            return rs.next();
        }
    }
}
