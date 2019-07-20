package pro.fessional.wings.faceless.spring.bean;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pro.fessional.mirana.id.LightIdBufferedProvider;
import pro.fessional.mirana.id.LightIdProvider;
import pro.fessional.wings.faceless.service.lightid.impl.LightIdMysqlLoader;
import pro.fessional.wings.faceless.spring.conf.WingsLightIdInsertProperties;
import pro.fessional.wings.faceless.spring.conf.WingsLightIdLoaderProperties;

/**
 * @author trydofor
 * @since 2019-06-01
 */
@Configuration
@ConditionalOnProperty(prefix = "spring.wings.lightid", name = "enabled", havingValue = "true")
public class WingsLightIdConfiguration {

    @Bean
    @ConditionalOnMissingBean(LightIdProvider.class)
    public LightIdProvider lightIdProvider(LightIdMysqlLoader loader,
                                           WingsLightIdLoaderProperties config) {

        LightIdBufferedProvider provider = new LightIdBufferedProvider(loader);
        provider.setTimeout(config.getTimeout());
        provider.setErrAlive(config.getErrAlive());
        provider.setMaxError(config.getMaxError());
        provider.setMaxCount(config.getMaxCount());

        return provider;
    }

    @Bean
    @ConfigurationProperties("wings.lightid.insert")
    public WingsLightIdInsertProperties insertProperties() {
        return new WingsLightIdInsertProperties();
    }

    @Bean
    @ConfigurationProperties("wings.lightid.loader")
    public WingsLightIdLoaderProperties loaderProperties() {
        return new WingsLightIdLoaderProperties();
    }
}
