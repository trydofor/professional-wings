package pro.fessional.wings.slardar.cache.cache2k;

import org.cache2k.Cache;
import org.cache2k.Cache2kBuilder;
import org.cache2k.extra.spring.SpringCache2kCache;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * @author trydofor
 * @since 2023-01-25
 */
public class NullsCache2k extends SpringCache2kCache {

    private final org.cache2k.Cache<Object, Object> nulls;

    public NullsCache2k(Cache<Object, Object> cache, int size, int live) {
        super(cache);
        this.nulls = size > 0 ? Cache2kBuilder.forUnknownTypes()
                .entryCapacity(size)
                .expireAfterWrite(live, TimeUnit.SECONDS)
                .build() : null;
    }

    @Override
    public ValueWrapper get(@NotNull Object key) {
        if (nulls != null && nulls.get(key) != null) {
            return null;
        }
        return super.get(key);
    }

    @Override
    public <T> T get(@NotNull Object key, Class<T> type) {
        if (nulls != null && nulls.get(key) != null) {
            return null;
        }
        return super.get(key, type);
    }

    @Override
    public <T> T get(@NotNull Object key, @NotNull Callable<T> valueLoader) {
        if (nulls != null && nulls.get(key) != null) {
            return null;
        }

        final T value = super.get(key, valueLoader);
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
            super.put(key, value);
        }
    }

    @Override
    public void evict(@NotNull Object key) {
        super.evict(key);
        if (nulls != null) {
            nulls.remove(key);
        }
    }

    @Override
    public void clear() {
        super.clear();
        if (nulls != null) {
            nulls.removeAll();
        }
    }
}
