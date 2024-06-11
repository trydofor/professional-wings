package pro.fessional.wings.slardar.fastjson;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONPath;
import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.JSONWriter;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.core.ResolvableType;
import org.springframework.core.convert.TypeDescriptor;
import pro.fessional.mirana.lock.ArrayKey;
import pro.fessional.wings.silencer.enhance.TypeSugar;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

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
     * do NOT modify these
     * <a href="https://github.com/alibaba/fastjson2/blob/main/docs/jsonpath_cn.md">jsonpath_cn</a>
     */
    public static final JSONPath.Feature[] WingsPath = {
        JSONPath.Feature.NullOnError
    };

    public static final long WingsPathMask = featureMask(WingsPath);

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
    public static <T> T object(String json, @NotNull Type targetType) {
        if (json == null) return null;
        return JSON.parseObject(json, targetType, WingsReader);
    }

    /**
     * Deserialization with the wings convention
     */
    @Contract("!null,_,_->!null")
    public static <T> T object(String json, @NotNull Class<?> targetType, Class<?>... generics) {
        if (json == null) return null;
        Type genericType = TypeSugar.type(targetType, generics);
        return JSON.parseObject(json, genericType, WingsReader);
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
    @Contract("!null,_,_->!null")
    public static <T> T object(InputStream json, @NotNull Class<?> targetType, Class<?>... generics) {
        if (json == null) return null;
        Type genericType = TypeSugar.type(targetType, generics);
        return JSON.parseObject(json, genericType, WingsReader);
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
    @Contract("!null,_,_->!null")
    public static <T> T object(byte[] json, @NotNull Class<?> targetType, Class<?>... generics) {
        if (json == null) return null;
        Type genericType = TypeSugar.type(targetType, generics);
        return JSON.parseObject(json, genericType, WingsReader);
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

    //// path

    private static final ConcurrentHashMap<ArrayKey, JSONPath> JsonPathCache = new ConcurrentHashMap<>();

    /**
     * weak cached JsonPath
     */
    public static JSONPath path(@NotNull String path) {
        return path(path, null, WingsPath);
    }

    /**
     * weak cached JsonPath
     */
    public static JSONPath path(@NotNull String path, Type type) {
        return path(path, type, WingsPath);
    }

    /**
     * weak cached JsonPath
     */
    public static JSONPath path(@NotNull String path, Class<?> type, Class<?>... generics) {
        Type genericType = TypeSugar.type(type, generics);
        return path(path, genericType, WingsPath);
    }

    /**
     * weak cached JsonPath
     */
    public static JSONPath path(@NotNull String path, Type type, JSONPath.Feature... features) {
        ArrayKey key = new ArrayKey(path, type, features);
        return JsonPathCache.computeIfAbsent(key, ignore -> JSONPath.of(path, type, features));
    }

    /**
     * cached JsonPath
     */
    public static JSONPath path(@NotNull String[] paths, Type[] types, JSONPath.Feature... features) {
        return path(paths, types, features, WingsReader);
    }

    /**
     * cached JsonPath
     */
    public static JSONPath path(@NotNull String[] paths, Type[] types, JSONPath.Feature[] pathFeatures, JSONReader.Feature... features) {
        ArrayKey key = new ArrayKey(paths, types, pathFeatures, features);
        long[] pfs = features(paths.length, pathFeatures);
        return JsonPathCache.computeIfAbsent(key, ignore -> JSONPath.of(paths, types, null, pfs, null, features));
    }

    public static long[] features(int size, JSONPath.Feature... features) {
        if (size <= 0) return null;
        long mask = featureMask(features);
        return features(size, mask);
    }

    public static long featureMask(JSONPath.Feature... features) {
        if (features == null || features.length == 0) return WingsPathMask;

        long ft = 0;
        for (JSONPath.Feature f : features) {
            ft = ft | f.mask;
        }
        return ft;
    }

    private static long @Nullable [] features(int size, long features) {
        long[] pts = new long[size];
        Arrays.fill(pts, features);
        return pts;
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
