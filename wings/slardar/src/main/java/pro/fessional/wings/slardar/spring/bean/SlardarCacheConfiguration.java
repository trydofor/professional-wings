package pro.fessional.wings.slardar.spring.bean;

import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cache2k.extra.spring.SpringCache2kCacheManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.AbstractCachingConfiguration;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.annotation.ProxyCachingConfiguration;
import org.springframework.cache.interceptor.AbstractCacheResolver;
import org.springframework.cache.interceptor.CacheInterceptor;
import org.springframework.cache.interceptor.CacheOperationSource;
import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.cache.interceptor.SimpleCacheResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import pro.fessional.wings.slardar.cache.WingsCache;
import pro.fessional.wings.slardar.cache.WingsCacheHelper;
import pro.fessional.wings.slardar.cache.cache2k.WingsCache2k;
import pro.fessional.wings.slardar.cache.spring.CacheEvictMultiKeys;
import pro.fessional.wings.slardar.cache.spring.WingsCacheAnnoOprSource;
import pro.fessional.wings.slardar.cache.spring.WingsCacheInterceptor;
import pro.fessional.wings.slardar.cache.spring.WingsCacheResolver;
import pro.fessional.wings.slardar.spring.prop.SlardarCacheProp;
import pro.fessional.wings.slardar.spring.prop.SlardarEnabledProp;
import pro.fessional.wings.spring.consts.OrderedSlardarConst;

import java.util.HashMap;
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
@AutoConfigureOrder(OrderedSlardarConst.CacheConfiguration)
public class SlardarCacheConfiguration {

    private static final Log log = LogFactory.getLog(SlardarCacheConfiguration.class);

    public SlardarCacheConfiguration(@Value("${spring.jmx.enabled:false}") boolean jmx) {
        log.info("Slardar spring.jmx.enabled=" + jmx + ", if can NOT disable, check IDEA 'Disable JMX agent' options");
        if (!jmx) {
            if (WingsCache2k.FeatureJmx != null) {
                log.info("Slardar cache2k jmx=false");
                WingsCache2k.FeatureJmx = null;
            }
        }
    }

    @Bean(Manager.Memory)
    @ConditionalOnMissingBean(SpringCache2kCacheManager.class)
    public SpringCache2kCacheManager cache2kCacheManager(SlardarCacheProp conf) {
        log.info("Slardar spring-bean cache2kCacheManager as " + Manager.Memory);
        // https://github.com/cache2k/cache2k/issues/123
        return new WingsCache2k.Manager("spring-wings-" + hashCode(), conf);
    }

    // //////////////////// aop ////////////////////
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
            CacheEvictMultiKeys.wingsSupport = true;
            WingsCacheInterceptor interceptor = new WingsCacheInterceptor();
            interceptor.configure(this.errorHandler, this.keyGenerator, this.cacheResolver, this.cacheManager);
            interceptor.setCacheOperationSource(cacheOperationSource);
            return interceptor;
        }
    }


    // //////////////////// cache ////////////////////
    @Configuration(proxyBeanMethods = false)
    @RequiredArgsConstructor
    @AutoConfigureOrder(OrderedSlardarConst.CachingConfigurerSupport)
    public static class SlardarCachingConfigurerSupport implements CachingConfigurer {
        private final Map<String, CacheManager> managers;
        private final Map<String, AbstractCacheResolver> resolvers;
        private final SlardarCacheProp cacheProp;
        private final ConfigurableListableBeanFactory beanFactory;

        @Bean
        @Primary
        @Override
        public CacheManager cacheManager() {
            final Map<String, CacheManager> resolverMap = new HashMap<>(managers);
            for (Map.Entry<String, AbstractCacheResolver> en : resolvers.entrySet()) {
                log.info("Slardar find cacheResolver bean=" + en.getKey());
                resolverMap.put(en.getKey(), en.getValue().getCacheManager());
            }

            // Dynamic register Bean cacheResolver
            for (Map.Entry<String, CacheManager> en : managers.entrySet()) {
                final String key = en.getKey() + WingsCache.Resolver.Suffix;
                final CacheManager cm = en.getValue();
                resolverMap.put(key, cm);
                if (beanFactory.containsBean(key)) {
                    log.info("Slardar skip cacheResolver bean=" + key + ", for existed");
                }
                else {
                    final boolean exp = cacheProp.isExpand();
                    final CacheResolver resolver = exp ? new WingsCacheResolver(cm) : new SimpleCacheResolver(cm);
                    beanFactory.registerSingleton(key, resolver);
                    log.info("Slardar spring-bean register dynamic cacheResolver bean=" + key + ", expand=" + exp);
                }
            }

            // name or resolver name
            WingsCacheHelper.putManagers(resolverMap);

            CacheManager pre = null;
            String cnm = null;
            String prim = cacheProp.getPrimary();
            for (Map.Entry<String, CacheManager> en : managers.entrySet()) {
                final String name = en.getKey();
                log.info("Slardar spring-bean cacheManager name=" + name);
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
}
