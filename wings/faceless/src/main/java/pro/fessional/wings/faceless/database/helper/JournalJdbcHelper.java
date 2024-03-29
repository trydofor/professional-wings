package pro.fessional.wings.faceless.database.helper;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import pro.fessional.mirana.best.DummyBlock;
import pro.fessional.mirana.time.DateFormatter;
import pro.fessional.wings.faceless.service.journal.JournalService;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * Helper for journal operations on databases, table must have delete_dt and commit_id columns.
 * Before deleting, update commit_id=? and delete_dt=NOW(3), and then actually delete the record.
 *
 * @author trydofor
 * @since 2019-09-28
 */
public class JournalJdbcHelper {

    public static final String COL_CREATE_DT = "create_dt";
    public static final String COL_MODIFY_DT = "modify_dt";
    public static final String COL_MODIFY_TM = "modify_time";
    public static final String COL_DELETE_DT = "delete_dt";
    public static final String COL_IS_DELETED = "is_deleted";
    public static final String COL_COMMIT_ID = "commit_id";

    private static final Map<String, String> tableJournal = new ConcurrentHashMap<>();
    private static final ResultSetExtractor<String> filedJournal = rs -> getJournalDateColumn(rs, false);

    public static String getJournalDateColumn(ResultSet rs, boolean needClose) {
        try {
            String[] columns = extractColumn(rs.getMetaData(), COL_DELETE_DT, COL_MODIFY_DT, COL_MODIFY_TM);
            if (columns[0] != null) return columns[0];
            if (columns[1] != null) return columns[1];
        }
        catch (SQLException e) {
            DummyBlock.ignore(e);
        }
        finally {
            if (needClose) {
                try {
                    rs.close();
                }
                catch (SQLException e) {
                    DummyBlock.ignore(e);
                }
            }
        }
        return "";
    }

    public static String getJournalDateColumn(String table, Function<String, String> fun) {
        return tableJournal.computeIfAbsent(table, fun);
    }

    public static String getJournalDateColumn(JdbcTemplate tmpl, String table) {
        return getJournalDateColumn(table, s -> {
            String tbl = JdbcTemplateHelper.safeTable(s);
            String sql = "SELECT * FROM " + tbl + " WHERE 1 = 0";
            return tmpl.query(sql, filedJournal);
        });
    }

    // jdbc

    public static int deleteByIds(JdbcTemplate tmpl, String table, JournalService.Journal commit, Long... ids) {
        return deleteByIds(tmpl, table, commit.getCommitId(), commit.getCommitDt(), ids);
    }

    public static int deleteByIds(JdbcTemplate tmpl, String table, JournalService.Journal commit, Collection<Long> ids) {
        return deleteByIds(tmpl, table, commit.getCommitId(), commit.getCommitDt(), ids);
    }

    public static int deleteByIds(JdbcTemplate tmpl, String table, Long commitId, Long... ids) {
        return deleteByIds(tmpl, table, commitId, null, ids);
    }

    public static int deleteByIds(JdbcTemplate tmpl, String table, Long commitId, Collection<Long> ids) {
        return deleteByIds(tmpl, table, commitId, null, ids);
    }

    public static int deleteByIds(JdbcTemplate tmpl, String table, Long commitId, LocalDateTime now, Long... ids) {
        if (ids == null || ids.length == 0) return 0;
        return deleteByIds(tmpl, table, commitId, now, Arrays.asList(ids));
    }

    public static int deleteByIds(JdbcTemplate tmpl, String table, Long commitId, LocalDateTime now, Collection<Long> ids) {
        if (ids == null || ids.isEmpty()) return 0;

        StringBuilder where = new StringBuilder(" WHERE id IN (");
        for (Long id : ids) {
            where.append(id).append(',');
        }
        where.deleteCharAt(where.length() - 1);
        where.append(')');
        return deleteWhere(tmpl, table, commitId, now, where.toString());
    }

    public static int deleteWhere(JdbcTemplate tmpl, String table, JournalService.Journal commit, String where, Object... args) {
        return deleteWhere(tmpl, table, commit.getCommitId(), commit.getCommitDt(), where, args);
    }

    public static int deleteWhere(JdbcTemplate tmpl, String table, Long commitId, String where, Object... args) {
        return deleteWhere(tmpl, table, commitId, null, where, args);
    }

    public static int deleteWhere(JdbcTemplate tmpl, String table, Long commitId, LocalDateTime now, String where, Object... args) {
        where = JdbcTemplateHelper.safeWhere(where);
        table = JdbcTemplateHelper.safeTable(table);

        String jf = getJournalDateColumn(tmpl, table);
        String journalSetter = " ";
        if (!jf.isEmpty()) {
            String ldt = now == null ? "NOW(3)" : "'" + DateFormatter.full19(now) + "'";
            journalSetter = ", " + jf + "=" + ldt + " ";
        }

        String update = "UPDATE " + table + " SET " + COL_COMMIT_ID + "=" + commitId + journalSetter + where;
        tmpl.update(update, args);

        String delete = "DELETE FROM " + table + " " + where;
        return tmpl.update(delete, args);
    }

    public static String[] extractColumn(ResultSetMetaData md, String... name) throws SQLException {
        String[] result = new String[name.length];
        int count = md.getColumnCount();
        for (int i = 1; i <= count; i++) {
            String cn = getFieldName(md.getColumnName(i));
            for (int j = 0; j < name.length; j++) {
                if (result[j] == null && cn.equalsIgnoreCase(name[j])) {
                    result[j] = cn;
                }
            }
        }
        return result;
    }

    public static String getFieldName(String name) {
        int dot = name.lastIndexOf('.');
        return dot < 0 ? name : name.substring(dot + 1);
    }
}
