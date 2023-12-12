package pro.fessional.wings.slardar.context;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pro.fessional.mirana.best.TypedReg;
import pro.fessional.wings.slardar.event.attr.AttributeRidEvent;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * App level, default ttl=12H, unbounded thread safe cache.
 * Need manually register and remove (or publish {@link AttributeRidEvent})
 *
 * @author trydofor
 * @see AttributeRidEvent
 * @since 2021-03-30
 */
public class AttributeHolder {
    /**
     * default ttl = 12H
     */
    public static final int TtlDefault = 12 * 3600;
    private static final ConcurrentHashMap<TypedReg<?, ?>, AttributeCache<?, ?>> Holder = new ConcurrentHashMap<>();

    @SuppressWarnings("unchecked")
    @NotNull
    public static <K, V> AttributeCache<K, V> getCache(@NotNull TypedReg<K, V> reg) {
        return (AttributeCache<K, V>) Holder.computeIfAbsent(reg, k -> {
            var cache = new AttributeCache<>(AttributeHolder.class, reg, 0, TtlDefault, 0);
            cache.register();
            return cache;
        });
    }

    @NotNull
    public static Set<TypedReg<?, ?>> holders() {
        return new HashSet<>(Holder.keySet());
    }

    /**
     * Registering a typed key-value and its loader
     *
     * @param reg    Type to register
     * @param loader returns `null` is not cached and is called every time, so it is recommended to return `nonnull`.
     * @param <K>    key type
     * @param <V>    value type
     */
    public static <K, V> void regLoader(@NotNull TypedReg<K, V> reg, @NotNull Function<K, V> loader) {
        getCache(reg).setLoader(loader);
    }

    /**
     * Put an attribute value to the typed key.
     *
     * @param reg   Type to register
     * @param key   unique key, e.g. userId
     * @param value value
     * @param <K>   key type
     * @param <V>   value type
     */
    public static <K, V> void putAttr(@NotNull TypedReg<K, V> reg, @NotNull K key, @NotNull V value) {
        getCache(reg).putAttr(key, value);
    }

    /**
     * Put an attribute value to the typed key.
     *
     * @param reg   Type to register
     * @param key   unique key, e.g. userId
     * @param value value
     * @param ttl   ttl in second
     * @param <K>   key type
     * @param <V>   value type
     */
    public static <K, V> void putAttr(@NotNull TypedReg<K, V> reg, @NotNull K key, @NotNull V value, int ttl) {
        getCache(reg).putAttr(key, value, ttl);
    }

    /**
     * Put all attribute value from map to the typed key.
     *
     * @param reg Type to register
     * @param map map of attribute
     * @param <K> key type
     * @param <V> value type
     */
    public static <K, V> void putAttrs(@NotNull TypedReg<K, V> reg, @NotNull Map<K, V> map) {
        getCache(reg).putAttrs(map);
    }

    /**
     * Put all attribute value from map to the typed key.
     *
     * @param reg Type to register
     * @param map map of attribute
     * @param ttl ttl in second
     * @param <K> key type
     * @param <V> value type
     */
    public static <K, V> void putAttrs(@NotNull TypedReg<K, V> reg, @NotNull Map<K, V> map, int ttl) {
        getCache(reg).putAttrs(map, ttl);
    }

    /**
     * Try to get an attribute by typed key, load it if not found, return `elze` if the result is null.
     *
     * @param reg  Type to register
     * @param key  unique key, e.g. userId
     * @param elze return `elze` if result is null
     * @param <K>  key type
     * @param <V>  value type
     */
    @Contract("_,_,!null->!null")
    public static <K, V> V tryAttr(@NotNull TypedReg<K, V> reg, @NotNull K key, V elze) {
        return getCache(reg).tryAttr(key, elze);
    }

    /**
     * Try to get an attribute by typed key, load it if not found, return `elze` if the result is null.
     *
     * @param reg  Type to register
     * @param key  unique key, e.g. userId
     * @param elze return `elze` if result is null
     * @param ttl  ttl in second
     * @param <K>  key type
     * @param <V>  value type
     */
    @Contract("_,_,!null,_->!null")
    public static <K, V> V tryAttr(@NotNull TypedReg<K, V> reg, @NotNull K key, V elze, int ttl) {
        return getCache(reg).tryAttr(key, elze, ttl);
    }

    /**
     * Try to get an attribute by typed key, load it if not found, throw NPE if the result is null.
     *
     * @param reg Type to register
     * @param key unique key, e.g. userId
     * @param <K> key type
     * @param <V> value type
     */
    @NotNull
    public static <K, V> V tryAttr(@NotNull TypedReg<K, V> reg, @NotNull K key) {
        return getCache(reg).tryAttr(key);
    }

    /**
     * Try to get an attribute by typed key, load it if not found, throw NPE if the result is null.
     *
     * @param reg Type to register
     * @param key unique key, e.g. userId
     * @param ttl ttl in second
     * @param <K> key type
     * @param <V> value type
     */
    @NotNull
    public static <K, V> V tryAttr(@NotNull TypedReg<K, V> reg, @NotNull K key, int ttl) {
        return getCache(reg).tryAttr(key, ttl);
    }

