package pro.fessional.wings.slardar.spring.bean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import pro.fessional.mirana.time.DateParser;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

/**
 * @author trydofor
 * @since 2019-12-03
 */
@Configuration
@ConditionalOnProperty(name = "spring.wings.slardar.enabled.datetime", havingValue = "true")
public class WingsDateTimeConfiguration implements WebMvcConfigurer {

    private static final Log logger = LogFactory.getLog(WingsDateTimeConfiguration.class);

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new LocalDateConverter());
        registry.addConverter(new LocalTimeConverter());
        registry.addConverter(new LocalDateTimeConverter());
        registry.addConverter(new UtilDateConverter());
    }

    /*
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
    public UtilDateConverter utilDateConverter() {
        logger.info("Wings conf utilDateConverter");
        return new UtilDateConverter();
    }
    */
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

    public static class UtilDateConverter implements Converter<String, Date> {
        @Override
        public Date convert(@NotNull String source) {
            return DateParser.parseUtilDate(source);
        }
    }
}
