package pro.fessional.wings.slardar.cache.caffeine;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import pro.fessional.wings.slardar.cache.NullsCache;
import pro.fessional.wings.slardar.cache.WingsCache;
import pro.fessional.wings.slardar.spring.prop.SlardarCacheProp;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import static pro.fessional.wings.slardar.spring.prop.SlardarCacheProp.inLevel;
import static pro.fessional.wings.slardar.spring.prop.SlardarCacheProp.maxInt;

/**
 * @author trydofor
 * @since 2021-02-11
 */
@Slf4j
public class WingsCaffeine {

    /**
     * 创建一个caffeine (Builder)
     *
     * @param max 最大值
     * @param ttl 存货秒数
     * @param tti 空闲秒数
     * @return cache
     */
    @NotNull
    public static Caffeine<Object, Object> builder(int max, int ttl, int tti) {
        return Caffeine.newBuilder()
                       .maximumSize(maxInt(max))
                       .expireAfterWrite(maxInt(ttl), TimeUnit.SECONDS)
                       .expireAfterAccess(maxInt(tti), TimeUnit.SECONDS);
    }

    /**
     * 创建一个caffeine Cache，使用 EchoLoader
     *
     * @param max 最大值
     * @param ttl 存货秒数
     * @param tti 空闲秒数
     * @return cache
     */
    @NotNull
    public static <K, V> Cache<K, V> newCache(int max, int ttl, int tti, CacheLoader<K, V> loader) {
        final Caffeine<Object, Object> builder = builder(max, ttl, tti);
        return loader == null ? builder.build() : builder.build(loader);
    }

    public static class Manager extends CaffeineCacheManager implements WingsCache.State {

        private final SlardarCacheProp slardarCacheProp;
        private final CacheLoader<Object, Object> loader;
        private final ConcurrentHashMap<String, Cache<Object, Object>> holder = new ConcurrentHashMap<>();

        public Manager(SlardarCacheProp config) {
            this(config, null);
        }

        public Manager(SlardarCacheProp config, CacheLoader<Object, Object> loader) {
            this.slardarCacheProp = config;
            this.loader = loader;
            setCacheLoader(loader);
        }

        @Override
        public org.springframework.cache.Cache getCache(@NotNull String name) {
            final org.springframework.cache.Cache cache = super.getCache(name);

            if (slardarCacheProp.isNullWeak()) {
                return new NullsCache(cache, false);
            }
            else if (slardarCacheProp.isNullSkip()) {
                return new NullsCache(cache, true);
            }
            else {
                return cache;
            }
        }

        @Override
        @NotNull
        protected Cache<Object, Object> createNativeCaffeineCache(@NotNull String name) {

            Caffeine<Object, Object> builder = null;
            for (Map.Entry<String, SlardarCacheProp.Conf> entry : slardarCacheProp.getLevel().entrySet()) {
                // 前缀同
                final String key = entry.getKey();
                if (inLevel(name, key)) {
                    final SlardarCacheProp.Conf level = entry.getValue();
                    builder = builder(level.getMaxSize(), level.getMaxLive(), level.getMaxIdle());
                    log.info("Wings Caffeine name={}, level={}", name, key);
                    break;
                }
            }

            if (builder == null) {
                final SlardarCacheProp.Conf common = slardarCacheProp.getCommon();
                builder = WingsCaffeine.builder(common.getMaxSize(),
                        common.getMaxLive(),
                        common.getMaxIdle());
                log.info("Wings Caffeine name={}, level=default", name);
            }

            final Cache<Object, Object> cache = loader == null ? builder.build() : builder.build(loader);
            holder.put(name, cache);
            return cache;
        }

        @Override
        @NotNull
        public Map<String, Integer> statsCacheSize() {
            final Collection<String> names = super.getCacheNames();
            final Map<String, Integer> stats = new TreeMap<>();
            for (String name : names) {
                final Cache<Object, Object> cache = holder.get(name);
                stats.put(name, cache == null ? -1 : (int) cache.estimatedSize());
            }
            return stats;
        }

        @Override
        @NotNull
        public Set<Object> statsCacheKeys(String name) {
            final Cache<Object, Object> cache = holder.get(name);
            if (cache == null) return Collections.emptySet();
            return cache.asMap().keySet();
        }
    }
}
