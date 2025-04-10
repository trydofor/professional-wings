package pro.fessional.wings.slardar.spring.bean;

import com.fasterxml.jackson.databind.ser.std.DateSerializer;
import com.fasterxml.jackson.datatype.jsr310.deser.InstantDeserializer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pro.fessional.wings.silencer.spring.boot.ConditionalWingsEnabled;
import pro.fessional.wings.slardar.jackson.AutoRegisterPropertyFilter;
import pro.fessional.wings.slardar.jackson.I18nAwarePropertyFilter;
import pro.fessional.wings.slardar.spring.prop.SlardarJacksonProp;

/**
 * @author trydofor
 * @link <a href="https://docs.spring.io/spring-boot/docs/3.0.3/reference/htmlsingle/#howto.spring-mvc.customize-jackson-objectmapper">Customize the Jackson ObjectMapper</a>
 * @see InstantDeserializer#ZONED_DATE_TIME
 * @since 2019-06-26
 */
@Configuration(proxyBeanMethods = false)
@ConditionalWingsEnabled
@ConditionalOnClass(DateSerializer.class)
public class SlardarJacksonConfiguration {

    private static final Log log = LogFactory.getLog(SlardarJacksonConfiguration.class);

    @Bean
    @ConditionalWingsEnabled
    public AutoRegisterPropertyFilter i18nMessagePropertyFilter(MessageSource messageSource, SlardarJacksonProp prop) {
        log.info("Slardar spring-bean i18nMessagePropertyFilter");
        return new I18nAwarePropertyFilter(messageSource::getMessage, prop.getI18nResultCompatible());
    }
}
