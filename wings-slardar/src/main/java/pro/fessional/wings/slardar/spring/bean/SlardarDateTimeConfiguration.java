package pro.fessional.wings.slardar.spring.bean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import pro.fessional.mirana.time.DateFormatter;
import pro.fessional.mirana.time.DateParser;
import pro.fessional.wings.slardar.autozone.spring.StringZonedConverter;
import pro.fessional.wings.slardar.autozone.spring.ZonedStringConverter;
import pro.fessional.wings.slardar.spring.prop.SlardarEnabledProp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

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
    public StringLocalDateConverter stringLocalDateConverter() {
        logger.info("Wings conf stringLocalDateConverter");
        return new StringLocalDateConverter();
    }
    @Bean
    public LocalDateStringConverter localDateStringConverter() {
        logger.info("Wings conf localDateStringConverter");
        return new LocalDateStringConverter();
    }

    @Bean
    public StringLocalTimeConverter stringLocalTimeConverter() {
        logger.info("Wings conf stringLocalTimeConverter");
        return new StringLocalTimeConverter();
    }

    @Bean
    public LocalTimeStringConverter localTimeStringConverter() {
        logger.info("Wings conf localTimeStringConverter");
        return new LocalTimeStringConverter();
    }

    @Bean
    public StringLocalDateTimeConverter stringLocalDateTimeConverter() {
        logger.info("Wings conf stringLocalDateTimeConverter");
        return new StringLocalDateTimeConverter();
    }

    @Bean
    public LocalDateTimeStringConverter localDateTimeStringConverter() {
        logger.info("Wings conf localDateTimeStringConverter");
        return new LocalDateTimeStringConverter();
    }


    @Bean
    public StringZonedConverter stringZonedDateTimeConverter() {
        logger.info("Wings conf stringZonedDateTimeConverter");
        return new StringZonedConverter();
    }

    @Bean
    public ZonedStringConverter zonedDateTimeStringConverter() {
        logger.info("Wings conf zonedDateTimeStringConverter");
        return new ZonedStringConverter();
    }

    @Bean
    public StringUtilDateConverter stringUtilDateConverter() {
        logger.info("Wings conf stringUtilDateConverter");
        return new StringUtilDateConverter();
    }

    @Bean
    public UtilDateStringConverter utilDateStringConverter() {
        logger.info("Wings conf utilDateStringConverter");
        return new UtilDateStringConverter();
    }

    //
    public static class StringLocalDateConverter implements Converter<String, LocalDate> {
        @Override
        public LocalDate convert(@NotNull String source) {
            return DateParser.parseDate(source);
        }
    }

    public static class LocalDateStringConverter implements Converter<LocalDate, String> {
        @Override
        public String convert(@NotNull LocalDate source) {
            return DateFormatter.date10(source);
        }
    }

    public static class StringLocalTimeConverter implements Converter<String, LocalTime> {
        @Override
        public LocalTime convert(@NotNull String source) {
            return DateParser.parseTime(source);
        }
    }

    public static class LocalTimeStringConverter implements Converter<LocalTime, String> {
        @Override
        public String convert(@NotNull LocalTime source) {
            return DateFormatter.time08(source);
        }
    }

    public static class StringLocalDateTimeConverter implements Converter<String, LocalDateTime> {
        @Override
        public LocalDateTime convert(@NotNull String source) {
            return DateParser.parseDateTime(source);
        }
    }


    public static class LocalDateTimeStringConverter implements Converter<LocalDateTime, String> {
        @Override
        public String convert(@NotNull LocalDateTime source) {
            return DateFormatter.full19(source);
        }
    }

    public static class StringUtilDateConverter implements Converter<String, Date> {
        @Override
        public Date convert(@NotNull String source) {
            return DateParser.parseUtilDate(source);
        }
    }

    public static class UtilDateStringConverter implements Converter<Date, String> {
        @Override
        public String convert(@NotNull Date source) {
            return DateFormatter.full19(source);
        }
    }
}
