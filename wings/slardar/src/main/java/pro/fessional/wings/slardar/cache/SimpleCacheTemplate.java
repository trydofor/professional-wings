package pro.fessional.wings.slardar.cache;

import lombok.Getter;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.SimpleKeyGenerator;
import org.springframework.core.convert.converter.Converter;
import pro.fessional.mirana.best.AssertArgs;
import pro.fessional.wings.silencer.spring.help.ApplicationContextHelper;

import java.util.function.Supplier;

/**
 * only support raw and method argument key.
 * NOT support SpEL or KeyGenerator
 * <p>
 * should declare it as a final field to enhance performance,
 * then use it after application context has started
 *
 * @author trydofor
 * @since 2024-06-25
 */
public class SimpleCacheTemplate<T> {

    private final String manager;
    private final String[] names;
    private final Cache[] caches;
    private volatile int status; // -1:uninit, 0:lazy-inited, 1: fixed

    @Getter
    private volatile Converter<T, Object> encoder = null;
    @Getter
    private volatile Converter<Object, T> decoder = null;
    @Getter
    private volatile BeanFactory beanFactory = null;

    public SimpleCacheTemplate(@NotNull String manager, @NotNull String... caches) {
        this.manager = manager;
        this.names = caches;
        this.caches = new Cache[caches.length];
        this.status = -1;
    }

    public SimpleCacheTemplate(@NotNull Cache... caches) {
        this.manager = null;
        this.names = null;
        this.caches = caches;
        this.status = 1;
    }

    /**
     * encode Value to Cache, default null
     */
    @Contract("_->this")
    public SimpleCacheTemplate<T> setEncoder(@Nullable Converter<T, Object> encoder) {
        this.encoder = encoder;
        return this;
    }

    /**
     * decode Value from Cache, default null
     */
    @Contract("_->this")
    public SimpleCacheTemplate<T> setDecoder(@Nullable Converter<Object, T> decoder) {
        this.decoder = decoder;
        return this;
    }

