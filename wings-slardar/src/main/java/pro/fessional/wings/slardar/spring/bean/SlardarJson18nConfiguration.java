package pro.fessional.wings.slardar.spring.bean;

import com.fasterxml.jackson.databind.ser.std.DateSerializer;
import com.fasterxml.jackson.datatype.jsr310.deser.InstantDeserializer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pro.fessional.mirana.data.R;
import pro.fessional.mirana.i18n.I18nString;
import pro.fessional.wings.slardar.jackson.I18nResultSerializer;
import pro.fessional.wings.slardar.jackson.I18nStringSerializer;
import pro.fessional.wings.slardar.spring.prop.SlardarEnabledProp;

/**
 * @author trydofor
 * @link https://docs.spring.io/spring-boot/docs/2.4.2/reference/htmlsingle/#howto-customize-the-jackson-objectmapper
 * @see InstantDeserializer#ZONED_DATE_TIME
 * @since 2019-06-26
 */
@Configuration
@ConditionalOnClass(DateSerializer.class)
@ConditionalOnProperty(name = SlardarEnabledProp.Key$json18n, havingValue = "true")
public class SlardarJson18nConfiguration {

    private static final Log logger = LogFactory.getLog(SlardarJson18nConfiguration.class);

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer customizerI18nSerializer(MessageSource messageSource) {
        logger.info("Wings conf customizerI18nSerializer");
        return builder -> {
            builder.serializerByType(R.I.class, new I18nResultSerializer(messageSource));
            builder.serializerByType(I18nString.class, new I18nStringSerializer(messageSource, true));
            builder.serializerByType(CharSequence.class, new I18nStringSerializer(messageSource, false));
        };
    }
}
