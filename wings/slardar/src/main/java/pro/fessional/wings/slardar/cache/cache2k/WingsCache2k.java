package pro.fessional.wings.slardar.cache.cache2k;

import org.cache2k.Cache2kBuilder;
import org.cache2k.config.ToggleFeature;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import pro.fessional.wings.slardar.cache.WingsCache;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author trydofor
 * @since 2023-01-25
 */
public class WingsCache2k {

    public static Class<? extends ToggleFeature> FeatureJmx = initFeatureJmx();

    @SuppressWarnings("unchecked")
    private static Class<? extends ToggleFeature> initFeatureJmx() {
        try {
            return (Class<? extends ToggleFeature>) Class.forName("org.cache2k.extra.jmx.JmxSupport");
        }
        catch (ClassNotFoundException e) {
            return null;
        }
    }

    private static final AtomicLong NameCounter = new AtomicLong(1);

    public static String name(Class<?> clz, String use) {
        if (clz == null) return null;
        return clz.getName() + WingsCache.Joiner + use + "-" + NameCounter.getAndIncrement();
    }

    @NotNull
    public static Cache2kBuilder<Object, Object> builder(Class<?> clz, String use, int max, int ttl, int tti) {
        return build(Cache2kBuilder.forUnknownTypes().name(name(clz, use)), max, ttl, tti, true);
    }

    @NotNull
    public static <K, V> Cache2kBuilder<K, V> builder(Class<?> clz, String use, int max, int ttl, int tti, Class<K> k, Class<V> v) {
        return build(Cache2kBuilder.of(k, v).name(name(clz, use)), max, ttl, tti, true);
    }

    @NotNull
    public static Cache2kBuilder<Object, Object> builder(Class<?> clz, String use, int max, Duration ttl, Duration tti) {
        return build(Cache2kBuilder.forUnknownTypes().name(name(clz, use)), max, ttl, tti, true);
    }

    @NotNull
    public static <K, V> Cache2kBuilder<K, V> builder(Class<?> clz, String use, int max, Duration ttl, Duration tti, Class<K> k, Class<V> v) {
        return build(Cache2kBuilder.of(k, v).name(name(clz, use)), max, ttl, tti, true);
    }

    @Contract("_,_,_,_,_->param1")
    public static <K, V> Cache2kBuilder<K, V> build(Cache2kBuilder<K, V> bld, int max, int ttl, int tti, boolean feature) {
        return build(bld, max, ttl > 0 ? Duration.ofSeconds(ttl) : null, tti > 0 ? Duration.ofSeconds(tti) : null, feature);
    }

    @Contract("_,_,_,_,_->param1")
    public static <K, V> Cache2kBuilder<K, V> build(Cache2kBuilder<K, V> bld, int max, Duration ttl, Duration tti, boolean feature) {

        if (feature && FeatureJmx != null) {
            bld.enable(FeatureJmx);
        }

        if (max > 0) {
            bld.entryCapacity(max);
        }
        if (ttl != null && ttl.toSeconds() > 0) {
            bld.expireAfterWrite(ttl);
        }
        if (tti != null && tti.toSeconds() > 0) {
            bld.idleScanTime(tti);
        }

        return bld;
    }
}
