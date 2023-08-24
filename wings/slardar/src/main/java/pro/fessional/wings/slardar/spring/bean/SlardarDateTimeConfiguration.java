package pro.fessional.wings.slardar.spring.bean;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.convert.ApplicationConversionService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
import pro.fessional.wings.slardar.spring.prop.SlardarEnabledProp;
import pro.fessional.wings.spring.consts.OrderedSlardarConst;

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
@ConditionalOnProperty(name = SlardarEnabledProp.Key$datetime, havingValue = "true")
@RequiredArgsConstructor
@AutoConfigureOrder(OrderedSlardarConst.DateTimeConfiguration)
public class SlardarDateTimeConfiguration {
    private static final Log log = LogFactory.getLog(SlardarDateTimeConfiguration.class);

    private final SlardarDatetimeProp slardarDatetimeProp;

    // spring boot can expose Beans instead of WebMvcConfigurer
    @Bean
    public String2LocalDateConverter stringLocalDateConverter() {
        log.info("Slardar spring-bean stringLocalDateConverter");
        val fmt = slardarDatetimeProp.getDate()
                                     .getSupport()
                                     .stream()
                                     .map(DateTimeFormatter::ofPattern)
                                     .collect(Collectors.toList());
        return new String2LocalDateConverter(fmt);
    }

    @Bean
    public LocalDate2StringConverter localDateStringConverter() {
        log.info("Slardar spring-bean localDateStringConverter");
        final DateTimeFormatter fmt = DateTimeFormatter.ofPattern(slardarDatetimeProp.getDate().getFormat());
        return new LocalDate2StringConverter(fmt);
    }

    @Bean
    public String2LocalTimeConverter stringLocalTimeConverter() {
        log.info("Slardar spring-bean stringLocalTimeConverter");
        val fmt = slardarDatetimeProp.getTime()
                                     .getSupport()
                                     .stream()
                                     .map(DateTimeFormatter::ofPattern)
                                     .collect(Collectors.toList());
        return new String2LocalTimeConverter(fmt);
    }

    @Bean
    public LocalTime2StringConverter localTimeStringConverter() {
        log.info("Slardar spring-bean localTimeStringConverter");
        final DateTimeFormatter fmt = DateTimeFormatter.ofPattern(slardarDatetimeProp.getTime().getFormat());
        return new LocalTime2StringConverter(fmt);
    }

    @Bean
    public String2LocalDateTimeConverter stringLocalDateTimeConverter() {
        log.info("Slardar spring-bean stringLocalDateTimeConverter");
        val fmt = slardarDatetimeProp.getDatetime()
                                     .getSupport()
                                     .stream()
                                     .map(DateTimeFormatter::ofPattern)
                                     .collect(Collectors.toList());
        return new String2LocalDateTimeConverter(fmt, slardarDatetimeProp.getDatetime().isAuto());
    }

    @Bean
    public LocalDateTime2StringConverter localDateTimeStringConverter() {
        log.info("Slardar spring-bean localDateTimeStringConverter");
        final DateTimeFormatter fmt = DateTimeFormatter.ofPattern(slardarDatetimeProp.getDatetime().getFormat());
        return new LocalDateTime2StringConverter(fmt, slardarDatetimeProp.getDatetime().isAuto());
    }

    @Bean
    public String2ZonedDateTimeConverter stringZonedDateTimeConverter() {
        log.info("Slardar spring-bean stringZonedDateTimeConverter");
        val fmt = slardarDatetimeProp.getZoned()
                                     .getSupport()
                                     .stream()
                                     .map(DateTimeFormatter::ofPattern)
                                     .collect(Collectors.toList());
        return new String2ZonedDateTimeConverter(fmt, slardarDatetimeProp.getZoned().isAuto());
    }

    @Bean
    public ZonedDateTime2StringConverter zonedDateTimeStringConverter() {
        log.info("Slardar spring-bean zonedDateTimeStringConverter");
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern(slardarDatetimeProp.getZoned().getFormat());
        return new ZonedDateTime2StringConverter(fmt, slardarDatetimeProp.getZoned().isAuto());
    }

    @Bean
    public String2OffsetDateTimeConverter stringOffsetDateTimeConverter() {
        log.info("Slardar spring-bean stringOffsetDateTimeConverter");
        val fmt = slardarDatetimeProp.getOffset()
                                     .getSupport()
                                     .stream()
                                     .map(DateTimeFormatter::ofPattern)
                                     .collect(Collectors.toList());
        return new String2OffsetDateTimeConverter(fmt, slardarDatetimeProp.getOffset().isAuto());
    }

    @Bean
    public OffsetDateTime2StringConverter offsetDateTimeStringConverter() {
        log.info("Slardar spring-bean offsetDateTimeStringConverter");
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern(slardarDatetimeProp.getOffset().getFormat());
        return new OffsetDateTime2StringConverter(fmt, slardarDatetimeProp.getOffset().isAuto());
    }
}
