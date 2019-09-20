package pro.fessional.wings.faceless.spring.bean;

import org.jooq.conf.Settings;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

/**
 * @author trydofor
 * @since 2019-08-12
 */
@Configuration
@ConditionalOnProperty(prefix = "spring.wings.jooq", name = "enabled", havingValue = "true")
public class WingsJooqConfiguration {

    @Bean
    @Order
    @ConditionalOnMissingBean(Settings.class)
    public Settings settings() {
        return new Settings()
                .withRenderCatalog(false)
                .withRenderSchema(false)
                ;
    }
}
