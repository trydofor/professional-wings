package pro.fessional.wings.slardar.cache.cache2k;

import lombok.extern.slf4j.Slf4j;
import org.cache2k.Cache2kBuilder;
import org.cache2k.config.CacheBuildContext;
import org.cache2k.config.Feature;
import org.cache2k.extra.spring.SpringCache2kCache;
import org.cache2k.extra.spring.SpringCache2kCacheManager;
import org.jetbrains.annotations.NotNull;
import pro.fessional.wings.slardar.cache.WingsCache;
import pro.fessional.wings.slardar.spring.prop.SlardarCacheProp;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * @author trydofor
 * @since 2023-11-01
 */
@Slf4j
public class WingsCache2kManager extends SpringCache2kCacheManager implements WingsCache.State {

    private final SlardarCacheProp slardarCacheProp;
    private final ConcurrentHashMap<String, NullsCache2k> nullsCache = new ConcurrentHashMap<>();

    private final Feature levelingFeature;
    private Function<Cache2kBuilder<?, ?>, Cache2kBuilder<?, ?>> defaultSetup = null;

    public WingsCache2kManager(String name, SlardarCacheProp slardarCacheProp) {
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
                    if (WingsCache.Naming.inLevel(name, key)) {
                        final SlardarCacheProp.Conf level = entry.getValue();
                        WingsCache2k.builder(bld, level.getMaxSize(), level.getMaxLive(), level.getMaxIdle(), false);
                        log.info("Wings Cache2k name={}, level={}", name, key);
                        return;
                    }
                }

                final SlardarCacheProp.Conf common = slardarCacheProp.getCommon();
                WingsCache2k.builder(bld, common.getMaxSize(), common.getMaxLive(), common.getMaxIdle(), false);
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

        if (WingsCache2k.FeatureJmx != null) {
            builder.enable(WingsCache2k.FeatureJmx);
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
