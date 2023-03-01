package pro.fessional.wings.faceless.spring.bean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import pro.fessional.wings.spring.consts.OrderedFacelessConst;
import pro.fessional.wings.faceless.service.wini18n.StandardI18nService;
import pro.fessional.wings.faceless.service.wini18n.impl.StandardI18nServiceJdbc;
import pro.fessional.wings.faceless.spring.prop.FacelessEnabledProp;
import pro.fessional.wings.silencer.message.CombinableMessageSource;

/**
 * @author trydofor
 * @since 2019-06-01
 */
@Configuration
@ConditionalOnProperty(name = FacelessEnabledProp.Key$enumi18n, havingValue = "true")
@ConditionalOnClass(name = "pro.fessional.wings.silencer.message.CombinableMessageSource")
@AutoConfigureOrder(OrderedFacelessConst.EnumI18nConfiguration)
public class FacelessEnumI18nConfiguration {

    private static final Log log = LogFactory.getLog(FacelessEnumI18nConfiguration.class);

    @Bean
    @ConditionalOnMissingBean(StandardI18nService.class)
    public StandardI18nService standardI18nService(
            JdbcTemplate jdbcTemplate,
            ObjectProvider<CombinableMessageSource> combinableMessageSource
    ) {
        log.info("Faceless spring-bean standardI18nService");
        return new StandardI18nServiceJdbc(jdbcTemplate, combinableMessageSource.getIfAvailable());
    }
}
