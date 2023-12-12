package pro.fessional.wings.slardar.cache.cache2k;

import org.cache2k.Cache2kBuilder;
import org.cache2k.config.ToggleFeature;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import pro.fessional.wings.slardar.cache.WingsCache.Naming;

import java.time.Duration;

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

    /**
     * construct UnknownTypes builder with max size, ttl and tti in second
     *
     * @param owner owner part of name
     * @param use   usage part of name
     * @param max   capacity, max size
     * @param ttl   time to live in second, skip if le 0
     * @param tti   time to idle in second, skip if le 0
     */
    @NotNull
    public static Cache2kBuilder<Object, Object> builder(Class<?> owner, String use, int max, int ttl, int tti) {
        return builder(Cache2kBuilder.forUnknownTypes().name(Naming.use(owner, use)), max, ttl, tti, true);
    }

    /**
     * construct K-V type builder with max size, ttl and tti in second
     *
     * @param owner owner part of name
     * @param use   usage part of name
     * @param max   capacity, max size
     * @param ttl   time to live in second, skip if le 0
     * @param tti   time to idle in second, skip if le 0
     */

    @NotNull
    public static <K, V> Cache2kBuilder<K, V> builder(Class<?> owner, String use, int max, int ttl, int tti, Class<K> k, Class<V> v) {
        return builder(Cache2kBuilder.of(k, v).name(Naming.use(owner, use)), max, ttl, tti, true);
    }

    /**
     * construct UnknownTypes builder with max size, ttl and tti in second
     *
     * @param owner owner part of name
     * @param use   usage part of name
     * @param max   capacity, max size
     * @param ttl   time to live, skip if null
     * @param tti   time to idle, skip if null
     */
    @NotNull
    public static Cache2kBuilder<Object, Object> builder(Class<?> owner, String use, int max, Duration ttl, Duration tti) {
        return builder(Cache2kBuilder.forUnknownTypes().name(Naming.use(owner, use)), max, ttl, tti, true);
    }

    /**
     * construct K-V type builder with max size, ttl and tti in second
     *
     * @param owner owner part of name
     * @param use   usage part of name
     * @param max   capacity, max size
     * @param ttl   time to live, skip if null
     * @param tti   time to idle, skip if null
     */
    @NotNull
    public static <K, V> Cache2kBuilder<K, V> builder(Class<?> owner, String use, int max, Duration ttl, Duration tti, Class<K> k, Class<V> v) {
        return builder(Cache2kBuilder.of(k, v).name(Naming.use(owner, use)), max, ttl, tti, true);
    }

    /**
     * builder with max size, ttl and tti in second, and feather
     *
     * @param bld     the builder
     * @param max     capacity, max size
     * @param ttl     time to live in second, skip if null
     * @param tti     time to idle in second, skip if null
     * @param feature whether enable feather
     */
    @Contract("_,_,_,_,_->param1")
    public static <K, V> Cache2kBuilder<K, V> builder(Cache2kBuilder<K, V> bld, int max, int ttl, int tti, boolean feature) {
        return builder(bld, max, ttl > 0 ? Duration.ofSeconds(ttl) : null, tti > 0 ? Duration.ofSeconds(tti) : null, feature);
    }

    /**
     * builder with max size, ttl and tti in second, and feather
     *
     * @param bld     the builder
     * @param max     capacity, max size
     * @param ttl     time to live, skip if null
     * @param tti     time to idle, skip if null
     * @param feature whether enable feather
     */
    @Contract("_,_,_,_,_->param1")
    public static <K, V> Cache2kBuilder<K, V> builder(Cache2kBuilder<K, V> bld, int max, Duration ttl, Duration tti, boolean feature) {

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
