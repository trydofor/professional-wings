package pro.fessional.wings.silencer.context;

import javax.validation.constraints.NotNull;
import java.time.ZoneId;
import java.util.Locale;
import java.util.TimeZone;

/**
 * @author trydofor
 * @since 2019-07-01
 */
public interface WingsI18nContext {

    @NotNull
    Locale getLocale();

    @NotNull
    TimeZone getTimeZone();

    @NotNull
    default ZoneId getZoneId() {
        return getTimeZone().toZoneId();
    }
}
