package pro.fessional.wings.slardar.spring.bean;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.fasterxml.jackson.databind.ser.std.DateSerializer;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.jsr310.deser.InstantDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import pro.fessional.mirana.i18n.I18nAware;
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
import pro.fessional.wings.slardar.jackson.JacksonIncludeValue;
import pro.fessional.wings.slardar.jackson.FormatNumberSerializer;
import pro.fessional.wings.slardar.jackson.FormatNumberSerializer.Digital;
import pro.fessional.wings.slardar.jackson.I18nAwarePropertyFilter;
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
            var jacksonLocalDateTimeSerializer = new JacksonLocalDateTimeSerializer(full, autoLocal);
            builder.serializerByType(LocalDateTime.class, jacksonLocalDateTimeSerializer);

            var fullPsr = prop.getDatetime()
                              .getSupport()
                              .stream()
                              .map(DateTimeFormatter::ofPattern)
                              .collect(Collectors.toList());
            var jacksonLocalDateTimeDeserializer = new JacksonLocalDateTimeDeserializer(full, fullPsr, autoLocal);
            builder.deserializerByType(LocalDateTime.class, jacksonLocalDateTimeDeserializer);
            log.info("SlardarWebmvc conf Jackson2ObjectMapperBuilderCustomizer LocalDateTime");

            // auto zoned
            DateTimeFormatter zoned = DateTimeFormatter.ofPattern(prop.getZoned().getFormat());
            final AutoZoneType autoZone = AutoZoneType.valueOf(prop.getZoned().isAuto());
            JacksonZonedDateTimeSerializer.defaultFormatter = zoned;
            JacksonZonedDateTimeSerializer.defaultAutoZone = autoZone;
            var jacksonZonedDateTimeSerializer = new JacksonZonedDateTimeSerializer(zoned, autoZone);
            builder.serializerByType(ZonedDateTime.class, jacksonZonedDateTimeSerializer);

            var zonePsr = prop.getZoned()
                              .getSupport()
                              .stream()
                              .map(DateTimeFormatter::ofPattern)
                              .collect(Collectors.toList());

            var jacksonZonedDateTimeDeserializer = new JacksonZonedDateTimeDeserializer(zoned, zonePsr, autoZone);
            builder.deserializerByType(ZonedDateTime.class, jacksonZonedDateTimeDeserializer);
            log.info("SlardarWebmvc conf Jackson2ObjectMapperBuilderCustomizer ZonedDateTime");

            // auto offset
            DateTimeFormatter offset = DateTimeFormatter.ofPattern(prop.getOffset().getFormat());
            final AutoZoneType autoOffset = AutoZoneType.valueOf(prop.getOffset().isAuto());
            JacksonOffsetDateTimeSerializer.defaultFormatter = offset;
            JacksonOffsetDateTimeSerializer.defaultAutoZone = autoOffset;
            var jacksonOffsetDateTimeSerializer = new JacksonOffsetDateTimeSerializer(offset, autoOffset);
            builder.serializerByType(OffsetDateTime.class, jacksonOffsetDateTimeSerializer);

            var offPsr = prop.getZoned()
                             .getSupport()
                             .stream()
                             .map(DateTimeFormatter::ofPattern)
                             .collect(Collectors.toList());

            var jacksonOffsetDateTimeDeserializer = new JacksonOffsetDateTimeDeserializer(offset, offPsr, autoOffset);
            builder.deserializerByType(OffsetDateTime.class, jacksonOffsetDateTimeDeserializer);
            log.info("SlardarWebmvc conf Jackson2ObjectMapperBuilderCustomizer OffsetDateTime");

            new JacksonHelper() {{
                beanLocalDateTimeSerializer = jacksonLocalDateTimeSerializer;
                beanLocalDateTimeDeserializer = jacksonLocalDateTimeDeserializer;
                beanZonedDateTimeSerializer = jacksonZonedDateTimeSerializer;
                beanZonedDateTimeDeserializer = PlainZonedDateTimeDeserializer;
                beanOffsetDateTimeSerializer = jacksonOffsetDateTimeSerializer;
                beanOffsetDateTimeDeserializer = jacksonOffsetDateTimeDeserializer;
            }};
        };
    }

    @Bean
    @ConditionalWingsEnabled(abs = SlardarEnabledProp.Key$jacksonEmpty)
    public Jackson2ObjectMapperBuilderCustomizer customizeJacksonEmpty(SlardarJacksonProp prop) {
        log.info("SlardarWebmvc spring-bean customizeJacksonEmpty");
        return builder -> {
            if (prop.getEmptyDate() != null) {
                log.info("SlardarWebmvc conf EmptyValuePropertyFilter");
                new JacksonIncludeValue(prop.getEmptyDate(), prop.getEmptyDateOffset()){};
                builder.postConfigurer(JacksonIncludeValue::configNonEmptyDates);
            }
        };
    }

    @Bean
    @ConditionalWingsEnabled(abs = SlardarEnabledProp.Key$jacksonNumber)
    public Jackson2ObjectMapperBuilderCustomizer customizeJacksonNumber(SlardarNumberProp prop) {
        log.info("SlardarWebmvc spring-bean customizeJacksonNumber");
        return builder -> {
            // Number
            final SlardarNumberProp.Nf integer = prop.getInteger();
            final DecimalFormat idf = integer.getWellFormat();
            final Digital idt = integer.getDigital();
            log.info("SlardarWebmvc conf Jackson2ObjectMapperBuilderCustomizer Integer&Long serializer, NumberFormat=" + integer);
            builder.serializerByType(Integer.class, new FormatNumberSerializer(Integer.class, idf, idt));
            builder.serializerByType(Integer.TYPE, new FormatNumberSerializer(Integer.TYPE, idf, idt));
            builder.serializerByType(Long.class, new FormatNumberSerializer(Long.class, idf, idt));
            builder.serializerByType(Long.TYPE, new FormatNumberSerializer(Long.TYPE, idf, idt));

            final SlardarNumberProp.Nf floats = prop.getFloats();
            final DecimalFormat fdf = floats.getWellFormat();
            final Digital fdt = floats.getDigital();
            log.info("SlardarWebmvc conf Jackson2ObjectMapperBuilderCustomizer Float&Double serializer, NumberFormat=" + floats);
            builder.serializerByType(Float.class, new FormatNumberSerializer(Float.class, fdf, fdt));
            builder.serializerByType(Float.TYPE, new FormatNumberSerializer(Float.TYPE, fdf, fdt));
            builder.serializerByType(Double.class, new FormatNumberSerializer(Double.class, fdf, fdt));
            builder.serializerByType(Double.TYPE, new FormatNumberSerializer(Double.TYPE, fdf, fdt));


            final SlardarNumberProp.Nf decimal = prop.getDecimal();
            final DecimalFormat ddf = decimal.getWellFormat();
            Digital ddt = decimal.getDigital();
            log.info("SlardarWebmvc conf Jackson2ObjectMapperBuilderCustomizer BigDecimal/BigInteger serializer, NumberFormat=" + decimal);
            builder.serializerByType(BigDecimal.class, new FormatNumberSerializer(BigDecimal.class, ddf, ddt));
            builder.serializerByType(BigInteger.class, new FormatNumberSerializer(BigInteger.class, ddf, ddt));

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
                I18nAware.I18nSource i18nSource = source::getMessage;
                builder.serializerByType(I18nString.class, new I18nStringSerializer(i18nSource, true));
                builder.serializerByType(CharSequence.class, new I18nStringSerializer(i18nSource, false));
                builder.mixIn(I18nAwarePropertyFilter.MixinClass, I18nAwarePropertyFilter.I18nAwareMixin.class);
            }
        };
    }

    @Bean
    @ConditionalWingsEnabled
    public Jackson2ObjectMapperBuilderCustomizer jacksonCustomizerFilter(List<FilterProvider> filterProviders) {
        log.info("SlardarWebmvc spring-bean jacksonCustomizerFilter");
        return builder -> {
            log.info("SlardarWebmvc conf Jackson2ObjectMapperBuilderCustomizer filters");
            for (FilterProvider fp : filterProviders) {
                builder.filters(fp);
            }
        };
    }

    @Bean
    @ConditionalWingsEnabled
    public SimpleFilterProvider jacksonFilterProvider(List<AutoRegisterPropertyFilter> filters) {
        log.info("SlardarWebmvc spring-bean jacksonFilterProvider");
        final SimpleFilterProvider bean = new SimpleFilterProvider();
        for (AutoRegisterPropertyFilter filter : filters) {
            bean.addFilter(filter.getId(), filter);
        }
        return bean;
    }

    @Bean
    @ConditionalWingsEnabled
    public ApplicationStartedEventRunner jacksonHelperRunner(ApplicationContext context) {
        log.info("SlardarWebmvc spring-runs jacksonHelperRunner");
        return new ApplicationStartedEventRunner(WingsOrdered.Lv1Config, ignored -> {
            log.info("SlardarWebmvc spring-conf JacksonHelper.initGlobal");
            new JacksonHelper() {{
                var builder = context.getBean(Jackson2ObjectMapperBuilder.class);

                // wings
                prepareWings(
                    builder.createXmlMapper(false).build(),
                    builder.createXmlMapper(true).build()
                );

                // bean
                var jsonBean = context.getBeanProvider(ObjectMapper.class);
                var xmlBean = context.getBeanProvider(XmlMapper.class);
                prepareBean(
                    jsonBean.getIfAvailable(() -> builder.createXmlMapper(false).build()),
                    xmlBean.getIfAvailable(() -> builder.createXmlMapper(true).build())
                );

                // at last, restore createXmlMapper to false
                builder.createXmlMapper(false);
            }};
        });
    }
}
