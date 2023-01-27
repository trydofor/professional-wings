package pro.fessional.wings.slardar.cache.cache2k;

import lombok.extern.slf4j.Slf4j;
import org.cache2k.Cache2kBuilder;
import org.cache2k.annotation.NonNull;
import org.cache2k.config.CacheBuildContext;
import org.cache2k.config.Feature;
import org.cache2k.extra.spring.SpringCache2kCache;
import org.cache2k.extra.spring.SpringCache2kCacheManager;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import pro.fessional.wings.slardar.cache.WingsCache;
import pro.fessional.wings.slardar.spring.prop.SlardarCacheProp;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static pro.fessional.wings.slardar.spring.prop.SlardarCacheProp.inLevel;
import static pro.fessional.wings.slardar.spring.prop.SlardarCacheProp.maxInt;

/**
 * @author trydofor
 * @since 2023-01-25
 */
@Slf4j
public class WingsCache2k {

    @Contract("_,_,_,_->param1")
    public static <K, V> Cache2kBuilder<K, V> builder(Cache2kBuilder<K, V> builder, int max, int ttl, int tti) {
        builder.entryCapacity(maxInt(max))
               .expireAfterWrite(maxInt(ttl), TimeUnit.SECONDS);
        //
        if (tti > 0) {
            builder.idleScanTime(maxInt(tti), TimeUnit.SECONDS);
        }
        return builder;
    }

    public static class Manager extends SpringCache2kCacheManager implements WingsCache.State {

        private final SlardarCacheProp slardarCacheProp;
        private final ConcurrentHashMap<String, NullsCache2k> nullsCache = new ConcurrentHashMap<>();
        private Function<Cache2kBuilder<?, ?>, Cache2kBuilder<?, ?>> defaultSetup = null;

        public Manager(SlardarCacheProp slardarCacheProp) {
            this.slardarCacheProp = slardarCacheProp;
            super.defaultSetup(this::levelingBuilder);
            super.setAllowUnknownCache(true);
        }


        @Override
        @NotNull
        public SpringCache2kCacheManager defaultSetup(Function<Cache2kBuilder<?, ?>, Cache2kBuilder<?, ?>> f) {
            defaultSetup = f;
            return this;
        }

        @Override
        @NonNull
        public SpringCache2kCache getCache(@NotNull String name) {
            final int size = slardarCacheProp.getNullSize();
            if (size < 0) {
                return super.getCache(name);
            }
            else {
                return nullsCache.computeIfAbsent(name, k -> new NullsCache2k(super.getCache(k).getNativeCache(), size, slardarCacheProp.getNullLive()));
            }
        }

        /**
         * <a href="https://github.com/cache2k/cache2k/issues/200">defaultSetup know which cache to build</a>
         */
        protected Cache2kBuilder<?, ?> levelingBuilder(@NotNull Cache2kBuilder<?, ?> builder) {

            if (defaultSetup != null) {
                defaultSetup.apply(builder);
            }

            builder.config().getFeatures().add(new Feature() {
                @Override
                public <K, V> void enlist(@NotNull CacheBuildContext<K, V> ctx) {
                    final String name = ctx.getName();
                    final Cache2kBuilder<K, V> bld = ctx.getConfig().builder();

                    for (Map.Entry<String, SlardarCacheProp.Conf> entry : slardarCacheProp.getLevel().entrySet()) {
                        // 前缀同
                        final String key = entry.getKey();
                        if (inLevel(name, key)) {
                            final SlardarCacheProp.Conf level = entry.getValue();
                            builder(bld, level.getMaxSize(), level.getMaxLive(), level.getMaxIdle());
                            log.info("Wings Cache2k name={}, level={}", name, key);
                            return;
                        }
                    }

                    final SlardarCacheProp.Conf common = slardarCacheProp.getCommon();
                    builder(bld, common.getMaxSize(), common.getMaxLive(), common.getMaxIdle());
                    log.info("Wings Cache2k name={}, level=default", name);
                }
            });

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
            if (cache == null) return Collections.emptySet();
            return cache.getNativeCache().keys();
        }
    }
}
