package pro.fessional.wings.slardar.spring.bean;

import com.hazelcast.config.GlobalSerializerConfig;
import com.hazelcast.config.SerializationConfig;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.autoconfigure.hazelcast.HazelcastConfigCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pro.fessional.wings.silencer.spring.boot.ConditionalWingsEnabled;
import pro.fessional.wings.slardar.cache.hazelcast.WingsHazelcastCacheCustomizer;
import pro.fessional.wings.slardar.serialize.KryoHazelcast;
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

    @Bean
    @ConditionalWingsEnabled
    public HazelcastConfigCustomizer wingsHazelcastGlobalSerializer() {
        log.info("SlardarHazelCaching spring-bean wingsHazelcastGlobalSerializer");
        return config -> {
            SerializationConfig serialization = config.getSerializationConfig();
            GlobalSerializerConfig gs = serialization.getGlobalSerializerConfig();
            if (gs == null) {
                GlobalSerializerConfig ngs = new GlobalSerializerConfig();
                ngs.setClassName(KryoHazelcast.class.getName());
                serialization.setGlobalSerializerConfig(ngs);
                log.info("Wings hazelcast setGlobalSerializerConfig class=KryoHazelcast");
            }
            else {
                log.info("Wings hazelcast setGlobalSerializerConfig skipped, current=" + gs);
            }
        };
    }
}
