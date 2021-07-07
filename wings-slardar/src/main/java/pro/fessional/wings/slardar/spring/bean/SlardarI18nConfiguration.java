package pro.fessional.wings.slardar.spring.bean;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.deser.InstantDeserializer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import pro.fessional.mirana.i18n.I18nString;
import pro.fessional.wings.slardar.jackson.I18nResultModifier;
import pro.fessional.wings.slardar.jackson.I18nStringSerializer;
import pro.fessional.wings.slardar.spring.prop.SlardarEnabledProp;

/**
 * @author trydofor
 * @link https://docs.spring.io/spring-boot/docs/2.4.2/reference/htmlsingle/#howto-customize-the-jackson-objectmapper
 * @see InstantDeserializer#ZONED_DATE_TIME
 * @since 2019-06-26
 */
@Configuration
public class SlardarI18nConfiguration {

    private static final Log logger = LogFactory.getLog(SlardarI18nConfiguration.class);

    @Bean
    @ConditionalOnProperty(name = SlardarEnabledProp.Key$jackson, havingValue = "true")
    public Module customizerI18nResultModule(MessageSource messageSource) {
        logger.info("Wings conf customizerI18nResultModule");
        SimpleModule i18n = new SimpleModule();
        i18n.setSerializerModifier(new I18nResultModifier(messageSource));
        i18n.addSerializer(I18nString.class, new I18nStringSerializer(messageSource, true));
        i18n.addSerializer(CharSequence.class, new I18nStringSerializer(messageSource, false));
        return i18n;
    }

    @Bean
    public LocalValidatorFactoryBean localValidatorFactoryBean(MessageSource messageSource) {
        logger.info("Wings conf localValidatorFactoryBean");
        LocalValidatorFactoryBean bean = new LocalValidatorFactoryBean();
        bean.setValidationMessageSource(messageSource);
        return bean;
    }

}
