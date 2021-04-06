package pro.fessional.wings.slardar.context;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.Data;
import lombok.ToString;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
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
    private static final ConcurrentHashMap<Reg<?, ?>, Function<?, ?>> HOLDER = new ConcurrentHashMap<>();

    /**
     * 注册一个属性及其加载器
     *
     * @param reg    类型
     * @param loader 加载器，返回null时不被缓存，每次都会调用，因此建议返回`空值`
     * @param <K>    key类型
     * @param <V>    value类型
     */
    public static <K, V> void regLoader(@NotNull Reg<K, V> reg, @NotNull Function<Key<K, V>, V> loader) {
        HOLDER.put(reg, loader);
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
    public static <K, V> void putAttr(@NotNull Reg<K, V> reg, @NotNull K key, @NotNull V value) {
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
    public static <K, V> void putAttr(@NotNull Reg<K, V> reg, @NotNull Map<K, V> map) {
        Map<Key<K, V>, V> kvs = new HashMap<>(map.size());
        for (Map.Entry<K, V> en : map.entrySet()) {
            kvs.put(new Key<>(reg, en.getKey()), en.getValue());
        }
        CACHE.putAll(kvs);
    }

    /**
     * 根据一个type获取属性，如果不存在，null时抛NPE异常
     *
     * @param reg  类型
     * @param key  唯一key，如userId
     * @param elze 唯一key，null时返回elze
     * @param <K>  key类型
     * @param <V>  value类型
     * @return 返回值
     */
    @NotNull
    public static <K, V> V getAttr(@NotNull Reg<K, V> reg, @NotNull K key, @NotNull V elze) {
        final V obj = getAttr(reg, key, false);
        return obj == null ? elze : obj;
    }

    /**
     * 根据一个type获取属性，如果不存在，null时抛NPE异常
     *
     * @param reg 类型
     * @param key 唯一key，如userId
     * @param <K> key类型
     * @param <V> value类型
     * @return 返回值
     */
    @NotNull
    public static <K, V> V getAttr(@NotNull Reg<K, V> reg, @NotNull K key) {
        return getAttr(reg, key, true);
    }

    /**
     * 根据一个type获取属性，如果不存在，选择null或异常
     *
     * @param reg     类型
     * @param key     唯一key，如userId
     * @param nonnull 是否 notnull
     * @param <K>     key类型
     * @param <V>     value类型
     * @return 返回值
     */
    @SuppressWarnings("unchecked")
    @Contract("_,_,true ->!null")
    public static <K, V> V getAttr(@NotNull Reg<K, V> reg, @NotNull K key, boolean nonnull) {
        Key<K, V> k = new Key<>(reg, key);
        final Function<Key<?, ?>, ?> ld = (Function<Key<?, ?>, ?>) HOLDER.get(reg);
        final Object rst = CACHE.get(k, ld);
        if (rst == null && nonnull) {
            throw new NullPointerException("aware=" + reg + ",key=" + key);
        }
        return (V) rst;
    }

    /**
     * 去掉一个缓存
     *
     * @param reg 类型
     * @param key 唯一key，如userId
     * @param <K> key类型
     */
    public static <K> void ridAttr(Reg<K, ?> reg, K key) {
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
    public static <K> void ridAttr(Reg<K, ?> reg, K... key) {
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
    public static <K> void ridAttrs(Reg<K, ?> reg, Collection<? extends K> key) {
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
    public static void ridAttrAll(Reg<?, ?>... reg) {
        if (reg == null || reg.length == 0) return;
        ridAttrAll(Arrays.asList(reg));
    }

    /**
     * 移除所有缓存
     *
     * @param reg 类型
     */
    public static void ridAttrAll(Collection<? extends Reg<?, ?>> reg) {
        if (reg == null || reg.isEmpty()) return;
        final Set<Reg<?, ?>> rgs;
        if (reg instanceof Set) {
            //noinspection unchecked
            rgs = (Set<Reg<?, ?>>) reg;
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
    public static void ridLoader(Reg<?, ?>... reg) {
        if (reg == null || reg.length == 0) return;
        ridLoader(Arrays.asList(reg));
    }

    /**
     * 移除属性及其已存在缓存
     *
     * @param reg 注册类型
     */
    public static void ridLoader(Collection<? extends Reg<?, ?>> reg) {
        if (reg == null || reg.isEmpty()) return;

        for (Reg<?, ?> r : reg) {
            HOLDER.remove(r);
        }
        ridAttrAll(reg);
    }

    // ////
    @Data
    @ToString
    public static class Key<K, R> {
        private final Reg<K, R> reg;
        private final K key;
    }

    /**
     * 使用方法，在接口中构造子类。
     * <pre>
     * public interface Solos {
     *  Reg<Integer, String> PasssaltByUid = new Reg<Integer, String>() {};
     *  Reg<Integer, Set<String>> PermitsByUid = new Reg<Integer, Set<String>>() {};
     * }
     * </pre>
     *
     * @param <K> key类型
     * @param <V> value类型
     */
    public static abstract class Reg<K, V> {
        private final Type keyType;
        private final Type valType;
        private final Class<?> regType;

        protected Reg() {
            final Class<?> clz = getClass();
            final Type sup = clz.getGenericSuperclass();
            final Type[] tps = ((ParameterizedType) sup).getActualTypeArguments();
            keyType = tps[0];
            valType = tps[1];
            regType = clz;
        }

        public Type getKeyType() {
            return keyType;
        }

        public Type getValType() {
            return valType;
        }

        public Class<?> getRegType() {
            return regType;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Reg)) return false;
            Reg<?, ?> reg = (Reg<?, ?>) o;
            return Objects.equals(regType, reg.regType);
        }

        @Override
        public int hashCode() {
            return Objects.hash(regType);
        }
    }
}
