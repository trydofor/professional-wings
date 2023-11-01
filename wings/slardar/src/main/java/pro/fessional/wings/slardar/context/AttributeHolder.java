package pro.fessional.wings.slardar.context;

import org.cache2k.Cache;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pro.fessional.mirana.best.TypedReg;
import pro.fessional.wings.slardar.cache.cache2k.WingsCache2k;
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
 * App level, default ttl=12H, unbounded cache.
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
    private static final ConcurrentHashMap<TypedReg<?, ?>, Function<Object, Object>> LOADER = new ConcurrentHashMap<>();

    private static final ConcurrentHashMap<TypedReg<?, ?>, Cache<Object, Object>> HOLDER = new ConcurrentHashMap<>();

    @NotNull
    private static Cache<Object, Object> getCache(@NotNull TypedReg<?, ?> reg) {
        return HOLDER.computeIfAbsent(reg, k ->
                WingsCache2k.builder(AttributeHolder.class,
                        reg.regType.getName().substring(reg.regType.getPackageName().length() + 1),
                        -1, TtlDefault, -1
                ).build());
    }

    @NotNull
    public static Set<TypedReg<?, ?>> holders() {
        return new HashSet<>(HOLDER.keySet());
    }

    @NotNull
    public static Set<TypedReg<?, ?>> loaders() {
        return new HashSet<>(LOADER.keySet());
    }

    /**
     * Registering a typed key-value and its loader
     *
     * @param reg    Type to register
     * @param loader returns `null` is not cached and is called every time, so it is recommended to return `nonnull`.
     * @param <K>    key type
     * @param <V>    value type
     */
    @SuppressWarnings("unchecked")
    public static <K, V> void regLoader(@NotNull TypedReg<K, V> reg, @NotNull Function<K, V> loader) {
        LOADER.put(reg, (Function<Object, Object>) loader);
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
        putAttr(reg, key, value, TtlDefault);
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
        getCache(reg).mutate(key, entry -> {
            entry.setValue(value);
            entry.setExpiryTime(entry.getStartTime() + ttl * 1000L);
        });
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
        putAttrs(reg, map, TtlDefault);
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
        if (map.isEmpty()) return;

        final Cache<Object, Object> cache = getCache(reg);
        for (Map.Entry<K, V> en : map.entrySet()) {
            cache.mutate(en.getKey(), entry -> {
                entry.setValue(en.getValue());
                entry.setExpiryTime(entry.getStartTime() + ttl * 1000L);
            });
        }
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
        return tryAttr(reg, key, elze, TtlDefault);
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
        final V obj = tryAttr(reg, key, false, ttl);
        return obj == null ? elze : obj;
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
        return tryAttr(reg, key, true, TtlDefault);
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
        return tryAttr(reg, key, true, ttl);
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
        return tryAttr(reg, key, notnull, TtlDefault);
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
    @SuppressWarnings("unchecked")
    @Contract("_,_,true,_ ->!null")
    public static <K, V> V tryAttr(@NotNull TypedReg<K, V> reg, @NotNull K key, boolean notnull, int ttl) {

        final Object rst = getCache(reg).invoke(key, entry -> {
            Object t = null;
            if (entry.exists()) {
                t = entry.getValue();
            }
            else {
                Function<Object, Object> ld = LOADER.get(reg);
                if (ld != null) {
                    t = ld.apply(key);
                    entry.setValue(t);
                    entry.setExpiryTime(entry.getStartTime() + ttl * 1000L);
                }
            }
            return t;
        });

        if (rst == null && notnull) {
            throw new NullPointerException("aware=" + reg + ",key=" + key);
        }
        else {
            return (V) rst;
        }
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
        Cache<Object, Object> cache = HOLDER.get(reg);
        return cache == null ? null : (V) cache.get(key);
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
        Cache<Object, Object> cache = HOLDER.get(reg);
        if (cache == null) return Collections.emptyMap();
        return (Map<K, V>) cache.getAll(key);
    }

    /**
     * remove an attribute by key
     *
     * @param reg Type to register
     * @param key unique key, e.g. userId
     * @param <K> key type
     */
    public static <K> void ridAttr(TypedReg<K, ?> reg, K key) {
        Cache<Object, Object> cache = HOLDER.get(reg);
        if (cache != null) {
            cache.remove(key);
        }
    }

    /**
     * remove all attribute by keys
     *
     * @param reg Type to register
     * @param key unique key, e.g. userId
     * @param <K> key type
     */
    @SafeVarargs
    public static <K> void ridAttrs(TypedReg<K, ?> reg, K... key) {
        if (key == null || key.length == 0) return;
        ridAttrs(reg, Arrays.asList(key));
    }

    /**
     * remove all attribute by keys
     *
     * @param reg Type to register
     * @param key unique key, e.g. userId
     * @param <K> key type
     */
    public static <K> void ridAttrs(TypedReg<K, ?> reg, Collection<? extends K> key) {
        if (key == null || key.isEmpty()) return;
        Cache<Object, Object> cache = HOLDER.get(reg);
        if (cache != null) {
            cache.removeAll(key);
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
            Cache<Object, Object> cache = HOLDER.get(tr);
            if (cache != null) {
                cache.removeAll();
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
        for (TypedReg<?, ?> r : reg) {
            LOADER.remove(r);
        }
    }
}
