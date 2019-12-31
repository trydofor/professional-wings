package pro.fessional.wings.silencer.spring.bean;

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
import com.fasterxml.jackson.datatype.jsr310.ser.ZonedDateTimeSerializer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import pro.fessional.wings.silencer.datetime.DateTimePattern;
import pro.fessional.wings.silencer.datetime.ZonedDeserializer;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.Date;

/**
 * @author trydofor
 * @link https://docs.spring.io/spring-boot/docs/current/reference/html/howto-spring-mvc.html#howto-customize-the-jackson-objectmapper
 * @see InstantDeserializer#ZONED_DATE_TIME
 * @since 2019-06-26
 */
@Configuration
@ConditionalOnClass(DateSerializer.class)
@ConditionalOnProperty(prefix = "spring.wings.jackson", name = "enabled", havingValue = "true")
public class WingsJacksonConfiguration {

    @Bean
    @Primary
    public ObjectMapper jacksonObjectMapper(Jackson2ObjectMapperBuilder builder) {
        return builder.createXmlMapper(false).build();
    }

    @Bean
    public XmlMapper jacksonXmlMapper(Jackson2ObjectMapperBuilder builder) {

        return builder.createXmlMapper(true).build();
    }

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer customizerFront() {
        return builder -> {
            DateFormat dateFormat = new SimpleDateFormat(DateTimePattern.PTN_FULL_19);

            builder.serializerByType(ZonedDateTime.class, new ZonedDateTimeSerializer(DateTimePattern.FMT_FULL_19));
            builder.serializerByType(LocalDateTime.class, new LocalDateTimeSerializer(DateTimePattern.FMT_FULL_19));
            builder.serializerByType(LocalTime.class, new LocalTimeSerializer(DateTimePattern.FMT_TIME_08));
            builder.serializerByType(LocalDate.class, new LocalDateSerializer(DateTimePattern.FMT_DATE_10));
            builder.serializerByType(Date.class, new DateSerializer(false, dateFormat));

            builder.deserializerByType(ZonedDateTime.class, new ZonedDeserializer(DateTimePattern.FMT_FULL_19));
            builder.deserializerByType(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimePattern.FMT_FULL_19));
            builder.deserializerByType(LocalTime.class, new LocalTimeDeserializer(DateTimePattern.FMT_TIME_08));
            builder.deserializerByType(LocalDate.class, new LocalDateDeserializer(DateTimePattern.FMT_DATE_10));

            DateDeserializers.DateDeserializer base = DateDeserializers.DateDeserializer.instance;
            DateDeserializers.DateDeserializer dateDeserializer = new DateDeserializers.DateDeserializer(base, dateFormat, DateTimePattern.PTN_FULL_19);
            builder.deserializerByType(Date.class, dateDeserializer);
        };
    }
}
