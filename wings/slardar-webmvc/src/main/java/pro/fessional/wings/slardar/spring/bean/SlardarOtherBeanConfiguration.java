package pro.fessional.wings.slardar.spring.bean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pro.fessional.wings.slardar.actuator.cache.SlardarCacheEndpoint;

import java.util.Map;


/**
 * @author trydofor
 * @since 2019-12-01
 */
@Configuration(proxyBeanMethods = false)
public class SlardarOtherBeanConfiguration {

    private final static Log log = LogFactory.getLog(SlardarOtherBeanConfiguration.class);

    @Bean
    @ConditionalOnAvailableEndpoint
    public SlardarCacheEndpoint slardarCacheManageEndpoint(Map<String, CacheManager> cacheManagers) {
        log.info("wings conf slardarCacheManageEndpoint");
        return new SlardarCacheEndpoint(cacheManagers);
    }
}
