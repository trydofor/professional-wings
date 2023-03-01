package pro.fessional.wings.faceless.database.jooq.helper;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Table;
import org.jooq.UpdatableRecord;
import org.jooq.UpdateSetMoreStep;
import org.jooq.impl.DAOImpl;
import pro.fessional.wings.faceless.convention.EmptyValue;
import pro.fessional.wings.faceless.database.helper.JournalJdbcHelper;
import pro.fessional.wings.faceless.service.journal.JournalService;

import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import static org.jooq.impl.DSL.field;

/**
 * @author trydofor
 * @since 2020-08-11
 */
public class JournalJooqHelper extends JournalJdbcHelper {

    public static String getJournalDateColumn(DSLContext dsl, String table) {
        return getJournalDateColumn(table, s -> {
            ResultSet rs = dsl.selectFrom(s + " where 1 = 0").fetchResultSet();
            return getJournalDateColumn(rs, true);
        });
    }

    public static String getJournalDateColumn(Table<? extends Record> table) {
        return getJournalDateColumn(table.getName(), s -> {
            String[] columns = extractColumn(table.fields(), COL_DELETE_DT, COL_MODIFY_DT, COL_MODIFY_TM);
            if (columns[0] != null) return columns[0];
            if (columns[1] != null) return columns[1];
            return "";
        });
    }

    // jooq

    public static int deleteByIds(DAOImpl<? extends UpdatableRecord<?>, ?, ?> dao, JournalService.Journal commit, Long... ids) {
        return deleteByIds(dao.ctx(), dao.getTable(), commit.getCommitId(), commit.getCommitDt(), ids);
    }

    public static int deleteByIds(DAOImpl<? extends UpdatableRecord<?>, ?, ?> dao, JournalService.Journal commit, Collection<Long> ids) {
        return deleteByIds(dao.ctx(), dao.getTable(), commit.getCommitId(), commit.getCommitDt(), ids);
    }

    public static int deleteByIds(DSLContext dsl, Table<? extends Record> table, JournalService.Journal commit, Long... ids) {
        return deleteByIds(dsl, table, commit.getCommitId(), commit.getCommitDt(), ids);
    }

    public static int deleteByIds(DSLContext dsl, Table<? extends Record> table, JournalService.Journal commit, Collection<Long> ids) {
        return deleteByIds(dsl, table, commit.getCommitId(), commit.getCommitDt(), ids);
    }

    public static int deleteByIds(DSLContext dsl, Table<? extends Record> table, Long commitId, Long... ids) {
        return deleteByIds(dsl, table, commitId, null, ids);
    }

    public static int deleteByIds(DSLContext dsl, Table<? extends Record> table, Long commitId, Collection<Long> ids) {
        return deleteByIds(dsl, table, commitId, null, ids);
    }

    public static int deleteByIds(DSLContext dsl, Table<? extends Record> table, Long commitId, LocalDateTime now, Long... ids) {
        if (ids == null || ids.length == 0) return 0;
        return deleteByIds(dsl, table, commitId, now, Arrays.asList(ids));
    }

    public static int deleteByIds(DSLContext dsl, Table<? extends Record> table, Long commitId, LocalDateTime now, Collection<Long> ids) {
        if (ids == null || ids.isEmpty()) return 0;
        Field<Long> id = field("id", Long.class);
        return deleteWhere(dsl, table, commitId, now, id.in(ids));
    }

    public static int deleteWhere(DAOImpl<? extends UpdatableRecord<?>, ?, ?> dao, JournalService.Journal commit, Condition where) {
        return deleteWhere(dao.ctx(), dao.getTable(), commit.getCommitId(), commit.getCommitDt(), where);
    }

    public static int deleteWhere(DSLContext dsl, Table<? extends Record> table, JournalService.Journal commit, Condition where) {
        return deleteWhere(dsl, table, commit.getCommitId(), commit.getCommitDt(), where);
    }

    public static int deleteWhere(DSLContext dsl, Table<? extends Record> table, Long commitId, Condition where) {
        return deleteWhere(dsl, table, commitId, null, where);
    }

