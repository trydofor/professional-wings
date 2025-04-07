package pro.fessional.wings.silencer.tweak;


import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.util.AntPathMatcher;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author trydofor
 * @since 2025-04-07
 */
public class AntMatcherMap<T> {

    protected final AntPathMatcher antMatcher;
    protected final Map<String, T> keyValue = new ConcurrentHashMap<>();
    protected final Map<String, T> antValue = new LinkedHashMap<>();

    public AntMatcherMap() {
        this(new AntPathMatcher());
    }

    public AntMatcherMap(AntPathMatcher antMatcher) {
        this.antMatcher = antMatcher;
    }

    public void putAll(Map<String, T> map) {
        for (Map.Entry<String, T> entry : map.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    @Nullable
    public T put(@NotNull String key, @NotNull T value) {
        T old = keyValue.put(key, value);
        if (antMatcher.isPattern(key)) {
            T o2 = antValue.put(key, value);
            if (old == null) {
                old = o2;
            }
        }
        return old;
    }

    @Nullable
    public T get(@NotNull String key) {
        final T v = keyValue.get(key);
        if (v != null) return v;

        for (Map.Entry<String, T> entry : antValue.entrySet()) {
            String k = entry.getKey();
            if (antMatcher.match(k, key)) {
                return entry.getValue();
            }
        }

        return null;
    }

    @Nullable
    public T remove(@NotNull String key) {
        final T v = keyValue.remove(key);
        if (v != null) return v;
        return antValue.remove(key);
    }
}
