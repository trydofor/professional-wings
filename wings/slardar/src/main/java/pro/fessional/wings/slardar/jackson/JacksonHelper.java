package pro.fessional.wings.slardar.jackson;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.cfg.MapperBuilder;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.SneakyThrows;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.ResolvableType;
import org.springframework.core.convert.TypeDescriptor;
import pro.fessional.mirana.text.WhiteUtil;
import pro.fessional.mirana.time.DateFormatter;
import pro.fessional.wings.silencer.datetime.DateTimePattern;
import pro.fessional.wings.silencer.support.TypeSugar;
import pro.fessional.wings.slardar.autozone.AutoZoneType;
import pro.fessional.wings.slardar.autozone.json.JacksonLocalDateTimeDeserializer;
import pro.fessional.wings.slardar.autozone.json.JacksonLocalDateTimeSerializer;
import pro.fessional.wings.slardar.autozone.json.JacksonOffsetDateTimeDeserializer;
import pro.fessional.wings.slardar.autozone.json.JacksonOffsetDateTimeSerializer;
import pro.fessional.wings.slardar.autozone.json.JacksonZonedDateTimeDeserializer;
import pro.fessional.wings.slardar.autozone.json.JacksonZonedDateTimeSerializer;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * <pre>
 * <a href="https://github.com/FasterXML/jackson-dataformat-xml#known-limitations">XML limitation</a>
 * The common uses are:
 * (1) single element node, XML can not distinguish between a single value or only one value in the array, unless nested wrap.
 * (2) Xml can not recognize the data type, while Json has string, number, boolean, object, array
 *
 * Jackson Plain
 * - `transient` output
 * - `@Transient` No output
 * - `byte[]` as base64, `[]` as `""`
 * - `char[]` as String, `[]` as `""`
 * - WRITE_DATES_AS_TIMESTAMPS = false
 * - `ZonedDateTime` parse as `2023-04-04T21:07:08Z` lost timezone
 * - `OffsetDateTime` parse as `2023-04-05T10:07:08Z` lost timezone
 * Jackson Wings
 * - `transient` No output
 * - WRITE_DATES_AS_TIMESTAMPS = false
 * - `LocalDateTime` as `"2023-04-05T06:07:08"`
 * - `ZonedDateTime` as `"2023-04-05T06:07:08[America/New_York]"` keep timezone
 * - `OffsetDateTime` as `"2023-04-05T06:07:08-04:00"` keep timezone
 * Jackson Bean
 * - `LocalDateTime` as `"2023-04-05 06:07:08"`
 * - `ZonedDateTime` as `"2023-04-05 06:07:08 Asia/Shanghai"`
 * - `OffsetDateTime` as `"2023-04-05 06:07:08 +08:00"`
 * - `float`,`double` as `"3.14159"`
 * - `BigDecimal`,`BigInteger` as `"299792458"`
 * </pre>
 *
 * @author trydofor
 * @since 2022-11-05
 */
public class JacksonHelper {

    public enum Style {
        /**
         * at web tier with ' ' datetime and auto timezone/i18n convert
         */
        Bean,
        /**
         * fastjon default with WRITE_DATES_AS_TIMESTAMPS=false and other Disable/Enable
         */
        Plain,
        /**
         * wings config with 'T' datetime format, without timezone/i18n conversion
         */
        Wings,
    }

