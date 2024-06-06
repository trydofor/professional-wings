package pro.fessional.wings.slardar.jackson;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.SneakyThrows;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pro.fessional.mirana.text.WhiteUtil;
import pro.fessional.mirana.time.DateFormatter;
import pro.fessional.wings.slardar.autozone.AutoZoneType;
import pro.fessional.wings.slardar.autozone.json.JacksonLocalDateTimeDeserializer;
import pro.fessional.wings.slardar.autozone.json.JacksonLocalDateTimeSerializer;
import pro.fessional.wings.slardar.autozone.json.JacksonOffsetDateTimeDeserializer;
import pro.fessional.wings.slardar.autozone.json.JacksonOffsetDateTimeSerializer;
import pro.fessional.wings.slardar.autozone.json.JacksonZonedDateTimeDeserializer;
import pro.fessional.wings.slardar.autozone.json.JacksonZonedDateTimeSerializer;

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
 * Jackson NOTE
 * * byte[] as base64, [] as ""
 * * char[] as String, [] as ""
 *
 * Wings NOTE
 * * LocalDateTime as "2023-04-05 06:07:08"
 * * ZoneDateTime as "2023-04-05 06:07:08 Asia/Shanghai"
 * * OffsetDateTime as "2023-04-05 06:07:08 +08:00"
 * </pre>
 *
 * @author trydofor
 * @since 2022-11-05
 */
public class JacksonHelper {

