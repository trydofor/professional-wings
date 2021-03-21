package pro.fessional.wings.slardar.spring.bean;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.DateDeserializers;
import com.fasterxml.jackson.databind.ser.std.DateSerializer;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.jsr310.deser.InstantDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import pro.fessional.wings.silencer.datetime.DateTimePattern;
import pro.fessional.wings.slardar.jackson.ZonedDeserializer;
import pro.fessional.wings.slardar.jackson.ZonedSerializer;
import pro.fessional.wings.slardar.spring.prop.SlardarEnabledProp;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.Date;

/**
 * @author trydofor
 * @link https://docs.spring.io/spring-boot/docs/2.4.2/reference/htmlsingle/#howto-customize-the-jackson-objectmapper
 * @see InstantDeserializer#ZONED_DATE_TIME
 * @since 2019-06-26
 */
@Configuration
@ConditionalOnClass(DateSerializer.class)
@ConditionalOnProperty(name = SlardarEnabledProp.Key$jackson, havingValue = "true")
public class SlardarJacksonConfiguration {

    private static final Log logger = LogFactory.getLog(SlardarJacksonConfiguration.class);

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
//            builder.timeZone(LocaleContextHolder.getTimeZone());

            builder.serializerByType(LocalDateTime.class, new LocalDateTimeSerializer(DateTimePattern.FMT_FULL_19));
            builder.serializerByType(LocalTime.class, new LocalTimeSerializer(DateTimePattern.FMT_TIME_08));
            builder.serializerByType(LocalDate.class, new LocalDateSerializer(DateTimePattern.FMT_DATE_10));

            builder.deserializerByType(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimePattern.FMT_FULL_19));
            builder.deserializerByType(LocalTime.class, new LocalTimeDeserializer(DateTimePattern.FMT_TIME_08));
            builder.deserializerByType(LocalDate.class, new LocalDateDeserializer(DateTimePattern.FMT_DATE_10));

            // zoned
            builder.serializerByType(ZonedDateTime.class, new ZonedSerializer(DateTimePattern.FMT_FULL_19));
            builder.deserializerByType(ZonedDateTime.class, new ZonedDeserializer(DateTimePattern.FMT_FULL_19));

            // util date
            DateFormat dateFormat = new SimpleDateFormat(DateTimePattern.PTN_FULL_19);
            builder.serializerByType(Date.class, new DateSerializer(false, dateFormat));
            DateDeserializers.DateDeserializer base = DateDeserializers.DateDeserializer.instance;
            DateDeserializers.DateDeserializer dateDeserializer = new DateDeserializers.DateDeserializer(base, dateFormat, DateTimePattern.PTN_FULL_19);
            builder.deserializerByType(Date.class, dateDeserializer);
        };
    }
}
