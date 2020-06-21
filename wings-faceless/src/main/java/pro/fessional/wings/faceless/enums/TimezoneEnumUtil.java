package pro.fessional.wings.faceless.enums;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pro.fessional.mirana.i18n.ZoneIdResolver;

import java.time.ZoneId;

/**
 * @author trydofor
 * @since 2020-06-11
 */
public class TimezoneEnumUtil extends ConstantEnumUtil {

    @SafeVarargs
    @Nullable
    public static <T extends StandardTimezoneEnum> T zoneIdOrNull(String zoneId, T... es) {
        if (zoneId == null || es == null || es.length == 0) return null;
        return zoneIdOrNull(ZoneIdResolver.zoneId(zoneId), es);
    }

    @SafeVarargs
    @Nullable
    public static <T extends StandardTimezoneEnum> T zoneIdOrNull(ZoneId zoneId, T... es) {
        if (zoneId == null || es == null || es.length == 0) return null;

        String zidStr = zoneId.getId();
        for (T e : es) {
            if (e.toZoneId().getId().equalsIgnoreCase(zidStr)) {
                return e;
            }
        }

        return null;
    }

    @SafeVarargs
    @NotNull
    public static <T extends StandardTimezoneEnum> T zoneIdOrThrow(String zoneId, T... es) {
        T t = zoneIdOrNull(zoneId, es);
        if (t == null) {
            throw new IllegalArgumentException("can not found StandardTimezoneEnum by zoneId=" + zoneId);
        } else {
            return t;
        }
    }

    @SafeVarargs
    @NotNull
    public static <T extends StandardTimezoneEnum> T zoneIdOrThrow(ZoneId zoneId, T... es) {
        T t = zoneIdOrNull(zoneId, es);
        if (t == null) {
            throw new IllegalArgumentException("can not found StandardTimezoneEnum by zoneId=" + zoneId);
        } else {
            return t;
        }
    }

    @SafeVarargs
    @NotNull
    public static <T extends StandardTimezoneEnum> T zoneIdOrHint(String zoneId, String hint, T... es) {
        T t = zoneIdOrNull(zoneId, es);
        if (t == null) {
            throw new IllegalArgumentException(hint);
        } else {
            return t;
        }
    }

    @SafeVarargs
    @NotNull
    public static <T extends StandardTimezoneEnum> T zoneIdOrHint(ZoneId zoneId, String hint, T... es) {
        T t = zoneIdOrNull(zoneId, es);
        if (t == null) {
            throw new IllegalArgumentException(hint);
        } else {
            return t;
        }
    }

    @SafeVarargs
    @NotNull
    public static <T extends StandardTimezoneEnum> T zoneIdOrElse(String zoneId, T el, T... es) {
        T t = zoneIdOrNull(zoneId, es);
        return t == null ? el : t;
    }

    @SafeVarargs
    @NotNull
    public static <T extends StandardTimezoneEnum> T zoneIdOrElse(ZoneId zoneId, T el, T... es) {
        T t = zoneIdOrNull(zoneId, es);
        return t == null ? el : t;
    }
}
