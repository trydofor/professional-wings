package pro.fessional.wings.slardar.spring.bean;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import pro.fessional.wings.slardar.spring.conf.WingsI18nResolverProperties;
import pro.fessional.wings.slardar.spring.help.WingsI18nWebResolver;

/**
 * @author trydofor
 * @since 2019-06-29
 */
@Configuration
@ConditionalOnProperty(prefix = "spring.wings.i18n.resolver", name = "enabled", havingValue = "true")
@ConditionalOnClass(LocaleResolver.class)
public class WingsI18nWebConfiguration {

    @Bean
    public LocaleResolver localeResolver(WingsI18nResolverProperties properties) {
        return new WingsI18nWebResolver(properties);
    }

    @Bean
    @ConfigurationProperties("wings.i18n.resolver")
    public WingsI18nResolverProperties resolverProperties() {
        return new WingsI18nResolverProperties();
    }

}
