package pro.fessional.wings.silencer.spring.boot;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.util.AntPathMatcher;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

/**
 * enabled override feature i.e. one-one style override one-many style.
 *
 * @author trydofor
 * @since 2024-02-17
 */
public class WingsEnabledContext {
    /**
     * string one-one style
     */
    public static final String PrefixEnabled = "wings.enabled";
    /**
     * ant-style one-many style
     */
    public static final String PrefixFeature = "wings.feature";

    // ///////////
    private static final AntPathMatcher DotAntMatcher = new AntPathMatcher();

    private static final Map<String, Boolean> FeatureError = new LinkedHashMap<>();
    private static final Map<String, String> FeaturePrefix = new LinkedHashMap<>();
    private static final Map<String, Boolean> FeatureEnable = new LinkedHashMap<>();

    public static void putFeatureError(String key, Boolean value) {
        FeatureError.put(key, value);
    }

    public static void putFeaturePrefix(String key, String value) {
        FeaturePrefix.put(key, value);
    }

    public static void putFeatureEnable(String key, Boolean value) {
        FeatureEnable.put(key, value);
    }


    public static void removeFeatureError(String key) {
        FeatureError.remove(key);
    }

    public static void removeFeaturePrefix(String key) {
        FeaturePrefix.remove(key);
    }

    public static void removeFeatureEnable(String key) {
        FeatureEnable.remove(key);
    }

    public static void clearFeatureError() {
        FeatureError.clear();
    }

    public static void clearFeaturePrefix() {
        FeaturePrefix.clear();
    }

    public static void clearFeatureEnable() {
        FeatureEnable.clear();
    }

    public static LinkedHashMap<String, Boolean> copyFeatureError() {
        return new LinkedHashMap<>(FeatureError);
    }

    public static LinkedHashMap<String, String> copyFeaturePrefix() {
        return new LinkedHashMap<>(FeaturePrefix);
    }

    public static LinkedHashMap<String, Boolean> copyFeatureEnable() {
        return new LinkedHashMap<>(FeatureEnable);
    }

    @NotNull
    public static String handlePrefix(@NotNull String key) {
        for (Map.Entry<String, String> en : FeaturePrefix.entrySet()) {
            if (DotAntMatcher.match(en.getKey(), key)) {
                return en.getValue();
            }
        }
        return PrefixEnabled;
    }

    @Nullable
    public static Boolean handleError(@NotNull String id) {
        for (Map.Entry<String, Boolean> en : FeatureError.entrySet()) {
            if (DotAntMatcher.match(en.getKey(), id)) {
                return en.getValue() == Boolean.TRUE;
            }
        }
        return null;
    }

    /**
     * id is ClassName or ClassName + '.' + MethodName
     */
    @Nullable
    public static Boolean handleFeature(@NotNull String id) {
        for (Map.Entry<String, Boolean> en : FeatureEnable.entrySet()) {
            if (DotAntMatcher.match(en.getKey(), id)) {
                return en.getValue() == Boolean.TRUE;
            }
        }

        return null;
    }

    private static final AtomicReference<Function<String, String>> EnabledProvider = new AtomicReference<>();

    public static void setEnabledProvider(@NotNull Function<String, String> prop) {
        EnabledProvider.set(prop);
    }


    private static final ConcurrentHashMap<String, Boolean> EnabledProperty = new ConcurrentHashMap<>();

    public static void putEnabledProperty(String key, Boolean value) {
        EnabledProperty.put(key, value);
    }

    public static void removeEnabledProperty(String key) {
        EnabledProperty.remove(key);
    }

    public static void clearEnabledProperty() {
        EnabledProperty.clear();
    }

    public static HashMap<String, Boolean> copyEnabledProperty() {
        return new HashMap<>(EnabledProperty);
    }

    /**
     * key is prefix + id
     */
    @Nullable
    public static Boolean handleEnabled(@NotNull String key) {

        Boolean bool = EnabledProperty.get(key);
        if (bool != null) return bool;

        Function<String, String> func = EnabledProvider.get();
        if (func == null) return null;

        String pp = func.apply(key);
        if ("false".equalsIgnoreCase(pp)) {
            bool = Boolean.FALSE;
        }
        else if ("true".equalsIgnoreCase(pp)) {
            bool = Boolean.TRUE;
        }

        // as default if not null
        if (bool != null) {
            EnabledProperty.put(key, bool);
        }

        return bool;
    }

    public static void reset() {
        clearFeatureError();
        clearFeaturePrefix();
        clearFeatureEnable();
        clearEnabledProperty();
    }
}