    // spring-jackson-79.properties
    public static final com.fasterxml.jackson.databind.DeserializationFeature[] EnableDeserializationFeature = {
        com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_NUMBERS_FOR_ENUMS,
        com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES,
        com.fasterxml.jackson.databind.DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY,
        com.fasterxml.jackson.databind.DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE
    };
    public static final com.fasterxml.jackson.core.JsonGenerator.Feature[] EnableJsonGeneratorFeature = {
        com.fasterxml.jackson.core.JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN
    };
    public static final com.fasterxml.jackson.databind.MapperFeature[] EnableMapperFeature = {
        com.fasterxml.jackson.databind.MapperFeature.PROPAGATE_TRANSIENT_MARKER,
        com.fasterxml.jackson.databind.MapperFeature.DEFAULT_VIEW_INCLUSION,
        com.fasterxml.jackson.databind.MapperFeature.ALLOW_EXPLICIT_PROPERTY_RENAMING
    };
    public static final com.fasterxml.jackson.core.JsonParser.Feature[] EnableParserFeature = {
        com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_COMMENTS,
        com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_YAML_COMMENTS,
        com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES,
        com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_SINGLE_QUOTES,
        com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_NUMERIC_LEADING_ZEROS,
        com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_MISSING_VALUES,
        com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_TRAILING_COMMA
    };
    public static final com.fasterxml.jackson.databind.SerializationFeature[] EnableSerializationFeature = {
        com.fasterxml.jackson.databind.SerializationFeature.CLOSE_CLOSEABLE
    };
    public static final com.fasterxml.jackson.core.JsonParser.Feature[] DisableParserFeature = {
        com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER
    };
    public static final com.fasterxml.jackson.databind.DeserializationFeature[] DisableDeserializationFeature = {
        com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES
    };
    public static final com.fasterxml.jackson.databind.SerializationFeature[] DisableSerializationFeature = {
        com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS,
        com.fasterxml.jackson.databind.SerializationFeature.FAIL_ON_EMPTY_BEANS
    };

    public static final JacksonLocalDateTimeDeserializer PlainLocalDateTimeDeserializer = new JacksonLocalDateTimeDeserializer(
        DateTimeFormatter.ISO_LOCAL_DATE_TIME, List.of(DateFormatter.FMT_FULL_PSE), AutoZoneType.Off);

    public static final JacksonOffsetDateTimeDeserializer PlainOffsetDateTimeDeserializer = new JacksonOffsetDateTimeDeserializer(
        DateTimeFormatter.ISO_OFFSET_DATE_TIME, List.of(DateFormatter.FMT_ZONE_PSE), AutoZoneType.Off);

    public static final JacksonZonedDateTimeSerializer PlainZonedDateTimeSerializer = new JacksonZonedDateTimeSerializer(
        DateTimePattern.FMT_FULL_19TV, AutoZoneType.Off);

    public static final JacksonZonedDateTimeDeserializer PlainZonedDateTimeDeserializer = new JacksonZonedDateTimeDeserializer(
        DateTimePattern.FMT_FULL_19TV, List.of(DateFormatter.FMT_ZONE_PSE), AutoZoneType.Off);

    public static final SimpleModule PlainDateTimeModule = new SimpleModule() {{
        addDeserializer(LocalDateTime.class, PlainLocalDateTimeDeserializer);
        addDeserializer(OffsetDateTime.class, PlainOffsetDateTimeDeserializer);
        addSerializer(ZonedDateTime.class, PlainZonedDateTimeSerializer);
        addDeserializer(ZonedDateTime.class, PlainZonedDateTimeDeserializer);
    }};

    @Contract("_->param1")
    public static <T extends MapperBuilder<?, ?>> T buildPlain(@NotNull T builder) {
        builder
            .enable(EnableDeserializationFeature)
            .enable(EnableJsonGeneratorFeature)
            .enable(EnableMapperFeature)
            .enable(EnableParserFeature)
            .enable(EnableSerializationFeature)
            .disable(DisableParserFeature)
            .disable(DisableDeserializationFeature)
            .disable(DisableSerializationFeature)
            // override
            .addModule(PlainDateTimeModule);
        return builder;
    }

    // https://github.com/FasterXML/jackson-modules-java8/tree/master/datetime#usage
    public static final ObjectMapper JsonPlain = buildPlain(JsonMapper.builder().findAndAddModules()).build();
    public static final XmlMapper XmlPlain = buildPlain(XmlMapper.builder().findAndAddModules()).build();
    public static final TypeFactory TypeFactoryPlain = JsonPlain.getTypeFactory();

    //// wings autoZone ser/des
    protected static JacksonLocalDateTimeSerializer beanLocalDateTimeSerializer;
    protected static JacksonLocalDateTimeDeserializer beanLocalDateTimeDeserializer;
    protected static JacksonZonedDateTimeSerializer beanZonedDateTimeSerializer;
    protected static JacksonZonedDateTimeDeserializer beanZonedDateTimeDeserializer;
    protected static JacksonOffsetDateTimeSerializer beanOffsetDateTimeSerializer;
    protected static JacksonOffsetDateTimeDeserializer beanOffsetDateTimeDeserializer;

