package pro.fessional.wings.slardar.spring.bean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.autoconfigure.hazelcast.HazelcastConfigCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pro.fessional.wings.silencer.spring.boot.ConditionalWingsEnabled;
import pro.fessional.wings.slardar.cache.hazelcast.WingsHazelcastCacheCustomizer;
import pro.fessional.wings.slardar.spring.prop.SlardarCacheProp;
import pro.fessional.wings.slardar.spring.prop.SlardarEnabledProp;

/**
 * @author trydofor
 * @since 2019-12-03
 */
@Configuration(proxyBeanMethods = false)
@ConditionalWingsEnabled
public class HazelcastConfigConfiguration {

    private static final Log log = LogFactory.getLog(HazelcastConfigConfiguration.class);

    @Bean
    @ConditionalWingsEnabled(abs = SlardarEnabledProp.Key$hazelcastStandalone, value = false)
    public HazelcastConfigCustomizer wingsHazelcastAloneCustomizer() {
        log.info("SlardarHazelCaching spring-bean simulator hazelcastInstance standalone");
        return config -> {
            config.setClusterName("standalone-" + System.identityHashCode(config));
            config.setProperty("hazelcast.shutdownhook.enabled", "false");
            var network = config.getNetworkConfig().getJoin();
            network.getTcpIpConfig().setEnabled(false);
            network.getMulticastConfig().setEnabled(false);
        };
    }

    @Bean
    @ConditionalWingsEnabled
    public HazelcastConfigCustomizer wingsHazelcastCacheCustomizer(SlardarCacheProp conf) {
        log.info("SlardarHazelCaching spring-bean wingsHazelcastCacheCustomizer");
        return new WingsHazelcastCacheCustomizer(conf);
    }
}
