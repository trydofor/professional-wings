package pro.fessional.wings.faceless.database.jooq.converter;

import org.jooq.impl.AbstractConverter;
import pro.fessional.mirana.data.CodeEnum;

/**
 * CodeEnum to wings jooq
 *
 * @author trydofor
 * @since 2021-01-14
 */
public class CodeEnumConverter<E extends CodeEnum> extends AbstractConverter<String, E> {

    private final E[] values;

    public CodeEnumConverter(Class<E> toType, E[] values) {
        super(String.class, toType);
        this.values = values;
    }

    @Override
    public E from(String databaseObject) {
        if (databaseObject == null) return null;
        for (E v : values) {
            if (v.getCode().equalsIgnoreCase(databaseObject)) return v;
        }
        return null;
    }

    @Override
    public String to(E userObject) {
        return userObject == null ? null : userObject.getCode();
    }

    public static <E extends Enum<E> & CodeEnum> CodeEnumConverter<E> of(Class<E> toType) {
        return new CodeEnumConverter<>(toType, toType.getEnumConstants());
    }

    public static <E extends CodeEnum> CodeEnumConverter<E> of(Class<E> toType, E[] values) {
        return new CodeEnumConverter<>(toType, values);
    }
}
