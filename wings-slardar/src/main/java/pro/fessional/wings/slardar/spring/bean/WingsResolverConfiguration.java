package pro.fessional.wings.slardar.spring.bean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import pro.fessional.wings.slardar.servlet.WingsFilterOrder;
import pro.fessional.wings.slardar.servlet.WingsLocaleResolver;
import pro.fessional.wings.slardar.servlet.WingsRemoteResolver;
import pro.fessional.wings.slardar.servlet.WingsTerminalFilter;

/**
 * @author trydofor
 * @since 2019-06-29
 */
@Configuration
public class WingsResolverConfiguration {

    private final Log logger = LogFactory.getLog(WingsResolverConfiguration.class);

    @Bean
    @ConditionalOnClass(LocaleResolver.class)
    @ConditionalOnProperty(name = "spring.wings.slardar.locale.enabled", havingValue = "true")
    public WingsLocaleResolver localeResolver(WingsLocaleResolver.Config conf) {
        logger.info("Wings conf WingsLocaleResolver");
        return new WingsLocaleResolver(conf);
    }

    @Bean
    @ConfigurationProperties("wings.slardar.locale")
    public WingsLocaleResolver.Config wingsI18nResolverConfig() {
        return new WingsLocaleResolver.Config();
    }

    @Bean
    @ConditionalOnProperty(name = "spring.wings.slardar.remote.enabled", havingValue = "true")
    public WingsRemoteResolver wingsTerminalResolver(WingsRemoteResolver.Config conf) {
        logger.info("Wings conf WingsRemoteResolver");
        return new WingsRemoteResolver(conf);
    }

    @Bean
    @ConfigurationProperties("wings.slardar.remote")
    public WingsRemoteResolver.Config wingsTerminalResolverConfig() {
        return new WingsRemoteResolver.Config();
    }

    @Bean
    @ConditionalOnBean({WingsLocaleResolver.class, WingsRemoteResolver.class})
    public WingsTerminalFilter wingsTerminalFilter(WingsLocaleResolver localeResolver, WingsRemoteResolver remoteResolver) {
        logger.info("Wings conf Terminal filter");
        WingsTerminalFilter filter = new WingsTerminalFilter(localeResolver, remoteResolver);
        filter.setOrder(WingsFilterOrder.TERMINAL);
        return filter;
    }
}
