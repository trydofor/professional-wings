package pro.fessional.wings.faceless.spring.bean;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import pro.fessional.wings.faceless.service.wini18n.StandardI18nService;
import pro.fessional.wings.faceless.service.wini18n.impl.StandardI18nServiceJdbc;
import pro.fessional.wings.silencer.message.CombinableMessageSource;

/**
 * @author trydofor
 * @since 2019-06-01
 */
@Configuration
@ConditionalOnProperty(name = "spring.wings.faceless.enabled.enumi18n", havingValue = "true")
@ConditionalOnClass(name = "pro.fessional.wings.silencer.message.CombinableMessageSource")
public class WingsEnumI18nConfiguration {

    @Bean
    @ConditionalOnMissingBean(StandardI18nService.class)
    public StandardI18nService standardI18nService(
            JdbcTemplate jdbcTemplate,
            ObjectProvider<CombinableMessageSource> combinableMessageSource
    ) {
        return new StandardI18nServiceJdbc(jdbcTemplate, combinableMessageSource.getIfAvailable());
    }
}
