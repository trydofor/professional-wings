package pro.fessional.wings.slardar.security.impl;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pro.fessional.mirana.cast.EnumConvertor;
import pro.fessional.wings.slardar.security.WingsAuthTypeParser;

import java.util.HashMap;
import java.util.Map;

/**
 * 使用HashMap构建
 *
 * @author trydofor
 * @since 2021-02-22
 */
public class DefaultWingsAuthTypeParser implements WingsAuthTypeParser {

    private final Map<String, Enum<?>> strEnumMap;
    private final Map<Enum<?>, String> enumStrMap;

    public DefaultWingsAuthTypeParser(Map<String, Enum<?>> authType) {
        this.strEnumMap = authType;
        this.enumStrMap = new HashMap<>(authType.size());
        for (Map.Entry<String, Enum<?>> en : authType.entrySet()) {
            Enum<?> k = en.getValue();
            final String v = enumStrMap.get(k);
            if (v == null) {
                enumStrMap.put(k, en.getKey());
            } else {
                throw new IllegalArgumentException("exist mapping for type=" + v + ", enum=" + EnumConvertor.enum2Str(k));
            }
        }
    }

    @Override
    public @Nullable Enum<?> parse(String at) {
        return strEnumMap.get(at);
    }

    @Override
    public @NotNull String parse(Enum<?> at) {
        final String s = enumStrMap.get(at);
        if (s == null) {
            final String mes = "can not parse enum to string, enum=" + EnumConvertor.enum2Str(at);
            throw new IllegalArgumentException(mes);
        }
        return s;
    }
}