    @Contract("_->param1")
    public static <T extends ObjectMapper> T buildWings(@NotNull T mapper) {
        SimpleModule beanDateTimeModule = new SimpleModule();
        // auto off
        boolean hasBean = false;
        if (beanLocalDateTimeSerializer != null) {
            hasBean = true;
            beanDateTimeModule.addSerializer(LocalDateTime.class, beanLocalDateTimeSerializer.autoOff());
        }
        if (beanLocalDateTimeDeserializer != null) {
            hasBean = true;
            beanDateTimeModule.addDeserializer(LocalDateTime.class, beanLocalDateTimeDeserializer.autoOff());
        }
        if (beanZonedDateTimeSerializer != null) {
            hasBean = true;
            beanDateTimeModule.addSerializer(ZonedDateTime.class, beanZonedDateTimeSerializer.autoOff());
        }
        if (beanZonedDateTimeDeserializer != null) {
            hasBean = true;
            beanDateTimeModule.addDeserializer(ZonedDateTime.class, beanZonedDateTimeDeserializer.autoOff());
        }
        if (beanOffsetDateTimeSerializer != null) {
            hasBean = true;
            beanDateTimeModule.addSerializer(OffsetDateTime.class, beanOffsetDateTimeSerializer.autoOff());
        }
        if (beanOffsetDateTimeDeserializer != null) {
            hasBean = true;
            beanDateTimeModule.addDeserializer(OffsetDateTime.class, beanOffsetDateTimeDeserializer.autoOff());
        }

        SimpleModule dtm = hasBean ? beanDateTimeModule : PlainDateTimeModule;
        mapper.registerModule(dtm);
        return mapper;
    }

    private static ObjectMapper JsonWings = JsonPlain;
    private static XmlMapper XmlWings = XmlPlain;

    protected static void bindJsonWings(@NotNull ObjectMapper mapper) {
        JsonWings = buildWings(mapper);
    }

    protected static void bindXmlWings(@NotNull XmlMapper mapper) {
        XmlWings = buildWings(mapper);
    }

    private static ObjectMapper JsonBean = null;
    private static XmlMapper XmlBean = null;

    protected static void bindJsonBean(@NotNull ObjectMapper mapper) {
        JsonBean = mapper;
    }

    protected static void bindXmlBean(@NotNull XmlMapper mapper) {
        XmlBean = mapper;
    }

    ////

    /**
     * spring bean style Mapper at web tier with ' ' datetime and auto timezone/i18n convert
     */
    @NotNull
    public static ObjectMapper JsonBean() {
        if (JsonBean == null) throw new IllegalStateException("JsonBean not init, check SlardarJacksonWebConfiguration.jacksonHelperRunner");
        return JsonBean;
    }

    /**
     * spring bean style Mapper at web tier with ' ' datetime and auto timezone/i18n convert
     */
    @NotNull
    public static XmlMapper XmlBean() {
        if (XmlBean == null) throw new IllegalStateException("XmlBean not init, check SlardarJacksonWebConfiguration.jacksonHelperRunner");
        return XmlBean;
    }

    /**
     * spring bean style Mapper at web tier with ' ' datetime and auto timezone/i18n convert
     */
    @NotNull
    public static ObjectMapper MapperBean(boolean json) {
        return json ? JsonBean() : XmlBean();
    }

    /**
     * fastjon default with WRITE_DATES_AS_TIMESTAMPS=false and other Disable/Enable
     */
    @NotNull
    public static ObjectMapper JsonPlain() {
        return JsonPlain;
    }

    /**
     * fastjon default with WRITE_DATES_AS_TIMESTAMPS=false and other Disable/Enable
     */
    @NotNull
    public static XmlMapper XmlPlain() {
        return XmlPlain;
    }

    /**
     * fastjon default with WRITE_DATES_AS_TIMESTAMPS=false and other Disable/Enable
     */
    @NotNull
    public static ObjectMapper MapperPlain(boolean json) {
        return json ? JsonPlain : XmlPlain;
    }

