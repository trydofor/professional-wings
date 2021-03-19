package pro.fessional.wings.slardar.spring.bean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.convert.converter.Converter;
import pro.fessional.mirana.time.DateParser;
import pro.fessional.wings.slardar.spring.prop.SlardarEnabledProp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.TimeZone;

/**
 * @author trydofor
 * @since 2019-12-03
 */
@Configuration
@ConditionalOnProperty(name = SlardarEnabledProp.Key$datetime, havingValue = "true")
public class SlardarDateTimeConfiguration {
    private static final Log logger = LogFactory.getLog(SlardarDateTimeConfiguration.class);

    // spring boot can expose Beans instead of WebMvcConfigurer
    @Bean
    public LocalDateConverter localDateConverter() {
        logger.info("Wings conf localDateConverter");
        return new LocalDateConverter();
    }

    @Bean
    public LocalTimeConverter localTimeConverter() {
        logger.info("Wings conf localTimeConverter");
        return new LocalTimeConverter();
    }

    @Bean
    public LocalDateTimeConverter localDateTimeConverter() {
        logger.info("Wings conf localDateTimeConverter");
        return new LocalDateTimeConverter();
    }

    @Bean
    public ZonedDateTimeConverter zonedDateTimeConverter() {
        logger.info("Wings conf zonedDateTimeConverter");
        return new ZonedDateTimeConverter();
    }

    @Bean
    public UtilDateConverter utilDateConverter() {
        logger.info("Wings conf utilDateConverter");
        return new UtilDateConverter();
    }

    //
    public static class LocalDateConverter implements Converter<String, LocalDate> {
        @Override
        public LocalDate convert(@NotNull String source) {
            return DateParser.parseDate(source);
        }
    }

    public static class LocalTimeConverter implements Converter<String, LocalTime> {
        @Override
        public LocalTime convert(@NotNull String source) {
            return DateParser.parseTime(source);
        }
    }

    public static class LocalDateTimeConverter implements Converter<String, LocalDateTime> {
        @Override
        public LocalDateTime convert(@NotNull String source) {
            return DateParser.parseDateTime(source);
        }
    }


    public static class ZonedDateTimeConverter implements Converter<String, ZonedDateTime> {
        @Override
        public ZonedDateTime convert(@NotNull String source) {
            final LocalDateTime ldt = DateParser.parseDateTime(source);
            final TimeZone tz = LocaleContextHolder.getTimeZone();
            return ZonedDateTime.of(ldt, tz.toZoneId());
        }
    }

    public static class UtilDateConverter implements Converter<String, Date> {
        @Override
        public Date convert(@NotNull String source) {
            return DateParser.parseUtilDate(source);
        }
    }
}
