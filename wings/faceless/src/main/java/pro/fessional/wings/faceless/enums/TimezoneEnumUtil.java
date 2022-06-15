package pro.fessional.wings.faceless.enums;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pro.fessional.mirana.i18n.ZoneIdResolver;

import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

/**
 * @author trydofor
 * @since 2020-06-11
 */
public class TimezoneEnumUtil extends ConstantEnumUtil {

    private static final Map<Integer, ZoneId> ZoneIdMap = new HashMap<>();

    public static void register(StandardTimezoneEnum en) {
        final ZoneId neu = en.toZoneId();
        final ZoneId old = ZoneIdMap.put(en.getId(), neu);
        if (old != null && !old.equals(neu)) {
            throw new IllegalArgumentException("need only one zoneId. old=" + old.getId() + ", new=" + neu.getId() + ", id=" + en.getId());
        }
    }

    public static void register(StandardTimezoneEnum... enums) {
        for (StandardTimezoneEnum en : enums) {
            register(en);
        }
    }

    @Nullable
    public static ZoneId zoneIdOrNull(Integer zoneId) {
        if (zoneId == null) return null;
        return ZoneIdMap.get(zoneId);
    }

    @NotNull
    public static ZoneId zoneIdOrThrow(Integer zoneId) {
        ZoneId t = zoneIdOrNull(zoneId);
        if (t == null) {
            throw new IllegalArgumentException("can not found ZoneId by zoneId=" + zoneId);
        } else {
            return t;
        }
    }

    @NotNull
    public static ZoneId zoneIdOrHint(Integer zoneId, String hint) {
        ZoneId t = zoneIdOrNull(zoneId);
        if (t == null) {
            throw new IllegalArgumentException(hint);
        } else {
            return t;
        }
    }

    @NotNull
    public static ZoneId zoneIdOrElse(Integer zoneId, ZoneId el) {
        ZoneId t = zoneIdOrNull(zoneId);
        return t == null ? el : t;
    }

    @Nullable
    public static Integer zoneIdOrNull(ZoneId zoneId) {
        if (zoneId == null) return null;
        for (Map.Entry<Integer, ZoneId> en : ZoneIdMap.entrySet()) {
            if (en.getValue().equals(zoneId)) return en.getKey();
        }
        return null;
    }

    @NotNull
    public static Integer zoneIdOrThrow(ZoneId zoneId) {
        Integer t = zoneIdOrNull(zoneId);
        if (t == null) {
            throw new IllegalArgumentException("can not found ZoneId by zoneId=" + zoneId);
        } else {
            return t;
        }
    }

    @NotNull
    public static Integer zoneIdOrHint(ZoneId zoneId, String hint) {
        Integer t = zoneIdOrNull(zoneId);
        if (t == null) {
            throw new IllegalArgumentException(hint);
        } else {
            return t;
        }
    }

    @NotNull
    public static Integer zoneIdOrElse(ZoneId zoneId, Integer el) {
        Integer t = zoneIdOrNull(zoneId);
        return t == null ? el : t;
    }

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
