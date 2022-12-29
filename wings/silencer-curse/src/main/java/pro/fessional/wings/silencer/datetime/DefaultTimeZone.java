package pro.fessional.wings.silencer.datetime;

import java.time.ZoneId;
import java.util.TimeZone;

/**
 * @author trydofor
 * @since 2019-09-01
 */
public class DefaultTimeZone {
    public static final TimeZone TZ_SYS = TimeZone.getDefault();
    public static final ZoneId ZID_SYS = ZoneId.systemDefault();

    public static final TimeZone TZ_UTC = TimeZone.getTimeZone("UTC");
    public static final ZoneId ZID_UTC = ZoneId.of("UTC");

    public static final TimeZone TZ_GMT = TimeZone.getTimeZone("GMT");
    public static final ZoneId ZID_GMT = ZoneId.of("GMT");
}