    /**
     * wings config with 'T' datetime format without timezone/i18n conversion
     */
    @NotNull
    public static ObjectMapper JsonWings() {
        return JsonWings;
    }

    /**
     * wings config with 'T' datetime format without timezone/i18n conversion
     */
    @NotNull
    public static XmlMapper XmlWings() {
        return XmlWings;
    }

    /**
     * wings config with 'T' datetime format without timezone/i18n conversion
     */
    @NotNull
    public static ObjectMapper MapperWings(boolean json) {
        return json ? JsonWings : XmlWings;
    }

    /**
     * wings configed Mapper without auto timezone convert
     */
    @NotNull
    public static ObjectMapper Mapper(@NotNull Style style, boolean json) {
        return switch (style) {
            case Wings -> MapperWings(json);
            case Plain -> MapperPlain(json);
            case Bean -> MapperBean(json);
        };
    }

    /**
     * whether `str` has xml characteristics, i.e. the first and last characters are angle brackets or not
     */
    @Contract("null->false")
    public static boolean asXml(String str) {
        if (str == null) return false;

        char c1 = WhiteUtil.firstNonWhite(str);
        if (c1 == '<') {
            char c2 = WhiteUtil.lastNonWhite(str);
            return c2 == '>';
        }
        return false;
    }

    /**
     * whether `str` has xml characteristics, i.e. the first and last characters are angle brackets or not
     */
    @Contract("null->false")
    public static boolean asXml(byte[] str) {
        if (str == null) return false;

        byte c1 = WhiteUtil.firstNonWhite(str);
        if (c1 == (byte) '<') {
            byte c2 = WhiteUtil.lastNonWhite(str);
            return c2 == (byte) '>';
        }
        return false;
    }

    /////////////

    /**
     * construct jackson's JavaType by TypeSugar
     */
    public static JavaType javaType(@NotNull Class<?> targetType, Class<?>... generics) {
        Type type = TypeSugar.type(targetType, generics);
        return TypeFactoryPlain.constructType(type);
    }

    /**
     * construct jackson's JavaType in spring way
     */
    public static JavaType javaType(@NotNull TypeDescriptor targetType) {
        Type type = targetType.getResolvableType().getType();
        return TypeFactoryPlain.constructType(type);
    }

    /**
     * construct jackson's JavaType in spring way
     */
    public static JavaType javaType(@NotNull ResolvableType targetType) {
        return TypeFactoryPlain.constructType(targetType.getType());
    }

    /**
     * wings style read text to object, if text asXml, read as xml, otherwise as json
     */
    @SneakyThrows
    @Contract("!null,_,_->!null")
    public static <T> T object(String text, @NotNull Class<?> targetType, Class<?>... generics) {
        if (text == null) return null;
        return MapperWings(!asXml(text)).readValue(text, javaType(targetType, generics));
    }


    /**
     * Auto read text to object, if text asXml, read as xml, otherwise as json
     */
    @SneakyThrows
    @Contract("_,!null,_,_ -> !null")
    public static <T> T object(@NotNull Style style, String text, @NotNull Class<?> targetType, Class<?>... generics) {
        if (text == null) return null;
        return Mapper(style, !asXml(text)).readValue(text, javaType(targetType, generics));
    }

    /**
     * wings style read text to object, if text asXml, read as xml, otherwise as json
     */
    @SneakyThrows
    @Contract("!null,_->!null")
    public static <T> T object(String text, @NotNull JavaType targetType) {
        if (text == null) return null;
        return MapperWings(!asXml(text)).readValue(text, targetType);
    }

    /**
     * Auto read text to object, if text asXml, read as xml, otherwise as json
     */
    @SneakyThrows
    @Contract("_,!null,_ -> !null")
    public static <T> T object(@NotNull Style style, String text, @NotNull JavaType targetType) {
        if (text == null) return null;
        return Mapper(style, !asXml(text)).readValue(text, targetType);
    }


