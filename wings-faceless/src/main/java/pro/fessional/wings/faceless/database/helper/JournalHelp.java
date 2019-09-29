package pro.fessional.wings.faceless.database.helper;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Table;
import org.jooq.UpdateSetMoreStep;
import org.springframework.jdbc.core.JdbcTemplate;
import pro.fessional.mirana.time.DateFormatter;
import pro.fessional.wings.faceless.service.journal.JournalService;

import java.time.LocalDateTime;

import static org.jooq.impl.DSL.field;

/**
 * 对数据库进行journal操作的助手类，表必须有 modify_dt和commit_id 字段。
 * delete前，先更新commit_id=?和modify_dt=NOW()，然后真正delete
 *
 * @author trydofor
 * @since 2019-09-28
 */
public class JournalHelp {

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

        String str = now == null ? "NOW()" : "'" + DateFormatter.full19(now) + "'";
        String update = "UPDATE " + table + " SET modify_dt=" + str + ", commit_id=" + commitId + " " + where;
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
        Field<Long> fc = field("commit_id", Long.class);
        UpdateSetMoreStep<? extends Record> update;
        if (now == null) {
            Field<String> fm = field("modify_dt", String.class);
            update = dsl.update(table).set(fm, field("NOW()", String.class));
        } else {
            Field<LocalDateTime> fm = field("modify_dt", LocalDateTime.class);
            update = dsl.update(table).set(fm, now);
        }

        update.set(fc, commitId)
              .where(where)
              .execute();

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
