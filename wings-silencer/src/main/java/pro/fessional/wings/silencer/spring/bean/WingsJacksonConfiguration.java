package pro.fessional.wings.silencer.spring.bean;

import com.fasterxml.jackson.databind.ser.std.DateSerializer;
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
import pro.fessional.mirana.time.DateFormatter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
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
    public Jackson2ObjectMapperBuilderCustomizer customizer() {
        return builder -> {
            DateFormat ud = new SimpleDateFormat(DateFormatter.PTN_FULL_19);

            builder.serializerByType(ZonedDateTime.class, new ZonedDateTimeSerializer(DateFormatter.FMT_FULL_19));
            builder.serializerByType(LocalDateTime.class, new LocalDateTimeSerializer(DateFormatter.FMT_FULL_19));
            builder.serializerByType(LocalTime.class, new LocalTimeSerializer(DateFormatter.FMT_TIME_08));
            builder.serializerByType(LocalDate.class, new LocalDateSerializer(DateFormatter.FMT_DATE_10));
            builder.serializerByType(Date.class, new DateSerializer(false, ud));

            builder.deserializerByType(ZonedDateTime.class, new ZonedDateTimeDeserializer(DateFormatter.FMT_FULL_19));
            builder.deserializerByType(LocalDateTime.class, new LocalDateTimeDeserializer(DateFormatter.FMT_FULL_19));
            builder.deserializerByType(LocalTime.class, new LocalTimeDeserializer(DateFormatter.FMT_TIME_08));
            builder.deserializerByType(LocalDate.class, new LocalDateDeserializer(DateFormatter.FMT_DATE_10));
        };
    }


    private class ZonedDateTimeDeserializer extends InstantDeserializer<ZonedDateTime> {
        private ZonedDateTimeDeserializer(DateTimeFormatter formatter) {
            super(ZonedDateTime.class,
                    formatter,
                    ZonedDateTime::from,
                    a -> ZonedDateTime.ofInstant(Instant.ofEpochMilli(a.value), a.zoneId),
                    a -> ZonedDateTime.ofInstant(Instant.ofEpochSecond(a.integer, a.fraction), a.zoneId),
                    ZonedDateTime::withZoneSameInstant,
                    false // keep zero offset and Z separate since zones explicitly supported
            );
        }
    }
}
