package pro.fessional.wings.slardar.spring.bean;

import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cache2k.extra.spring.SpringCache2kCacheManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
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
import org.springframework.context.annotation.Role;
import pro.fessional.wings.silencer.spring.boot.ConditionalWingsEnabled;
import pro.fessional.wings.slardar.cache.WingsCache;
import pro.fessional.wings.slardar.cache.WingsCacheHelper;
import pro.fessional.wings.slardar.cache.cache2k.WingsCache2k;
import pro.fessional.wings.slardar.cache.cache2k.WingsCache2kManager;
import pro.fessional.wings.slardar.cache.spring.WingsCacheAnnoOprSource;
import pro.fessional.wings.slardar.cache.spring.WingsCacheInterceptor;
import pro.fessional.wings.slardar.cache.spring.WingsCacheResolver;
import pro.fessional.wings.slardar.spring.prop.SlardarCacheProp;

import java.util.HashMap;
import java.util.Map;

import static pro.fessional.wings.slardar.cache.WingsCache.Manager;

/**
 * @author trydofor
 * @since 2019-12-03
 */
@EnableCaching
@Configuration(proxyBeanMethods = false)
@ConditionalWingsEnabled
public class SlardarCacheConfiguration {

    private static final Log log = LogFactory.getLog(SlardarCacheConfiguration.class);

    // set FeatureJmx early
    public SlardarCacheConfiguration(@Value("${spring.jmx.enabled:false}") boolean jmx) {
        log.info("Slardar spring.jmx.enabled=" + jmx + ", if can NOT disable, check IDEA 'Disable JMX agent' options");
        if (!jmx && WingsCache2k.FeatureJmx != null) {
            log.info("Slardar cache2k FeatureJmx but spring.jmx.enabled=false");
            WingsCache2k.FeatureJmx = null;
        }
    }

    // //////////////////// aop ////////////////////

    /**
     *  @see ProxyCachingConfiguration
     */
    @Configuration(proxyBeanMethods = false)
    @ConditionalWingsEnabled
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public static class CacheAop extends AbstractCachingConfiguration {

        @Bean
        @Primary
        @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
        public CacheOperationSource wingsCacheOperationSource() {
            log.info("Slardar spring-bean wingsCacheOperationSource as Primary");
            return new WingsCacheAnnoOprSource();
        }

        @Bean
        @Primary
        @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
        public CacheInterceptor wingsCacheInterceptor(CacheOperationSource cacheOperationSource) {
            log.info("Slardar spring-bean wingsCacheInterceptor as Primary");
            WingsCacheInterceptor interceptor = new WingsCacheInterceptor();
            interceptor.configure(this.errorHandler, this.keyGenerator, this.cacheResolver, this.cacheManager);
            interceptor.setCacheOperationSource(cacheOperationSource);
            return interceptor;
        }
    }


    // //////////////////// CacheManager ////////////////////
    @Configuration(proxyBeanMethods = false)
    @ConditionalWingsEnabled
    @RequiredArgsConstructor
    public static class CacheMgr implements CachingConfigurer {
        private final ConfigurableListableBeanFactory beanFactory;

        @Bean
        @Primary
        @Override
        public CacheManager cacheManager() {
            final var managers = beanFactory.getBeansOfType(CacheManager.class);
            final var resolvers = beanFactory.getBeansOfType(AbstractCacheResolver.class);
            final var cacheProp = beanFactory.getBean(SlardarCacheProp.class);

            final Map<String, CacheManager> managerMap = new HashMap<>(managers);
            for (Map.Entry<String, AbstractCacheResolver> en : resolvers.entrySet()) {
                log.info("Slardar find CacheManager via resolvers, name=" + en.getKey());
                managerMap.put(en.getKey(), en.getValue().getCacheManager());
            }

            // Dynamic register Bean cacheResolver
            for (Map.Entry<String, CacheManager> en : managers.entrySet()) {
                final String key = en.getKey() + WingsCache.Resolver.Suffix;
                final CacheManager cm = en.getValue();
                managerMap.put(key, cm);
                if (beanFactory.containsBean(key)) {
                    log.debug("Slardar skip register cacheResolver bean=" + key + ", for existed");
                }
                else {
                    final boolean exp = cacheProp.isExpand();
                    final CacheResolver resolver = exp ? new WingsCacheResolver(cm) : new SimpleCacheResolver(cm);
                    beanFactory.registerSingleton(key, resolver);
                    log.info("Slardar spring-bean register dynamic cacheResolver bean=" + key + ", expand=" + exp);
                }
            }

            // name or resolver name
            WingsCacheHelper.putManagers(managerMap);

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

    @Bean(Manager.Memory)
    @ConditionalWingsEnabled
    public SpringCache2kCacheManager cache2kCacheManager(SlardarCacheProp conf) {
        log.info("Slardar spring-bean cache2kCacheManager as " + Manager.Memory);
        // https://github.com/cache2k/cache2k/issues/123
        return new WingsCache2kManager("spring-wings-" + hashCode(), conf);
    }
}
