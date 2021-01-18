package pro.fessional.wings.faceless.database.jooq.converter;

import org.jetbrains.annotations.NotNull;
import org.jooq.Converter;
import pro.fessional.wings.faceless.converter.ConsEnumConverter;
import pro.fessional.wings.faceless.enums.ConstantEnum;

/**
 * @author trydofor
 * @since 2021-01-18
 */
public class JooqConsEnumConverter<E extends ConstantEnum> extends ConsEnumConverter<E> implements Converter<Integer, E> {

    public JooqConsEnumConverter(Class<E> toType, E[] values) {
        super(toType, values);
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
        return sourceType();
    }

    @Override
    public @NotNull Class<E> toType() {
        return targetType();
    }
}
