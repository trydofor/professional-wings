package pro.fessional.wings.slardar.spring.bean;

import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.fasterxml.jackson.databind.ser.std.DateSerializer;
import com.fasterxml.jackson.datatype.jsr310.deser.InstantDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.util.StringUtils;
import pro.fessional.mirana.data.R;
import pro.fessional.mirana.i18n.I18nString;
import pro.fessional.wings.silencer.runner.ApplicationStartedEventRunner;
import pro.fessional.wings.silencer.spring.WingsOrdered;
import pro.fessional.wings.silencer.spring.boot.ConditionalWingsEnabled;
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
import pro.fessional.wings.slardar.jackson.ResourceSerializer;
import pro.fessional.wings.slardar.spring.prop.SlardarDatetimeProp;
import pro.fessional.wings.slardar.spring.prop.SlardarEnabledProp;
import pro.fessional.wings.slardar.spring.prop.SlardarJacksonProp;
import pro.fessional.wings.slardar.spring.prop.SlardarNumberProp;

import java.math.BigDecimal;
import java.math.BigInteger;
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
 * @link <a href="https://docs.spring.io/spring-boot/docs/3.0.3/reference/htmlsingle/#howto.spring-mvc.customize-jackson-objectmapper">Customize the Jackson ObjectMapper</a>
 * @see InstantDeserializer#ZONED_DATE_TIME
 * @since 2019-06-26
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(DateSerializer.class)
@ConditionalWingsEnabled
public class SlardarJacksonWebConfiguration {

    private static final Log log = LogFactory.getLog(SlardarJacksonWebConfiguration.class);

    /**
     * The context's Jackson2ObjectMapperBuilder can be customized by one or more
     * Jackson2ObjectMapperBuilderCustomizer beans. Such customizer beans can be ordered
     * (Boot's own customizer has an order of 0), letting additional
     * customization be applied both before and after Boot's customization.
     * <p>
     * If you provide any @Beans of type MappingJackson2HttpMessageConverter,
     * they replace the default value in the MVC configuration. Also,
     * a convenience bean of type HttpMessageConverters is provided
     * (and is always available if you use the default MVC configuration).
     * It has some useful methods to access the default and user-enhanced message converters.
     */
    @Bean
    @ConditionalWingsEnabled(abs = SlardarEnabledProp.Key$jacksonDatetime)
    public Jackson2ObjectMapperBuilderCustomizer customizeJacksonDatetime(SlardarDatetimeProp prop) {
        log.info("SlardarWebmvc spring-bean customizeJacksonDatetime");
        return builder -> {
            // local
            var date = DateTimeFormatter.ofPattern(prop.getDate().getFormat());
            var datePsr = prop.getDate()
                              .getSupport()
                              .stream()
                              .map(DateTimeFormatter::ofPattern)
                              .collect(Collectors.toList());
            builder.serializerByType(LocalDate.class, new LocalDateSerializer(date));
            builder.deserializerByType(LocalDate.class, new JacksonLocalDateDeserializer(date, datePsr));
            log.info("SlardarWebmvc conf Jackson2ObjectMapperBuilderCustomizer LocalDate");

            var time = DateTimeFormatter.ofPattern(prop.getTime().getFormat());
            var timePsr = prop.getTime()
                              .getSupport()
                              .stream()
                              .map(DateTimeFormatter::ofPattern)
                              .collect(Collectors.toList());
            builder.serializerByType(LocalTime.class, new LocalTimeSerializer(time));
            builder.deserializerByType(LocalTime.class, new JacksonLocalTimeDeserializer(time, timePsr));
            log.info("SlardarWebmvc conf Jackson2ObjectMapperBuilderCustomizer LocalTime");

            // auto local
            var full = DateTimeFormatter.ofPattern(prop.getDatetime().getFormat());
            final AutoZoneType autoLocal = AutoZoneType.valueOf(prop.getDatetime().isAuto());
            JacksonLocalDateTimeSerializer.defaultFormatter = full;
            JacksonLocalDateTimeSerializer.defaultAutoZone = autoLocal;
            builder.serializerByType(LocalDateTime.class, new JacksonLocalDateTimeSerializer(full, autoLocal));

            var fullPsr = prop.getDatetime()
                              .getSupport()
                              .stream()
                              .map(DateTimeFormatter::ofPattern)
                              .collect(Collectors.toList());
            builder.deserializerByType(LocalDateTime.class, new JacksonLocalDateTimeDeserializer(full, fullPsr, autoLocal));
            log.info("SlardarWebmvc conf Jackson2ObjectMapperBuilderCustomizer LocalDateTime");

            // auto zoned
            DateTimeFormatter zoned = DateTimeFormatter.ofPattern(prop.getZoned().getFormat());
            final AutoZoneType autoZone = AutoZoneType.valueOf(prop.getZoned().isAuto());
            JacksonZonedDateTimeSerializer.defaultFormatter = zoned;
            JacksonZonedDateTimeSerializer.defaultAutoZone = autoZone;
            builder.serializerByType(ZonedDateTime.class, new JacksonZonedDateTimeSerializer(zoned, autoZone));

            var zonePsr = prop.getZoned()
                              .getSupport()
                              .stream()
                              .map(DateTimeFormatter::ofPattern)
                              .collect(Collectors.toList());

            builder.deserializerByType(ZonedDateTime.class, new JacksonZonedDateTimeDeserializer(zoned, zonePsr, autoZone));
            log.info("SlardarWebmvc conf Jackson2ObjectMapperBuilderCustomizer ZonedDateTime");

            // auto offset
            DateTimeFormatter offset = DateTimeFormatter.ofPattern(prop.getOffset().getFormat());
            final AutoZoneType autoOffset = AutoZoneType.valueOf(prop.getOffset().isAuto());
            JacksonOffsetDateTimeSerializer.defaultFormatter = offset;
            JacksonOffsetDateTimeSerializer.defaultAutoZone = autoOffset;
            builder.serializerByType(OffsetDateTime.class, new JacksonOffsetDateTimeSerializer(offset, autoOffset));

            var offPsr = prop.getZoned()
                             .getSupport()
                             .stream()
                             .map(DateTimeFormatter::ofPattern)
                             .collect(Collectors.toList());

            builder.deserializerByType(OffsetDateTime.class, new JacksonOffsetDateTimeDeserializer(offset, offPsr, autoOffset));
            log.info("SlardarWebmvc conf Jackson2ObjectMapperBuilderCustomizer OffsetDateTime");
        };
    }