    // spring-jackson-79.properties
    public static final com.fasterxml.jackson.databind.DeserializationFeature[] EnableDeserializationFeature = {
        com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_NUMBERS_FOR_ENUMS,
        com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES,
        com.fasterxml.jackson.databind.DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY,
        com.fasterxml.jackson.databind.DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE
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

    public static final SimpleModule DateTimeModule = new SimpleModule();

    static {
        DateTimeFormatter localFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        DateTimeModule.addDeserializer(LocalDateTime.class, new JacksonLocalDateTimeDeserializer(localFormatter, List.of(DateFormatter.FMT_FULL_PSE), AutoZoneType.Off));

        DateTimeFormatter offsetFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
        DateTimeModule.addDeserializer(OffsetDateTime.class, new JacksonOffsetDateTimeDeserializer(offsetFormatter, List.of(DateFormatter.FMT_ZONE_PSE), AutoZoneType.Off));

        DateTimeFormatter zonedFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'['VV']'");
        DateTimeModule.addSerializer(ZonedDateTime.class, new JacksonZonedDateTimeSerializer(zonedFormatter, AutoZoneType.Off));
        DateTimeModule.addDeserializer(ZonedDateTime.class, new JacksonZonedDateTimeDeserializer(zonedFormatter, List.of(DateFormatter.FMT_ZONE_PSE), AutoZoneType.Off));
    }

    // https://github.com/FasterXML/jackson-modules-java8/tree/master/datetime#usage
    public static final ObjectMapper JsonPlain = JsonMapper
        .builder()
        .findAndAddModules()
        .enable(EnableDeserializationFeature)
        .enable(EnableMapperFeature)
        .enable(EnableParserFeature)
        .enable(EnableSerializationFeature)
        .disable(DisableParserFeature)
        .disable(DisableDeserializationFeature)
        .disable(DisableSerializationFeature)
        .addModule(DateTimeModule)
        .build();
    public static final XmlMapper XmlPlain = XmlMapper
        .builder()
        .findAndAddModules()
        .enable(EnableDeserializationFeature)
        .enable(EnableMapperFeature)
        .enable(EnableParserFeature)
        .enable(EnableSerializationFeature)
        .disable(DisableParserFeature)
        .disable(DisableDeserializationFeature)
        .disable(DisableSerializationFeature)
        .addModule(DateTimeModule)
        .build();

    //// wings autoZone ser/des
    protected static JacksonLocalDateTimeSerializer localDateTimeSerializer;
    protected static JacksonLocalDateTimeDeserializer localDateTimeDeserializer;
    protected static JacksonZonedDateTimeSerializer zonedDateTimeSerializer;
    protected static JacksonZonedDateTimeDeserializer zonedDateTimeDeserializer;
    protected static JacksonOffsetDateTimeSerializer offsetDateTimeSerializer;
    protected static JacksonOffsetDateTimeDeserializer offsetDateTimeDeserializer;

    protected static ObjectMapper JsonWings = JsonPlain;
    protected static XmlMapper XmlWings = XmlPlain;

    ////

    /**
     * wings configed mapper without auto timezone convert
     */
    @NotNull
    public static ObjectMapper JsonWings() {
        return JsonWings;
    }

    /**
     * wings configed mapper without auto timezone convert
     */
    @NotNull
    public static XmlMapper XmlWings() {
        return XmlWings;
    }

    /**
     * wings configed mapper without auto timezone convert
     */
    @NotNull
    public static ObjectMapper wings(boolean json) {
        return json ? JsonWings : XmlWings;
    }

    /**
     * wings configed mapper without auto timezone convert
     */
    @NotNull
    public static ObjectMapper mapper(boolean json, boolean wings) {
        return json
            ? (wings ? JsonWings : JsonPlain)
            : (wings ? XmlWings : XmlPlain);
    }


    /**
     * whether `str` has xml characteristics, i.e. the first and last characters are angle brackets or not
     */
    public static boolean asXml(@Nullable String str) {
        if (str == null) return false;
        int cnt = 0;
        final int len = str.length();
        for (int i = 0; i < len; i++) {
            char c = str.charAt(i);
            if (!WhiteUtil.isWhiteSpace(c)) {
                if (c == '<') {
                    cnt++;
                    break;
                }
                else {
                    return false;
                }
            }
        }
        for (int i = len - 1; i > 0; i--) {
            char c = str.charAt(i);
            if (!WhiteUtil.isWhiteSpace(c)) {
                if (c == '>') {
                    cnt++;
                    break;
                }
                else {
                    return false;
                }
            }
        }

        return cnt == 2;
    }

    /**
     * Auto read text to object, if text asXml, read as xml, otherwise as json
     */
    @SneakyThrows
    @Contract("!null,_->!null")
    public static <T> T object(@Nullable String text, @NotNull Class<T> targetType) {
        return mapper(!asXml(text), true).readValue(text, targetType);
    }


    /**
     * Auto read text to object, if text asXml, read as xml, otherwise as json
     */
    @SneakyThrows
    @Contract("_,!null,_->!null")
    public static <T> T object(boolean wings, @Nullable String text, @NotNull Class<T> targetType) {
        return mapper(!asXml(text), wings).readValue(text, targetType);
    }

    /**
     * Auto read text to object, if text asXml, read as xml, otherwise as json
     */
    @SneakyThrows
    @Contract("!null,_->!null")
    public static <T> T object(@Nullable String text, @NotNull JavaType targetType) {
        return mapper(!asXml(text), true).readValue(text, targetType);
    }

    /**
     * Auto read text to object, if text asXml, read as xml, otherwise as json
     */
    @SneakyThrows
    @Contract("_,!null,_->!null")
    public static <T> T object(boolean wings, @Nullable String text, @NotNull JavaType targetType) {
        return mapper(!asXml(text), wings).readValue(text, targetType);
    }

    /**
     * Auto read text to object, if text asXml, read as xml, otherwise as json
     */
    @SneakyThrows
    @Contract("!null,_->!null")
    public static <T> T object(@Nullable String text, @NotNull TypeReference<T> targetType) {
        return mapper(!asXml(text), true).readValue(text, targetType);
    }

    /**
     * Auto read text to object, if text asXml, read as xml, otherwise as json
     */
    @SneakyThrows
    @Contract("_,!null,_->!null")
    public static <T> T object(boolean wings, @Nullable String text, @NotNull TypeReference<T> targetType) {
        return mapper(!asXml(text), wings).readValue(text, targetType);
    }

    /**
     * Auto read text to object, if text asXml, read as xml, otherwise as json
     */
    @SneakyThrows
    @Contract("!null->!null")
    public static JsonNode object(@Nullable String text) {
        return mapper(!asXml(text), true).readTree(text);
    }

    /**
     * Auto read text to object, if text asXml, read as xml, otherwise as json
     */
    @SneakyThrows
    @Contract("_,!null->!null")
    public static JsonNode object(boolean wings, @Nullable String text) {
        return mapper(!asXml(text), wings).readTree(text);
    }

    /**
     * Serialization (json) using the wings convention,
     * output as string wherever possible to ensure data precision
     */
    @SneakyThrows
    @Contract("!null->!null")
    public static String string(@Nullable Object obj) {
        return obj == null ? null : mapper(true, true).writeValueAsString(obj);
    }

    /**
     * Serialization (json) using the wings convention,
     * output as string wherever possible to ensure data precision
     */
    @SneakyThrows
    @Contract("_,!null->!null")
    public static String string(boolean wings, @Nullable Object obj) {
        return obj == null ? null : mapper(true, wings).writeValueAsString(obj);
    }

    /**
     * Serialization (json or xml) using the wings convention,
     * output as string wherever possible to ensure data precision
     */
    @SneakyThrows
    @Contract("!null,_->!null")
    public static String string(@Nullable Object obj, boolean json) {
        return obj == null ? null : mapper(json, true).writeValueAsString(obj);
    }

    /**
     * Serialization (json or xml) using the wings convention,
     * output as string wherever possible to ensure data precision
     */
    @SneakyThrows
    @Contract("_,!null,_->!null")
    public static String string(boolean wings, @Nullable Object obj, boolean json) {
        return obj == null ? null : mapper(json, wings).writeValueAsString(obj);
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