    /**
     * set the application context to re-init Cache by CacheManager bean.
     * mostly for unit test to avoid multiple application context.
     */
    @Contract("_->this")
    public SimpleCacheTemplate<T> setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
        this.status = -1;
        return this;
    }
    // ////////

    /**
     * get and decode Value from cache by argument key
     */
    public T getArgKey(@NotNull Object... args) {
        return getRawKey(rawKey(args));
    }

    /**
     * get and decode Value from cache by raw key
     */
    public T getRawKey(@NotNull Object key) {
        for (Cache cache : getCaches()) {
            Cache.ValueWrapper vw = cache.get(key);
            if (vw != null && vw.get() != null) {
                return decode(vw.get());
            }
        }
        return null;
    }

    /**
     * get and decode Value from cache by argument key, put the non-null value if no cache found.
     */
    public T getArgKey(@NotNull Supplier<T> value, @NotNull Object... args) {
        return getRawKey(value, rawKey(args));
    }

    /**
     * get and decode Value from cache by raw key, put the non-null value if no cache found.
     */
    public T getRawKey(@NotNull Supplier<T> value, @NotNull Object key) {
        for (Cache cache : getCaches()) {
            Cache.ValueWrapper vw = cache.get(key);
            if (vw != null && vw.get() != null) {
                return decode(vw.get());
            }
        }

        T v = value.get();
        if (v != null) {
            putRawKey(v, key);
        }
        return v;
    }

    /**
     * get, decode and convert Value from cache by argument key, put the non-null value if no cache found.
     */
    public <U> U getArgKey(@NotNull Converter<T, U> converter, @NotNull Object... args) {
        return getRawKey(converter, rawKey(args));
    }

    /**
     * get, decode and convert Value from cache by raw key, put the non-null value if no cache found.
     */
    public <U> U getRawKey(@NotNull Converter<T, U> converter, @NotNull Object key) {
        T t = getRawKey(key);
        return converter.convert(t);
    }

    /**
     * get, decode and convert Value from cache by argument key, put the non-null value if no cache found.
     */
    public <U> U getArgKey(@NotNull Converter<T, U> converter, @NotNull Supplier<T> value, @NotNull Object... args) {
        return getRawKey(converter, value, rawKey(args));
    }

    /**
     * get, decode and convert Value from cache by raw key, put the non-null value if no cache found.
     */
    public <U> U getRawKey(@NotNull Converter<T, U> converter, @NotNull Supplier<T> value, @NotNull Object key) {
        T t = getRawKey(value, key);
        return converter.convert(t);
    }

    // ////////

    public void putArgKey(@NotNull T value, @NotNull Object... args) {
        putRawKey(value, rawKey(args));
    }

    public void putRawKey(@NotNull T value, @NotNull Object key) {
        final Object obj = encode(value);
        for (Cache cache : getCaches()) {
            cache.put(key, obj);
        }
    }

    /**
     * encode and put value to cache, return the decoded existing Value
     *
     * @see Cache#putIfAbsent(Object, Object)
     */
    public T putArgKeyIfPresent(@NotNull T value, @NotNull Object... args) {
        return putRawKeyIfPresent(value, rawKey(args));
    }

    /**
     * encode and put value to cache, return the decoded existing Value
     *
     * @see Cache#putIfAbsent(Object, Object)
     */
    public T putRawKeyIfPresent(@NotNull T value, @NotNull Object key) {
        final Object obj = encode(value);
        Cache.ValueWrapper vw = null;
        for (Cache cache : getCaches()) {
            vw = cache.putIfAbsent(key, obj);
        }
        return vw == null ? null : decode(vw.get());
    }

    /**
     * encode and put value to cache, return the decoded and converted existing Value
     *
     * @see Cache#putIfAbsent(Object, Object)
     */
    public <U> U putArgKeyIfPresent(@NotNull Converter<T, U> converter, @NotNull T value, @NotNull Object... args) {
        return putRawKeyIfPresent(converter, value, rawKey(args));
    }

    /**
     * encode and put value to cache, return the decoded and converted existing Value
     *
     * @see Cache#putIfAbsent(Object, Object)
     */
    public <U> U putRawKeyIfPresent(@NotNull Converter<T, U> converter, @NotNull T value, @NotNull Object key) {
        T t = putRawKeyIfPresent(value, key);
        return converter.convert(t);
    }

    // ////////

    public void evictArgKey(@NotNull Object... args) {
        evictRawKey(rawKey(args));
    }

    public void evictRawKey(@NotNull Object key) {
        for (Cache cache : getCaches()) {
            cache.evict(key);
        }
    }

    public void evictAll() {
        for (Cache cache : getCaches()) {
            cache.clear();
        }
    }

    public Cache[] getCaches() {
        if (status == -1) {
            synchronized (caches) {
                if (status == -1) {
                    AssertArgs.notNull(manager, "empty manager={}", manager);
                    CacheManager cacheManager = beanFactory == null
                        ? ApplicationContextHelper.getBean(manager)
                        : beanFactory.getBean(manager, CacheManager.class);

                    AssertArgs.notNull(cacheManager, "cacheManager not found, manager={}", manager);
                    AssertArgs.notNull(names, "cache is null");
                    for (int i = 0; i < names.length; i++) {
                        caches[i] = cacheManager.getCache(names[i]);
                        AssertArgs.notNull(caches[i], "cache not found,name={}, manager={}", names[i], manager);
                    }
                }
                status = 0;
            }
        }

        return caches;
    }

    /**
     * decode value from cache, force to cast type if the decoder is null
     */
    @SuppressWarnings("unchecked")
    public T decode(Object obj) {
        return decoder == null ? (T) obj : decoder.convert(obj);
    }

    /**
     * encode value to cache if the encoder not null
     */
    public Object encode(T obj) {
        return encoder == null ? obj : encoder.convert(obj);
    }

    /**
     * generate raw key by args/parmas
     *
     * @see SimpleKeyGenerator#generateKey(Object...)
     */
    public Object rawKey(Object... args) {
        return SimpleKeyGenerator.generateKey(args);
    }
}
