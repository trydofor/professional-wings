package pro.fessional.wings.slardar.context;

import org.cache2k.Cache;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import pro.fessional.mirana.best.TypedReg;
import pro.fessional.mirana.best.TypedReg.Key;
import pro.fessional.wings.slardar.cache.cache2k.WingsCache2k;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * App level, ttl=12H, unbounded cache.
 * Need manually register and remove.
 *
 * @author trydofor
 * @since 2021-03-30
 */
public class GlobalAttributeHolder {


    //
    @SuppressWarnings("all")
    private static final Cache<Key, Object> CACHE = WingsCache2k.builder(GlobalAttributeHolder.class, "CACHE", -1, Duration.ofHours(12), null, Key.class, Object.class).build();
    private static final ConcurrentHashMap<TypedReg<?, ?>, Function<?, ?>> LOADER = new ConcurrentHashMap<>();

    /**
     * Registering a typed key-value and its loader
     *
     * @param reg    Type to register
     * @param loader returns `null` is not cached and is called every time, so it is recommended to return `nonnull`.
     * @param <K>    key type
     * @param <V>    value type
     */
    public static <K, V> void regLoader(@NotNull TypedReg<K, V> reg, @NotNull Function<Key<K, V>, V> loader) {
        LOADER.put(reg, loader);
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
        Key<K, V> k = new Key<>(reg, key);
        CACHE.put(k, value);
    }

    /**
     * Put all attribute value from map to the typed key.
     *
     * @param reg Type to register
     * @param map map of attribute
     * @param <K> key type
     * @param <V> value type
     */
    public static <K, V> void putAttr(@NotNull TypedReg<K, V> reg, @NotNull Map<K, V> map) {
        Map<Key<K, V>, V> kvs = new HashMap<>(map.size());
        for (Map.Entry<K, V> en : map.entrySet()) {
            kvs.put(new Key<>(reg, en.getKey()), en.getValue());
        }
        CACHE.putAll(kvs);
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
        final V obj = tryAttr(reg, key, false);
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
        return tryAttr(reg, key, true);
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
    @SuppressWarnings({"unchecked", "rawtypes"})
    @Contract("_,_,true ->!null")
    public static <K, V> V tryAttr(@NotNull TypedReg<K, V> reg, @NotNull K key, boolean notnull) {
        Key<K, V> k = new Key<>(reg, key);
        final Function<Key, ?> ld = (Function<Key, ?>) LOADER.get(reg);
        final Object rst;
        if (ld == null) {
            rst = CACHE.get(k);
        }
        else {
            rst = CACHE.computeIfAbsent(k, ld);
        }

        if (rst == null && notnull) {
            throw new NullPointerException("aware=" + reg + ",key=" + key);
        }
        return (V) rst;
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
    public static <K, V> V getAttr(@NotNull TypedReg<K, V> reg, @NotNull K key) {
        Key<K, V> k = new Key<>(reg, key);
        final Object rst = CACHE.get(k);
        return (V) rst;
    }

    /**
     * remove an attribute by key
     *
     * @param reg Type to register
     * @param key unique key, e.g. userId
     * @param <K> key type
     */
    public static <K> void ridAttr(TypedReg<K, ?> reg, K key) {
        CACHE.remove(new Key<>(reg, key));
    }

    /**
     * remove all attribute by keys
     *
     * @param reg Type to register
     * @param key unique key, e.g. userId
     * @param <K> key type
     */
    @SafeVarargs
    public static <K> void ridAttr(TypedReg<K, ?> reg, K... key) {
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
        final Set<Key<K, ?>> ks = new HashSet<>();
        for (K k : key) {
            ks.add(new Key<>(reg, k));
        }
        CACHE.removeAll(ks);
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
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void ridAttrAll(Collection<? extends TypedReg<?, ?>> reg) {
        if (reg == null || reg.isEmpty()) return;
        final Set<TypedReg<?, ?>> rgs;
        if (reg instanceof Set) {
            rgs = (Set<TypedReg<?, ?>>) reg;
        }
        else {
            rgs = new HashSet<>(reg);
        }

        final Set<Key> keys = CACHE
                .asMap()
                .keySet()
                .stream()
                .filter(it -> rgs.contains(it.reg))
                .collect(Collectors.toSet());
        CACHE.removeAll(keys);
    }

    /**
     * remove all attribute of type and its loader
     *
     * @param reg Type to register
     */
    public static void ridLoader(TypedReg<?, ?>... reg) {
        if (reg == null || reg.length == 0) return;
        ridLoader(Arrays.asList(reg));
    }

    /**
     * remove all attribute of type and its loader
     *
     * @param reg Type to register
     */
    public static void ridLoader(Collection<? extends TypedReg<?, ?>> reg) {
        if (reg == null || reg.isEmpty()) return;

        for (TypedReg<?, ?> r : reg) {
            LOADER.remove(r);
        }
        ridAttrAll(reg);
    }
}
