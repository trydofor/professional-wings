package pro.fessional.wings.faceless.database.helper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

/**
 * mysql服务器，程序，会话时区
 * https://dev.mysql.com/doc/refman/8.0/en/time-zone-support.html#time-zone-variables
 *
 * @author trydofor
 * @since 2021-04-14
 */
@Slf4j
public class TimezoneChecker {

    public static final String SQL = "SELECT @@system_time_zone,  @@global.time_zone, @@session.time_zone, NOW(), UTC_TIMESTAMP()";

    /**
     * 如果不一致，抛出 IllegalStateException
     *
     * @param ds jdbc template
     */
    public static void mysql(DataSource ds) {

        final JdbcTemplate tmpl = new JdbcTemplate(ds);
//        tmpl.execute("set TIME_ZONE ='America/New_York'");
        tmpl.query(SQL, rs -> {
            final StringBuilder sb = new StringBuilder();
            sb.append("\nsystem_time_zone=").append(rs.getString(1));
            sb.append("\nglobal.time_zone=").append(rs.getString(2));
            sb.append("\nsession.time_zone==").append(rs.getString(3));
            sb.append("\njvm-timezone=").append(ZoneId.systemDefault().getId());

            final long nowSys = rs.getTimestamp(4).getTime();
            final long nowUtc = rs.getTimestamp(5).getTime();
            final int sqlOff = (int) ((nowSys - nowUtc) / 1000);
            final int sysOff = ZonedDateTime.now().getOffset().getTotalSeconds();
            final int allOff = sqlOff - sysOff;
            sb.append("\nZoneOffset=").append(ZoneOffset.ofTotalSeconds(allOff));

            log.info(sb.substring(1).replace("\n", ", "));

            if (Math.abs(allOff) < 10) {
                sb.setLength(0);
                return;
            }

            sb.append("\n=== DIFF TIMEZONE===");
            sb.append("\nthe flowing can make session at same zone.");
            sb.append("\n - mysql server `default-time-zone = '+08:00'`");
            sb.append("\n - jdbc url `?serverTimezone=Asia/Shanghai`");
            sb.append("\n - wings conf `wings.silencer.i18n.zoneid=Asia/Shanghai`");
            sb.append("\n - java args `-Duser.timezone=Asia/Shanghai`");
            sb.append("\n - java code `TimeZone.setDefault(TimeZone.getTimeZone(\"Asia/Shanghai\"));`");

            throw new IllegalStateException(sb.toString());
        });
    }
}
