package pro.fessional.wings.slardar.cache.cache2k;

import lombok.extern.slf4j.Slf4j;
import org.cache2k.Cache2kBuilder;
import org.cache2k.config.CacheBuildContext;
import org.cache2k.config.Feature;
import org.cache2k.config.ToggleFeature;
import org.cache2k.extra.spring.SpringCache2kCache;
import org.cache2k.extra.spring.SpringCache2kCacheManager;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import pro.fessional.wings.slardar.cache.WingsCache;
import pro.fessional.wings.slardar.spring.prop.SlardarCacheProp;

import java.time.Duration;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

import static pro.fessional.wings.slardar.spring.prop.SlardarCacheProp.inLevel;

/**
 * @author trydofor
 * @since 2023-01-25
 */
@Slf4j
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
        return clz.getName() + "." + use + "-" + NameCounter.getAndIncrement();
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

    public static class Manager extends SpringCache2kCacheManager implements WingsCache.State {

        private final SlardarCacheProp slardarCacheProp;
        private final ConcurrentHashMap<String, NullsCache2k> nullsCache = new ConcurrentHashMap<>();

        private final Feature levelingFeature;
        private Function<Cache2kBuilder<?, ?>, Cache2kBuilder<?, ?>> defaultSetup = null;

        public Manager(String name, SlardarCacheProp slardarCacheProp) {
            super(name == null ? DEFAULT_SPRING_CACHE_MANAGER_NAME : name);
            this.slardarCacheProp = slardarCacheProp;
            this.levelingFeature = new Feature() {
                @Override
                public <K, V> void enlist(@NotNull CacheBuildContext<K, V> ctx) {
                    final String name = ctx.getName();
                    final Cache2kBuilder<K, V> bld = ctx.getConfig().builder();

                    for (Map.Entry<String, SlardarCacheProp.Conf> entry : slardarCacheProp.getLevel().entrySet()) {
                        // same prefix
                        final String key = entry.getKey();
                        if (inLevel(name, key)) {
                            final SlardarCacheProp.Conf level = entry.getValue();
                            build(bld, level.getMaxSize(), level.getMaxLive(), level.getMaxIdle(), false);
                            log.info("Wings Cache2k name={}, level={}", name, key);
                            return;
                        }
                    }

                    final SlardarCacheProp.Conf common = slardarCacheProp.getCommon();
                    build(bld, common.getMaxSize(), common.getMaxLive(), common.getMaxIdle(), false);
                    log.info("Wings Cache2k name={}, level=default", name);
                }
            };
            super.defaultSetup(this::levelingBuilder);
            super.setAllowUnknownCache(true);
        }


        @Override
        @NotNull
        public SpringCache2kCacheManager defaultSetup(@NotNull Function<Cache2kBuilder<?, ?>, Cache2kBuilder<?, ?>> f) {
            defaultSetup = f;
            return this;
        }

        @Override
        @NotNull
        public SpringCache2kCache getCache(@NotNull String name) {
            final int size = slardarCacheProp.getNullSize();
            if (size < 0) {
                return super.getCache(name);
            }
            else {
                return nullsCache.computeIfAbsent(name, k -> {
                    final SpringCache2kCache cache = super.getCache(k);
                    return new NullsCache2k(cache.getNativeCache(), size, slardarCacheProp.getNullLive());
                });
            }
        }

        /**
         * <a href="https://github.com/cache2k/cache2k/issues/200">defaultSetup know which cache to build</a>
         */
        protected Cache2kBuilder<?, ?> levelingBuilder(@NotNull Cache2kBuilder<?, ?> builder) {

            if (defaultSetup != null) {
                defaultSetup.apply(builder);
            }

            if (FeatureJmx != null) {
                builder.enable(FeatureJmx);
            }

            builder.config().getFeatures().add(levelingFeature);

            return builder;
        }

        @Override
        @NotNull
        public Map<String, Integer> statsCacheSize() {
            final Map<String, Integer> stats = new TreeMap<>();
            final Map<String, SpringCache2kCache> cacheMap = super.getCacheMap();
            for (Map.Entry<String, SpringCache2kCache> en : cacheMap.entrySet()) {
                final SpringCache2kCache cache = en.getValue();
                stats.put(en.getKey(), cache == null ? -1 : cache.getNativeCache().entries().size());
            }

            return stats;
        }

        @Override
        @NotNull
        public Set<Object> statsCacheKeys(String name) {
            final SpringCache2kCache cache = getCache(name);
            return cache.getNativeCache().keys();
        }
    }
}
