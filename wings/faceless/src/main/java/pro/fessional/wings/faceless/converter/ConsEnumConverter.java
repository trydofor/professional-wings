package pro.fessional.wings.faceless.converter;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pro.fessional.wings.faceless.enums.ConstantEnum;
import pro.fessional.wings.faceless.enums.ConstantEnumUtil;

/**
 * ConstantEnum to wings jooq
 *
 * @author trydofor
 * @since 2021-01-14
 */
public class ConsEnumConverter<E extends ConstantEnum> implements WingsConverter<Integer, E> {

    private final E[] values;
    private final Class<Integer> srcClz;
    private final Class<E> tgtClz;

    public ConsEnumConverter(Class<E> toType, E[] values) {
        this.srcClz = Integer.class;
        this.tgtClz = toType;
        this.values = values;
    }

    @Override
    public E from(Integer databaseObject) {
        return toTarget(databaseObject);
    }

    @Override
    public Integer to(E userObject) {
        return toSource(userObject);
    }

    @Override
    public @NotNull Class<Integer> fromType() {
        return srcClz;
    }

    @Override
    public @NotNull Class<E> toType() {
        return tgtClz;
    }

    @Override
    public @NotNull Class<Integer> sourceType() {
        return srcClz;
    }

    @Override
    public @NotNull Class<E> targetType() {
        return tgtClz;
    }

    @Override
    public @Nullable E toTarget(Integer s) {
        return ConstantEnumUtil.idOrNull(s, values);
    }

    @Override
    public @Nullable Integer toSource(E t) {
        return t == null ? null : t.getId();
    }

    public static <E extends Enum<E> & ConstantEnum> ConsEnumConverter<E> of(Class<E> toType) {
        return new ConsEnumConverter<>(toType, toType.getEnumConstants());
    }

    @SafeVarargs
    public static <E extends ConstantEnum> ConsEnumConverter<E> of(Class<E> toType, E... values) {
        return new ConsEnumConverter<>(toType, values);
    }
}
