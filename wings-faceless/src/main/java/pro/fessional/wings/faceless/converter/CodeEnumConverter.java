package pro.fessional.wings.faceless.converter;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pro.fessional.mirana.data.CodeEnum;
import pro.fessional.wings.faceless.enums.ConstantEnumUtil;


/**
 * CodeEnum to wings jooq
 *
 * @author trydofor
 * @since 2021-01-14
 */
public class CodeEnumConverter<E extends CodeEnum> implements WingsConverter<String, E> {

    private final E[] values;
    private final Class<String> srcClz;
    private final Class<E> tgtClz;

    public CodeEnumConverter(Class<E> toType, E[] values) {
        this.srcClz = String.class;
        this.tgtClz = toType;
        this.values = values;
    }

    @Override
    public @NotNull Class<String> sourceType() {
        return srcClz;
    }

    @Override
    public @NotNull Class<E> targetType() {
        return tgtClz;
    }

    @Override
    public @Nullable E toTarget(String s) {
        return ConstantEnumUtil.codeOrNull(s, values);
    }

    @Override
    public @Nullable String toSource(E t) {
        return t == null ? null : t.getCode();
    }

    public static <E extends Enum<E> & CodeEnum> CodeEnumConverter<E> of(Class<E> toType) {
        return new CodeEnumConverter<>(toType, toType.getEnumConstants());
    }

    @SafeVarargs
    public static <E extends CodeEnum> CodeEnumConverter<E> of(Class<E> toType, E... values) {
        return new CodeEnumConverter<>(toType, values);
    }
}
