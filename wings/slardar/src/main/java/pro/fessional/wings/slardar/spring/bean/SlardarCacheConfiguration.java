package pro.fessional.wings.slardar.spring.bean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.AbstractCachingConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.annotation.ProxyCachingConfiguration;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.cache.interceptor.CacheInterceptor;
import org.springframework.cache.interceptor.CacheOperationSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import pro.fessional.wings.slardar.cache.WingsCacheHelper;
import pro.fessional.wings.slardar.cache.caffeine.WingsCaffeine;
import pro.fessional.wings.slardar.cache.spring.CacheEvictResult;
import pro.fessional.wings.slardar.cache.spring.WingsCacheAnnoOprSource;
import pro.fessional.wings.slardar.cache.spring.WingsCacheInterceptor;
import pro.fessional.wings.slardar.spring.prop.SlardarCacheProp;
import pro.fessional.wings.slardar.spring.prop.SlardarEnabledProp;

import java.util.Map;

import static pro.fessional.wings.slardar.cache.WingsCache.Manager;

/**
 * @author trydofor
 * @see ProxyCachingConfiguration
 * @since 2019-12-03
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(name = SlardarEnabledProp.Key$caching, havingValue = "true")
@EnableCaching
public class SlardarCacheConfiguration {

    private static final Log log = LogFactory.getLog(SlardarCacheConfiguration.class);

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnExpression("${" + SlardarEnabledProp.Key$caching + ":false} && ${" + SlardarEnabledProp.Key$cachingAop + ":false}")
    public static class SlardarCacheAopConfiguration extends AbstractCachingConfiguration {

        @Primary
        @Bean
        public CacheOperationSource wingsCacheOperationSource() {
            log.info("Slardar spring-bean wingsCacheOperationSource Primary");
            return new WingsCacheAnnoOprSource();
        }

        @Primary
        @Bean
        public CacheInterceptor wingsCacheInterceptor(CacheOperationSource cacheOperationSource) {
            log.info("Slardar spring-bean wingsCacheInterceptor Primary");
            CacheEvictResult.wingsSupport = true;
            WingsCacheInterceptor interceptor = new WingsCacheInterceptor();
            interceptor.configure(this.errorHandler, this.keyGenerator, this.cacheResolver, this.cacheManager);
            interceptor.setCacheOperationSource(cacheOperationSource);
            return interceptor;
        }
    }

    // //////////////////// caffeine ////////////////////
    @Bean(Manager.Memory)
    @ConditionalOnMissingBean(CaffeineCacheManager.class)
    public CaffeineCacheManager caffeineCacheManager(SlardarCacheProp conf) {
        log.info("Slardar spring-bean caffeineCacheManager as " + Manager.Memory);
        return new WingsCaffeine.Manager(conf);
    }

    @Bean
    @Primary
    public CacheManager cacheManager(Map<String, CacheManager> managers, SlardarCacheProp prop) {
        WingsCacheHelper.setManagers(managers);

        CacheManager pre = null;
        String cnm = null;
        String prim = prop.getPrimary();
        for (Map.Entry<String, CacheManager> en : managers.entrySet()) {
            final String name = en.getKey();
            if (name.equalsIgnoreCase(prim)) {
                log.info("Slardar spring-bean cacheManager Primary=" + name);
                return en.getValue();
            }
            else if (pre == null && name.startsWith(prim)) {
                cnm = name;
                pre = en.getValue();
            }
        }
        log.info("Slardar spring-bean cacheManager Primary=" + cnm);
        return pre;
    }
}
