package pro.fessional.wings.silencer.datetime;

import java.time.ZoneId;
import java.util.TimeZone;

/**
 * @author trydofor
 * @since 2019-09-01
 */
public class TimeZoneDefault {
    public static final TimeZone TIME_ZONE = TimeZone.getDefault();
    public static final ZoneId ZONE_ID = ZoneId.systemDefault();
}