    @Bean
    @ConditionalWingsEnabled(abs = SlardarEnabledProp.Key$jacksonEmpty)
    public Jackson2ObjectMapperBuilderCustomizer customizeJacksonEmpty(SlardarJacksonProp prop) {
        log.info("SlardarWebmvc spring-bean customizeJacksonEmpty");
        return builder -> {
            if (StringUtils.hasText(prop.getEmptyDate()) || prop.isEmptyMap() || prop.isEmptyList()) {
                log.info("SlardarWebmvc conf EmptyValuePropertyFilter's EmptyDateMixin");
                builder.mixIn(Object.class, EmptyValuePropertyFilter.EmptyDateMixin.class);
            }
        };
    }

    @Bean
    @ConditionalWingsEnabled(abs = SlardarEnabledProp.Key$jacksonNumber)
    public Jackson2ObjectMapperBuilderCustomizer customizeJacksonNumber(SlardarNumberProp prop) {
        log.info("SlardarWebmvc spring-bean customizeJacksonNumber");
        return builder -> {
            // Number
            final SlardarNumberProp.Nf ints = prop.getInteger();
            if (ints.isEnable()) {
                final DecimalFormat df = ints.getWellFormat();
                final Digital digital = ints.getDigital();
                log.info("SlardarWebmvc conf Jackson2ObjectMapperBuilderCustomizer Integer&Long serializer");
                builder.serializerByType(Integer.class, new FormatNumberSerializer(Integer.class, df, digital));
                builder.serializerByType(Integer.TYPE, new FormatNumberSerializer(Integer.TYPE, df, digital));
                builder.serializerByType(Long.class, new FormatNumberSerializer(Long.class, df, digital));
                builder.serializerByType(Long.TYPE, new FormatNumberSerializer(Long.TYPE, df, digital));
            }

            final SlardarNumberProp.Nf floats = prop.getFloats();
            if (floats.isEnable()) {
                final DecimalFormat df = floats.getWellFormat();
                final Digital digital = floats.getDigital();
                log.info("SlardarWebmvc conf Jackson2ObjectMapperBuilderCustomizer Float&Double serializer");
                builder.serializerByType(Float.class, new FormatNumberSerializer(Float.class, df, digital));
                builder.serializerByType(Float.TYPE, new FormatNumberSerializer(Float.TYPE, df, digital));
                builder.serializerByType(Double.class, new FormatNumberSerializer(Double.class, df, digital));
                builder.serializerByType(Double.TYPE, new FormatNumberSerializer(Double.TYPE, df, digital));
            }

            final SlardarNumberProp.Nf decimal = prop.getDecimal();
            if (decimal.isEnable()) {
                final DecimalFormat df = decimal.getWellFormat();
                log.info("SlardarWebmvc conf Jackson2ObjectMapperBuilderCustomizer BigDecimal/BigInteger serializer");
                builder.serializerByType(BigDecimal.class, new FormatNumberSerializer(BigDecimal.class, df, decimal.getDigital()));
                builder.serializerByType(BigInteger.class, new FormatNumberSerializer(BigInteger.class, df, decimal.getDigital()));
            }
        };
    }

