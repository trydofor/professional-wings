package pro.fessional.wings.faceless.database.jooq.converter;

import org.jooq.impl.AbstractConverter;
import pro.fessional.wings.faceless.enums.ConstantEnum;

/**
 * ConstantEnum to wings jooq
 *
 * @author trydofor
 * @since 2021-01-14
 */
public class ConsEnumConverter<E extends ConstantEnum> extends AbstractConverter<Integer, E> {

    private final E[] values;

    public ConsEnumConverter(Class<E> toType, E[] values) {
        super(Integer.class, toType);
        this.values = values;
    }

    @Override
    public E from(Integer databaseObject) {
        if (databaseObject == null) return null;
        final int iv = databaseObject;
        for (E v : values) {
            if (v.getId() == iv) return v;
        }
        return null;
    }

    @Override
    public Integer to(E userObject) {
        return userObject == null ? null : userObject.getId();
    }

    public static <E extends Enum<E> & ConstantEnum> ConsEnumConverter<E> of(Class<E> toType) {
        return new ConsEnumConverter<>(toType, toType.getEnumConstants());
    }

    public static <E extends ConstantEnum> ConsEnumConverter<E> of(Class<E> toType, E[] values) {
        return new ConsEnumConverter<>(toType, values);
    }
}
