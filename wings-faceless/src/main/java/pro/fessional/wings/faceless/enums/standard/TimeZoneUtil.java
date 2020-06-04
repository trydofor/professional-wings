package pro.fessional.wings.faceless.enums.standard;

import pro.fessional.mirana.i18n.ZoneIdResolver;
import pro.fessional.wings.silencer.context.WingsI18nContext;

import javax.validation.constraints.NotNull;
import java.time.ZoneId;

/**
 * @author trydofor
 * @since 2019-10-16
 */
public class TimeZoneUtil {

    public static StandardTimezone DEFAULT_STZ = StandardTimezone.CN_Shanghai;

    public static StandardTimezone getDefaultStandardTimezone() {
        return DEFAULT_STZ;
    }

    public static void setDefaultStandardTimezone(StandardTimezone defaultStz) {
        DEFAULT_STZ = defaultStz;
    }

    @NotNull
    public static StandardTimezone of(String code) {
        ZoneId zid = ZoneIdResolver.zoneId(code);
        return of(zid);
    }

    @NotNull
    public static StandardTimezone of(String code, WingsI18nContext i18nContext) {
        ZoneId zid = ZoneIdResolver.zoneId(code);
        return of(zid, i18nContext);
    }

    @NotNull
    public static StandardTimezone of(Long id) {
        if (id != null) {
            long il = id;
            for (StandardTimezone e : StandardTimezone.values()) {
                if (e.getId() == il) {
                    return e;
                }
            }
        }
        return DEFAULT_STZ;
    }

    @NotNull
    public static StandardTimezone of(Long id, WingsI18nContext i18nContext) {
        if (id != null) {
            long il = id;
            for (StandardTimezone e : StandardTimezone.values()) {
                if (e.getId() == il) {
                    return e;
                }
            }
        }
        return of(i18nContext);
    }

    @NotNull
    public static StandardTimezone of(ZoneId zid, WingsI18nContext i18nContext) {
        for (StandardTimezone e : StandardTimezone.values()) {
            if (e.getZoneId().equals(zid)) {
                return e;
            }
        }
        return of(i18nContext);
    }

    @NotNull
    public static StandardTimezone of(WingsI18nContext i18nContext) {
        ZoneId zid = i18nContext.getZoneIdOrDefault();
        for (StandardTimezone e : StandardTimezone.values()) {
            if (e.getZoneId().equals(zid)) {
                return e;
            }
        }
        return DEFAULT_STZ;
    }

    @NotNull
    public static StandardTimezone of(ZoneId zid) {
        for (StandardTimezone e : StandardTimezone.values()) {
            if (e.getZoneId().equals(zid)) {
                return e;
            }
        }
        return DEFAULT_STZ;
    }
}