    /**
     * wings style read text to object, if text asXml, read as xml, otherwise as json
     */
    @SneakyThrows
    @Contract("!null,_->!null")
    public static <T> T object(String text, @NotNull ResolvableType targetType) {
        if (text == null) return null;
        return MapperWings(!asXml(text)).readValue(text, javaType(targetType));
    }

    /**
     * Auto read text to object, if text asXml, read as xml, otherwise as json
     */
    @SneakyThrows
    @Contract("_,!null,_ -> !null")
    public static <T> T object(@NotNull Style style, String text, @NotNull ResolvableType targetType) {
        if (text == null) return null;
        return Mapper(style, !asXml(text)).readValue(text, javaType(targetType));
    }


    /**
     * wings style read text to object, if text asXml, read as xml, otherwise as json
     */
    @SneakyThrows
    @Contract("!null,_->!null")
    public static <T> T object(String text, @NotNull TypeDescriptor targetType) {
        if (text == null) return null;
        return MapperWings(!asXml(text)).readValue(text, javaType(targetType));
    }

    /**
     * Auto read text to object, if text asXml, read as xml, otherwise as json
     */
    @SneakyThrows
    @Contract("_,!null,_ -> !null")
    public static <T> T object(@NotNull Style style, String text, @NotNull TypeDescriptor targetType) {
        if (text == null) return null;
        return Mapper(style, !asXml(text)).readValue(text, javaType(targetType));
    }


    /**
     * wings style read text to object, if text asXml, read as xml, otherwise as json
     */
    @SneakyThrows
    @Contract("!null->!null")
    public static JsonNode object(String text) {
        if (text == null) return null;
        return MapperWings(!asXml(text)).readTree(text);
    }

    /**
     * wings style read text to object, if text asXml, read as xml, otherwise as json
     */
    @SneakyThrows
    @Contract("!null,_,_->!null")
    public static <T> T object(byte[] text, @NotNull Class<?> targetType, Class<?>... generics) {
        if (text == null) return null;
        return MapperWings(!asXml(text)).readValue(text, javaType(targetType, generics));
    }


    /**
     * Auto read text to object, if text asXml, read as xml, otherwise as json
     */
    @SneakyThrows
    @Contract("_,!null,_,_ -> !null")
    public static <T> T object(@NotNull Style style, byte[] text, @NotNull Class<?> targetType, Class<?>... generics) {
        if (text == null) return null;
        return Mapper(style, !asXml(text)).readValue(text, javaType(targetType, generics));
    }

    /**
     * wings style read text to object, if text asXml, read as xml, otherwise as json
     */
    @SneakyThrows
    @Contract("!null,_->!null")
    public static <T> T object(byte[] text, @NotNull JavaType targetType) {
        if (text == null) return null;
        return MapperWings(!asXml(text)).readValue(text, targetType);
    }

    /**
     * Auto read text to object, if text asXml, read as xml, otherwise as json
     */
    @SneakyThrows
    @Contract("_, !null, _ -> !null")
    public static <T> T object(@NotNull Style style, byte[] text, @NotNull JavaType targetType) {
        if (text == null) return null;
        return Mapper(style, !asXml(text)).readValue(text, targetType);
    }

    /**
     * wings style read text to object, if text asXml, read as xml, otherwise as json
     */
    @SneakyThrows
    @Contract("!null,_->!null")
    public static <T> T object(byte[] text, @NotNull ResolvableType targetType) {
        if (text == null) return null;
        return MapperWings(!asXml(text)).readValue(text, javaType(targetType));
    }

    /**
     * Auto read text to object, if text asXml, read as xml, otherwise as json
     */
    @SneakyThrows
    @Contract("_, !null, _ -> !null")
    public static <T> T object(@NotNull Style style, byte[] text, @NotNull ResolvableType targetType) {
        if (text == null) return null;
        return Mapper(style, !asXml(text)).readValue(text, javaType(targetType));
    }

    /**
     * wings style read text to object, if text asXml, read as xml, otherwise as json
     */
    @SneakyThrows
    @Contract("!null,_->!null")
    public static <T> T object(byte[] text, @NotNull TypeDescriptor targetType) {
        if (text == null) return null;
        return MapperWings(!asXml(text)).readValue(text, javaType(targetType));
    }

