package pro.fessional.wings.slardar.fastjson;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONFactory;
import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.TypeReference;
import com.alibaba.fastjson2.filter.Filter;
import com.alibaba.fastjson2.reader.ObjectReaderProvider;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.core.ResolvableType;
import org.springframework.core.convert.TypeDescriptor;

import java.lang.reflect.Type;
import java.time.OffsetDateTime;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * FastJson的工具类，不推荐在复杂类型中使用，兼容性问题较大，推荐是用JacksonHelper
 *
 * @author trydofor
 * @since 2022-04-22
 */
public class FastJsonHelper {

    private static final ConcurrentHashMap<Object, Boolean> Inited = new ConcurrentHashMap<>();

    /**
     * 初始或移除全局的默认配置
     *
     * @param init 初始还是移除
     */
    public static void initGlobal(boolean init) {
        final ObjectReaderProvider readerProvider = JSONFactory.getDefaultObjectReaderProvider();
        if (init) {
            {
                final Object obj = readerProvider.registerIfAbsent(OffsetDateTime.class, FastJsonReaders.OffsetDateTimeReader);
                Inited.put(FastJsonReaders.OffsetDateTimeReader, obj == null);
            }
        }
        else {
            if (Inited.getOrDefault(FastJsonReaders.OffsetDateTimeReader, Boolean.FALSE)) {
                readerProvider.unregisterObjectReader(OffsetDateTime.class);
                Inited.remove(FastJsonReaders.OffsetDateTimeReader);
            }
        }
    }

    //
    private static final EnumSet<JSONReader.Feature> ReaderEnum = EnumSet.of(
            JSONReader.Feature.SupportSmartMatch,
            JSONReader.Feature.UseNativeObject,
            JSONReader.Feature.IgnoreSetNullValue,
            JSONReader.Feature.ErrorOnNotSupportAutoType
    );

    /**
     * 添加或移除默认的Feature
     */
    public void enableFeature(@NotNull JSONReader.Feature f, boolean enable) {
        synchronized (ReaderEnum) {
            if (enable) {
                ReaderEnum.add(f);
            }
            else {
                ReaderEnum.remove(f);
            }
        }
        ReaderCache = null;
    }

    private static JSONReader.Feature[] ReaderCache = null;

    @NotNull
    public static JSONReader.Feature[] DefaultReader() {
        if (ReaderCache == null) {
            synchronized (ReaderEnum) {
                ReaderCache = ReaderEnum.toArray(JSONReader.Feature[]::new);
            }
        }
        return ReaderCache;
    }

    private static final EnumSet<JSONWriter.Feature> WriterEnum = EnumSet.of(
            JSONWriter.Feature.WriteEnumsUsingName,
            JSONWriter.Feature.WriteBigDecimalAsPlain,
            JSONWriter.Feature.WriteNonStringValueAsString
    );

    /**
     * 添加或移除默认的Feature
     */
    public void enableFeature(@NotNull JSONWriter.Feature f, boolean enable) {
        synchronized (WriterEnum) {
            if (enable) {
                WriterEnum.add(f);
            }
            else {
                WriterEnum.remove(f);
            }
        }
        WriterCache = null;
    }

    private static JSONWriter.Feature[] WriterCache = null;

    @NotNull
    public static JSONWriter.Feature[] DefaultWriter() {
        if (WriterCache == null) {
            synchronized (WriterEnum) {
                WriterCache = WriterEnum.toArray(JSONWriter.Feature[]::new);
            }
        }
        return WriterCache;
    }

    private static final LinkedHashMap<String, Filter> FilterList = new LinkedHashMap<>();

    static {
        FilterList.put("NumberAsString", FastJsonFilters.NumberAsString);
    }

    private static Filter[] FilterCache = null;

    /**
     * 添加或删除默认的Filter，按添加顺序排序。
     *
     * @param name   名字
     * @param filter null时remove，否则put
     */
    public static void enableFilter(@NotNull String name, @Nullable Filter filter) {
        synchronized (FilterList) {
            if (filter == null) {
                FilterList.remove(name);
            }
            else {
                FilterList.put(name, filter);
            }
        }
        FilterCache = null;
    }

    @NotNull
    public static Filter[] DefaultFilter() {
        if (FilterCache == null) {
            synchronized (FilterList) {
                FilterCache = FilterList.values().toArray(Filter[]::new);
            }
        }
        return FilterCache;
    }

    ////

    /**
     * 采用wings约定反序列化
     */
    @Contract("!null,_->!null")
    public static <T> T object(@Nullable String json, @NotNull ResolvableType targetType) {
        if (json == null) return null;
        return JSON.parseObject(json, targetType.getType(), DefaultReader());
    }

    /**
     * 采用wings约定反序列化
     */
    @Contract("!null,_->!null")
    public static <T> T object(@Nullable String json, @NotNull TypeDescriptor targetType) {
        if (json == null) return null;
        return JSON.parseObject(json, targetType.getResolvableType().getType(), DefaultReader());
    }

    /**
     * 采用wings约定反序列化
     */
    @Contract("!null,_->!null")
    public static <T> T object(@Nullable String json, @NotNull TypeReference<T> targetType) {
        if (json == null) return null;
        return JSON.parseObject(json, targetType, DefaultReader());
    }

    /**
     * 采用wings约定反序列化
     */
    @Contract("!null,_->!null")
    public static <T> T object(@Nullable String json, @NotNull Type targetType) {
        if (json == null) return null;
        return JSON.parseObject(json, targetType, DefaultReader());
    }

    /**
     * 采用wings约定反序列化
     */
    @Contract("!null,_->!null")
    public static <T> T object(@Nullable String json, @NotNull Class<T> targetType) {
        if (json == null) return null;
        return JSON.parseObject(json, targetType, DefaultReader());
    }

    /**
     * 采用wings约定序列化，尽可能以字符串输出，保证数据精度，但不影响Java类型反解析
     */
    @Contract("!null->!null")
    public static String string(@Nullable Object obj) {
        if (obj == null) return null;
        return JSON.toJSONString(obj, DefaultFilter(), DefaultWriter());
    }
}
