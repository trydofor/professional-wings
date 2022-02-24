package pro.fessional.wings.slardar.spring.bean;

import com.hazelcast.core.HazelcastInstance;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pro.fessional.wings.slardar.actuator.cache.SlardarCacheEndpoint;
import pro.fessional.wings.slardar.concur.HazelcastFlakeId;

import java.util.Map;


/**
 * @author trydofor
 * @since 2019-12-01
 */
@Configuration(proxyBeanMethods = false)
public class SlardarOtherBeanConfiguration {

    private final static Log logger = LogFactory.getLog(SlardarOtherBeanConfiguration.class);

    @Bean
    @ConditionalOnAvailableEndpoint
    public SlardarCacheEndpoint slardarCacheManageEndpoint(Map<String, CacheManager> cacheManagers) {
        logger.info("wings conf slardarCacheManageEndpoint");
        return new SlardarCacheEndpoint(cacheManagers);
    }

    @Bean
    @ConditionalOnBean(HazelcastInstance.class)
    public HazelcastFlakeId hazelcastFlakeId(HazelcastInstance instance) {
        logger.info("wings conf hazelcastFlakeId");
        return new HazelcastFlakeId(instance);
    }
}
