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
 * [XML limitation](https://github.com/FasterXML/jackson-dataformat-xml#known-limitations)
 * 常见的有：
 * ①单个元素的节点，XML不能区分是单值还是数组中只有一个值，除非嵌套了wrap。
 * ②Xml无法识别数据类型，而Json有string,number,boolean,object,array
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
     * 初始化Wings配置的ObjectMapper
     *
     * @param jsonMapper 负责json
     * @param xmlMapper  负责xml
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
     * 根据text是否有xml特征，自动选择Wings读取Xml/Json
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
     * 根据text是否有xml特征，自动选择Wings读取Xml/Json
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
     * 根据text是否有xml特征，自动选择Wings读取Xml/Json
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
     * 根据text是否有xml特征，自动选择Wings读取Xml/Json
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
     * str是否具有xml特征，即，首尾的字符是否为尖角括号
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
     * 采用wings约定序列化(json)，尽可能以字符串输出
     */
    @SneakyThrows
    @Contract("!null->!null")
    public static String string(@Nullable Object obj) {
        return string(obj, true);
    }

    /**
     * 采用wings约定序列化(json或xml)，尽可能以字符串输出
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
