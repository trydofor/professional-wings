package pro.fessional.wings.silencer.spring.bean;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pro.fessional.wings.silencer.cache.CaffeineUtil;

import java.util.HashMap;
import java.util.Map;

import static pro.fessional.wings.silencer.cache.WingsCache.Manager;

/**
 * @author trydofor
 * @since 2019-12-03
 */
@Configuration
@Slf4j
@ConditionalOnProperty(name = "spring.wings.cache.enabled", havingValue = "true")
public class WingsCacheConfiguration {

    private static final Log logger = LogFactory.getLog(WingsCacheConfiguration.class);

    // //////////
    @Bean
    @ConfigurationProperties("wings.cache")
    public CacheLevel cacheLevel() {
        logger.info("config bean cacheLevel");
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
    @Configuration
    @EnableCaching
    @ConditionalOnClass(name = {"org.springframework.cache.caffeine.CaffeineCacheManager"})
    static class CaffeineCacheManagerConfiguration {
        @Bean(Manager.CAFFEINE)
        public CaffeineCacheManager caffeineCacheManager(CacheLevel conf) {

            logger.info("config bean caffeineCacheManager");
            final Map<String, Caffeine<Object, Object>> caffeines = new HashMap<>();
            for (Map.Entry<String, Conf> entry : conf.level.entrySet()) {
                Conf c = entry.getValue();
                caffeines.put(entry.getKey(), CaffeineUtil.build(c.maxSize, c.ttl, c.maxIdleTime));
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
            cacheManager.setCaffeine(CaffeineUtil.build(conf.maxSize, conf.ttl, conf.maxIdleTime));

            return cacheManager;
        }
    }

    // //////////////////// redis ////////////////////

}