    /**
     * Try to get an attribute by typed key, load it if not found, throw NPE if notnull and the result is null.
     *
     * @param reg     Type to register
     * @param key     unique key, e.g. userId
     * @param notnull whether notnull
     * @param <K>     key type
     * @param <V>     value type
     */
    @Contract("_,_,true ->!null")
    public static <K, V> V tryAttr(@NotNull TypedReg<K, V> reg, @NotNull K key, boolean notnull) {
        return getCache(reg).tryAttr(key, notnull);
    }

    /**
     * Try to get an attribute by typed key, load it if not found, throw NPE if notnull and the result is null.
     *
     * @param reg     Type to register
     * @param key     unique key, e.g. userId
     * @param notnull whether notnull
     * @param ttl     ttl in second
     * @param <K>     key type
     * @param <V>     value type
     */
    @Contract("_,_,true,_ ->!null")
    public static <K, V> V tryAttr(@NotNull TypedReg<K, V> reg, @NotNull K key, boolean notnull, int ttl) {
        return getCache(reg).tryAttr(key, notnull, ttl);
    }

    /**
     * Get an attribute by typed key, and NOT load if not found.
     *
     * @param reg Type to register
     * @param key unique key, e.g. userId
     * @param <K> key type
     * @param <V> value type
     */
    @SuppressWarnings("unchecked")
    @Nullable
    public static <K, V> V getAttr(@NotNull TypedReg<K, V> reg, @NotNull K key) {
        AttributeCache<K, V> cache = (AttributeCache<K, V>) Holder.get(reg);
        return cache == null ? null : cache.getAttr(key);
    }

    /**
     * Get all attributes by typed keys, and NOT load if not found.
     *
     * @param reg Type to register
     * @param key unique key, e.g. userId
     * @param <K> key type
     * @param <V> value type
     */
    @SuppressWarnings("unchecked")
    @NotNull
    public static <K, V> Map<K, V> getAttrs(@NotNull TypedReg<K, V> reg, @NotNull Collection<K> key) {
        AttributeCache<K, V> cache = (AttributeCache<K, V>) Holder.get(reg);
        return cache == null ? Collections.emptyMap() : cache.getAttrs(key);
    }

    /**
     * remove an attribute by key
     *
     * @param reg Type to register
     * @param key unique key, e.g. userId
     * @param <K> key type
     */
    @SuppressWarnings("unchecked")
    public static <K> void ridAttr(TypedReg<K, ?> reg, K key) {
        AttributeCache<K, ?> cache = (AttributeCache<K, ?>) Holder.get(reg);
        if (cache != null) {
            cache.ridAttr(key);
        }
    }

    /**
     * remove all attribute by keys
     *
     * @param reg Type to register
     * @param key unique key, e.g. userId
     * @param <K> key type
     */
    @SuppressWarnings("unchecked")
    public static <K> void ridAttrs(TypedReg<K, ?> reg, Collection<? extends K> key) {
        if (key == null || key.isEmpty()) return;
        AttributeCache<K, ?> cache = (AttributeCache<K, ?>) Holder.get(reg);
        if (cache != null) {
            cache.ridAttrs(key);
        }
    }

    /**
     * remove all attribute of type
     *
     * @param reg Type to register
     */
    public static void ridAttrAll(TypedReg<?, ?>... reg) {
        if (reg == null || reg.length == 0) return;
        ridAttrAll(Arrays.asList(reg));
    }

    /**
     * remove all attribute of type
     *
     * @param reg Type to register
     */
    public static void ridAttrAll(Collection<? extends TypedReg<?, ?>> reg) {
        if (reg == null || reg.isEmpty()) return;

        for (TypedReg<?, ?> tr : reg) {
            AttributeCache<?, ?> cache = Holder.get(tr);
            if (cache != null) {
                cache.ridAttrAll();
            }
        }
    }

    /**
     * remove the loader
     *
     * @param reg Type to register
     */
    public static void ridLoader(TypedReg<?, ?>... reg) {
        if (reg == null || reg.length == 0) return;
        ridLoader(Arrays.asList(reg));
    }

    /**
     * remove the loader
     *
     * @param reg Type to register
     */
    public static void ridLoader(Collection<? extends TypedReg<?, ?>> reg) {
        if (reg == null || reg.isEmpty()) return;

        for (TypedReg<?, ?> tr : reg) {
            AttributeCache<?, ?> cache = Holder.get(tr);
            if (cache != null) {
                cache.setLoader(null);
            }
        }
    }

    /**
     * unregister from the event
     */
    public static void unregister(TypedReg<?, ?>... reg) {
        for (TypedReg<?, ?> tr : reg) {
            AttributeCache<?, ?> cache = Holder.get(tr);
            if (cache != null) {
                cache.unregister();
            }
        }
    }
}
