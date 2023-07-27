package pro.fessional.wings.slardar.security.impl;

import org.jetbrains.annotations.NotNull;
import pro.fessional.mirana.cast.EnumConvertor;
import pro.fessional.wings.slardar.security.WingsAuthTypeParser;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Construct the  one-to-one (except for the default value) mapping with  HashMap.
 *
 * @author trydofor
 * @since 2021-02-22
 */
public class DefaultWingsAuthTypeParser implements WingsAuthTypeParser {

    private final Map<String, Enum<?>> strEnumMap;
    private final Map<Enum<?>, String> enumStrMap;
    private final String defaultAuthTypeName;
    private final Enum<?> defaultAuthTypeEnum;

    public DefaultWingsAuthTypeParser(Enum<?> defaultType, Map<String, Enum<?>> authType) {
        this.strEnumMap = Collections.unmodifiableMap(authType);
        Map<Enum<?>, String> map = new HashMap<>(strEnumMap.size());
        for (Map.Entry<String, Enum<?>> en : strEnumMap.entrySet()) {
            Enum<?> k = en.getValue();
            final String v = map.get(k);
            if (v == null) {
                map.put(k, en.getKey());
            }
            else {
                throw new IllegalArgumentException("exist mapping for type=" + v + ", enum=" + EnumConvertor.enum2Str(k));
            }
        }
        final String dae = map.get(defaultType);
        if (defaultType == null || dae == null) {
            throw new IllegalArgumentException("default MUST mapping, enum=" + EnumConvertor.enum2Str(defaultType));
        }

        this.enumStrMap = Collections.unmodifiableMap(map);
        this.defaultAuthTypeEnum = defaultType;
        this.defaultAuthTypeName = dae;
    }

    @Override
    public @NotNull Enum<?> parse(String at) {
        for (Map.Entry<String, Enum<?>> en : strEnumMap.entrySet()) {
            if (en.getKey().equalsIgnoreCase(at)) {
                return en.getValue();
            }
        }
        return defaultAuthTypeEnum;
    }

    @Override
    public @NotNull String parse(Enum<?> at) {
        if (at == null) return defaultAuthTypeName;
        return enumStrMap.getOrDefault(at, defaultAuthTypeName);
    }

    @Override
    public @NotNull Map<String, Enum<?>> types() {
        return strEnumMap;
    }
}
