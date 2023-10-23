package pro.fessional.wings.slardar.spring.bean;

import com.hazelcast.config.Config;
import com.hazelcast.config.NetworkConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import pro.fessional.wings.slardar.spring.prop.SlardarEnabledProp;

/**
 * @author trydofor
 * @since 2019-12-03
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(name = SlardarEnabledProp.Key$mockHazelcast, havingValue = "true")
public class HazelcastMockConfiguration {

    private static final Log log = LogFactory.getLog(HazelcastMockConfiguration.class);

    @Bean
    public HazelcastInstance hazelcastInstance() {
        log.info("SlardarHazelCaching spring-bean Standalone hazelcastInstance for mock");
        Config config = new Config();
        config.setClusterName("slardar-standalone");
        config.setProperty("hazelcast.shutdownhook.enabled", "false");
        NetworkConfig network = config.getNetworkConfig();
        network.getJoin().getTcpIpConfig().setEnabled(false);
        network.getJoin().getMulticastConfig().setEnabled(false);

        return StringUtils.hasText(config.getInstanceName()) ?
               Hazelcast.getOrCreateHazelcastInstance(config) :
               Hazelcast.newHazelcastInstance(config);
    }
}
