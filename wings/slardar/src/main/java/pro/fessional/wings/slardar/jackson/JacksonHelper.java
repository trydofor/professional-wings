package pro.fessional.wings.slardar.jackson;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.SneakyThrows;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pro.fessional.mirana.text.WhiteUtil;

/**
 * <pre>
 * <a href="https://github.com/FasterXML/jackson-dataformat-xml#known-limitations">XML limitation</a>
 * The common uses are:
 * (1) single element node, XML can not distinguish between a single value or only one value in the array, unless nested wrap.
 * (2) Xml can not recognize the data type, while Json has string, number, boolean, object, array
 * </pre>
 *
 * @author trydofor
 * @since 2022-11-05
 */
public class JacksonHelper {

    public static final ObjectMapper JsonPlain = new ObjectMapper();
    public static final XmlMapper XmlPlain = new XmlMapper();

    private static ObjectMapper JsonWings = JsonPlain;
    private static XmlMapper XmlWings = XmlPlain;

    /**
     * Init the ObjectMapper for Wings configuration
     *
     * @param jsonMapper handle json
     * @param xmlMapper  handle xml
     */
    public static void initGlobal(ObjectMapper jsonMapper, XmlMapper xmlMapper) {
        if (jsonMapper != null) {
            JsonWings = jsonMapper;
        }
        if (xmlMapper != null) {
            XmlWings = xmlMapper;
        }
    }

    ////

    @NotNull
    public static ObjectMapper JsonWings() {
        return JsonWings;
    }

    @NotNull
    public static XmlMapper XmlWings() {
        return XmlWings;
    }

    @NotNull
    public static ObjectMapper wings(boolean json) {
        return json ? JsonWings : XmlWings;
    }

    /**
     * Auto read text to object, if text asXml, read as xml, otherwise as json
     */
    @SneakyThrows
    @Contract("!null,_->!null")
    public static <T> T object(@Nullable String text, @NotNull Class<T> targetType) {
        if (asXml(text)) {
            return XmlWings.readValue(text, targetType);
        }
        else {
            return JsonWings.readValue(text, targetType);
        }
    }

    /**
     * Auto read text to object, if text asXml, read as xml, otherwise as json
     */
    @SneakyThrows
    @Contract("!null,_->!null")
    public static <T> T object(@Nullable String text, @NotNull JavaType targetType) {
        if (asXml(text)) {
            return XmlWings.readValue(text, targetType);
        }
        else {
            return JsonWings.readValue(text, targetType);
        }
    }

    /**
     * Auto read text to object, if text asXml, read as xml, otherwise as json
     */
    @SneakyThrows
    @Contract("!null,_->!null")
    public static <T> T object(@Nullable String text, @NotNull TypeReference<T> targetType) {
        if (asXml(text)) {
            return XmlWings.readValue(text, targetType);
        }
        else {
            return JsonWings.readValue(text, targetType);
        }
    }

    /**
     * Auto read text to object, if text asXml, read as xml, otherwise as json
     */
    @SneakyThrows
    @Contract("!null->!null")
    public static JsonNode object(@Nullable String text) {
        if (asXml(text)) {
            return XmlWings.readTree(text);
        }
        else {
            return JsonWings.readTree(text);
        }
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
     * Serialization (json) using the wings convention,
     * output as string wherever possible to ensure data precision
     */
    @SneakyThrows
    @Contract("!null->!null")
    public static String string(@Nullable Object obj) {
        return string(obj, true);
    }

    /**
     * Serialization (json or xml) using the wings convention,
     * output as string wherever possible to ensure data precision
     */
    @SneakyThrows
    @Contract("!null,_->!null")
    public static String string(@Nullable Object obj, boolean json) {
        if (obj == null) return null;
        if (json) {
            return JsonWings.writeValueAsString(obj);
        }
        else {
            return XmlWings.writeValueAsString(obj);
        }
    }

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
