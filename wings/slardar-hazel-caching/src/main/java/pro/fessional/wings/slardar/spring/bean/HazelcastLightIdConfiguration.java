package pro.fessional.wings.slardar.spring.bean;

import com.hazelcast.core.HazelcastInstance;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pro.fessional.mirana.id.LightIdProvider;
import pro.fessional.wings.faceless.service.flakeid.FlakeIdService;
import pro.fessional.wings.faceless.spring.bean.FacelessLightIdConfiguration;
import pro.fessional.wings.faceless.spring.prop.LightIdProviderProp;
import pro.fessional.wings.slardar.service.lightid.HazelcastLightIdProvider;

/**
 * @author trydofor
 * @since 2019-12-03
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(FlakeIdService.class)
@AutoConfigureBefore(FacelessLightIdConfiguration.class)
public class HazelcastLightIdConfiguration {

    private static final Log log = LogFactory.getLog(HazelcastLightIdConfiguration.class);

    @Bean
    @ConditionalOnMissingBean(LightIdProvider.class)
    @ConditionalOnProperty(name = LightIdProviderProp.Key$monotonic, havingValue = "hz")
    public LightIdProvider lightIdProvider(LightIdProvider.Loader lightIdLoader, LightIdProviderProp providerProp, HazelcastInstance hazelcastInstance) {
        final String mono = providerProp.getMonotonic();
        log.info("Faceless spring-bean lightIdProvider via Hazelcast");
        if ("hz".equalsIgnoreCase(mono)) {
            // avg=1.065ms
            HazelcastLightIdProvider provider = new HazelcastLightIdProvider(lightIdLoader, hazelcastInstance);
            provider.setTimeout(providerProp.getTimeout());
            return provider;
        }
        else {
            throw new IllegalArgumentException("unsupported monotonic type=" + mono);
        }
    }
}
