package pro.fessional.wings.slardar.security.impl;

import org.jetbrains.annotations.NotNull;
import pro.fessional.mirana.cast.EnumConvertor;
import pro.fessional.mirana.data.Null;
import pro.fessional.wings.slardar.security.WingsAuthTypeParser;

import java.util.HashMap;
import java.util.Map;

/**
 * 使用HashMap构建，Null.Str=Null.Enm
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
            }
            else {
                throw new IllegalArgumentException("exist mapping for type=" + v + ", enum=" + EnumConvertor.enum2Str(k));
            }
        }
    }

    @Override
    public @NotNull Enum<?> parse(String at, @NotNull Enum<?> elz) {
        if (at == null) return elz;
        if (at.equals(Null.Str)) return Null.Enm;

        final Enum<?> en = strEnumMap.get(at);
        return en == null ? elz : en;
    }

    @Override
    public @NotNull String parse(Enum<?> at) {
        if (at == Null.Enm) return Null.Str;

        final String s = enumStrMap.get(at);
        if (s == null) {
            final String mes = "failed to parse enum=" + EnumConvertor.enum2Str(at);
            throw new IllegalArgumentException(mes);
        }
        return s;
    }
}
