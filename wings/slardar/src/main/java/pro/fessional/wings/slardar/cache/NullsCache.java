package pro.fessional.wings.slardar.cache;

import org.cache2k.Cache2kBuilder;
import org.jetbrains.annotations.NotNull;
import org.springframework.cache.Cache;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * 对nulls进行统一处理的缓存，支持weak及skip处理。
 * 正数:缓存大小，默认ttl=3600s; 0:不缓存null；其他值则不统一处理
 *
 * @author trydofor
 * @since 2022-03-13
 */
public class NullsCache implements Cache {

    private final Cache backend;
    private final org.cache2k.Cache<Object, Object> nulls;

    public NullsCache(Cache cache, int size, int live) {
        this.backend = cache;
        this.nulls = size > 0 ? Cache2kBuilder.forUnknownTypes()
                .entryCapacity(size)
                .expireAfterWrite(live, TimeUnit.SECONDS)
                .build() : null;
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
        if (nulls != null && nulls.get(key) != null) {
            return null;
        }
        return backend.get(key);
    }

    @Override
    public <T> T get(@NotNull Object key, Class<T> type) {
        if (nulls != null && nulls.get(key) != null) {
            return null;
        }
        return backend.get(key, type);
    }

    @Override
    public <T> T get(@NotNull Object key, @NotNull Callable<T> valueLoader) {
        if (nulls != null && nulls.get(key) != null) {
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
            nulls.remove(key);
        }
    }

    @Override
    public void clear() {
        backend.clear();
        if (nulls != null) {
            nulls.removeAll();
        }
    }
}
