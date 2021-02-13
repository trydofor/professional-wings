package pro.fessional.wings.slardar.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.cache.caffeine.CaffeineCacheManager;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import static pro.fessional.wings.slardar.cache.WingsCacheConfig.inLevel;
import static pro.fessional.wings.slardar.cache.WingsCacheConfig.maxInt;

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
    public static Cache<Object, Object> cache(int max, int ttl, int tti, CacheLoader<Object, Object> loader) {
        final Caffeine<Object, Object> builder = builder(max, ttl, tti);
        return loader == null ? builder.build() : builder.build(loader);
    }

    public static class Manager extends CaffeineCacheManager {
        private final WingsCacheConfig wingsCacheConfig;
        private final CacheLoader<Object, Object> loader;

        public Manager(WingsCacheConfig config) {
            this.wingsCacheConfig = config;
            this.loader = null;
            setAllowNullValues(config.isNulls());
        }
        public Manager(WingsCacheConfig config, CacheLoader<Object, Object> loader) {
            this.wingsCacheConfig = config;
            this.loader = loader;
            setAllowNullValues(config.isNulls());
            setCacheLoader(loader);
        }

        @Override
        @NotNull
        protected Cache<Object, Object> createNativeCaffeineCache(@NotNull String name) {

            Caffeine<Object, Object> builder = null;
            for (Map.Entry<String, WingsCacheConfig.Level> entry : wingsCacheConfig.getLevel().entrySet()) {
                // 前缀同
                final String key = entry.getKey();
                if (inLevel(name, key)) {
                    final WingsCacheConfig.Level level = entry.getValue();
                    builder = builder(level.getMaxSize(), level.getMaxLive(), level.getMaxIdle());
                    log.info("Wings Caffeine name={}, level={}", name, key);
                    break;
                }
            }

            if (builder == null) {
                builder = WingsCaffeine.builder(wingsCacheConfig.getMaxSize(),
                        wingsCacheConfig.getMaxLive(),
                        wingsCacheConfig.getMaxIdle());
                log.info("Wings Caffeine name={}, level=default", name);
            }

            return loader == null ? builder.build() : builder.build(loader);
        }
    }
}
