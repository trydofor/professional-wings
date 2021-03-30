package pro.fessional.wings.slardar.context;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.Data;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

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

    private static final Cache<K, Object> CACHE = Caffeine.newBuilder()
                                                          .expireAfterAccess(12, TimeUnit.HOURS)
                                                          .build();
    private static final ConcurrentHashMap<Enum<?>, Function<K, Object>> HOLDER = new ConcurrentHashMap<>();

    /**
     * 注册一个属性及其加载器
     *
     * @param type   类型
     * @param loader 加载器
     * @param <T>    类型
     */
    public static <T extends Enum<?> & Aware> void register(T type, Function<K, Object> loader) {
        HOLDER.put(type, loader);
    }

    /**
     * 根据一个type获取属性，如果不存在，null时抛NPE异常
     *
     * @param type 类型
     * @param key  唯一key，如userId
     * @param <T>  type类型
     * @param <R>  返回值
     * @return 返回值
     */
    @NotNull
    public static <T extends Enum<?> & Aware, R> R get(T type, Object key) {
        return get(type, key, true);
    }

    /**
     * 根据一个type获取属性，如果不存在，选择null或异常
     *
     * @param type    类型
     * @param key     唯一key，如userId
     * @param nonnull 是否 notnull
     * @param <T>     type类型
     * @param <R>     返回值
     * @return 返回值
     */
    @SuppressWarnings("unchecked")
    @Contract("_,_,true ->!null")
    public static <T extends Enum<?> & Aware, R> R get(T type, Object key, boolean nonnull) {
        K k = new K(type, key);
        final Object rst = CACHE.get(k, HOLDER.get(type));
        if (rst == null && nonnull) {
            throw new NullPointerException("type=" + type + ",key=" + key);
        }
        return (R) rst;
    }

    /**
     * 移除一个缓存
     *
     * @param type 类型
     * @param key  唯一key，如userId
     * @param <T>  类型
     */
    public static <T extends Enum<?> & Aware> void remove(T type, Object key) {
        CACHE.invalidate(new K(type, key));
    }

    /**
     * 移除一个缓存
     *
     * @param type 类型
     * @param <T>  类型
     */
    @SuppressWarnings("EqualsBetweenInconvertibleTypes")
    public static <T extends Enum<?> & Aware> void remove(T type) {
        HOLDER.remove(type);
        final Set<K> keys = CACHE.asMap()
                                 .keySet()
                                 .stream()
                                 .filter(it -> Objects.equals(type, it.type))
                                 .collect(Collectors.toSet());
        CACHE.invalidateAll(keys);
    }

    //
    public interface Aware {}

    @Data
    public static class K {
        private final Enum<?> type;
        private final Object key;
    }
}
