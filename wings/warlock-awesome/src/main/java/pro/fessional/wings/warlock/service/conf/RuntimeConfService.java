package pro.fessional.wings.warlock.service.conf;

import org.springframework.core.convert.TypeDescriptor;
import pro.fessional.mirana.cast.EnumConvertor;

import java.util.List;
import java.util.Map;

/**
 * 支持ConversionService和Json解析配置
 *
 * @author trydofor
 * @since 2022-03-09
 */
public interface RuntimeConfService {

    default String getString(String key) {
        return getObject(key, TypeDescriptor.valueOf(String.class));
    }

    default String getString(Class<?> key) {
        return getString(key.getName());
    }

    default String getString(Enum<?> key) {
        return getString(EnumConvertor.enum2Str(key));
    }

    default int getInt(String key, int els) {
        final Integer obj = getSimple(key, Integer.class);
        return obj == null ? els : obj;
    }

    default int getInt(Class<?> key, int els) {
        return getInt(key.getName(), els);
    }

    default int getInt(Enum<?> key, int els) {
        return getInt(EnumConvertor.enum2Str(key), els);
    }

    default boolean getBoolean(String key, boolean els) {
        final Boolean obj = getSimple(key, Boolean.class);
        return obj == null ? els : obj;
    }

    default boolean getBoolean(Class<?> key, boolean els) {
        return getBoolean(key.getName(), els);
    }

    default boolean getBoolean(Enum<?> key, boolean els) {
        return getBoolean(EnumConvertor.enum2Str(key), els);
    }

    default long getLong(String key, long els) {
        final Long obj = getSimple(key, Long.class);
        return obj == null ? els : obj;
    }

    default long getLong(Class<?> key, long els) {
        return getLong(key.getName(), els);
    }

    default long getLong(Enum<?> key, long els) {
        return getLong(EnumConvertor.enum2Str(key), els);
    }

    default <T> T getSimple(String key, Class<T> vt) {
        return getObject(key, TypeDescriptor.valueOf(vt));
    }

    default <T> T getSimple(Class<?> key, Class<T> vt) {
        return getSimple(key.getName(), vt);
    }

    default <T> T getSimple(Enum<?> key, Class<T> vt) {
        return getSimple(EnumConvertor.enum2Str(key), vt);
    }

    default <T extends Enum<T>> T getEnum(Class<T> key) {
        return getSimple(key.getName(), key);
    }

    default <T extends Enum<T>> List<T> getEnums(Class<T> key) {
        return getList(key.getName(), key);
    }

    default <T> List<T> getList(String key, Class<T> vt) {
        return getObject(key, TypeDescriptor.collection(List.class, TypeDescriptor.valueOf(vt)));
    }

    default <T> List<T> getList(Class<?> key, Class<T> vt) {
        return getList(key.getName(), vt);
    }

    default <T> List<T> getList(Enum<?> key, Class<T> vt) {
        return getList(EnumConvertor.enum2Str(key), vt);
    }

    default <K, V> Map<K, V> getMap(String key, Class<K> kt, Class<V> vt) {
        return getObject(key, TypeDescriptor.map(Map.class, TypeDescriptor.valueOf(kt), TypeDescriptor.valueOf(vt)));
    }

    default <K, V> Map<K, V> getMap(Class<?> key, Class<K> kt, Class<V> vt) {
        return getMap(key.getName(), kt, vt);
    }

    default <K, V> Map<K, V> getMap(Enum<?> key, Class<K> kt, Class<V> vt) {
        return getMap(EnumConvertor.enum2Str(key), kt, vt);
    }

    default <T> T getObject(Class<?> key, TypeDescriptor type) {
        return getObject(key.getName(), type);
    }

    default <T> T getObject(Enum<?> key, TypeDescriptor type) {
        return getObject(EnumConvertor.enum2Str(key), type);
    }

    /**
     * 按类型读取配置项
     *
     * @param key  key
     * @param type 类型描述
     * @param <T>  类型
     * @return 配置
     */
    <T> T getObject(String key, TypeDescriptor type);


    /**
     * 写入配置项
     *
     * @param key   key
     * @param value 配置
     */
    void setObject(String key, Object value);

    default void setObject(Class<?> key, Object value) {
        setObject(key.getName(), value);
    }

    default void setObject(Enum<?> key, Object value) {
        setObject(EnumConvertor.enum2Str(key), value);
    }

    /**
     * 新建一个配置项
     *
     * @param key     key
     * @param value   初始值
     * @param comment 注释
     * @param handler 处理器
     * @return 是否被处理
     */
    boolean newObject(String key, Object value, String comment, String handler);

    default boolean newObject(Class<?> key, Object value, String comment, String handler) {
        return newObject(key.getName(), value, comment, handler);
    }

    default boolean newObject(Enum<?> key, Object value, String comment, String handler) {
        return newObject(EnumConvertor.enum2Str(key), value, comment, handler);
    }

    /**
     * 新建一个配置项，自动选择handler，一定成功，否异常。
     *
     * @param key     key
     * @param value   初始值
     * @param comment 注释
     */
    boolean newObject(String key, Object value, String comment);

    default boolean newObject(Class<?> key, Object value, String comment) {
        return newObject(key.getName(), value, comment);
    }

    default boolean newObject(Enum<?> key, Object value, String comment) {
        return newObject(EnumConvertor.enum2Str(key), value, comment);
    }

}
