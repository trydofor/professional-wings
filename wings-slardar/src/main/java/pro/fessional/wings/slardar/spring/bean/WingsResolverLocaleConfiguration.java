package pro.fessional.wings.slardar.spring.bean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import pro.fessional.wings.slardar.servlet.resolver.WingsLocaleResolver;

/**
 * @author trydofor
 * @since 2019-06-29
 */
@Configuration
@ConditionalOnProperty(name = "spring.wings.slardar.locale.enabled", havingValue = "true")
public class WingsResolverLocaleConfiguration {

    private final Log logger = LogFactory.getLog(WingsResolverLocaleConfiguration.class);

    @Bean
    @ConditionalOnClass(LocaleResolver.class)
    public WingsLocaleResolver localeResolver(WingsLocaleResolver.Config conf) {
        logger.info("Wings conf WingsLocaleResolver");
        return new WingsLocaleResolver(conf);
    }

    @Bean
    @ConfigurationProperties("wings.slardar.locale")
    public WingsLocaleResolver.Config wingsI18nResolverConfig() {
        return new WingsLocaleResolver.Config();
    }
}
