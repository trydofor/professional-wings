package pro.fessional.wings.warlock.service.conf;

import org.jetbrains.annotations.NotNull;
import org.springframework.core.ResolvableType;
import org.springframework.core.convert.TypeDescriptor;
import pro.fessional.mirana.cast.EnumConvertor;
import pro.fessional.wings.silencer.enhance.TypeSugar;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Support for ConversionService and Json parsing configuration
 *
 * @author trydofor
 * @since 2022-03-09
 */
public interface RuntimeConfService {

    default String getString(@NotNull String key) {
        return getObject(key, TypeSugar.StringDescriptor);
    }

    default String getString(@NotNull Class<?> key) {
        return getString(key.getName());
    }

    default String getString(@NotNull Enum<?> key) {
        return getString(EnumConvertor.enum2Str(key));
    }

    default int getInt(@NotNull String key, int els) {
        final Integer obj = getSimple(key, Integer.class);
        return obj == null ? els : obj;
    }

    default int getInt(@NotNull Class<?> key, int els) {
        return getInt(key.getName(), els);
    }

    default int getInt(@NotNull Enum<?> key, int els) {
        return getInt(EnumConvertor.enum2Str(key), els);
    }

    default boolean getBoolean(@NotNull String key, boolean els) {
        final Boolean obj = getSimple(key, Boolean.class);
        return obj == null ? els : obj;
    }

    default boolean getBoolean(@NotNull Class<?> key, boolean els) {
        return getBoolean(key.getName(), els);
    }

    default boolean getBoolean(@NotNull Enum<?> key, boolean els) {
        return getBoolean(EnumConvertor.enum2Str(key), els);
    }

    default long getLong(String key, long els) {
        final Long obj = getSimple(key, Long.class);
        return obj == null ? els : obj;
    }

    default long getLong(@NotNull Class<?> key, long els) {
        return getLong(key.getName(), els);
    }

    default long getLong(@NotNull Enum<?> key, long els) {
        return getLong(EnumConvertor.enum2Str(key), els);
    }

    default <T> T getSimple(@NotNull String key, @NotNull Class<T> type) {
        return getObject(key, TypeSugar.describe(type));
    }

    default <T> T getSimple(@NotNull Class<?> key, @NotNull Class<T> type) {
        return getSimple(key.getName(), type);
    }

    default <T> T getSimple(@NotNull Enum<?> key, @NotNull Class<T> type) {
        return getSimple(EnumConvertor.enum2Str(key), type);
    }

    default <T extends Enum<T>> T getEnum(@NotNull Class<T> key) {
        return getSimple(key.getName(), key);
    }

    default <T extends Enum<T>> List<T> getEnums(@NotNull Class<T> key) {
        return getList(key.getName(), key);
    }

    @NotNull
    default <T> List<T> getList(@NotNull String key, @NotNull Class<T> type) {
        return getObject(key, TypeSugar.describe(List.class, type));
    }

    @NotNull
    default <T> List<T> getList(@NotNull Class<?> key, @NotNull Class<T> type) {
        return getList(key.getName(), type);
    }

    @NotNull
    default <T> List<T> getList(@NotNull Enum<?> key, @NotNull Class<T> type) {
        return getList(EnumConvertor.enum2Str(key), type);
    }


    @NotNull
    default <T> Set<T> getSet(@NotNull String key, @NotNull Class<T> type) {
        return getObject(key, TypeSugar.describe(Set.class, type));
    }

    @NotNull
    default <T> Set<T> getSet(@NotNull Class<?> key, @NotNull Class<T> type) {
        return getSet(key.getName(), type);
    }

    @NotNull
    default <T> Set<T> getSet(@NotNull Enum<?> key, @NotNull Class<T> type) {
        return getSet(EnumConvertor.enum2Str(key), type);
    }

    @NotNull
    default <K, V> Map<K, V> getMap(@NotNull String key, @NotNull Class<K> keyType, @NotNull Class<V> valueType) {
        return getObject(key, TypeSugar.describe(Map.class, keyType, valueType));
    }

    @NotNull
    default <K, V> Map<K, V> getMap(@NotNull Class<?> key, @NotNull Class<K> keyType, @NotNull Class<V> valueType) {
        return getMap(key.getName(), keyType, valueType);
    }

    @NotNull
    default <K, V> Map<K, V> getMap(@NotNull Enum<?> key, @NotNull Class<K> keyType, @NotNull Class<V> valueType) {
        return getMap(EnumConvertor.enum2Str(key), keyType, valueType);
    }

    default <T> T getObject(@NotNull Class<?> key, @NotNull TypeDescriptor type) {
        return getObject(key.getName(), type);
    }

