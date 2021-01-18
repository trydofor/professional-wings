package pro.fessional.wings.faceless.database.jooq.converter;

import org.jetbrains.annotations.NotNull;
import org.jooq.Converter;
import pro.fessional.mirana.data.CodeEnum;
import pro.fessional.wings.faceless.converter.CodeEnumConverter;

/**
 * @author trydofor
 * @since 2021-01-18
 */
public class JooqCodeEnumConverter<E extends CodeEnum> extends CodeEnumConverter<E> implements Converter<String,E> {

    public JooqCodeEnumConverter(Class<E> toType, E[] values) {
        super(toType, values);
    }

    @Override
    public E from(String databaseObject) {
        return toTarget(databaseObject);
    }

    @Override
    public String to(E userObject) {
        return toSource(userObject);
    }

    @Override
    public @NotNull Class<String> fromType() {
        return sourceType();
    }

    @Override
    public @NotNull Class<E> toType() {
        return targetType();
    }
}
