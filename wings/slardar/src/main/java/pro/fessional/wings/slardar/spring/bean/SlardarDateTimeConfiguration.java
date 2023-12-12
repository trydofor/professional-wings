package pro.fessional.wings.slardar.spring.bean;

import lombok.val;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.convert.ApplicationConversionService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pro.fessional.wings.silencer.spring.boot.ConditionalWingsEnabled;
import pro.fessional.wings.slardar.autozone.spring.LocalDate2StringConverter;
import pro.fessional.wings.slardar.autozone.spring.LocalDateTime2StringConverter;
import pro.fessional.wings.slardar.autozone.spring.LocalTime2StringConverter;
import pro.fessional.wings.slardar.autozone.spring.OffsetDateTime2StringConverter;
import pro.fessional.wings.slardar.autozone.spring.String2LocalDateConverter;
import pro.fessional.wings.slardar.autozone.spring.String2LocalDateTimeConverter;
import pro.fessional.wings.slardar.autozone.spring.String2LocalTimeConverter;
import pro.fessional.wings.slardar.autozone.spring.String2OffsetDateTimeConverter;
import pro.fessional.wings.slardar.autozone.spring.String2ZonedDateTimeConverter;
import pro.fessional.wings.slardar.autozone.spring.ZonedDateTime2StringConverter;
import pro.fessional.wings.slardar.spring.prop.SlardarDatetimeProp;

import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

/**
 * Auto inject by ApplicationConversionService#addBeans
 *
 * @author trydofor
 * @see ApplicationConversionService#addBeans
 * @since 2019-12-03
 */
@Configuration(proxyBeanMethods = false)
@ConditionalWingsEnabled
public class SlardarDateTimeConfiguration {
    private static final Log log = LogFactory.getLog(SlardarDateTimeConfiguration.class);

    // spring boot can expose Beans instead of WebMvcConfigurer
    @Bean
    @ConditionalWingsEnabled
    public String2LocalDateConverter stringLocalDateConverter(SlardarDatetimeProp prop) {
        log.info("Slardar spring-bean stringLocalDateConverter");
        val fmt = prop.getDate()
                      .getSupport()
                      .stream()
                      .map(DateTimeFormatter::ofPattern)
                      .collect(Collectors.toList());
        return new String2LocalDateConverter(fmt);
    }

    @Bean
    @ConditionalWingsEnabled
    public LocalDate2StringConverter localDateStringConverter(SlardarDatetimeProp prop) {
        log.info("Slardar spring-bean localDateStringConverter");
        final DateTimeFormatter fmt = DateTimeFormatter.ofPattern(prop.getDate().getFormat());
        return new LocalDate2StringConverter(fmt);
    }

    @Bean
    @ConditionalWingsEnabled
    public String2LocalTimeConverter stringLocalTimeConverter(SlardarDatetimeProp prop) {
        log.info("Slardar spring-bean stringLocalTimeConverter");
        val fmt = prop.getTime()
                      .getSupport()
                      .stream()
                      .map(DateTimeFormatter::ofPattern)
                      .collect(Collectors.toList());
        return new String2LocalTimeConverter(fmt);
    }

    @Bean
    @ConditionalWingsEnabled
    public LocalTime2StringConverter localTimeStringConverter(SlardarDatetimeProp prop) {
        log.info("Slardar spring-bean localTimeStringConverter");
        final DateTimeFormatter fmt = DateTimeFormatter.ofPattern(prop.getTime().getFormat());
        return new LocalTime2StringConverter(fmt);
    }

    @Bean
    @ConditionalWingsEnabled
    public String2LocalDateTimeConverter stringLocalDateTimeConverter(SlardarDatetimeProp prop) {
        log.info("Slardar spring-bean stringLocalDateTimeConverter");
        val fmt = prop.getDatetime()
                      .getSupport()
                      .stream()
                      .map(DateTimeFormatter::ofPattern)
                      .collect(Collectors.toList());
        return new String2LocalDateTimeConverter(fmt, prop.getDatetime().isAuto());
    }

    @Bean
    @ConditionalWingsEnabled
    public LocalDateTime2StringConverter localDateTimeStringConverter(SlardarDatetimeProp prop) {
        log.info("Slardar spring-bean localDateTimeStringConverter");
        final DateTimeFormatter fmt = DateTimeFormatter.ofPattern(prop.getDatetime().getFormat());
        return new LocalDateTime2StringConverter(fmt, prop.getDatetime().isAuto());
    }

    @Bean
    @ConditionalWingsEnabled
    public String2ZonedDateTimeConverter stringZonedDateTimeConverter(SlardarDatetimeProp prop) {
        log.info("Slardar spring-bean stringZonedDateTimeConverter");
        val fmt = prop.getZoned()
                      .getSupport()
                      .stream()
                      .map(DateTimeFormatter::ofPattern)
                      .collect(Collectors.toList());
        return new String2ZonedDateTimeConverter(fmt, prop.getZoned().isAuto());
    }

    @Bean
    @ConditionalWingsEnabled
    public ZonedDateTime2StringConverter zonedDateTimeStringConverter(SlardarDatetimeProp prop) {
        log.info("Slardar spring-bean zonedDateTimeStringConverter");
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern(prop.getZoned().getFormat());
        return new ZonedDateTime2StringConverter(fmt, prop.getZoned().isAuto());
    }

    @Bean
    @ConditionalWingsEnabled
    public String2OffsetDateTimeConverter stringOffsetDateTimeConverter(SlardarDatetimeProp prop) {
        log.info("Slardar spring-bean stringOffsetDateTimeConverter");
        val fmt = prop.getOffset()
                      .getSupport()
                      .stream()
                      .map(DateTimeFormatter::ofPattern)
                      .collect(Collectors.toList());
        return new String2OffsetDateTimeConverter(fmt, prop.getOffset().isAuto());
    }

    @Bean
    @ConditionalWingsEnabled
    public OffsetDateTime2StringConverter offsetDateTimeStringConverter(SlardarDatetimeProp prop) {
        log.info("Slardar spring-bean offsetDateTimeStringConverter");
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern(prop.getOffset().getFormat());
        return new OffsetDateTime2StringConverter(fmt, prop.getOffset().isAuto());
    }
}
