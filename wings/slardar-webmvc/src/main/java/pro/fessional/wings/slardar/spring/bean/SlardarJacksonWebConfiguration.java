package pro.fessional.wings.slardar.spring.bean;

import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.fasterxml.jackson.databind.ser.std.DateSerializer;
import com.fasterxml.jackson.datatype.jsr310.deser.InstantDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.util.StringUtils;
import pro.fessional.mirana.data.R;
import pro.fessional.mirana.i18n.I18nString;
import pro.fessional.wings.slardar.autozone.AutoZoneType;
import pro.fessional.wings.slardar.autozone.json.JacksonLocalDateDeserializer;
import pro.fessional.wings.slardar.autozone.json.JacksonLocalDateTimeDeserializer;
import pro.fessional.wings.slardar.autozone.json.JacksonLocalDateTimeSerializer;
import pro.fessional.wings.slardar.autozone.json.JacksonLocalTimeDeserializer;
import pro.fessional.wings.slardar.autozone.json.JacksonOffsetDateTimeDeserializer;
import pro.fessional.wings.slardar.autozone.json.JacksonOffsetDateTimeSerializer;
import pro.fessional.wings.slardar.autozone.json.JacksonZonedDateTimeDeserializer;
import pro.fessional.wings.slardar.autozone.json.JacksonZonedDateTimeSerializer;
import pro.fessional.wings.slardar.jackson.AutoRegisterPropertyFilter;
import pro.fessional.wings.slardar.jackson.EmptyValuePropertyFilter;
import pro.fessional.wings.slardar.jackson.FormatNumberSerializer;
import pro.fessional.wings.slardar.jackson.FormatNumberSerializer.Digital;
import pro.fessional.wings.slardar.jackson.I18nResultPropertyFilter;
import pro.fessional.wings.slardar.jackson.I18nStringSerializer;
import pro.fessional.wings.slardar.jackson.JacksonHelper;
import pro.fessional.wings.slardar.spring.prop.SlardarDatetimeProp;
import pro.fessional.wings.slardar.spring.prop.SlardarEnabledProp;
import pro.fessional.wings.slardar.spring.prop.SlardarJacksonProp;
import pro.fessional.wings.slardar.spring.prop.SlardarNumberProp;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author trydofor
 * @link https://docs.spring.io/spring-boot/docs/2.6.6/reference/htmlsingle/#howto-customize-the-jackson-objectmapper
 * @see InstantDeserializer#ZONED_DATE_TIME
 * @since 2019-06-26
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(DateSerializer.class)
@ConditionalOnProperty(name = SlardarEnabledProp.Key$jackson, havingValue = "true")
@RequiredArgsConstructor
@AutoConfigureAfter(SlardarJacksonConfiguration.class)
public class SlardarJacksonWebConfiguration {

    private static final Log log = LogFactory.getLog(SlardarJacksonWebConfiguration.class);

