package pro.fessional.wings.slardar.cache;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.jetbrains.annotations.NotNull;
import org.springframework.cache.Cache;

import java.util.concurrent.Callable;

/**
 * 对nulls进行统一处理的缓存，支持weak及skip处理。
 * weak是把null放到weak引用中。
 * skip是当null时，不put
 *
 * @author trydofor
 * @since 2022-03-13
 */
public class NullsCache implements Cache {

    private final Cache backend;
    private final com.github.benmanes.caffeine.cache.Cache<Object, Object> nulls;

    public NullsCache(Cache cache, boolean skip) {
        this.backend = cache;
        this.nulls = skip ? null : Caffeine.newBuilder()
                                           .weakKeys()
                                           .maximumSize(10_000)
                                           .build();
    }

    @Override
    public @NotNull String getName() {
        return backend.getName();
    }

    @Override
    public @NotNull Object getNativeCache() {
        return backend.getNativeCache();
    }

    @Override
    public ValueWrapper get(@NotNull Object key) {
        if (nulls != null && nulls.getIfPresent(key) != null) {
            return null;
        }
        return backend.get(key);
    }

    @Override
    public <T> T get(@NotNull Object key, Class<T> type) {
        if (nulls != null && nulls.getIfPresent(key) != null) {
            return null;
        }
        return backend.get(key, type);
    }

    @Override
    public <T> T get(@NotNull Object key, @NotNull Callable<T> valueLoader) {
        if (nulls != null && nulls.getIfPresent(key) != null) {
            return null;
        }

        final T value = backend.get(key, valueLoader);
        if (value == null && nulls != null) {
            nulls.put(key, Boolean.TRUE);
        }
        return value;
    }

    @Override
    public void put(@NotNull Object key, Object value) {
        if (value == null) {
            if (nulls != null) {
                nulls.put(key, Boolean.TRUE);
            }
            else {
                // skip null
            }
        }
        else {
            backend.put(key, value);
        }
    }

    @Override
    public void evict(@NotNull Object key) {
        backend.evict(key);
        if (nulls != null) {
            nulls.invalidate(key);
        }
    }

    @Override
    public void clear() {
        backend.clear();
        if (nulls != null) {
            nulls.cleanUp();
        }
    }
}
