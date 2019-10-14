package pro.fessional.wings.silencer.spring.bean;

import com.fasterxml.jackson.databind.ser.std.DateSerializer;
import com.fasterxml.jackson.datatype.jsr310.deser.InstantDeserializer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pro.fessional.mirana.data.R;
import pro.fessional.mirana.i18n.I18nString;
import pro.fessional.wings.silencer.context.WingsI18nContext;
import pro.fessional.wings.silencer.jackson.I18nResultSerializer;
import pro.fessional.wings.silencer.jackson.I18nStringSerializer;

/**
 * @author trydofor
 * @link https://docs.spring.io/spring-boot/docs/current/reference/html/howto-spring-mvc.html#howto-customize-the-jackson-objectmapper
 * @see InstantDeserializer#ZONED_DATE_TIME
 * @since 2019-06-26
 */
@Configuration
@ConditionalOnClass(DateSerializer.class)
@ConditionalOnProperty(prefix = "spring.wings.json18n", name = "enabled", havingValue = "true")
public class WingsJson18nConfiguration {

    @Bean("jackson2ObjectMapperBuilderCustomizer-Json18n")
    public Jackson2ObjectMapperBuilderCustomizer customizer(MessageSource messageSource, WingsI18nContext i18nContext) {
        return builder -> {
            builder.serializerByType(R.I.class, new I18nResultSerializer(messageSource, i18nContext));
            builder.serializerByType(I18nString.class, new I18nStringSerializer(messageSource, i18nContext, true));
            builder.serializerByType(CharSequence.class, new I18nStringSerializer(messageSource, i18nContext, false));
        };
    }
}