    public static int deleteWhere(DSLContext dsl, Table<? extends Record> table, Long commitId, LocalDateTime now, Condition where) {
        UpdateSetMoreStep<? extends Record> update = dsl
                                                             .update(table)
                                                             .set(field(COL_COMMIT_ID, Long.class), commitId);

        String jf = getJournalDateColumn(table);
        if (!jf.isEmpty()) {
            if (now == null) {
                update = update.set(field(jf, String.class), field("NOW(3)", String.class));
            }
            else {
                update = update.set(field(jf, LocalDateTime.class), now);
            }
        }
        update.where(where).execute();

        return dsl.deleteFrom(table).where(where).execute();
    }


    @SuppressWarnings("DuplicatedCode")
    public static String[] extractColumn(Field<?>[] fields, String... name) {
        String[] result = new String[name.length];
        for (Field<?> fd : fields) {
            String cn = getFieldName(fd.getName());
            for (int j = 0; j < name.length; j++) {
                if (result[j] == null && cn.equalsIgnoreCase(name[j])) {
                    result[j] = cn;
                }
            }
        }
        return result;
    }

    @SuppressWarnings("DuplicatedCode")
    public static Field<?>[] extractField(Field<?>[] fields, String... name) {
        Field<?>[] result = new Field<?>[name.length];
        for (Field<?> fd : fields) {
            String cn = getFieldName(fd.getName());
            for (int j = 0; j < name.length; j++) {
                if (result[j] == null && cn.equalsIgnoreCase(name[j])) {
                    result[j] = fd;
                }
            }
        }
        return result;
    }

    public static <T extends Table<?>> void create(@NotNull JournalService.Journal journal, @Nullable T table, @Nullable Map<?, ?> setter) {
        commit(journal, table, setter, COL_COMMIT_ID, COL_CREATE_DT, COL_MODIFY_DT, COL_DELETE_DT);
    }

    public static <T extends Table<?>> void modify(@NotNull JournalService.Journal journal, @Nullable T table, @Nullable Map<?, ?> setter) {
        commit(journal, table, setter, COL_COMMIT_ID, COL_MODIFY_DT);
    }

    public static <T extends Table<?>> void delete(@NotNull JournalService.Journal journal, @Nullable T table, @Nullable Map<?, ?> setter) {
        commit(journal, table, setter, COL_COMMIT_ID, COL_DELETE_DT);
    }

    public static <T extends Table<?>> void commit(@NotNull JournalService.Journal journal, @Nullable T table, @Nullable Map<?, ?> setter, String... field) {
        if (table == null || setter == null || field == null) return;
        Field<?>[] fields = extractField(table.fields(), field);
        commit(journal, setter, fields);
    }

    public static void commit(@NotNull JournalService.Journal journal, @Nullable Map<?, ?> setter, Field<?>... field) {
        if (setter == null || field == null) return;
        @SuppressWarnings("unchecked")
        Map<Object, Object> putter = (Map<Object, Object>) setter;

        Field<?> createDt = null;
        Field<?> modifyDt = null;
        Field<?> deleteDt = null;
        for (Field<?> fd : field) {
            String k = getFieldName(fd.getName());
            if (k.equalsIgnoreCase(COL_COMMIT_ID)) {
                putter.put(fd, journal.getCommitId());
            }
            else if (k.equalsIgnoreCase(COL_CREATE_DT)) {
                createDt = fd;
            }
            else if (k.equalsIgnoreCase(COL_MODIFY_DT)) {
                modifyDt = fd;
            }
            else if (k.equalsIgnoreCase(COL_DELETE_DT)) {
                deleteDt = fd;
            }
        }

        LocalDateTime commitDt = journal.getCommitDt();
        if (createDt == null) {
            if (modifyDt != null) putter.put(modifyDt, commitDt);
            if (deleteDt != null) putter.put(deleteDt, commitDt);
        }
        else {
            putter.put(createDt, commitDt);
            if (modifyDt != null) putter.put(modifyDt, EmptyValue.DATE_TIME);
            if (deleteDt != null) putter.put(deleteDt, EmptyValue.DATE_TIME);
        }
    }
}
