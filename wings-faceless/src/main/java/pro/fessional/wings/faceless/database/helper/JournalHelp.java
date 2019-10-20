package pro.fessional.wings.faceless.database.helper;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Table;
import org.jooq.UpdateSetMoreStep;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import pro.fessional.mirana.time.DateFormatter;
import pro.fessional.wings.faceless.service.journal.JournalService;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.jooq.impl.DSL.field;

/**
 * 对数据库进行journal操作的助手类，表必须有 delete_dt和commit_id 字段。
 * delete前，先更新commit_id=?和delete_dt=NOW()，然后真正delete
 *
 * @author trydofor
 * @since 2019-09-28
 */
public class JournalHelp {

    private static final Map<String, String> tableJournal = new ConcurrentHashMap<>();
    private static final ResultSetExtractor<String> filedJournal = rs -> getJournalField(rs, false);

    private static String getJournalField(ResultSet rs, boolean needClose) {
        boolean hasModifyFd = false;
        try {
            ResultSetMetaData md = rs.getMetaData();
            int count = md.getColumnCount();
            for (int i = 1; i <= count; i++) {
                String cn = md.getColumnName(i).toLowerCase();
                if (cn.endsWith("delete_dt")) return "delete_dt";
                if (cn.endsWith("modify_dt")) hasModifyFd = true;
            }
        } catch (SQLException e) {
            //
        } finally {
            if (needClose) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    //
                }
            }
        }
        return hasModifyFd ? "modify_dt" : "";
    }

    public static String getJournalField(JdbcTemplate tmpl, String table) {
        return tableJournal.computeIfAbsent(table, s -> tmpl.query("select * from " + s + " where 1 = 0", filedJournal));
    }

    public static String getJournalField(DSLContext dsl, String table) {
        return tableJournal.computeIfAbsent(table, s -> {
            ResultSet rs = dsl.selectFrom(s + " where 1 = 0").fetchResultSet();
            return getJournalField(rs, true);
        });
    }

    public static String getJournalField(Table<? extends Record> table) {
        return tableJournal.computeIfAbsent(table.getName(), s -> {
            boolean hasModifyFd = false;
            for (Field<?> f : table.fields()) {
                String cn = f.getName().toLowerCase();
                if (cn.endsWith("delete_dt")) return "delete_dt";
                if (cn.endsWith("modify_dt")) hasModifyFd = true;
            }
            return hasModifyFd ? "modify_dt" : "";
        });
    }

    // jdbc

    public static int deleteByIds(JdbcTemplate tmpl, String table, JournalService.Journal journal, Long... ids) {
        return deleteByIds(tmpl, table, journal.getCommitId(), journal.getCommitDt(), ids);
    }

    public static int deleteByIds(JdbcTemplate tmpl, String table, Long commitId, Long... ids) {
        return deleteByIds(tmpl, table, commitId, null, ids);
    }

    public static int deleteByIds(JdbcTemplate tmpl, String table, Long commitId, LocalDateTime now, Long... ids) {
        if (ids == null || ids.length == 0) return 0;
        StringBuilder where = new StringBuilder(" WHERE id IN (");
        for (Long id : ids) {
            where.append(id).append(",");
        }
        where.deleteCharAt(where.length() - 1);
        where.append(")");
        return deleteWhere(tmpl, table, commitId, now, where.toString());
    }

    public static int deleteWhere(JdbcTemplate tmpl, String table, JournalService.Journal journal, String where, Object... args) {
        return deleteWhere(tmpl, table, journal.getCommitId(), journal.getCommitDt(), where, args);
    }

    public static int deleteWhere(JdbcTemplate tmpl, String table, Long commitId, String where, Object... args) {
        return deleteWhere(tmpl, table, commitId, null, where, args);
    }

    public static int deleteWhere(JdbcTemplate tmpl, String table, Long commitId, LocalDateTime now, String where, Object... args) {
        checkTableName(table);
        String jf = getJournalField(tmpl, table);
        String journalSetter = " ";
        if (!jf.isEmpty()) {
            String ldt = now == null ? "NOW()" : "'" + DateFormatter.full19(now) + "'";
            journalSetter = ", " + jf + "=" + ldt + " ";
        }
        String update = "UPDATE " + table + " SET commit_id=" + commitId + journalSetter + where;
        tmpl.update(update, args);

        String delete = "DELETE FROM " + table + " " + where;
        return tmpl.update(delete, args);
    }

    // jooq

    public static int deleteByIds(DSLContext dsl, Table<? extends Record> table, JournalService.Journal journal, Long... ids) {
        return deleteByIds(dsl, table, journal.getCommitId(), journal.getCommitDt(), ids);
    }

    public static int deleteByIds(DSLContext dsl, Table<? extends Record> table, Long commitId, Long... ids) {
        return deleteByIds(dsl, table, commitId, null, ids);
    }

    public static int deleteByIds(DSLContext dsl, Table<? extends Record> table, Long commitId, LocalDateTime now, Long... ids) {
        if (ids == null || ids.length == 0) return 0;
        Field<Long> id = field("id", Long.class);
        return deleteWhere(dsl, table, commitId, now, id.in(ids));
    }

    public static int deleteWhere(DSLContext dsl, Table<? extends Record> table, JournalService.Journal journal, Condition where) {
        return deleteWhere(dsl, table, journal.getCommitId(), journal.getCommitDt(), where);
    }

    public static int deleteWhere(DSLContext dsl, Table<? extends Record> table, Long commitId, Condition where) {
        return deleteWhere(dsl, table, commitId, null, where);
    }

    public static int deleteWhere(DSLContext dsl, Table<? extends Record> table, Long commitId, LocalDateTime now, Condition where) {
        UpdateSetMoreStep<? extends Record> update = dsl
                .update(table)
                .set(field("commit_id", Long.class), commitId);

        String jf = getJournalField(table);
        if (!jf.isEmpty()) {
            if (now == null) {
                update = update.set(field(jf, String.class), field("NOW()", String.class));
            } else {
                update = update.set(field(jf, LocalDateTime.class), now);
            }
        }
        update.where(where).execute();

        return dsl.deleteFrom(table).where(where).execute();
    }

    // ////

    private static void checkTableName(String table) {
        if (table == null) throw new NullPointerException("table is null");
        if (table.contains(" ") || table.contains("\t") || table.contains("\r") || table.contains("\n") || table.contains("=")) {
            throw new IllegalArgumentException("table is may be sql-injected");
        }
    }
}
