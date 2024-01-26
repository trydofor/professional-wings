package pro.fessional.wings.faceless.database.helper;

import org.springframework.jdbc.core.ResultSetExtractor;

/**
 * @author trydofor
 * @since 2024-01-25
 */
public class JdbcTemplateHelper {

    public static final ResultSetExtractor<String> FirstStringOrNull = rs -> rs.next() ? rs.getString(1) : null;

    public static final ResultSetExtractor<Long> FirstLongOrNull = rs -> {
        if (rs.next()) {
            long v = rs.getLong(1);
            return rs.wasNull() ? null : v;
        }
        return null;
    };

    public static final ResultSetExtractor<Integer> FirstIntegerOrNull = rs -> {
        if (rs.next()) {
            int v = rs.getInt(1);
            return rs.wasNull() ? null : v;
        }
        return null;
    };

    public static final ResultSetExtractor<Boolean> FirstBooleanOrNull = rs -> {
        if (rs.next()) {
            boolean v = rs.getBoolean(1);
            return rs.wasNull() ? null : v;
        }
        return null;
    };
}
