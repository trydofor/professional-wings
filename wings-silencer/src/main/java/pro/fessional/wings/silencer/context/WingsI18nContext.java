package pro.fessional.wings.silencer.context;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.ZoneId;
import java.util.Locale;
import java.util.TimeZone;

/**
 * @author trydofor
 * @since 2019-07-01
 */
public interface WingsI18nContext {

    WingsI18nContext NULL = new WingsI18nContext() {
        @Nullable
        @Override
        public Locale getLocale() {
            return null;
        }

        @Nullable
        @Override
        public TimeZone getTimeZone() {
            return null;
        }
    };

    @Nullable
    Locale getLocale();

    @Nullable
    TimeZone getTimeZone();

    @Nullable
    default ZoneId getZoneId() {
        TimeZone t = getTimeZone();
        return t == null ? null : t.toZoneId();
    }

    @NotNull
    default Locale getLocaleOrDefault() {
        Locale l = getLocale();
        return l == null ? Locale.getDefault() : l;
    }

    @NotNull
    default TimeZone getTimeZoneOrDefault() {
        TimeZone t = getTimeZone();
        return t == null ? TimeZone.getDefault() : t;
    }

    @NotNull
    default ZoneId getZoneIdOrDefault() {
        ZoneId z = getZoneId();
        return z == null ? ZoneId.systemDefault() : z;
    }

    /**
     * @param locale null表示remove
     */
    default void setLocale(Locale locale) {
    }

    /**
     * @param timeZone null表示remove
     */
    default void setTimeZone(TimeZone timeZone) {
    }
}
