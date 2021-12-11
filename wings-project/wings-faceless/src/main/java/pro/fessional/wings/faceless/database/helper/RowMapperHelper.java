package pro.fessional.wings.faceless.database.helper;

import org.simpleflatmapper.jdbc.spring.JdbcTemplateMapperFactory;
import org.springframework.jdbc.core.RowMapper;
import pro.fessional.wings.faceless.converter.WingsConverter;

import java.sql.ResultSet;

/**
 * @author trydofor
 * @since 2021-01-17
 */
public class RowMapperHelper {

    public static <E> RowMapper<E> of(Class<E> clz) {
        return JdbcTemplateMapperFactory.newInstance().newRowMapper(clz);
    }

    public static <E> RowMapper<E> of(Class<E> clz, WingsConverter<?, ?>... convertors) {
        JdbcTemplateMapperFactory factory = JdbcTemplateMapperFactory.newInstance();
        for (WingsConverter<?, ?> conv : convertors) {
            factory.addGetterForType(conv.targetType(), (ResultSet rs, int i) -> {
                final Object src = rs.getObject(i);
                final Object tgt = conv.tryToTarget(src);
                return tgt == null ? src : tgt;
            });
        }
        return factory.newRowMapper(clz);
    }
}
