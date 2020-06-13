package pro.fessional.wings.faceless.enums;

import java.time.ZoneId;
import java.util.TimeZone;

/**
 * @author trydofor
 * @since 2020-06-11
 */
public interface StandardTimezoneEnum extends ConstantEnum, StandardI18nEnum {

    TimeZone toTimeZone();

    ZoneId toZoneId();
}
