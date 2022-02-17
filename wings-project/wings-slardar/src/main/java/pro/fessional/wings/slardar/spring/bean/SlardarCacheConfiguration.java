package pro.fessional.wings.slardar.spring.bean;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.spring.cache.HazelcastCacheManager;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pro.fessional.wings.slardar.cache.WingsCache;
import pro.fessional.wings.slardar.cache.WingsCacheHelper;
import pro.fessional.wings.slardar.cache.caffeine.WingsCaffeine;
import pro.fessional.wings.slardar.cache.hazelcast.WingsHazelcast;
import pro.fessional.wings.slardar.spring.prop.SlardarCacheProp;
import pro.fessional.wings.slardar.spring.prop.SlardarEnabledProp;

import static pro.fessional.wings.slardar.cache.WingsCache.Manager;

/**
 * @author trydofor
 * @since 2019-12-03
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(name = SlardarEnabledProp.Key$caching, havingValue = "true")
@EnableCaching
public class SlardarCacheConfiguration {

    private static final Log logger = LogFactory.getLog(SlardarCacheConfiguration.class);

    // //////////////////// caffeine ////////////////////
    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass(CaffeineCacheManager.class)
    public static class CaffeineCacheManagerConfiguration {
        @Bean(Manager.Memory)
        @ConditionalOnMissingBean(CaffeineCacheManager.class)
        public CaffeineCacheManager caffeineCacheManager(SlardarCacheProp conf) {
            logger.info("Wings conf " + Manager.Memory);
            return new WingsCaffeine.Manager(conf);
        }
    }

    // //////////////////// hazelcast ////////////////////
    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass({HazelcastInstance.class, HazelcastCacheManager.class})
    public static class HazelcastCacheConfiguration {
        @ConditionalOnMissingBean(name = Manager.Server)
        @Bean(Manager.Server)
        public HazelcastCacheManager hazelcastCacheManager(SlardarCacheProp conf, ObjectProvider<HazelcastInstance> hazelcastInstance) {
            logger.info("Wings conf " + Manager.Server);
            return new WingsHazelcast.Manager(conf, hazelcastInstance.getIfAvailable());
        }
    }

    // //////////////////// resolver ////////////////////
    @Configuration(proxyBeanMethods = false)
    @ConditionalOnMissingBean(name = "cacheManager")
    @RequiredArgsConstructor
    public static class CachingPrimary extends CachingConfigurerSupport {
        private final ApplicationContext context;
        private final SlardarCacheProp conf;

        @Override
        public CacheManager cacheManager() {
            final String[] names = context.getBeanNamesForType(CacheManager.class);
            String prim = conf.getPrimary();
            CacheManager pre = null;
            for (String name : names) {
                if (name.equalsIgnoreCase(prim)) {
                    return context.getBean(name, CacheManager.class);
                }
                else if (pre == null && name.startsWith(prim)) {
                    pre = context.getBean(name, CacheManager.class);
                }
            }
            return pre;
        }
    }

    @Bean
    public WingsCacheHelper wingsCacheHelper(
            @Qualifier(WingsCache.Manager.Server) CacheManager ser,
            @Qualifier(WingsCache.Manager.Memory) CacheManager mem
    ) {
        logger.info("Wings conf WingsCacheHelper");
        return new WingsCacheHelper(ser, mem) {};
    }
}
