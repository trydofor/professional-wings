package pro.fessional.wings.slardar.spring.bean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pro.fessional.wings.slardar.servlet.resolver.WingsRemoteResolver;

/**
 * @author trydofor
 * @since 2019-06-29
 */
@Configuration
@ConditionalOnProperty(name = "spring.wings.slardar.enabled.remote", havingValue = "true")
public class WingsResolverRemoteConfiguration {

    private final Log logger = LogFactory.getLog(WingsResolverRemoteConfiguration.class);

    @Bean
    public WingsRemoteResolver wingsTerminalResolver(WingsRemoteResolver.Config conf) {
        logger.info("Wings conf WingsRemoteResolver");
        return new WingsRemoteResolver(conf);
    }

    @Bean
    @ConfigurationProperties("wings.slardar.remote")
    public WingsRemoteResolver.Config wingsTerminalResolverConfig() {
        return new WingsRemoteResolver.Config();
    }
}