    /**
     * Auto read text to object, if text asXml, read as xml, otherwise as json
     */
    @SneakyThrows
    @Contract("_, !null, _ -> !null")
    public static <T> T object(@NotNull Style style, byte[] text, @NotNull TypeDescriptor targetType) {
        if (text == null) return null;
        return Mapper(style, !asXml(text)).readValue(text, javaType(targetType));
    }

    /**
     * wings style read text to object, if text asXml, read as xml, otherwise as json
     */
    @SneakyThrows
    @Contract("!null->!null")
    public static JsonNode object(byte[] text) {
        if (text == null) return null;
        return MapperWings(!asXml(text)).readTree(text);
    }

    /**
     * Auto read text to object, if text asXml, read as xml, otherwise as json
     */
    @SneakyThrows
    @Contract("_, !null -> !null")
    public static JsonNode object(@NotNull Style style, byte[] text) {
        if (text == null) return null;
        return Mapper(style, !asXml(text)).readTree(text);
    }

    /**
     * wings style serialization to json
     */
    @SneakyThrows
    @Contract("!null->!null")
    public static String string(Object obj) {
        return obj == null ? null : MapperWings(true).writeValueAsString(obj);
    }

    /**
     * serialization to json
     */
    @SneakyThrows
    @Contract("_, !null -> !null")
    public static String string(@NotNull Style style, Object obj) {
        return obj == null ? null : Mapper(style, true).writeValueAsString(obj);
    }

    /**
     * wings style serialization to json/xml
     */
    @SneakyThrows
    @Contract("!null,_->!null")
    public static String string(Object obj, boolean json) {
        return obj == null ? null : MapperWings(json).writeValueAsString(obj);
    }

    /**
     * serialization to json/xml
     */
    @SneakyThrows
    @Contract("_, !null, _ -> !null")
    public static String string(@NotNull Style style, Object obj, boolean json) {
        return obj == null ? null : Mapper(style, json).writeValueAsString(obj);
    }

    /**
     * wings style serialization to json
     */
    @SneakyThrows
    @Contract("!null->!null")
    public static byte[] bytes(Object obj) {
        return obj == null ? null : MapperWings(true).writeValueAsBytes(obj);
    }

    /**
     * serialization to json
     */
    @SneakyThrows
    @Contract("_, !null -> !null")
    public static byte[] bytes(@NotNull Style style, Object obj) {
        return obj == null ? null : Mapper(style, true).writeValueAsBytes(obj);
    }

    /**
     * wings style serialization to json/xml
     */
    @SneakyThrows
    @Contract("!null,_->!null")
    public static byte[] bytes(Object obj, boolean json) {
        return obj == null ? null : MapperWings(json).writeValueAsBytes(obj);
    }

    /**
     * serialization to json/xml
     */
    @SneakyThrows
    @Contract("_, !null, _ -> !null")
    public static byte[] bytes(@NotNull Style style, Object obj, boolean json) {
        return obj == null ? null : Mapper(style, json).writeValueAsBytes(obj);
    }

    ////

    @Contract("_,_,!null->!null")
    public static String getString(JsonNode node, String field, String defaults) {
        if (node == null) return defaults;
        final JsonNode jn = node.get(field);
        return jn != null ? jn.asText(defaults) : defaults;
    }

    public static boolean getBoolean(JsonNode node, String field, boolean defaults) {
        if (node == null) return defaults;
        final JsonNode jn = node.get(field);
        return jn != null ? jn.asBoolean(defaults) : defaults;
    }

    public static int getInt(JsonNode node, String field, int defaults) {
        if (node == null) return defaults;
        final JsonNode jn = node.get(field);
        return jn != null ? jn.asInt(defaults) : defaults;
    }

    public static long getLong(JsonNode node, String field, long defaults) {
        if (node == null) return defaults;
        final JsonNode jn = node.get(field);
        return jn != null ? jn.asLong(defaults) : defaults;
    }

    public static double getDouble(JsonNode node, String field, double defaults) {
        if (node == null) return defaults;
        final JsonNode jn = node.get(field);
        return jn != null ? jn.asDouble(defaults) : defaults;
    }
}
