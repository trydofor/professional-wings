package pro.fessional.wings.warlock.service.conf;

import org.springframework.core.convert.TypeDescriptor;
import pro.fessional.mirana.cast.EnumConvertor;

import java.util.List;
import java.util.Map;

/**
 * Support for ConversionService and Json parsing configuration
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
     * Read the value of config by type
     *
     * @param key  key
     * @param type type descriptor
     * @param <T>  Type of value
     * @return value
     */
    <T> T getObject(String key, TypeDescriptor type);


    /**
     * set value of config
     *
     * @param key   key
     * @param value config
     */
    void setObject(String key, Object value);

    default void setObject(Class<?> key, Object value) {
        setObject(key.getName(), value);
    }

    default void setObject(Enum<?> key, Object value) {
        setObject(EnumConvertor.enum2Str(key), value);
    }

    /**
     * create new config
     *
     * @param key     config key
     * @param value   config value
     * @param comment config comment
     * @param handler type handler name
     * @return whether handled
     */
    boolean newObject(String key, Object value, String comment, String handler);

    default boolean newObject(Class<?> key, Object value, String comment, String handler) {
        return newObject(key.getName(), value, comment, handler);
    }

    default boolean newObject(Enum<?> key, Object value, String comment, String handler) {
        return newObject(EnumConvertor.enum2Str(key), value, comment, handler);
    }

    /**
     * create new config with auto selected handler, success or throw an error.
     *
     * @param key     config key
     * @param value   config value
     * @param comment config comment
     */
    boolean newObject(String key, Object value, String comment);

    default boolean newObject(Class<?> key, Object value, String comment) {
        return newObject(key.getName(), value, comment);
    }

    default boolean newObject(Enum<?> key, Object value, String comment) {
        return newObject(EnumConvertor.enum2Str(key), value, comment);
    }

}
