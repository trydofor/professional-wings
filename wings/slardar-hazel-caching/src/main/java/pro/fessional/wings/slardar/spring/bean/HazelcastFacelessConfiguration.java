package pro.fessional.wings.slardar.spring.bean;

import com.hazelcast.core.HazelcastInstance;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pro.fessional.mirana.id.LightIdProvider;
import pro.fessional.wings.faceless.service.flakeid.FlakeIdService;
import pro.fessional.wings.faceless.spring.prop.LightIdProviderProp;
import pro.fessional.wings.silencer.spring.boot.ConditionalWingsEnabled;
import pro.fessional.wings.slardar.service.flakeid.FlakeIdHazelcastImpl;
import pro.fessional.wings.slardar.service.lightid.HazelcastLightIdProvider;

/**
 * @author trydofor
 * @since 2019-12-03
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(LightIdProviderProp.class)
@ConditionalWingsEnabled
public class HazelcastFacelessConfiguration {

    private static final Log log = LogFactory.getLog(HazelcastFacelessConfiguration.class);

    @Bean
    @ConditionalWingsEnabled
    public FlakeIdService flakeIdService(HazelcastInstance instance) {
        log.info("SlardarHazelCaching spring-bean hazelcastFlakeId Overriding");
        return new FlakeIdHazelcastImpl(instance);
    }

    @Bean
    @ConditionalWingsEnabled
    @ConditionalOnProperty(name = LightIdProviderProp.Key$monotonic, havingValue = "hz")
    public LightIdProvider hzLightIdProvider(LightIdProvider.Loader loader, LightIdProviderProp prop, HazelcastInstance instance) {
        log.info("Faceless spring-bean hzLightIdProvider");
        // avg=1.065ms
        HazelcastLightIdProvider provider = new HazelcastLightIdProvider(loader, instance);
        provider.setTimeout(prop.getTimeout());
        return provider;
    }
}
