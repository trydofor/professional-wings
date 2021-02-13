package pro.fessional.wings.slardar.spring.bean;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.spring.cache.HazelcastCacheManager;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pro.fessional.wings.slardar.cache.WingsCacheConfig;
import pro.fessional.wings.slardar.cache.WingsCaffeine;
import pro.fessional.wings.slardar.cache.WingsHazelcast;

import static pro.fessional.wings.slardar.cache.WingsCache.Manager;

/**
 * @author trydofor
 * @since 2019-12-03
 */
@Configuration
@ConditionalOnProperty(name = "spring.wings.slardar.enabled.caching", havingValue = "true")
@EnableCaching
public class WingsCacheConfiguration {

    private static final Log logger = LogFactory.getLog(WingsCacheConfiguration.class);

    // //////////
    @Bean
    @ConfigurationProperties("wings.slardar.cache")
    public WingsCacheConfig wingsCacheConfig() {
        logger.info("Wings conf cacheLevel");
        return new WingsCacheConfig();
    }

    // //////////////////// resolver ////////////////////
    @Configuration
    @ConditionalOnMissingBean(name = "cacheManager")
    @RequiredArgsConstructor
    public static class CachingPrimary extends CachingConfigurerSupport {
        private final ApplicationContext context;
        private final WingsCacheConfig conf;

        @Override
        public CacheManager cacheManager() {
            final String[] names = context.getBeanNamesForType(CacheManager.class);
            String prim = conf.getPrimary();
            CacheManager pre = null;
            for (String name : names) {
                if (name.equalsIgnoreCase(prim)) {
                    return context.getBean(name, CacheManager.class);
                } else if (pre == null && name.startsWith(prim)) {
                    pre = context.getBean(name, CacheManager.class);
                }
            }
            return pre;
        }
    }

    // //////////////////// caffeine ////////////////////
    @Configuration
    @ConditionalOnClass(CaffeineCacheManager.class)
    public static class CaffeineCacheManagerConfiguration {
        @Bean(Manager.Memory)
        @ConditionalOnMissingBean
        public CaffeineCacheManager caffeineCacheManager(WingsCacheConfig conf) {
            logger.info("Wings conf " + Manager.Memory);
            return new WingsCaffeine.Manager(conf);
        }
    }

    // //////////////////// hazelcast ////////////////////
    @Configuration
    @ConditionalOnClass({HazelcastInstance.class, HazelcastCacheManager.class})
    public static class HazelcastCacheConfiguration {
        @ConditionalOnMissingBean
        @Bean(Manager.Server)
        public HazelcastCacheManager hazelcastCacheManager(WingsCacheConfig conf, ObjectProvider<HazelcastInstance> hazelcastInstance) {
            logger.info("Wings conf " + Manager.Server);
            return new WingsHazelcast.Manager(conf, hazelcastInstance.getIfAvailable());
        }
    }
}
