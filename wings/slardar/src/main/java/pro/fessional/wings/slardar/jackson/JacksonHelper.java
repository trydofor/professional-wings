package pro.fessional.wings.slardar.jackson;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.SneakyThrows;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pro.fessional.mirana.text.WhiteUtil;

/**
 * @author trydofor
 * @since 2022-11-05
 */
public class JacksonHelper {

    public static final ObjectMapper JsonDefault = new ObjectMapper();
    public static final XmlMapper XmlDefault = new XmlMapper();

    private static ObjectMapper JsonWings = JsonDefault;
    private static XmlMapper XmlWings = XmlDefault;

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
     * str是否具有xml特征，即，收尾的字符是否为尖角括号
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
}
