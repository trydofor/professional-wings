package pro.fessional.wings.slardar.spring.bean;

import com.fasterxml.jackson.databind.ser.std.DateSerializer;
import com.fasterxml.jackson.datatype.jsr310.deser.InstantDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pro.fessional.wings.slardar.autozone.json.JacksonZonedDeserializer;
import pro.fessional.wings.slardar.autozone.json.JacksonZonedSerializer;
import pro.fessional.wings.slardar.spring.prop.SlardarDatetimeProp;
import pro.fessional.wings.slardar.spring.prop.SlardarEnabledProp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author trydofor
 * @link https://docs.spring.io/spring-boot/docs/2.4.2/reference/htmlsingle/#howto-customize-the-jackson-objectmapper
 * @see InstantDeserializer#ZONED_DATE_TIME
 * @since 2019-06-26
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(DateSerializer.class)
@ConditionalOnProperty(name = SlardarEnabledProp.Key$jackson, havingValue = "true")
@RequiredArgsConstructor
public class SlardarJacksonConfiguration {

    private static final Log logger = LogFactory.getLog(SlardarJacksonConfiguration.class);

    private final SlardarDatetimeProp slardarDatetimeProp;
/*
    @Bean
    @Primary
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public ObjectMapper objectMapper(Jackson2ObjectMapperBuilder builder) {
        logger.info("Wings conf jackson ObjectMapper");
        return builder.createXmlMapper(false).build();
    }

    @Bean
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public XmlMapper xmlMapper(Jackson2ObjectMapperBuilder builder) {
        logger.info("Wings conf jackson XmlMapper");
        return builder.createXmlMapper(true).build();
    }
*/

    /**
     * The context’s Jackson2ObjectMapperBuilder can be customized by one or more
     * Jackson2ObjectMapperBuilderCustomizer beans. Such customizer beans can be ordered
     * (Boot’s own customizer has an order of 0), letting additional
     * customization be applied both before and after Boot’s customization.
     * <p>
     * If you provide any @Beans of type MappingJackson2HttpMessageConverter,
     * they replace the default value in the MVC configuration. Also,
     * a convenience bean of type HttpMessageConverters is provided
     * (and is always available if you use the default MVC configuration).
     * It has some useful methods to access the default and user-enhanced message converters.
     */
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer customizerFront() {
        logger.info("Wings conf Jackson2ObjectMapperBuilderCustomizer");
        return builder -> {

            DateTimeFormatter full = DateTimeFormatter.ofPattern(slardarDatetimeProp.getDatetime().getFormat());
            DateTimeFormatter date = DateTimeFormatter.ofPattern(slardarDatetimeProp.getDate().getFormat());
            DateTimeFormatter time = DateTimeFormatter.ofPattern(slardarDatetimeProp.getTime().getFormat());

            // local
            builder.serializerByType(LocalDateTime.class, new LocalDateTimeSerializer(full));
            builder.serializerByType(LocalDate.class, new LocalDateSerializer(date));
            builder.serializerByType(LocalTime.class, new LocalTimeSerializer(time));

            builder.deserializerByType(LocalDateTime.class, new LocalDateTimeDeserializer(full));
            builder.deserializerByType(LocalDate.class, new LocalDateDeserializer(date));
            builder.deserializerByType(LocalTime.class, new LocalTimeDeserializer(time));

            // auto zoned
            DateTimeFormatter zoned = DateTimeFormatter.ofPattern(slardarDatetimeProp.getZoned().getFormat());

            builder.serializerByType(ZonedDateTime.class, new JacksonZonedSerializer(zoned));
            builder.deserializerByType(ZonedDateTime.class, new JacksonZonedDeserializer(zoned));
        };
    }
}
