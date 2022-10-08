package pro.fessional.wings.faceless.database.helper;

import org.jetbrains.annotations.NotNull;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import pro.fessional.wings.faceless.converter.WingsConverter;

import java.beans.PropertyDescriptor;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;

/**
 * @author trydofor
 * @since 2021-01-17
 */
public class RowMapperHelper {

    public static <E> RowMapper<E> of(Class<E> clz) {
        return new BeanPropertyRowMapper<>(clz);
    }

    public static <E> RowMapper<E> of(Class<E> clz, WingsConverter<?, ?>... convertors) {
        return new BeanPropertyRowMapperConverter<>(clz, Arrays.asList(convertors));
    }

    public static <E> RowMapper<E> of(Class<E> clz, Collection<WingsConverter<?, ?>> convertors) {
        return new BeanPropertyRowMapperConverter<>(clz, convertors);
    }

    public static class BeanPropertyRowMapperConverter<T> extends BeanPropertyRowMapper<T> {

        private final Collection<WingsConverter<?, ?>> convertors;

        public BeanPropertyRowMapperConverter(Class<T> mappedClass, Collection<WingsConverter<?, ?>> convertors) {
            super(mappedClass);
            this.convertors = convertors;
        }

        @Override
        protected Object getColumnValue(@NotNull ResultSet rs, int index, @NotNull PropertyDescriptor pd) throws SQLException {
            if (!convertors.isEmpty()) {
                final Object src = rs.getObject(index);
                final Class<?> tc = pd.getPropertyType();
                for (WingsConverter<?, ?> conv : convertors) {
                    final Object tgt = conv.tryToTarget(tc, src);
                    if (tgt != null) return tgt;
                }
            }

            return super.getColumnValue(rs, index, pd);
        }
    }
}
