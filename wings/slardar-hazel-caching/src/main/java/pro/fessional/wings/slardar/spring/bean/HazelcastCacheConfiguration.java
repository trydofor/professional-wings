package pro.fessional.wings.slardar.spring.bean;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.spring.cache.HazelcastCacheManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.hazelcast.HazelcastConfigCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pro.fessional.wings.slardar.cache.hazelcast.WingsHazelcastCacheCustomizer;
import pro.fessional.wings.slardar.cache.hazelcast.WingsHazelcastCacheManager;
import pro.fessional.wings.slardar.spring.prop.SlardarCacheProp;
import pro.fessional.wings.slardar.spring.prop.SlardarEnabledProp;

import static pro.fessional.wings.slardar.cache.WingsCache.Manager;

/**
 * @author trydofor
 * @since 2019-12-03
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(name = SlardarEnabledProp.Key$caching, havingValue = "true")
@EnableConfigurationProperties(SlardarCacheProp.class)
public class HazelcastCacheConfiguration {

    private static final Log log = LogFactory.getLog(HazelcastCacheConfiguration.class);

    @Bean
    public HazelcastConfigCustomizer wingsHazelcastCacheCustomizer(SlardarCacheProp conf) {
        log.info("SlardarHazelCaching spring-bean wingsHazelcastCacheCustomizer");
        return new WingsHazelcastCacheCustomizer(conf);
    }

    @Bean(Manager.Server)
    public HazelcastCacheManager hazelcastCacheManager(SlardarCacheProp conf, HazelcastInstance instance) {
        log.info("SlardarHazelCaching spring-bean hazelcast as " + Manager.Server);
        return new WingsHazelcastCacheManager(conf, instance);
    }
}
