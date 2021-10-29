package pro.fessional.wings.slardar.spring.bean;

import com.fasterxml.jackson.datatype.jsr310.deser.InstantDeserializer;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import pro.fessional.mirana.data.R;
import pro.fessional.mirana.i18n.I18nString;
import pro.fessional.wings.slardar.jackson.AutoRegisterPropertyFilter;
import pro.fessional.wings.slardar.jackson.I18nResultPropertyFilter;
import pro.fessional.wings.slardar.jackson.I18nStringSerializer;
import pro.fessional.wings.slardar.spring.prop.SlardarEnabledProp;

/**
 * @author trydofor
 * @link https://docs.spring.io/spring-boot/docs/2.4.2/reference/htmlsingle/#howto-customize-the-jackson-objectmapper
 * @see InstantDeserializer#ZONED_DATE_TIME
 * @since 2019-06-26
 */
@Configuration
@RequiredArgsConstructor
public class SlardarI18nConfiguration {

    private static final Log logger = LogFactory.getLog(SlardarI18nConfiguration.class);

    private final MessageSource messageSource;

    @Bean
    @ConditionalOnProperty(name = SlardarEnabledProp.Key$jackson, havingValue = "true")
    public Jackson2ObjectMapperBuilderCustomizer customizerObjectMapperI18n() {
        logger.info("Wings conf customizerObjectMapperI18n");
        return builder -> {
            builder.serializerByType(I18nString.class, new I18nStringSerializer(messageSource, true));
            builder.serializerByType(CharSequence.class, new I18nStringSerializer(messageSource, false));
            builder.mixIn(R.class, I18nResultPropertyFilter.I18nResultMixin.class);
        };
    }

    @Bean
    @ConditionalOnProperty(name = SlardarEnabledProp.Key$jackson, havingValue = "true")
    public AutoRegisterPropertyFilter i18nResultPropertyFilter() {
        logger.info("Wings conf i18nResultPropertyFilter");
        return new I18nResultPropertyFilter(messageSource);
    }

    @Bean
    public LocalValidatorFactoryBean localValidatorFactoryBean() {
        logger.info("Wings conf localValidatorFactoryBean");
        LocalValidatorFactoryBean bean = new LocalValidatorFactoryBean();
        bean.setValidationMessageSource(messageSource);
        return bean;
    }
}
