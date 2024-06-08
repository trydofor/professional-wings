package pro.fessional.wings.slardar.fastjson;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.TypeReference;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.ResolvableType;
import org.springframework.core.convert.TypeDescriptor;

import java.io.InputStream;
import java.lang.reflect.Type;

/**
 * <pre>
 * FastJson Util, not recommended for use in complex types
 *
 * Fastjson NOTE
 * * LocalDateTime as "2023-04-05 06:07:08"
 * * ZoneDateTime as "2023-04-05T06:07:08[America/New_York]"
 * * OffsetDateTime as "2023-04-05T06:07:08-04:00"
 * </pre>
 *
 * @author trydofor
 * @see pro.fessional.wings.slardar.jackson.JacksonHelper
 * @since 2022-04-22
 */
public class FastJsonHelper {

    /**
     * do NOT modify these
     */
    public static final JSONReader.Feature[] WingsReader = {
        JSONReader.Feature.SupportSmartMatch,
        JSONReader.Feature.UseNativeObject,
        JSONReader.Feature.IgnoreSetNullValue,
        JSONReader.Feature.ErrorOnNotSupportAutoType,
        JSONReader.Feature.AllowUnQuotedFieldNames
    };

    /**
     * do NOT modify these
     */
    public static final JSONWriter.Feature[] WingsWriter = {
        JSONWriter.Feature.WriteEnumsUsingName,
        JSONWriter.Feature.WriteBigDecimalAsPlain,
        JSONWriter.Feature.BrowserCompatible,
//        JSONWriter.Feature.WriteNonStringValueAsString, // https://github.com/alibaba/fastjson2/issues/2560
    };

    /**
     * Deserialization with the wings convention
     */
    @Contract("!null,_->!null")
    public static <T> T object(String json, @NotNull ResolvableType targetType) {
        if (json == null) return null;
        return JSON.parseObject(json, targetType.getType(), WingsReader);
    }

    /**
     * Deserialization with the wings convention
     */
    @Contract("!null,_->!null")
    public static <T> T object(String json, @NotNull TypeDescriptor targetType) {
        if (json == null) return null;
        return JSON.parseObject(json, targetType.getResolvableType().getType(), WingsReader);
    }

    /**
     * Deserialization with the wings convention
     */
    @Contract("!null,_->!null")
    public static <T> T object(String json, @NotNull TypeReference<T> targetType) {
        if (json == null) return null;
        return JSON.parseObject(json, targetType, WingsReader);
    }

    /**
     * Deserialization with the wings convention
     */
    @Contract("!null,_->!null")
    public static <T> T object(String json, @NotNull Type targetType) {
        if (json == null) return null;
        return JSON.parseObject(json, targetType, WingsReader);
    }

    /**
     * Deserialization with the wings convention
     */
    @Contract("!null,_->!null")
    public static <T> T object(String json, @NotNull Class<T> targetType) {
        if (json == null) return null;
        return JSON.parseObject(json, targetType, WingsReader);
    }

    /**
     * Deserialization with the wings convention
     */
    @Contract("!null->!null")
    public static JSONObject object(String json) {
        if (json == null) return null;
        return JSON.parseObject(json, WingsReader);
    }

    /**
     * Deserialization with the wings convention
     */
    @Contract("!null,_->!null")
    public static <T> T object(InputStream json, @NotNull ResolvableType targetType) {
        if (json == null) return null;
        return JSON.parseObject(json, targetType.getType(), WingsReader);
    }

    /**
     * Deserialization with the wings convention
     */
    @Contract("!null,_->!null")
    public static <T> T object(InputStream json, @NotNull TypeDescriptor targetType) {
        if (json == null) return null;
        return JSON.parseObject(json, targetType.getResolvableType().getType(), WingsReader);
    }

    /**
     * Deserialization with the wings convention
     */
    @Contract("!null,_->!null")
    public static <T> T object(InputStream json, @NotNull Type targetType) {
        if (json == null) return null;
        return JSON.parseObject(json, targetType, WingsReader);
    }

    /**
     * Deserialization with the wings convention
     */
    @Contract("!null,_->!null")
    public static <T> T object(InputStream json, @NotNull Class<T> targetType) {
        if (json == null) return null;
        return JSON.parseObject(json, targetType, WingsReader);
    }

    /**
     * Deserialization with the wings convention
     */
    @Contract("!null->!null")
    public static JSONObject object(InputStream json) {
        if (json == null) return null;
        return JSON.parseObject(json, WingsReader);
    }

    /**
     * Deserialization with the wings convention
     */
    @Contract("!null,_->!null")
    public static <T> T object(byte[] json, @NotNull ResolvableType targetType) {
        if (json == null) return null;
        return JSON.parseObject(json, targetType.getType(), WingsReader);
    }

    /**
     * Deserialization with the wings convention
     */
    @Contract("!null,_->!null")
    public static <T> T object(byte[] json, @NotNull TypeDescriptor targetType) {
        if (json == null) return null;
        return JSON.parseObject(json, targetType.getResolvableType().getType(), WingsReader);
    }

    /**
     * Deserialization with the wings convention
     */
    @Contract("!null,_->!null")
    public static <T> T object(byte[] json, @NotNull Type targetType) {
        if (json == null) return null;
        return JSON.parseObject(json, targetType, WingsReader);
    }

    /**
     * Deserialization with the wings convention
     */
    @Contract("!null,_->!null")
    public static <T> T object(byte[] json, @NotNull Class<T> targetType) {
        if (json == null) return null;
        return JSON.parseObject(json, targetType, WingsReader);
    }

    /**
     * Deserialization with the wings convention
     */
    @Contract("!null->!null")
    public static JSONObject object(byte[] json) {
        if (json == null) return null;
        return JSON.parseObject(json, WingsReader);
    }

    /**
     * Serialization using the wings convention,
     * output as string wherever possible to ensure data precision,
     * but not affecting Java type inverse parsing
     */
    @Contract("!null->!null")
    public static String string(Object obj) {
        if (obj == null) return null;
        return JSON.toJSONString(obj, WingsWriter);
    }

    @Contract("!null->!null")
    public static byte[] bytes(Object obj) {
        if (obj == null) return null;
        return JSON.toJSONBytes(obj, WingsWriter);
    }

    ////

    @Contract("_,_,!null->!null")
    public static String getString(JSONObject node, String field, String defaults) {
        if (node == null) return defaults;
        final String jn = node.getString(field);
        return jn != null ? jn : defaults;
    }

    public static boolean getBoolean(JSONObject node, String field, boolean defaults) {
        if (node == null) return defaults;
        final Boolean jn = node.getBoolean(field);
        return jn != null ? jn : defaults;
    }

    public static int getInt(JSONObject node, String field, int defaults) {
        if (node == null) return defaults;
        final Integer jn = node.getInteger(field);
        return jn != null ? jn : defaults;
    }

    public static long getLong(JSONObject node, String field, long defaults) {
        if (node == null) return defaults;
        final Long jn = node.getLong(field);
        return jn != null ? jn : defaults;
    }

    public static double getDouble(JSONObject node, String field, double defaults) {
        if (node == null) return defaults;
        final Double jn = node.getDouble(field);
        return jn != null ? jn : defaults;
    }

}
