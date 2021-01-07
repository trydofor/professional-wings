package pro.fessional.wings.slardar.spring.bean;

import com.fasterxml.jackson.databind.ser.std.DateSerializer;
import com.fasterxml.jackson.datatype.jsr310.deser.InstantDeserializer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pro.fessional.mirana.data.R;
import pro.fessional.mirana.i18n.I18nString;
import pro.fessional.wings.silencer.context.DefaultI18nContext;
import pro.fessional.wings.silencer.context.WingsI18nContext;
import pro.fessional.wings.slardar.jackson.I18nResultSerializer;
import pro.fessional.wings.slardar.jackson.I18nStringSerializer;

/**
 * @author trydofor
 * @link https://docs.spring.io/spring-boot/docs/2.4.0/reference/htmlsingle/#howto-customize-the-jackson-objectmapper
 * @see InstantDeserializer#ZONED_DATE_TIME
 * @since 2019-06-26
 */
@Configuration
@ConditionalOnClass(DateSerializer.class)
@ConditionalOnProperty(name = "spring.wings.slardar.json18n.enabled", havingValue = "true")
public class WingsJson18nConfiguration {

    private static final Log logger = LogFactory.getLog(WingsJson18nConfiguration.class);

    @Bean
    @ConditionalOnMissingBean(WingsI18nContext.class)
    public WingsI18nContext wingsI18nContext() {
        logger.info("config bean wingsI18nContext");
        return new DefaultI18nContext();
    }

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer customizerI18nSerializer(MessageSource messageSource, WingsI18nContext i18nContext) {
        logger.info("config bean customizerI18nSerializer");
        return builder -> {
            builder.serializerByType(R.I.class, new I18nResultSerializer(messageSource, i18nContext));
            builder.serializerByType(I18nString.class, new I18nStringSerializer(messageSource, i18nContext, true));
            builder.serializerByType(CharSequence.class, new I18nStringSerializer(messageSource, i18nContext, false));
        };
    }
}