    private final SlardarJacksonProp slardarJacksonProp;
    private final SlardarDatetimeProp slardarDatetimeProp;
    private final SlardarNumberProp slardarNumberProp;
    private final MessageSource messageSource;

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
    public Jackson2ObjectMapperBuilderCustomizer customizerObjectMapperDatetime() {
        log.info("SlardarWebmvc spring-bean customizerObjectMapperDatetime");
        return builder -> {
            // local
            val date = DateTimeFormatter.ofPattern(slardarDatetimeProp.getDate().getFormat());
            val datePsr = slardarDatetimeProp.getDate()
                                             .getSupport()
                                             .stream()
                                             .map(DateTimeFormatter::ofPattern)
                                             .collect(Collectors.toList());
            builder.serializerByType(LocalDate.class, new LocalDateSerializer(date));
            builder.deserializerByType(LocalDate.class, new JacksonLocalDateDeserializer(date, datePsr));
            log.info("SlardarWebmvc conf Jackson2ObjectMapperBuilderCustomizer LocalDate");

            val time = DateTimeFormatter.ofPattern(slardarDatetimeProp.getTime().getFormat());
            val timePsr = slardarDatetimeProp.getTime()
                                             .getSupport()
                                             .stream()
                                             .map(DateTimeFormatter::ofPattern)
                                             .collect(Collectors.toList());
            builder.serializerByType(LocalTime.class, new LocalTimeSerializer(time));
            builder.deserializerByType(LocalTime.class, new JacksonLocalTimeDeserializer(time, timePsr));
            log.info("SlardarWebmvc conf Jackson2ObjectMapperBuilderCustomizer LocalTime");

            // auto local
            val full = DateTimeFormatter.ofPattern(slardarDatetimeProp.getDatetime().getFormat());
            final AutoZoneType autoLocal = AutoZoneType.valueOf(slardarDatetimeProp.getDatetime().isAuto());
            JacksonLocalDateTimeSerializer.defaultFormatter = full;
            JacksonLocalDateTimeSerializer.defaultAutoZone = autoLocal;
            builder.serializerByType(LocalDateTime.class, new JacksonLocalDateTimeSerializer(full, autoLocal));

            val fullPsr = slardarDatetimeProp.getDatetime()
                                             .getSupport()
                                             .stream()
                                             .map(DateTimeFormatter::ofPattern)
                                             .collect(Collectors.toList());
            builder.deserializerByType(LocalDateTime.class, new JacksonLocalDateTimeDeserializer(full, fullPsr, autoLocal));
            log.info("SlardarWebmvc conf Jackson2ObjectMapperBuilderCustomizer LocalDateTime");

            // auto zoned
            DateTimeFormatter zoned = DateTimeFormatter.ofPattern(slardarDatetimeProp.getZoned().getFormat());
            final AutoZoneType autoZone = AutoZoneType.valueOf(slardarDatetimeProp.getZoned().isAuto());
            JacksonZonedDateTimeSerializer.defaultFormatter = zoned;
            JacksonZonedDateTimeSerializer.defaultAutoZone = autoZone;
            builder.serializerByType(ZonedDateTime.class, new JacksonZonedDateTimeSerializer(zoned, autoZone));

            val zonePsr = slardarDatetimeProp.getZoned()
                                             .getSupport()
                                             .stream()
                                             .map(DateTimeFormatter::ofPattern)
                                             .collect(Collectors.toList());

            builder.deserializerByType(ZonedDateTime.class, new JacksonZonedDateTimeDeserializer(zoned, zonePsr, autoZone));
            log.info("SlardarWebmvc conf Jackson2ObjectMapperBuilderCustomizer ZonedDateTime");

            // auto offset
            DateTimeFormatter offset = DateTimeFormatter.ofPattern(slardarDatetimeProp.getOffset().getFormat());
            final AutoZoneType autoOffset = AutoZoneType.valueOf(slardarDatetimeProp.getOffset().isAuto());
            JacksonOffsetDateTimeSerializer.defaultFormatter = offset;
            JacksonOffsetDateTimeSerializer.defaultAutoZone = autoOffset;
            builder.serializerByType(OffsetDateTime.class, new JacksonOffsetDateTimeSerializer(offset, autoOffset));

            val offPsr = slardarDatetimeProp.getZoned()
                                            .getSupport()
                                            .stream()
                                            .map(DateTimeFormatter::ofPattern)
                                            .collect(Collectors.toList());

            builder.deserializerByType(OffsetDateTime.class, new JacksonOffsetDateTimeDeserializer(offset, offPsr, autoOffset));
            log.info("SlardarWebmvc conf Jackson2ObjectMapperBuilderCustomizer OffsetDateTime");
        };
    }

