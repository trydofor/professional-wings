package pro.fessional.wings.silencer.spring.bean;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static pro.fessional.wings.silencer.cache.WingsCache.Manager;

/**
 * @author trydofor
 * @since 2019-12-03
 */
@Configuration
@EnableCaching
@Slf4j
@ConditionalOnProperty(name = "spring.wings.cache.enabled", havingValue = "true")
public class WingsCacheConfiguration {

    // //////////
    @Bean
    @ConfigurationProperties("wings.cache")
    public CacheLevel cacheLevel() {
        return new CacheLevel();
    }

    @Data
    public static class CacheLevel {
        private long ttl;
        private long maxIdleTime;
        private int maxSize;

        private Map<String, Conf> level = new HashMap<>();
    }

    @Data
    public static class Conf {
        private long ttl;
        private long maxIdleTime;
        private int maxSize;
    }

    // //////////////////// caffeine ////////////////////
    @Bean(Manager.CAFFEINE)
    public CaffeineCacheManager caffeineCacheManager(CacheLevel conf) {

        final Map<String, Caffeine<Object, Object>> caffeines = new HashMap<>();
        for (Map.Entry<String, Conf> entry : conf.level.entrySet()) {
            Conf c = entry.getValue();
            caffeines.put(entry.getKey(), newCaffeine(c.maxSize, c.ttl, c.maxIdleTime));
        }


        CacheLoader<Object, Object> loader = new CacheLoader<Object, Object>() {
            @Override
            public Object load(@NotNull Object key) {
                return null;
            }

            @Override
            public Object reload(@NotNull Object key, @NotNull Object oldValue) {
                return oldValue;
            }
        };

        CaffeineCacheManager cacheManager = new CaffeineCacheManager() {
            @Override
            @NotNull
            protected Cache<Object, Object> createNativeCaffeineCache(@NotNull String name) {
                for (Map.Entry<String, Caffeine<Object, Object>> entry : caffeines.entrySet()) {
                    if (name.startsWith(entry.getKey())) {
                        return entry.getValue().build(loader);
                    }
                }
                return super.createNativeCaffeineCache(name);
            }
        };

        cacheManager.setAllowNullValues(false);
        cacheManager.setCacheLoader(loader);
        cacheManager.setCaffeine(newCaffeine(conf.maxSize, conf.ttl, conf.maxIdleTime));

        return cacheManager;
    }

    private Caffeine<Object, Object> newCaffeine(int max, long ttl, long idle) {
        return Caffeine.newBuilder()
                       .maximumSize(max <= 0 ? Integer.MAX_VALUE : max)
                       .expireAfterWrite(ttl <= 0 ? Integer.MAX_VALUE : ttl, TimeUnit.SECONDS)
                       .expireAfterAccess(idle <= 0 ? Integer.MAX_VALUE : idle, TimeUnit.SECONDS);
    }
}
