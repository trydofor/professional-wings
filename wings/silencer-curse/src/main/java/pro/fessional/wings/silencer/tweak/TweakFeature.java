package pro.fessional.wings.silencer.tweak;

import com.alibaba.ttl.TransmittableThreadLocal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author trydofor
 * @since 2024-02-18
 */
public class TweakFeature {

    private static final ConcurrentHashMap<Class<?>, Boolean> GlobalFlag = new ConcurrentHashMap<>();
    private static final ThreadLocal<Map<Class<?>, Boolean>> ThreadFlag = new TransmittableThreadLocal<>();

    // global

    @Nullable
    public static Boolean globalValue(@NotNull Class<?> feature) {
        return GlobalFlag.get(feature);
    }

    public static void tweakGlobal(@NotNull Class<?> feature, boolean enabled) {
        GlobalFlag.put(feature, enabled);
    }

    public static void resetGlobal(@NotNull Class<?> feature) {
        GlobalFlag.remove(feature);
    }

    public static void resetGlobal() {
        GlobalFlag.clear();
    }

    public static HashMap<Class<?>, Boolean> copyGlobal() {
        return new HashMap<>(GlobalFlag);
    }

    // thread

    @Nullable
    public static Boolean threadlValue(@NotNull Class<?> feature) {
        Map<Class<?>, Boolean> map = ThreadFlag.get();
        if (map != null) {
            return map.get(feature);
        }
        return null;
    }

    public static void tweakThread(@NotNull Class<?> feature, boolean enabled) {
        Map<Class<?>, Boolean> map = ThreadFlag.get();
        if (map == null) {
            map = new HashMap<>();
            ThreadFlag.set(map);
        }
        map.put(feature, enabled);
    }

    public static void resetThread(@NotNull Class<?> feature) {
        Map<Class<?>, Boolean> map = ThreadFlag.get();
        if (map != null) {
            map.remove(feature);
        }
    }

    public static void resetThread() {
        ThreadFlag.remove();
    }

    public static HashMap<Class<?>, Boolean> copyThread() {
        Map<Class<?>, Boolean> map = ThreadFlag.get();
        return map == null ? new HashMap<>() : new HashMap<>(GlobalFlag);
    }
}
