package pro.fessional.wings.silencer.tweak;

import org.jetbrains.annotations.NotNull;
import pro.fessional.wings.silencer.spring.boot.WingsEnabledContext;

/**
 * @author trydofor
 * @since 2024-02-17
 */
public class FeatureFlag {

    /**
     * has any of feature, empty means false
     */
    public static boolean any(@NotNull Class<?>... feature) {
        for (Class<?> f : feature) {
            if (has(f)) return true;
        }
        return false;
    }

    /**
     * has all features, empty means true
     */
    public static boolean all(@NotNull Class<?>... feature) {
        for (Class<?> f : feature) {
            if (not(f)) return false;
        }
        return true;
    }

    /**
     * not has the feature
     */
    public static boolean not(@NotNull Class<?> feature) {
        return !has(feature);
    }

    /**
     * has the feature, default false
     */
    public static boolean has(@NotNull Class<?> feature) {
        Boolean tv = TweakFeature.threadlValue(feature);
        if (tv != null) return tv;

        Boolean gv = TweakFeature.globalValue(feature);
        if (gv != null) return gv;

        String id = feature.getName();
        String pre = WingsEnabledContext.handlePrefix(id);

        String key = pre + "." + id;
        Boolean ev = WingsEnabledContext.handleEnabled(key);
        if (ev != null) return ev;

        Boolean fv = WingsEnabledContext.handleFeature(id);
        return fv == Boolean.TRUE;
    }

}