    @Bean
    @ConditionalOnProperty(name = SlardarEnabledProp.Key$number, havingValue = "true")
    public Jackson2ObjectMapperBuilderCustomizer customizerObjectMapperNumber() {
        log.info("SlardarWebmvc spring-bean customizerObjectMapperNumber");
        return builder -> {
            // Number
            final SlardarNumberProp.Nf ints = slardarNumberProp.getInteger();
            if (ints.isEnable()) {
                final DecimalFormat df = ints.getWellFormat();
                final Digital digital = ints.getDigital();
                log.info("SlardarWebmvc conf Jackson2ObjectMapperBuilderCustomizer Integer&Long serializer");
                builder.serializerByType(Integer.class, new FormatNumberSerializer(Integer.class, df, digital));
                builder.serializerByType(Integer.TYPE, new FormatNumberSerializer(Integer.TYPE, df, digital));
                builder.serializerByType(Long.class, new FormatNumberSerializer(Long.class, df, digital));
                builder.serializerByType(Long.TYPE, new FormatNumberSerializer(Long.TYPE, df, digital));
            }

            final SlardarNumberProp.Nf floats = slardarNumberProp.getFloats();
            if (floats.isEnable()) {
                final DecimalFormat df = floats.getWellFormat();
                final Digital digital = floats.getDigital();
                log.info("SlardarWebmvc conf Jackson2ObjectMapperBuilderCustomizer Float&Double serializer");
                builder.serializerByType(Float.class, new FormatNumberSerializer(Float.class, df, digital));
                builder.serializerByType(Float.TYPE, new FormatNumberSerializer(Float.TYPE, df, digital));
                builder.serializerByType(Double.class, new FormatNumberSerializer(Double.class, df, digital));
                builder.serializerByType(Double.TYPE, new FormatNumberSerializer(Double.TYPE, df, digital));
            }

            final SlardarNumberProp.Nf decimal = slardarNumberProp.getDecimal();
            if (decimal.isEnable()) {
                final DecimalFormat df = decimal.getWellFormat();
                log.info("SlardarWebmvc conf Jackson2ObjectMapperBuilderCustomizer BigDecimal serializer");
                builder.serializerByType(BigDecimal.class, new FormatNumberSerializer(BigDecimal.class, df, decimal.getDigital()));
            }
        };
    }

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer customizerObjectMapperJackson() {
        log.info("SlardarWebmvc spring-bean customizerObjectMapperJackson");
        return builder -> {
            if (StringUtils.hasText(slardarJacksonProp.getEmptyDate()) ||
                slardarJacksonProp.isEmptyMap() || slardarJacksonProp.isEmptyList()) {
                log.info("SlardarWebmvc conf EmptyValuePropertyFilter's EmptyDateMixin");
                builder.mixIn(Object.class, EmptyValuePropertyFilter.EmptyDateMixin.class);
            }

            if (slardarJacksonProp.isI18nResult()) {
                log.info("SlardarWebmvc conf I18nResultPropertyFilter's I18nResultMixin");
                builder.serializerByType(I18nString.class, new I18nStringSerializer(messageSource, true));
                builder.serializerByType(CharSequence.class, new I18nStringSerializer(messageSource, false));
                builder.mixIn(R.class, I18nResultPropertyFilter.I18nResultMixin.class);
            }
        };
    }

    @Bean
    public FilterProvider slardarFilterProvider(List<AutoRegisterPropertyFilter> filters) {
        log.info("SlardarWebmvc spring-bean slardarFilterProvider");
        final SimpleFilterProvider bean = new SimpleFilterProvider();
        for (AutoRegisterPropertyFilter filter : filters) {
            bean.addFilter(filter.getId(), filter);
        }
        return bean;
    }

    @Bean
    @ConditionalOnBean(FilterProvider.class)
    public Jackson2ObjectMapperBuilderCustomizer customizerObjectMapperFilterProvider(FilterProvider filterProvider) {
        log.info("SlardarWebmvc spring-bean customizerObjectMapperFilterProvider");
        return builder -> builder.filters(filterProvider);
    }

    @Bean
    public CommandLineRunner runnerJacksonHelper(Jackson2ObjectMapperBuilder builder) {
        log.info("SlardarWebmvc spring-runs runnerJacksonHelper");
        return args -> {
            log.info("SlardarWebmvc spring-conf JacksonHelper.initGlobal");
            JacksonHelper.initGlobal(
                    builder.createXmlMapper(false).build(),
                    builder.createXmlMapper(true).build()
            );
        };
    }
}
