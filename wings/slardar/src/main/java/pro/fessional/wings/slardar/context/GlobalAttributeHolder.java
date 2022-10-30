package pro.fessional.wings.slardar.context;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import pro.fessional.mirana.best.TypedReg;
import pro.fessional.mirana.best.TypedReg.Key;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 应用程序级Atl=12H无界缓存，需手动注册和移除。
 *
 * @author trydofor
 * @since 2021-03-30
 */
public class GlobalAttributeHolder {


    //
    private static final Cache<Key<?, ?>, Object> CACHE = Caffeine.newBuilder()
                                                                  .expireAfterAccess(12, TimeUnit.HOURS)
                                                                  .build();
    private static final ConcurrentHashMap<TypedReg<?, ?>, Function<?, ?>> LOADER = new ConcurrentHashMap<>();

    /**
     * 注册一个属性及其加载器
     *
     * @param reg    类型
     * @param loader 加载器，返回null时不被缓存，每次都会调用，因此建议返回`空值`
     * @param <K>    key类型
     * @param <V>    value类型
     */
    public static <K, V> void regLoader(@NotNull TypedReg<K, V> reg, @NotNull Function<Key<K, V>, V> loader) {
        LOADER.put(reg, loader);
    }

    /**
     * 放入一个type的值，对loader的补充，如生效前
     *
     * @param reg   类型
     * @param key   唯一key，如userId
     * @param value 值
     * @param <K>   key类型
     * @param <V>   value类型
     */
    public static <K, V> void putAttr(@NotNull TypedReg<K, V> reg, @NotNull K key, @NotNull V value) {
        Key<K, V> k = new Key<>(reg, key);
        CACHE.put(k, value);
    }

    /**
     * 放入一个type的值，对loader的补充，如生效前
     *
     * @param reg 类型
     * @param map 唯一key，如userId
     * @param <K> key类型
     * @param <V> value类型
     */
    public static <K, V> void putAttr(@NotNull TypedReg<K, V> reg, @NotNull Map<K, V> map) {
        Map<Key<K, V>, V> kvs = new HashMap<>(map.size());
        for (Map.Entry<K, V> en : map.entrySet()) {
            kvs.put(new Key<>(reg, en.getKey()), en.getValue());
        }
        CACHE.putAll(kvs);
    }

    /**
     * 根据一个type获取属性，尝试Loader加载，如果不存在，null时抛NPE异常
     *
     * @param reg  类型
     * @param key  唯一key，如userId
     * @param elze 唯一key，null时返回elze
     * @param <K>  key类型
     * @param <V>  value类型
     * @return 返回值
     */
    @Contract("_,_,!null->!null")
    public static <K, V> V tryAttr(@NotNull TypedReg<K, V> reg, @NotNull K key, V elze) {
        final V obj = tryAttr(reg, key, false);
        return obj == null ? elze : obj;
    }

    /**
     * 根据一个type获取属性，尝试Loader加载，如果不存在，null时抛NPE异常
     *
     * @param reg 类型
     * @param key 唯一key，如userId
     * @param <K> key类型
     * @param <V> value类型
     * @return 返回值
     */
    @NotNull
    public static <K, V> V tryAttr(@NotNull TypedReg<K, V> reg, @NotNull K key) {
        return tryAttr(reg, key, true);
    }

    /**
     * 根据一个type获取属性，尝试Loader加载，如果不存在，选择null或异常
     *
     * @param reg     类型
     * @param key     唯一key，如userId
     * @param notnull 是否 notnull
     * @param <K>     key类型
     * @param <V>     value类型
     * @return 返回值
     */
    @SuppressWarnings("unchecked")
    @Contract("_,_,true ->!null")
    public static <K, V> V tryAttr(@NotNull TypedReg<K, V> reg, @NotNull K key, boolean notnull) {
        Key<K, V> k = new Key<>(reg, key);
        final Function<Key<?, ?>, ?> ld = (Function<Key<?, ?>, ?>) LOADER.get(reg);
        final Object rst;
        if (ld == null) {
            rst = CACHE.getIfPresent(k);
        }
        else {
            rst = CACHE.get(k, ld);
        }

        if (rst == null && notnull) {
            throw new NullPointerException("aware=" + reg + ",key=" + key);
        }
        return (V) rst;
    }

    /**
     * 获取当前缓存的type属性，不会调用Loader，如果不存在，返回null
     *
     * @param reg 类型
     * @param key 唯一key，如userId
     * @param <K> key类型
     * @param <V> value类型
     * @return 返回值
     */
    @SuppressWarnings("unchecked")
    public static <K, V> V getAttr(@NotNull TypedReg<K, V> reg, @NotNull K key) {
        Key<K, V> k = new Key<>(reg, key);
        final Object rst = CACHE.getIfPresent(k);
        return (V) rst;
    }

    /**
     * 去掉一个缓存
     *
     * @param reg 类型
     * @param key 唯一key，如userId
     * @param <K> key类型
     */
    public static <K> void ridAttr(TypedReg<K, ?> reg, K key) {
        CACHE.invalidate(new Key<>(reg, key));
    }

    /**
     * 去掉一个缓存
     *
     * @param reg 类型
     * @param key 唯一key，如userId
     * @param <K> key类型
     */
    @SafeVarargs
    public static <K> void ridAttr(TypedReg<K, ?> reg, K... key) {
        if (key == null || key.length == 0) return;
        ridAttrs(reg, Arrays.asList(key));
    }

    /**
     * 去掉一个缓存
     *
     * @param <K> key类型
     * @param reg 类型
     * @param key 唯一key，如userId
     */
    public static <K> void ridAttrs(TypedReg<K, ?> reg, Collection<? extends K> key) {
        if (key == null || key.isEmpty()) return;
        final Set<Key<K, ?>> ks = new HashSet<>();
        for (K k : key) {
            ks.add(new Key<>(reg, k));
        }
        CACHE.invalidate(ks);
    }

    /**
     * 移除所有缓存
     *
     * @param reg 类型
     */
    public static void ridAttrAll(TypedReg<?, ?>... reg) {
        if (reg == null || reg.length == 0) return;
        ridAttrAll(Arrays.asList(reg));
    }

    /**
     * 移除所有缓存
     *
     * @param reg 类型
     */
    public static void ridAttrAll(Collection<? extends TypedReg<?, ?>> reg) {
        if (reg == null || reg.isEmpty()) return;
        final Set<TypedReg<?, ?>> rgs;
        if (reg instanceof Set) {
            //noinspection unchecked
            rgs = (Set<TypedReg<?, ?>>) reg;
        }
        else {
            rgs = new HashSet<>(reg);
        }

        final Set<?> keys = CACHE.asMap()
                                 .keySet()
                                 .stream()
                                 .filter(it -> rgs.contains(it.reg))
                                 .collect(Collectors.toSet());
        CACHE.invalidateAll(keys);
    }

    /**
     * 移除属性及其已存在缓存
     *
     * @param reg 注册类型
     */
    public static void ridLoader(TypedReg<?, ?>... reg) {
        if (reg == null || reg.length == 0) return;
        ridLoader(Arrays.asList(reg));
    }

    /**
     * 移除属性及其已存在缓存
     *
     * @param reg 注册类型
     */
    public static void ridLoader(Collection<? extends TypedReg<?, ?>> reg) {
        if (reg == null || reg.isEmpty()) return;

        for (TypedReg<?, ?> r : reg) {
            LOADER.remove(r);
        }
        ridAttrAll(reg);
    }
}
