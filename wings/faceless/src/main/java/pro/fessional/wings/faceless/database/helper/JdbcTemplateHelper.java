package pro.fessional.wings.faceless.database.helper;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.util.HashSet;
import java.util.function.Function;

/**
 * @author trydofor
 * @since 2024-01-25
 */
@Slf4j
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

    /**
     * <pre>
     * where clause of PreparedStatement, must,
     * - start with ' WHERE ' case-ignored
     * - not empty
     * - not contains ';'
     * </pre>
     */
    public static String safeWhere(String where) {
        if (where == null || where.isEmpty()) {
            throw new IllegalArgumentException("where clause is empty");
        }
        if (where.contains(";")) {
            throw new IllegalArgumentException("where clause may be sql-injected, should not contains ';'");
        }
        final String key = " WHERE ";
        if (!where.regionMatches(true, 0, key, 0, key.length())) {
            throw new IllegalArgumentException("missing ' WHERE ' in where clause");
        }
        return where;
    }

    protected static final HashSet<String> SafeTables = new HashSet<>();
    protected static final HashSet<String> QuotedTables = new HashSet<>();

    /**
     * the sql to list table, and get the table name at the first column
     */
    @NotNull
    protected static String ShowTableSql = "SHOW TABLES";

    /**
     * in mysql (not ANSI_QUOTES), return `table`
     */
    @NotNull
    protected static Function<String, String> Quotes = (String name) -> "`" + name + "`";

    public static void initSafeTable(JdbcTemplate tmpl) {
        tmpl.query(ShowTableSql, rs -> {
            String tbl = rs.getString(1);
            log.info("init safe table={}", tbl);

            String lc = tbl.toLowerCase();
            String uc = tbl.toUpperCase();
            SafeTables.add(lc);
            SafeTables.add(uc);

            QuotedTables.add(Quotes.apply(lc));
            QuotedTables.add(Quotes.apply(uc));
        });
    }

    /**
     * whether the table is in the safe list
     */
    public static boolean isSafeTable(String table) {
        return SafeTables.contains(table) || QuotedTables.contains(table);
    }

    /**
     * quote name, e.g. `table`
     */
    public static String safeName(@NotNull String name) {
        return Quotes.apply(name);
    }

    /**
     * check and quote table
     */
    public static String safeTable(String table) {
        if (table == null || table.isEmpty()) {
            throw new NullPointerException("table is empty");
        }

        int safe = 0;
        if (SafeTables.contains(table)) {
            safe = 1;
        }
        else if (QuotedTables.contains(table)) {
            safe = 2;
        }

        if (safe == 0) {
            throw new IllegalArgumentException("unsafe table " + table);
        }

        return safe == 1 ? Quotes.apply(table) : table;
    }
}