    default <T> T getObject(@NotNull Enum<?> key, @NotNull TypeDescriptor type) {
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
    <T> T getObject(@NotNull String key, @NotNull TypeDescriptor type);


    /**
     * set value of config, return false if not found
     *
     * @param key   key
     * @param value config
     */
    boolean setObject(@NotNull String key, @NotNull Object value);

    default boolean setObject(@NotNull Class<?> key, @NotNull Object value) {
        return setObject(key.getName(), value);
    }

    default boolean setObject(@NotNull Enum<?> key, @NotNull Object value) {
        return setObject(EnumConvertor.enum2Str(key), value);
    }

    /**
     * create new config, return true if handled
     *
     * @param key     config key
     * @param value   config value
     * @param comment config comment, empty if null
     * @param handler type handler name, auto select if null
     * @param outline type outline, resolved from value if null
     * @return whether handled
     */
    boolean newObject(@NotNull String key, @NotNull Object value, String comment, String handler, ResolvableType outline);

    default boolean newObject(@NotNull Class<?> key, @NotNull Object value, String comment, String handler, ResolvableType outline) {
        return newObject(key.getName(), value, comment, handler, outline);
    }

    default boolean newObject(@NotNull Enum<?> key, @NotNull Object value, String comment, String handler, ResolvableType outline) {
        return newObject(EnumConvertor.enum2Str(key), value, comment, handler, outline);
    }

    default boolean newObject(@NotNull String key, @NotNull Object value, String comment, String handler, TypeDescriptor outline) {
        return newObject(key, value, comment, handler, outline == null ? (ResolvableType) null : outline.getResolvableType());
    }

    default boolean newObject(@NotNull Class<?> key, @NotNull Object value, String comment, String handler, TypeDescriptor outline) {
        return newObject(key.getName(), value, comment, handler, outline == null ? (ResolvableType) null : outline.getResolvableType());
    }

    default boolean newObject(@NotNull Enum<?> key, @NotNull Object value, String comment, String handler, TypeDescriptor outline) {
        return newObject(EnumConvertor.enum2Str(key), value, comment, handler, outline == null ? (ResolvableType) null : outline.getResolvableType());
    }

    default boolean newObject(@NotNull String key, @NotNull Object value, String comment, String handler, Class<?> outline, Class<?>... gernics) {
        return newObject(key, value, comment, handler, outline == null ? (ResolvableType) null : TypeSugar.resolve(outline, gernics));
    }

    default boolean newObject(@NotNull Class<?> key, @NotNull Object value, String comment, String handler, Class<?> outline, Class<?>... gernics) {
        return newObject(key.getName(), value, comment, handler, outline == null ? (ResolvableType) null : TypeSugar.resolve(outline, gernics));
    }

    default boolean newObject(@NotNull Enum<?> key, @NotNull Object value, String comment, String handler, Class<?> outline, Class<?>... gernics) {
        return newObject(EnumConvertor.enum2Str(key), value, comment, handler, outline == null ? (ResolvableType) null : TypeSugar.resolve(outline, gernics));
    }

    default boolean newObject(@NotNull String key, @NotNull Object value, String comment, String handler) {
        return newObject(key, value, comment, handler, (ResolvableType) null);
    }

    default boolean newObject(@NotNull Class<?> key, @NotNull Object value, String comment, String handler) {
        return newObject(key.getName(), value, comment, handler, (ResolvableType) null);
    }

    default boolean newObject(@NotNull Enum<?> key, @NotNull Object value, String comment, String handler) {
        return newObject(EnumConvertor.enum2Str(key), value, comment, handler, (ResolvableType) null);
    }

    default boolean newObject(@NotNull String key, @NotNull Object value, String comment) {
        return newObject(key, value, comment, null, (ResolvableType) null);
    }

    default boolean newObject(@NotNull Class<?> key, @NotNull Object value, String comment) {
        return newObject(key.getName(), value, comment, null, (ResolvableType) null);
    }

    default boolean newObject(@NotNull Enum<?> key, @NotNull Object value, String comment) {
        return newObject(EnumConvertor.enum2Str(key), value, comment, null, (ResolvableType) null);
    }

    /**
     * enable/disable the config, success or throw an error.
     */
    boolean enable(@NotNull String key, boolean enable);

    default boolean enable(@NotNull Class<?> key, boolean enable) {
        return enable(key.getName(), enable);
    }

    default boolean enable(@NotNull Enum<?> key, boolean enable) {
        return enable(EnumConvertor.enum2Str(key), enable);
    }
}
