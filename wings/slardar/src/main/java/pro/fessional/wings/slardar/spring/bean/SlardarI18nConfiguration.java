package pro.fessional.wings.slardar.spring.bean;

import com.fasterxml.jackson.datatype.jsr310.deser.InstantDeserializer;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import pro.fessional.wings.slardar.autodto.AutoDtoHelper;
import pro.fessional.wings.slardar.autodto.AutoZoneVisitor;
import pro.fessional.wings.slardar.autodto.I18nStringVisitor;
import pro.fessional.wings.slardar.context.LocaleZoneIdUtil;

/**
 * @author trydofor
 * @link https://docs.spring.io/spring-boot/docs/2.6.6/reference/htmlsingle/#howto-customize-the-jackson-objectmapper
 * @see InstantDeserializer#ZONED_DATE_TIME
 * @since 2019-06-26
 */
@Configuration(proxyBeanMethods = false)
@RequiredArgsConstructor
public class SlardarI18nConfiguration {

    private static final Log log = LogFactory.getLog(SlardarI18nConfiguration.class);

    @Bean
    public LocalValidatorFactoryBean localValidatorFactoryBean(MessageSource messageSource) {
        log.info("Slardar spring-bean localValidatorFactoryBean");
        LocalValidatorFactoryBean bean = new LocalValidatorFactoryBean();
        bean.setValidationMessageSource(messageSource);
        return bean;
    }

    @Bean
    public CommandLineRunner runnerAutoDtoHelper(MessageSource messageSource) {
        log.info("Slardar spring-runs runnerAutoDtoHelper");
        return (arg) -> new AutoDtoHelper() {{
            final I18nStringVisitor i18nStringVisitor = new I18nStringVisitor(messageSource, LocaleZoneIdUtil.LocaleNonnull);

            RequestVisitor.add(AutoDtoHelper.AutoDtoVisitor);
            RequestVisitor.add(new AutoZoneVisitor(LocaleZoneIdUtil.ZoneIdNonnull, true));
            log.info("Slardar conf addRequestVisitor AutoZoneVisitorRequest");
            RequestVisitor.add(i18nStringVisitor);
            log.info("Slardar conf addRequestVisitor I18nStringVisitor");

            ResponseVisitor.add(AutoDtoHelper.AutoDtoVisitor);
            ResponseVisitor.add(new AutoZoneVisitor(LocaleZoneIdUtil.ZoneIdNonnull, false));
            log.info("Slardar conf addResponseVisitor AutoZoneVisitorResponse");
            ResponseVisitor.add(i18nStringVisitor);
            log.info("Slardar conf addResponseVisitor I18nStringVisitor");
        }};
    }
}