    @Bean
    @ConditionalWingsEnabled(abs = SlardarEnabledProp.Key$jacksonResource)
    public Jackson2ObjectMapperBuilderCustomizer customizeJacksonResource() {
        log.info("SlardarWebmvc spring-bean customizeJacksonResource");
        return builder -> {
            log.info("SlardarWebmvc conf Jackson2ObjectMapperBuilderCustomizer Resource");
            builder.serializerByType(Resource.class, new ResourceSerializer());
        };
    }

    @Bean
    @ConditionalWingsEnabled(abs = SlardarEnabledProp.Key$jacksonResult)
    public Jackson2ObjectMapperBuilderCustomizer customizeJacksonResult(SlardarJacksonProp prop, MessageSource source) {
        log.info("SlardarWebmvc spring-bean customizerObjectMapperJackson");
        return builder -> {
            if (prop.isI18nResult()) {
                log.info("SlardarWebmvc conf I18nResultPropertyFilter's I18nResultMixin");
                builder.serializerByType(I18nString.class, new I18nStringSerializer(source, true));
                builder.serializerByType(CharSequence.class, new I18nStringSerializer(source, false));
                builder.mixIn(R.class, I18nResultPropertyFilter.I18nResultMixin.class);
            }
        };
    }

    @Bean
    @ConditionalWingsEnabled
    public Jackson2ObjectMapperBuilderCustomizer jacksonCustomizerFilter(FilterProvider filterProvider) {
        log.info("SlardarWebmvc spring-bean jacksonCustomizerFilter");
        return builder -> {
            log.info("SlardarWebmvc conf Jackson2ObjectMapperBuilderCustomizer filters");
            builder.filters(filterProvider);
        };
    }

    @Bean
    @ConditionalWingsEnabled
    public FilterProvider jacksonFilterProvider(List<AutoRegisterPropertyFilter> filters) {
        log.info("SlardarWebmvc spring-bean jacksonFilterProvider");
        final SimpleFilterProvider bean = new SimpleFilterProvider();
        for (AutoRegisterPropertyFilter filter : filters) {
            bean.addFilter(filter.getId(), filter);
        }
        return bean;
    }

    @Bean
    @ConditionalWingsEnabled
    public ApplicationStartedEventRunner jacksonHelperRunner(Jackson2ObjectMapperBuilder builder) {
        log.info("SlardarWebmvc spring-runs jacksonHelperRunner");
        return new ApplicationStartedEventRunner(WingsOrdered.Lv1Config, ignored -> {
            log.info("SlardarWebmvc spring-conf JacksonHelper.initGlobal");
            JacksonHelper.initGlobal(
                    builder.createXmlMapper(false).build(),
                    builder.createXmlMapper(true).build()
            );
        });
    }
}
