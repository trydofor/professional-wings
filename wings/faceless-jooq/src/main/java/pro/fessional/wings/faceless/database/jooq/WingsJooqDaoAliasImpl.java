package pro.fessional.wings.faceless.database.jooq;

import com.google.common.collect.Lists;
import org.intellij.lang.annotations.MagicConstant;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jooq.BatchBindStep;
import org.jooq.Condition;
import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.InsertOnDuplicateSetMoreStep;
import org.jooq.InsertOnDuplicateSetStep;
import org.jooq.InsertReturningStep;
import org.jooq.Loader;
import org.jooq.LoaderOptionsStep;
import org.jooq.OrderField;
import org.jooq.QueryPart;
import org.jooq.Record;
import org.jooq.RecordMapper;
import org.jooq.Result;
import org.jooq.SelectConditionStep;
import org.jooq.SelectFieldOrAsterisk;
import org.jooq.SelectSelectStep;
import org.jooq.Table;
import org.jooq.TableRecord;
import org.jooq.UpdatableRecord;
import org.jooq.impl.DAOImpl;
import org.jooq.impl.DSL;
import org.jooq.impl.TableImpl;
import pro.fessional.mirana.best.AssertState;
import pro.fessional.mirana.cast.TypedCastUtil;
import pro.fessional.mirana.data.Null;
import pro.fessional.mirana.data.U;
import pro.fessional.mirana.pain.IORuntimeException;
import pro.fessional.wings.faceless.database.helper.DatabaseChecker;
import pro.fessional.wings.faceless.database.jooq.helper.JournalDiffHelper;
import pro.fessional.wings.faceless.service.journal.JournalDiff;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * <pre>
 * In principle, the database information carried by Record should not be spread,
 * so it's recommended to use Pojo instead of Record outside of Dao.
 *
 * For read method, it always returns Pojo; for write method, it supports both Record and Pojo.
 * For the convenience of coding and to reduce data copying, you can use Record for operation.
 * In batch processing, new Record is always used to improve performance.
 *
 * Note that alias is used in multi-table query, filed/condition and table must have the same name,
 * otherwise there will be a syntax error. I.e., fields that are in different alias from the table.
 * </pre>
 *
 * @param <T> Table
 * @param <R> Record
 * @param <P> POJO
 * @param <K> primary Key type
 * @author trydofor
 * @since 2019-10-12
 */
public abstract class WingsJooqDaoAliasImpl<T extends Table<R> & WingsAliasTable<T>, R extends UpdatableRecord<R>, P, K> extends DAOImpl<R, P, K> {

    protected final T table;
    protected final Field<?>[] pkeys;
    protected volatile int tableExist = -1;

    protected volatile Supplier<DSLContext> dslSup = null;

    protected WingsJooqDaoAliasImpl(T table, Class<P> type) {
        this(table, type, null);
    }

    protected WingsJooqDaoAliasImpl(T table, Class<P> type, Configuration conf) {
        super(table, type, conf);
        this.table = table;
        this.pkeys = WingsJooqUtil.primaryKeys(table);
    }

    @Override
    @NotNull
    public DSLContext ctx() {
        if (dslSup == null) return super.ctx();

        DSLContext dsl = dslSup.get();
        return dsl != null ? dsl : super.ctx();
    }

    /**
     * set/remove dsl Supplier to current instance, e.g. mocking DSL.
     * if Supplier or its result is null, return the original Dsl.
     *
     * @param sup to supply Dsl.
     */
    public void setDslContext(@Nullable Supplier<DSLContext> sup) {
        this.dslSup = sup;
    }

    /**
     * -N:Unchecked | 0:Not exist | 1:Exists
     *
     * @param type -1|0|1
     */
    public void setTableExist(@MagicConstant(intValues = {-1, 0, 1}) int type) {
        tableExist = type;
    }

    /**
     * Use `SELECT 1 FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME=? AND TABLE_SCHEMA=SCHEMA()`
     * to check the table existence in the current database.
     *
     * @return Whether not exist
     */
    public boolean notTableExist() {
        if (tableExist < 0) {
            synchronized (this) {
                if (tableExist < 0) {
                    try {
                        ctx().connection(conn -> tableExist = DatabaseChecker.existTable(conn, table.getName()) ? 1 : 0);
                    }
                    catch (Exception e) {
                        tableExist++;
                    }
                }
            }
        }
        //
        return tableExist <= 0;
    }

    /**
     * Create a new table with the same table structure.
     * Used in sharding table, shadow table scenario
     *
     * @param name new table name
     * @return new table
     * @see TableImpl#rename(String)
     */
    @SuppressWarnings("unchecked")
    @NotNull
    public T newTable(String name) {
        return (T) ((TableImpl<?>) table).rename(name);
    }

    /**
     * Based on the current table name, add prefixes, suffixes
     */
    @NotNull
    public T newTable(String prefix, String postfix) {
        return newTable(prefix + table.getName() + postfix);
    }

    @SuppressWarnings("unchecked")
    @NotNull
    public T getAlias(String alias) {
        return (T) table.as(alias);
    }

    @Override
    @NotNull
    public T getTable() {
        return table;
    }

    /**
     * Get the system default table alias.
     */
    @NotNull
    public T getAlias() {
        return table.getAliasTable();
    }

    /**
     * Create new Record by object mapping.
     *
     * @param obj object with some mapping rules.
     * @return record
     */
    @NotNull
    public R newRecord(Object obj) {
        return ctx().newRecord(table, obj);
    }

    /**
     * Create a list of records by pojo, usually used in batch.
     *
     * @param pos pojos
     * @return list of record
     */
    @NotNull
    public List<R> newRecord(Collection<P> pos) {
        final DSLContext ctx = ctx();
        return pos.stream()
                  .map(it -> ctx.newRecord(table, it))
                  .collect(Collectors.toList());
    }

    ///////////////// batch /////////////////////

    /**
     * <pre>
     * Batch load records at once, and ignore/update on duplicate.
     * ignore - check by `from dual where exists select * where `id` = ?` first,
     * replace - use on duplicate key update statement
     * </pre>
     *
     * @param records         all record
     * @param ignoreOrReplace ignore or update on duplicate
     * @return result, should use ModifyAssert to check
     * @see DSLContext#loadInto(Table)
     */
    @NotNull
    public Loader<R> batchLoad(Collection<R> records, boolean ignoreOrReplace) {
        checkBatchMysql();

        DSLContext dsl = ctx();
        LoaderOptionsStep<R> ldi = dsl.loadInto(table);
        if (ignoreOrReplace) {
            ldi = ldi.onDuplicateKeyIgnore();
        }
        else {
            ldi = ldi.onDuplicateKeyUpdate();
        }

        try {
            return ldi.loadRecords(records)
                      .fields(table.fields())
                      .execute();
        }
        catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    private void checkBatchMysql() {
        if (WingsJooqEnv.daoBatchMysql) {
            throw new IllegalStateException("Use #batchInsert(Collection<R>, int, boolean) instead. `insert ignore` and `replace into` are more efficient mysql statements than `from dual where exists` and `on duplicate key update`");
        }
    }


    /**
     * Insert Pojo, use mysql `insert ignore` or `replace into`,
     * Note jooq mergeInto must all have values, and replace won't.
     *
     * @param pojo            pojo
     * @param ignoreOrReplace ignore or update on duplicate
     * @return result, should use ModifyAssert to check
     */
    public int insertInto(P pojo, boolean ignoreOrReplace) {
        return insertInto(pojo, ignoreOrReplace, null);
    }

    /**
     * Insert Pojo with ignoreOrReplace=false, and return the diff.
     *
     * @see #diffInsert(Object, boolean)
     */
    @NotNull
    public JournalDiff diffInsert(P pojo) {
        return diffInsert(pojo, false);
    }

    /**
     * Insert Pojo and return the diff.
     *
     * @see #insertInto(Object, boolean)
     */
    @NotNull
    public JournalDiff diffInsert(P pojo, boolean ignoreOrReplace) {
        final JournalDiff diff = new JournalDiff();
        diff.setTable(table.getName());
        diff.setTyped(true);
        insertInto(pojo, ignoreOrReplace, diff);
        return diff;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private int insertInto(P pojo, boolean ignoreOrReplace, JournalDiff diff) {

        final DSLContext dsl = ctx();
        final R record = dsl.newRecord(table, pojo);
        final int rc;
        final Field<?>[] fields = table.fields();
        final Object[] values = record.intoArray();
        if (ignoreOrReplace) {
            // insert ignore
            rc = dsl.insertInto(table)
                    .columns(fields)
                    .values(values)
                    .onDuplicateKeyIgnore()
                    .execute();

        }
        else {
            rc = dsl.insertInto(table)
                    .columns(fields)
                    .values(values)
                    .onDuplicateKeyUpdate()
                    .set(record)
                    .execute();
        }

        if (diff != null) {
            final Result<? extends Record> rs2;
            Condition cond = null;
            for (Field pk : pkeys) {
                final Object v = record.get(pk);
                Condition c = pk.eq(v);
                cond = cond == null ? c : cond.and(c);
            }
            // maybe ignore some value ,re-select
            rs2 = dsl.selectFrom(table)
                     .where(cond)
                     .fetch();
            AssertState.aEqb(1, rs2.size(), "should find 1 record after insert");
            JournalDiffHelper.helpInsert(diff, rs2);
        }

        return rc;
    }

    /**
     * batchInsert syntax sugar
     *
     * @param pos             pojo records
     * @param ignoreOrReplace ignore or replace if DuplicateKey
     * @return array of affected records, can use ModifyAssert to check
     */
    public int @NotNull [] insertInto(Collection<P> pos, boolean ignoreOrReplace) {
        return batchInsert(newRecord(pos), 0, ignoreOrReplace);
    }

    /**
     * @see #mergeInto(Table, Object, Field[])
     */
    public int mergeInto(P pojo, Function<T, Field<?>[]> fun) {
        return mergeInto(table, pojo, fun.apply(table));
    }

    /**
     * insert one record by insert into DuplicateKey update.
     *
     * @param table        table with the same name as updateFields
     * @param pojo         pojo record
     * @param updateFields fields to update if Duplicate Key, should not use table alias
     * @return affected records, can use ModifyAssert to check
     */
    public int mergeInto(T table, P pojo, Field<?>... updateFields) {
        Map<Field<?>, Object> map = new LinkedHashMap<>();
        DSLContext dsl = ctx();
        R record = dsl.newRecord(table, pojo);

        for (Field<?> field : updateFields) {
            Object t = record.get(field);
            map.put(field, t);
        }

        return dsl.insertInto(table)
                  .columns(table.fields())
                  .values(record.intoArray())
                  .onDuplicateKeyUpdate()
                  .set(map)
                  .execute();
    }

    /**
     * @see #batchMerge(Table, Collection, int, Field[])
     */
    public int @NotNull [] batchMerge(Collection<R> records, int size, Function<T, Field<?>[]> fun) {
        return batchMerge(table, records, size, fun.apply(table));
    }

    /**
     * Select first, then insert or update depending on  whether record exists.
     *
     * @param table        table with the same name as updateFields
     * @param records      collection of record
     * @param size         batch size, &lt;=0 mean no batching
     * @param updateFields fields to update if Duplicate Key, should not use table alias
     * @return array of affected records, can use ModifyAssert to check
     */
    public int @NotNull [] batchMerge(T table, Collection<R> records, int size, Field<?>... updateFields) {
        if (records == null || records.isEmpty()) return Null.Ints;

        BiFunction<DSLContext, Collection<R>, int[]> batchMergeExec = (dsl, rs) -> {
            Map<Field<?>, Object> map = new LinkedHashMap<>();
            for (Field<?> field : updateFields) {
                map.put(field, null);
            }

            Field<?>[] fields = table.fields();
            int fldLen = fields.length;
            int updLen = updateFields.length;
            BatchBindStep batch = dsl.batch(
                    dsl.insertInto(table)
                       .columns(fields)
                       .values(new Object[fldLen])
                       .onDuplicateKeyUpdate()
                       .set(map)
            );

            for (R r : rs) {
                Object[] vals = new Object[fldLen + updLen];
                for (int i = 0; i < fldLen; i++) {
                    vals[i] = r.get(i);
                }
                for (int i = 0; i < updLen; i++) {
                    vals[i + fldLen] = r.get(updateFields[i]);
                }
                batch = batch.bind(vals);
            }

            return batch.execute();
        };

        return batchExecute(records, size, batchMergeExec);
    }

    /**
     * Use this method if there are no unique constraints in the db.
     * (1) batch SELECT based on KEYS first, (2) INSERT or UPDATE based on the records.
     * String comparison ignores case
     *
     * @param table        table with the same name as updateFields
     * @param keys         keys of Duplicate Key
     * @param records      collection of record
     * @param size         batch size, &lt;=0 mean no batching
     * @param updateFields fields to update if Duplicate Key, should not use table alias
     * @return array of affected records, can use ModifyAssert to check
     */
    public int @NotNull [] batchMerge(T table, Field<?>[] keys, Collection<R> records, int size, Field<?>... updateFields) {
        return batchMerge(table, keys, caseIgnore, records, size, updateFields);
    }

    private final BiPredicate<Object, Object> caseIgnore = (o1, o2) -> {
        if (o1 instanceof String s1 && o2 instanceof String s2) {
            return s1.equalsIgnoreCase(s2);
        }
        else {
            return o1.equals(o2);
        }
    };

    /**
     * Use this method if there are no unique constraints in the db.
     * (1) batch SELECT based on KEYS first, (2) INSERT or UPDATE based on the records.
     *
     * @param table        table with the same name as updateFields
     * @param keys         keys of Duplicate Key
     * @param equals       predicate of equals
     * @param records      collection of record
     * @param size         batch size, &lt;=0 mean no batching
     * @param updateFields fields to update if Duplicate Key, should not use table alias
     * @return array of affected records, can use ModifyAssert to check
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public int @NotNull [] batchMerge(T table, Field<?>[] keys, BiPredicate<Object, Object> equals, Collection<R> records, int size, Field<?>... updateFields) {
        if (records == null || records.isEmpty()) return Null.Ints;

        DSLContext dsl = ctx();
        int[] result = new int[records.size()];
        int off = 0;
        List<R> upd = new ArrayList<>(size);
        List<R> ins = new ArrayList<>(size);
        for (List<R> rcds : partition(records, size)) {
            upd.clear();
            ins.clear();


            Condition where = null;
            for (R rcd : rcds) {
                Condition cand = null;
                for (Field key : keys) {
                    Object o = rcd.get(key);
                    if (cand == null) {
                        cand = key.eq(o);
                    }
                    else {
                        cand = cand.and(key.eq(o));
                    }
                }
                if (where == null) {
                    where = cand;
                }
                else {
                    where = where.or(cand);
                }
            }
            // throw NPE, if stupid keys or upp is empty

            Result<Record> res = dsl.select(keys)
                                    .from(table)
                                    .where(where)
                                    .fetch();

            List<Record> tmp = new ArrayList<>(res);
            for (R rcd : rcds) {
                boolean has = false;
                for (Iterator<Record> it = tmp.iterator(); it.hasNext(); ) {
                    Record d = it.next();
                    int eq = 0;
                    for (int i = 0; i < keys.length; i++) {
                        if (equals.test(rcd.get(keys[i]), d.get(i))) {
                            eq++;
                        }
                    }
                    if (eq == keys.length) {
                        it.remove();
                        has = true;
                        break;
                    }
                }

                if (has) {
                    upd.add(rcd);
                }
                else {
                    ins.add(rcd);
                }
            }

            if (!ins.isEmpty()) {
                int[] r = batchInsert(ins, size);
                System.arraycopy(r, 0, result, off, r.length);
                off += r.length;
            }

            if (!upd.isEmpty()) {
                int[] r = batchUpdate(table, keys, upd, size, updateFields);
                System.arraycopy(r, 0, result, off, r.length);
                off += r.length;
            }
        }

        return result;
    }

    /**
     * Batch insert records, use mysql's `insert ignore` or `replace into`.
     * Note that jooq mergeInto is not perfect, requires both to have values, while `replace` does not.
     *
     * @param records         collection of record
     * @param size            batch size, &lt;=0 mean no batching
     * @param ignoreOrReplace ignore or replace if Duplicate Key
     * @return array of affected records, can use ModifyAssert to check
     * @see DSLContext#mergeInto(Table)
     */
    @SuppressWarnings({"unchecked", "ResultOfMethodCallIgnored"})
    public int @NotNull [] batchInsert(Collection<R> records, int size, boolean ignoreOrReplace) {
        if (records == null || records.isEmpty()) return Null.Ints;

        BiFunction<DSLContext, Collection<R>, int[]> batchIgnoreExec = (dsl, rs) -> {
            Field<Object>[] fields = (Field<Object>[]) table.fields();
            if (ignoreOrReplace) {
                // insert ignore

                final InsertReturningStep<R> step = dsl
                        .insertInto(table)
                        .columns(fields)
                        .values(new Object[fields.length])
                        .onDuplicateKeyIgnore();

                BatchBindStep batch = dsl.batch(step);
                for (R r : rs) {
                    batch.bind(r.intoArray());
                }
                return batch.execute();
            }
            else {
                final InsertOnDuplicateSetStep<R> step = dsl
                        .insertInto(table)
                        .columns(fields)
                        .values(new Object[fields.length])
                        .onDuplicateKeyUpdate();

                InsertOnDuplicateSetMoreStep<R> set = null;
                for (Field<Object> fld : fields) {
                    set = step.set(fld, (Object) null);
                }

                BatchBindStep batch = dsl.batch(set);
                for (R r : rs) {
                    final Object[] ay = r.intoArray();
                    final int len = ay.length;
                    Object[] vl = new Object[len * 2];
                    System.arraycopy(ay, 0, vl, 0, len);
                    System.arraycopy(ay, 0, vl, len, len);
                    batch.bind(vl);
                }
                return batch.execute();
            }
        };

        return batchExecute(records, size, batchIgnoreExec);
    }

    /**
     * Batch insert records
     *
     * @param records collection of record
     * @param size    batch size, &lt;=0 mean no batching
     * @return array of affected records, can use ModifyAssert to check
     * @see DSLContext#batchInsert(TableRecord[])
     */
    public int @NotNull [] batchInsert(Collection<R> records, int size) {
        return batchExecute(records, size, batchInsertExec);
    }

    private final BiFunction<DSLContext, Collection<R>, int[]> batchInsertExec = (dsl, rs) -> dsl.batchInsert(rs).execute();

    /**
     * Batch store (insert/update) record.
     *
     * @param records collection of record
     * @param size    batch size, &lt;=0 mean no batching
     * @return array of affected records, can use ModifyAssert to check
     * @see DSLContext#batchStore(UpdatableRecord[])
     */
    public int @NotNull [] batchStore(Collection<R> records, int size) {
        return batchExecute(records, size, batchStoreExec);
    }

    private final BiFunction<DSLContext, Collection<R>, int[]> batchStoreExec = (dsl, rs) -> dsl.batchStore(rs).execute();

    /**
     * Batch update record.
     *
     * @param table        table with the same name as updateFields
     * @param whereFields  where condition fields
     * @param records      collection of record
     * @param size         batch size, &lt;=0 mean no batching
     * @param updateFields fields to update
     * @return array of affected records, can use ModifyAssert to check
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public int @NotNull [] batchUpdate(T table, Field<?>[] whereFields, Collection<R> records, int size, Field<?>... updateFields) {
        if (records == null || records.isEmpty()) return Null.Ints;

        BiFunction<DSLContext, Collection<R>, int[]> batchMergeExec = (dsl, rs) -> {
            Map<Field<?>, Object> map = new LinkedHashMap<>();
            for (Field<?> uf : updateFields) {
                map.put(uf, null);
            }
            Condition where = null;
            for (Field wf : whereFields) {
                if (where == null) {
                    where = wf.eq((Object) null);
                }
                else {
                    where = where.and(wf.eq((Object) null));
                }
            }

            BatchBindStep batch = dsl.batch(
                    dsl.update(table)
                       .set(map)
                       .where(where)
            );

            for (R r : rs) {
                Object[] vals = new Object[whereFields.length + updateFields.length];
                int off = 0;
                for (Field<?> uf : updateFields) {
                    vals[off++] = r.get(uf);
                }
                for (Field<?> uf : whereFields) {
                    vals[off++] = r.get(uf);
                }
                batch = batch.bind(vals);
            }

            return batch.execute();
        };

        return batchExecute(records, size, batchMergeExec);
    }

    /**
     * Batch update record.
     *
     * @param records collection of record
     * @param size    batch size, &lt;=0 mean no batching
     * @return array of affected records, can use ModifyAssert to check
     * @see DSLContext#batchUpdate(UpdatableRecord[])
     */

    public int @NotNull [] batchUpdate(Collection<R> records, int size) {
        return batchExecute(records, size, batchUpdateExec);
    }

    private final BiFunction<DSLContext, Collection<R>, int[]> batchUpdateExec = (dsl, rs) -> dsl.batchUpdate(rs).execute();

    //
    public int @NotNull [] batchExecute(Collection<R> records, int size, BiFunction<DSLContext, Collection<R>, int[]> exec) {
        if (records == null || records.isEmpty()) return Null.Ints;

        DSLContext dsl = ctx();
        if (size <= 0 || records.size() <= size) {
            return exec.apply(dsl, records);
        }

        int[] rst = new int[records.size()];
        int off = 0;
        List<R> rds;
        if (records instanceof List) {
            rds = (List<R>) records;
        }
        else {
            rds = new ArrayList<>(records);
        }
        for (List<R> pt : partition(rds, size)) {
            int[] rt = exec.apply(dsl, pt);
            System.arraycopy(rt, 0, rst, off, rt.length);
            off += rt.length;
        }
        return rst;
    }

    private List<List<R>> partition(Collection<R> records, int size) {
        List<R> rds;
        if (records instanceof List) {
            rds = (List<R>) records;
        }
        else {
            rds = new ArrayList<>(records);
        }
        return Lists.partition(rds, size);
    }

    ///////////////// select list /////////////////////

    @NotNull
    public List<P> fetch(Function<T, Condition> fun) {
        final Condition cond = fun.apply(table);
        return fetch(table, cond);
    }

    @NotNull
    public List<P> fetch(int limit, Function<T, Condition> fun) {
        return fetch(0, limit, fun);
    }

    @NotNull
    public List<P> fetch(int offset, int limit, Function<T, Condition> fun) {
        final Condition cond = fun.apply(table);
        return fetch(table, offset, limit, cond);
    }

    @NotNull
    public List<P> fetch(BiConsumer<T, SelectWhereOrder> fun) {
        final SelectWhereOrder soc = new SelectWhereOrder();
        fun.accept(table, soc);
        return fetch(table, soc.getWhere(), soc.getParts());
    }

    @NotNull
    public List<P> fetch(int limit, BiConsumer<T, SelectWhereOrder> fun) {
        return fetch(0, limit, fun);
    }

    @NotNull
    public List<P> fetch(int offset, int limit, BiConsumer<T, SelectWhereOrder> fun) {
        final SelectWhereOrder soc = new SelectWhereOrder();
        fun.accept(table, soc);
        return fetch(table, offset, limit, soc.getWhere(), soc.getParts());
    }

    @NotNull
    public <E> List<E> fetch(Class<E> claz, BiConsumer<T, SelectWhereOrder> fun) {
        final SelectWhereOrder soc = new SelectWhereOrder();
        fun.accept(table, soc);
        return fetch(claz, table, soc.getWhere(), soc.getParts());
    }

    @NotNull
    public <E> List<E> fetch(Class<E> claz, int limit, BiConsumer<T, SelectWhereOrder> fun) {
        return fetch(claz, 0, limit, fun);
    }

    @NotNull
    public <E> List<E> fetch(Class<E> claz, int offset, int limit, BiConsumer<T, SelectWhereOrder> fun) {
        final SelectWhereOrder soc = new SelectWhereOrder();
        fun.accept(table, soc);
        return fetch(claz, offset, limit, table, soc.getWhere(), soc.getParts());
    }

    @NotNull
    public <E> List<E> fetch(RecordMapper<? super Record, E> mapper, BiConsumer<T, SelectWhereOrder> fun) {
        final SelectWhereOrder soc = new SelectWhereOrder();
        fun.accept(table, soc);
        return fetch(mapper, table, soc.getWhere(), soc.getParts());
    }

    @NotNull
    public <E> List<E> fetch(RecordMapper<? super Record, E> mapper, int limit, BiConsumer<T, SelectWhereOrder> fun) {
        return fetch(mapper, 0, limit, fun);
    }

    @NotNull
    public <E> List<E> fetch(RecordMapper<? super Record, E> mapper, int offset, int limit, BiConsumer<T, SelectWhereOrder> fun) {
        final SelectWhereOrder soc = new SelectWhereOrder();
        fun.accept(table, soc);
        return fetch(mapper, offset, limit, table, soc.getWhere(), soc.getParts());
    }

    ////////
    @NotNull
    public List<P> fetch(T table, Condition cond) {
        return fetch(table, -1, -1, cond, Collections.emptyList(), Collections.emptyList());
    }

    @NotNull
    public List<P> fetch(T table, Condition cond, QueryPart... selectsOrders) {
        return fetch(table, -1, -1, cond, selectsOrders);
    }

    @NotNull
    public List<P> fetch(T table, Condition cond, Collection<? extends QueryPart> selectsOrders) {
        return fetch(table, -1, -1, cond, selectsOrders);
    }

    @NotNull
    public List<P> fetch(T table, int limit, QueryPart... selectsOrders) {
        return fetch(table, 0, limit, null, selectsOrders);
    }

    @NotNull
    public List<P> fetch(T table, int limit, Collection<? extends QueryPart> selectsOrders) {
        return fetch(table, 0, limit, null, selectsOrders);
    }

    @NotNull
    public List<P> fetch(T table, int offset, int limit, QueryPart... selectsOrders) {
        return fetch(table, offset, limit, null, selectsOrders);
    }

    @NotNull
    public List<P> fetch(T table, int offset, int limit, Collection<? extends QueryPart> selectsOrders) {
        return fetch(table, offset, limit, null, selectsOrders);
    }

    @NotNull
    public List<P> fetch(T table, int limit, Condition cond, QueryPart... selectsOrders) {
        return fetch(table, 0, limit, cond, selectsOrders);
    }

    @NotNull
    public List<P> fetch(T table, int limit, Condition cond, Collection<? extends QueryPart> selectsOrders) {
        return fetch(table, 0, limit, cond, selectsOrders);
    }

    @NotNull
    public List<P> fetch(T table, int offset, int limit, Condition cond, QueryPart... selectsOrders) {
        return fetch(getType(), offset, limit, table, cond, selectsOrders);
    }

    @NotNull
    public List<P> fetch(T table, int offset, int limit, Condition cond, Collection<? extends QueryPart> selectsOrders) {
        return fetch(getType(), offset, limit, table, cond, selectsOrders);
    }

    @NotNull
    public List<P> fetch(T table, int offset, int limit, Condition cond, Collection<? extends SelectFieldOrAsterisk> selects, Collection<? extends OrderField<?>> orderBy) {
        return fetch(getType(), offset, limit, table, cond, selects, orderBy);
    }

    ////////
    @NotNull
    public <E> List<E> fetch(Class<E> claz, T table, QueryPart... selectsOrders) {
        return fetch(claz, table, null, selectsOrders);
    }

    @NotNull
    public <E> List<E> fetch(Class<E> claz, T table, Collection<? extends QueryPart> selectsOrders) {
        return fetch(claz, table, null, selectsOrders);
    }

    @NotNull
    public <E> List<E> fetch(Class<E> claz, T table, Condition cond, QueryPart... selectsOrders) {
        return fetch(claz, -1, -1, table, cond, selectsOrders);
    }

    @NotNull
    public <E> List<E> fetch(Class<E> claz, T table, Condition cond, Collection<? extends QueryPart> selectsOrders) {
        return fetch(claz, -1, -1, table, cond, selectsOrders);
    }

    @NotNull
    public <E> List<E> fetch(Class<E> claz, int limit, T table, QueryPart... selectsOrders) {
        return fetch(claz, 0, limit, table, null, selectsOrders);
    }

    @NotNull
    public <E> List<E> fetch(Class<E> claz, int limit, T table, Collection<? extends QueryPart> selectsOrders) {
        return fetch(claz, 0, limit, table, null, selectsOrders);
    }

    @NotNull
    public <E> List<E> fetch(Class<E> claz, int offset, int limit, T table, QueryPart... selectsOrders) {
        return fetch(claz, offset, limit, table, null, selectsOrders);
    }

    @NotNull
    public <E> List<E> fetch(Class<E> claz, int offset, int limit, T table, Collection<? extends QueryPart> selectsOrders) {
        return fetch(claz, offset, limit, table, null, selectsOrders);
    }

    @NotNull
    public <E> List<E> fetch(Class<E> claz, int limit, T table, Condition cond, QueryPart... selectsOrders) {
        return fetch(claz, 0, limit, table, cond, selectsOrders);
    }

    @NotNull
    public <E> List<E> fetch(Class<E> claz, int limit, T table, Condition cond, Collection<? extends QueryPart> selectsOrders) {
        return fetch(claz, 0, limit, table, cond, selectsOrders);
    }

    @NotNull
    public <E> List<E> fetch(Class<E> claz, int offset, int limit, T table, Condition cond, QueryPart... selectsOrders) {
        final var two = selectAndOrders(List.of(selectsOrders));
        return fetch(claz, offset, limit, table, cond, two.one(), two.two());
    }

    @NotNull
    public <E> List<E> fetch(Class<E> claz, int offset, int limit, T table, Condition cond, Collection<? extends QueryPart> selectsOrders) {
        final var two = selectAndOrders(selectsOrders);
        return fetch(claz, offset, limit, table, cond, two.one(), two.two());
    }

    @NotNull
    public <E> List<E> fetch(Class<E> claz, int offset, int limit, T table, Condition cond, Collection<? extends SelectFieldOrAsterisk> selects, Collection<? extends OrderField<?>> orderBy) {
        final SelectConditionStep<R> where = selectWhere(table, cond, selects);

        if (offset < 0 || limit < 0) {
            if (orderBy == null || orderBy.isEmpty()) {
                return where.fetch().into(claz);
            }
            else {
                return where.orderBy(orderBy).fetch().into(claz);
            }
        }
        else {
            if (orderBy == null || orderBy.isEmpty()) {
                return where.limit(offset, limit).fetch().into(claz);
            }
            else {
                return where.orderBy(orderBy).limit(offset, limit).fetch().into(claz);
            }
        }
    }

    ////////
    @NotNull
    public <E> List<E> fetch(RecordMapper<? super Record, E> mapper, T table, QueryPart... selectsOrders) {
        return fetch(mapper, table, null, selectsOrders);
    }

    @NotNull
    public <E> List<E> fetch(RecordMapper<? super Record, E> mapper, T table, Collection<? extends QueryPart> selectsOrders) {
        return fetch(mapper, table, null, selectsOrders);
    }

    @NotNull
    public <E> List<E> fetch(RecordMapper<? super Record, E> mapper, T table, Condition cond, QueryPart... selectsOrders) {
        return fetch(mapper, -1, -1, table, cond, selectsOrders);
    }

    @NotNull
    public <E> List<E> fetch(RecordMapper<? super Record, E> mapper, T table, Condition cond, Collection<? extends QueryPart> selectsOrders) {
        return fetch(mapper, -1, -1, table, cond, selectsOrders);
    }

    @NotNull
    public <E> List<E> fetch(RecordMapper<? super Record, E> mapper, int limit, T table, QueryPart... selectsOrders) {
        return fetch(mapper, 0, limit, table, null, selectsOrders);
    }

    @NotNull
    public <E> List<E> fetch(RecordMapper<? super Record, E> mapper, int limit, T table, Collection<? extends QueryPart> selectsOrders) {
        return fetch(mapper, 0, limit, table, null, selectsOrders);
    }

    @NotNull
    public <E> List<E> fetch(RecordMapper<? super Record, E> mapper, int offset, int limit, T table, QueryPart... selectsOrders) {
        return fetch(mapper, offset, limit, table, null, selectsOrders);
    }

    @NotNull
    public <E> List<E> fetch(RecordMapper<? super Record, E> mapper, int offset, int limit, T table, Collection<? extends QueryPart> selectsOrders) {
        return fetch(mapper, offset, limit, table, null, selectsOrders);
    }

    @NotNull
    public <E> List<E> fetch(RecordMapper<? super Record, E> mapper, int limit, T table, Condition cond, QueryPart... selectsOrders) {
        return fetch(mapper, 0, limit, table, cond, selectsOrders);
    }

    @NotNull
    public <E> List<E> fetch(RecordMapper<? super Record, E> mapper, int limit, T table, Condition cond, Collection<? extends QueryPart> selectsOrders) {
        return fetch(mapper, 0, limit, table, cond, selectsOrders);
    }

    @NotNull
    public <E> List<E> fetch(RecordMapper<? super Record, E> mapper, int offset, int limit, T table, Condition cond, QueryPart... selectsOrders) {
        return fetch(mapper, offset, limit, table, cond, List.of(selectsOrders));
    }

    @NotNull
    public <E> List<E> fetch(RecordMapper<? super Record, E> mapper, int offset, int limit, T table, Condition cond, Collection<? extends QueryPart> selectsOrders) {
        final var two = selectAndOrders(selectsOrders);
        return fetch(mapper, offset, limit, table, cond, two.one(), two.two());
    }

    @NotNull
    public <E> List<E> fetch(RecordMapper<? super Record, E> mapper, int offset, int limit, T table, Condition cond, Collection<? extends SelectFieldOrAsterisk> selects, Collection<? extends OrderField<?>> orderBy) {
        final SelectConditionStep<R> where = selectWhere(table, cond, selects);
        if (offset < 0 || limit < 0) {
            if (orderBy == null || orderBy.isEmpty()) {
                return where.fetch().map(mapper);
            }
            else {
                return where.orderBy(orderBy).fetch().map(mapper);
            }
        }
        else {
            if (orderBy == null || orderBy.isEmpty()) {
                return where.limit(offset, limit).fetch().map(mapper);
            }
            else {
                return where.orderBy(orderBy).limit(offset, limit).fetch().map(mapper);
            }
        }
    }

    ///////////////// select one /////////////////////
    @Nullable
    public P fetchOne(Function<T, Condition> fun) {
        final Condition cond = fun.apply(table);
        return fetchOne(table, cond);
    }

    @Nullable
    public P fetchLimitOne(Function<T, Condition> fun) {
        final Condition cond = fun.apply(table);
        return fetchLimitOne(table, cond);
    }

    @NotNull
    public Optional<P> fetchOptional(Function<T, Condition> fun) {
        return Optional.ofNullable(fetchOne(fun));
    }

    @NotNull
    public Optional<P> fetchLimitOptional(Function<T, Condition> fun) {
        return Optional.ofNullable(fetchLimitOne(fun));
    }

    @Nullable
    public P fetchOne(BiConsumer<T, SelectWhereOrder> fun) {
        final SelectWhereOrder soc = new SelectWhereOrder();
        fun.accept(table, soc);
        return fetchOne(table, soc.getWhere(), soc.getParts());
    }

    @Nullable
    public P fetchLimitOne(BiConsumer<T, SelectWhereOrder> fun) {
        final SelectWhereOrder soc = new SelectWhereOrder();
        fun.accept(table, soc);
        return fetchLimitOne(table, soc.getWhere(), soc.getParts());
    }

    @NotNull
    public Optional<P> fetchOptional(BiConsumer<T, SelectWhereOrder> fun) {
        return Optional.ofNullable(fetchOne(fun));
    }

    @NotNull
    public Optional<P> fetchLimitOptional(BiConsumer<T, SelectWhereOrder> fun) {
        return Optional.ofNullable(fetchLimitOne(fun));
    }

    @Nullable
    public <E> E fetchOne(Class<E> claz, BiConsumer<T, SelectWhereOrder> fun) {
        final SelectWhereOrder soc = new SelectWhereOrder();
        fun.accept(table, soc);
        return fetchOne(claz, table, soc.getWhere(), soc.getParts());
    }

    @Nullable
    public <E> E fetchLimitOne(Class<E> claz, BiConsumer<T, SelectWhereOrder> fun) {
        final SelectWhereOrder soc = new SelectWhereOrder();
        fun.accept(table, soc);
        return fetchLimitOne(claz, table, soc.getWhere(), soc.getParts());
    }

    @NotNull
    public <E> Optional<E> fetchOptional(Class<E> claz, BiConsumer<T, SelectWhereOrder> fun) {
        return Optional.ofNullable(fetchOne(claz, fun));
    }

    @NotNull
    public <E> Optional<E> fetchLimitOptional(Class<E> claz, BiConsumer<T, SelectWhereOrder> fun) {
        return Optional.ofNullable(fetchLimitOne(claz, fun));
    }

    @Nullable
    public <E> E fetchOne(RecordMapper<? super Record, E> mapper, BiConsumer<T, SelectWhereOrder> fun) {
        final SelectWhereOrder soc = new SelectWhereOrder();
        fun.accept(table, soc);
        return fetchOne(mapper, table, soc.getWhere(), soc.getParts());
    }

    @Nullable
    public <E> E fetchLimitOne(RecordMapper<? super Record, E> mapper, BiConsumer<T, SelectWhereOrder> fun) {
        final SelectWhereOrder soc = new SelectWhereOrder();
        fun.accept(table, soc);
        return fetchLimitOne(mapper, table, soc.getWhere(), soc.getParts());
    }

    @NotNull
    public <E> Optional<E> fetchOptional(RecordMapper<? super Record, E> mapper, BiConsumer<T, SelectWhereOrder> fun) {
        return Optional.ofNullable(fetchOne(mapper, fun));
    }

    @NotNull
    public <E> Optional<E> fetchLimitOptional(RecordMapper<? super Record, E> mapper, BiConsumer<T, SelectWhereOrder> fun) {
        return Optional.ofNullable(fetchLimitOne(mapper, fun));
    }


    /////////////////
    @Nullable
    public P fetchOne(T table, QueryPart... selectsOrders) {
        return fetchOne(table, null, selectsOrders);
    }

    @Nullable
    public P fetchOne(T table, Collection<? extends QueryPart> selectsOrders) {
        return fetchOne(table, null, selectsOrders);
    }

    @Nullable
    public P fetchLimitOne(T table, QueryPart... selectsOrders) {
        return fetchLimitOne(table, null, selectsOrders);
    }

    @Nullable
    public P fetchLimitOne(T table, Collection<? extends QueryPart> selectsOrders) {
        return fetchLimitOne(table, null, selectsOrders);
    }

    @NotNull
    public Optional<P> fetchOptional(T table, QueryPart... selectsOrders) {
        return Optional.ofNullable(fetchOne(table, null, selectsOrders));
    }

    @NotNull
    public Optional<P> fetchOptional(T table, Collection<? extends QueryPart> selectsOrders) {
        return Optional.ofNullable(fetchOne(table, null, selectsOrders));
    }

    @NotNull
    public Optional<P> fetchLimitOptional(T table, QueryPart... selectsOrders) {
        return Optional.ofNullable(fetchLimitOne(table, null, selectsOrders));
    }

    @NotNull
    public Optional<P> fetchLimitOptional(T table, Collection<? extends QueryPart> selectsOrders) {
        return Optional.ofNullable(fetchLimitOne(table, null, selectsOrders));
    }

    public P fetchOne(T table, Condition cond, QueryPart... selectsOrders) {
        final var two = selectAndOrders(List.of(selectsOrders));
        return fetchOne(table, cond, two.one(), two.two(), false);
    }

    public P fetchOne(T table, Condition cond, Collection<? extends QueryPart> selectsOrders) {
        final var two = selectAndOrders(selectsOrders);
        return fetchOne(table, cond, two.one(), two.two(), false);
    }

    @Nullable
    public P fetchLimitOne(T table, Condition cond, QueryPart... selectsOrders) {
        final var two = selectAndOrders(List.of(selectsOrders));
        return fetchOne(table, cond, two.one(), two.two(), true);
    }

    @Nullable
    public P fetchLimitOne(T table, Condition cond, Collection<? extends QueryPart> selectsOrders) {
        final var two = selectAndOrders(selectsOrders);
        return fetchOne(table, cond, two.one(), two.two(), true);
    }

    @NotNull
    public Optional<P> fetchOptional(T table, Condition cond, QueryPart... selectsOrders) {
        return Optional.ofNullable(fetchOne(table, cond, selectsOrders));
    }

    @NotNull
    public Optional<P> fetchOptional(T table, Condition cond, Collection<? extends QueryPart> selectsOrders) {
        return Optional.ofNullable(fetchOne(table, cond, selectsOrders));
    }

    @NotNull
    public Optional<P> fetchLimitOptional(T table, Condition cond, QueryPart... selectsOrders) {
        return Optional.ofNullable(fetchLimitOne(table, cond, selectsOrders));
    }

    @NotNull
    public Optional<P> fetchLimitOptional(T table, Condition cond, Collection<? extends QueryPart> selectsOrders) {
        return Optional.ofNullable(fetchLimitOne(table, cond, selectsOrders));
    }

    @Nullable
    public P fetchOne(T table, Condition cond, Collection<? extends SelectFieldOrAsterisk> selects, Collection<? extends OrderField<?>> orderBy, boolean limit) {
        return fetchOne(getType(), table, cond, selects, orderBy, limit);
    }

    /////////////////
    @Nullable
    public <E> E fetchOne(Class<E> claz, T table, QueryPart... selectsOrders) {
        return fetchOne(claz, table, null, selectsOrders);
    }

    @Nullable
    public <E> E fetchOne(Class<E> claz, T table, Collection<? extends QueryPart> selectsOrders) {
        return fetchOne(claz, table, null, selectsOrders);
    }

    @Nullable
    public <E> E fetchLimitOne(Class<E> claz, T table, QueryPart... selectsOrders) {
        return fetchLimitOne(claz, table, null, selectsOrders);
    }

    @Nullable
    public <E> E fetchLimitOne(Class<E> claz, T table, Collection<? extends QueryPart> selectsOrders) {
        return fetchLimitOne(claz, table, null, selectsOrders);
    }

    @NotNull
    public <E> Optional<E> fetchOptional(Class<E> claz, T table, QueryPart... selectsOrders) {
        return Optional.ofNullable(fetchOne(claz, table, null, selectsOrders));
    }

    @NotNull
    public <E> Optional<E> fetchOptional(Class<E> claz, T table, Collection<? extends QueryPart> selectsOrders) {
        return Optional.ofNullable(fetchOne(claz, table, null, selectsOrders));
    }

    @NotNull
    public <E> Optional<E> fetchLimitOptional(Class<E> claz, T table, QueryPart... selectsOrders) {
        return Optional.ofNullable(fetchLimitOne(claz, table, null, selectsOrders));
    }

    @NotNull
    public <E> Optional<E> fetchLimitOptional(Class<E> claz, T table, Collection<? extends QueryPart> selectsOrders) {
        return Optional.ofNullable(fetchLimitOne(claz, table, null, selectsOrders));
    }

    public <E> E fetchOne(Class<E> claz, T table, Condition cond, QueryPart... selectsOrders) {
        final var two = selectAndOrders(List.of(selectsOrders));
        return fetchOne(claz, table, cond, two.one(), two.two(), false);
    }

    public <E> E fetchOne(Class<E> claz, T table, Condition cond, Collection<? extends QueryPart> selectsOrders) {
        final var two = selectAndOrders(selectsOrders);
        return fetchOne(claz, table, cond, two.one(), two.two(), false);
    }

    @Nullable
    public <E> E fetchLimitOne(Class<E> claz, T table, Condition cond, QueryPart... selectsOrders) {
        final var two = selectAndOrders(List.of(selectsOrders));
        return fetchOne(claz, table, cond, two.one(), two.two(), true);
    }

    @Nullable
    public <E> E fetchLimitOne(Class<E> claz, T table, Condition cond, Collection<? extends QueryPart> selectsOrders) {
        final var two = selectAndOrders(selectsOrders);
        return fetchOne(claz, table, cond, two.one(), two.two(), true);
    }

    @NotNull
    public <E> Optional<E> fetchOptional(Class<E> claz, T table, Condition cond, QueryPart... selectsOrders) {
        return Optional.ofNullable(fetchOne(claz, table, cond, selectsOrders));
    }

    @NotNull
    public <E> Optional<E> fetchOptional(Class<E> claz, T table, Condition cond, Collection<? extends QueryPart> selectsOrders) {
        return Optional.ofNullable(fetchOne(claz, table, cond, selectsOrders));
    }

    @NotNull
    public <E> Optional<E> fetchLimitOptional(Class<E> claz, T table, Condition cond, QueryPart... selectsOrders) {
        return Optional.ofNullable(fetchLimitOne(claz, table, cond, selectsOrders));
    }

    @NotNull
    public <E> Optional<E> fetchLimitOptional(Class<E> claz, T table, Condition cond, Collection<? extends QueryPart> selectsOrders) {
        return Optional.ofNullable(fetchLimitOne(claz, table, cond, selectsOrders));
    }

    @Nullable
    public <E> E fetchOne(Class<E> claz, T table, Condition cond, Collection<? extends SelectFieldOrAsterisk> selects, Collection<? extends OrderField<?>> orderBy, boolean limit) {
        final SelectConditionStep<R> where = selectWhere(table, cond, selects);
        if (limit) {
            if (orderBy == null || orderBy.isEmpty()) {
                return where.fetchOneInto(claz);
            }
            else {
                return where.orderBy(orderBy).fetchOneInto(claz);
            }
        }
        else {
            if (orderBy == null || orderBy.isEmpty()) {
                return where.limit(1).fetchOneInto(claz);
            }
            else {
                return where.orderBy(orderBy).limit(1).fetchOneInto(claz);
            }
        }
    }

    /////////////////
    @Nullable
    public <E> E fetchOne(RecordMapper<? super Record, E> mapper, T table, QueryPart... selectsOrders) {
        return fetchOne(mapper, table, null, selectsOrders);
    }

    @Nullable
    public <E> E fetchOne(RecordMapper<? super Record, E> mapper, T table, Collection<? extends QueryPart> selectsOrders) {
        return fetchOne(mapper, table, null, selectsOrders);
    }

    @Nullable
    public <E> E fetchLimitOne(RecordMapper<? super Record, E> mapper, T table, QueryPart... selectsOrders) {
        return fetchLimitOne(mapper, table, null, selectsOrders);
    }

    @Nullable
    public <E> E fetchLimitOne(RecordMapper<? super Record, E> mapper, T table, Collection<? extends QueryPart> selectsOrders) {
        return fetchLimitOne(mapper, table, null, selectsOrders);
    }

    @NotNull
    public <E> Optional<E> fetchOptional(RecordMapper<? super Record, E> mapper, T table, QueryPart... selectsOrders) {
        return Optional.ofNullable(fetchOne(mapper, table, null, selectsOrders));
    }

    @NotNull
    public <E> Optional<E> fetchOptional(RecordMapper<? super Record, E> mapper, T table, Collection<? extends QueryPart> selectsOrders) {
        return Optional.ofNullable(fetchOne(mapper, table, null, selectsOrders));
    }

    @NotNull
    public <E> Optional<E> fetchLimitOptional(RecordMapper<? super Record, E> mapper, T table, QueryPart... selectsOrders) {
        return Optional.ofNullable(fetchLimitOne(mapper, table, null, selectsOrders));
    }

    @NotNull
    public <E> Optional<E> fetchLimitOptional(RecordMapper<? super Record, E> mapper, T table, Collection<? extends QueryPart> selectsOrders) {
        return Optional.ofNullable(fetchLimitOne(mapper, table, null, selectsOrders));
    }

    public <E> E fetchOne(RecordMapper<? super Record, E> mapper, T table, Condition cond, QueryPart... selectsOrders) {
        final var two = selectAndOrders(List.of(selectsOrders));
        return fetchOne(mapper, table, cond, two.one(), two.two(), false);
    }

    public <E> E fetchOne(RecordMapper<? super Record, E> mapper, T table, Condition cond, Collection<? extends QueryPart> selectsOrders) {
        final var two = selectAndOrders(selectsOrders);
        return fetchOne(mapper, table, cond, two.one(), two.two(), false);
    }

    @Nullable
    public <E> E fetchLimitOne(RecordMapper<? super Record, E> mapper, T table, Condition cond, QueryPart... selectsOrders) {
        final var two = selectAndOrders(List.of(selectsOrders));
        return fetchOne(mapper, table, cond, two.one(), two.two(), true);
    }

    @Nullable
    public <E> E fetchLimitOne(RecordMapper<? super Record, E> mapper, T table, Condition cond, Collection<? extends QueryPart> selectsOrders) {
        final var two = selectAndOrders(selectsOrders);
        return fetchOne(mapper, table, cond, two.one(), two.two(), true);
    }

    @NotNull
    public <E> Optional<E> fetchOptional(RecordMapper<? super Record, E> mapper, T table, Condition cond, QueryPart... selectsOrders) {
        return Optional.ofNullable(fetchOne(mapper, table, cond, selectsOrders));
    }

    @NotNull
    public <E> Optional<E> fetchOptional(RecordMapper<? super Record, E> mapper, T table, Condition cond, Collection<? extends QueryPart> selectsOrders) {
        return Optional.ofNullable(fetchOne(mapper, table, cond, selectsOrders));
    }

    @NotNull
    public <E> Optional<E> fetchLimitOptional(RecordMapper<? super Record, E> mapper, T table, Condition cond, QueryPart... selectsOrders) {
        return Optional.ofNullable(fetchLimitOne(mapper, table, cond, selectsOrders));
    }

    @NotNull
    public <E> Optional<E> fetchLimitOptional(RecordMapper<? super Record, E> mapper, T table, Condition cond, Collection<? extends QueryPart> selectsOrders) {
        return Optional.ofNullable(fetchLimitOne(mapper, table, cond, selectsOrders));
    }

    @Nullable
    public <E> E fetchOne(RecordMapper<? super Record, E> mapper, T table, Condition cond, Collection<? extends SelectFieldOrAsterisk> selects, Collection<? extends OrderField<?>> orderBy, boolean limit) {
        final SelectConditionStep<R> where = selectWhere(table, cond, selects);
        if (limit) {
            if (orderBy == null || orderBy.isEmpty()) {
                return where.fetchOne(mapper);
            }
            else {
                return where.orderBy(orderBy).fetchOne(mapper);
            }
        }
        else {
            if (orderBy == null || orderBy.isEmpty()) {
                return where.limit(1).fetchOne(mapper);
            }
            else {
                return where.orderBy(orderBy).limit(1).fetchOne(mapper);
            }
        }
    }

    ///////////////// delete /////////////////////

    public int delete(Function<T, Condition> fun) {
        return delete(table, fun.apply(table));
    }

    /**
     * Delete by condition
     *
     * @param table the table
     * @param cond  where condition
     * @return affected records
     */
    public int delete(T table, Condition cond) {
        return ctx().delete(table)
                    .where(cond)
                    .execute();
    }

    /**
     * Delete a record and get the Diff
     */
    @NotNull
    public JournalDiff diffDelete(T table, Condition cond) {
        final JournalDiff diff = new JournalDiff();
        diff.setTable(table.getName());
        diff.setTyped(true);

        final DSLContext dsl = ctx();
        Result<R> rs1 = dsl.selectFrom(table)
                           .where(cond)
                           .fetch();

        final int size = rs1.size();
        if (size == 0) {
            return diff;
        }

        int rc = dsl.delete(table)
                    .where(cond)
                    .execute();

        AssertState.aEqb(rc, size, "delete mismatched records. cond={}", cond);
        JournalDiffHelper.helpDelete(diff, rs1);
        return diff;
    }

    ///////////////// update /////////////////////

    /**
     * Update a record and get the Diff
     */
    @NotNull
    public JournalDiff diffUpdate(T table, Map<Field<?>, ?> setter, Condition cond) {
        final JournalDiff diff = new JournalDiff();
        diff.setTable(table.getName());
        diff.setTyped(true);

        final DSLContext dsl = ctx();
        final Field<?>[] fields = setter.keySet().toArray(Field<?>[]::new);

        final SelectConditionStep<Record> select = dsl.select(fields)
                                                      .from(table)
                                                      .where(cond);
        final Result<Record> rs1 = select.fetch();
        final int size = rs1.size();

        if (size == 0) return diff;

        int rc = dsl.update(table)
                    .set(setter)
                    .where(cond)
                    .execute();

        AssertState.aEqb(rc, size, "update mismatched records. cond={}", cond);
        final Result<Record> rs2 = select.fetch();

        JournalDiffHelper.helpUpdate(diff, rs1, rs2);
        return diff;
    }

    /**
     * @see #update(Table, Map, Condition, boolean)
     */
    public int update(T table, Map<?, ?> setter, Condition cond) {
        return update(table, setter, cond, false);
    }

    /**
     * <pre>
     * Keys can either be of type String, Name, or Field.
     * Values can either be of type <T> or Field<T>.
     *
     * val t = dao.tableForWriter
     * val setter = hashMapOf<Any, Any>()
     * setter.put(t.Id, 1L)
     * setter.put(t.CommitId, t.Id)
     * val ui = dao.update(setter, t.Id.eq(2L))
     * </pre>
     *
     * @param table    table with the same name as condition/setter
     * @param setter   update key and value
     * @param cond     condition
     * @param skipNull whether skip `null` values, true requires map to be editable.
     * @return affected records
     * @see org.jooq.UpdateSetStep#set(Map)
     */
    public int update(T table, Map<?, ?> setter, Condition cond, boolean skipNull) {
        if (skipNull) {
            setter.entrySet().removeIf(it -> it.getValue() == null);
        }

        if (setter.isEmpty()) return 0;

        return ctx().update(table)
                    .set(setter)
                    .where(cond)
                    .execute();
    }

    /**
     * @see #update(Table, Object, Condition, boolean)
     */
    public int update(T table, P pojo, Condition cond) {
        return update(table, pojo, cond, false);
    }

    /**
     * Update record by pojo key and value, skip null.
     *
     * @param table table with the same name as condition
     * @param pojo  pojo
     * @param cond  condition
     * @return affected records
     */
    public int update(T table, P pojo, Condition cond, boolean skipNull) {
        DSLContext dsl = ctx();
        R record = dsl.newRecord(table, pojo);

        Map<Field<?>, Object> setter = new LinkedHashMap<>();
        int size = record.size();
        for (int i = 0; i < size; i++) {
            if (record.get(i) != null) {
                setter.put(record.field(i), record.get(i));
            }
        }

        return update(table, setter, cond, skipNull);
    }

    /**
     * Update record by pojo key and value, by PK
     *
     * @param pojo     pojo
     * @param skipNull whether skip `null` values
     * @return affected records
     */
    public int update(P pojo, boolean skipNull) {
        DSLContext dsl = ctx();
        R record = dsl.newRecord(table, pojo);
        skipPkAndNull(record, skipNull);
        return record.update();
    }


    /**
     * Update record by pojo key and value, by PK
     *
     * @param pojos    pojos
     * @param skipNull whether skip `null` values
     * @return array of affected records
     */
    public int @NotNull [] update(Collection<P> pojos, boolean skipNull) {
        List<R> records = new ArrayList<>(pojos.size());
        DSLContext dsl = ctx();
        for (P pojo : pojos) {
            R record = dsl.newRecord(table, pojo);
            skipPkAndNull(record, skipNull);
            records.add(record);
        }
        return dsl.batchUpdate(records).execute();
    }

    ///////////////// count /////////////////////

    /**
     * @see #count(Table, Condition)
     */
    public long count(Function<T, Condition> fun) {
        return count(table, fun.apply(table));
    }

    /**
     * count table by condition, requires table with the same name as condition
     */
    public long count(T table, Condition cond) {
        Long cnt = ctx().selectCount()
                        .from(table)
                        .where(cond)
                        .fetchOne(0, Long.class);
        return cnt == null ? 0 : cnt;
    }

    ///////////////// other /////////////////////
    public void skipPkAndNull(R record, boolean skipNull) {
        WingsJooqUtil.skipFields(record, pkeys);

        if (skipNull) {
            WingsJooqUtil.skipNullVals(record);
        }
    }

    ////////
    private U.Two<Collection<SelectFieldOrAsterisk>, Collection<OrderField<?>>> selectAndOrders(Collection<? extends QueryPart> selectsOrders) {
        if (selectsOrders == null || selectsOrders.isEmpty()) {
            return U.of(Collections.emptyList(), Collections.emptyList());
        }

        int size = selectsOrders.size();
        final ArrayList<SelectFieldOrAsterisk> fields = new ArrayList<>(size);
        final ArrayList<OrderField<?>> orders = new ArrayList<>(size);

        for (QueryPart qp : selectsOrders) {
            if (qp instanceof SelectFieldOrAsterisk) {
                fields.add((SelectFieldOrAsterisk) qp);
            }
            else if (qp instanceof OrderField) {
                orders.add((OrderField<?>) qp);
            }
        }

        return U.of(fields, orders);
    }

    private SelectConditionStep<R> selectWhere(T table, Condition cond, Collection<? extends SelectFieldOrAsterisk> selects) {
        if (selects == null || selects.isEmpty()) {
            return ctx().selectFrom(table).where(cond);
        }
        else {
            final SelectSelectStep<R> select = TypedCastUtil.castObject(ctx().select(selects));
            return select.from(table).where(cond);
        }
    }

    /////
    public static class SelectWhereOrder {
        private Condition where = null;
        private final List<QueryPart> parts = new ArrayList<>(16);

        /**
         * t.Id.gt(1L).and(t.CommitId.lt(200L))
         *
         * @param cond condition
         * @return this
         */
        @Contract("_ -> this")
        public SelectWhereOrder where(Condition cond) {
            where = cond;
            return this;
        }

        /**
         * t.Id, t.CommitId, t.Id.desc()
         *
         * @param part fields to select and order by
         * @return this
         */
        @Contract("_ -> this")
        public SelectWhereOrder query(QueryPart... part) {
            Collections.addAll(parts, part);
            return this;
        }

        /**
         * t.Id, t.CommitId, t.Id.desc()
         *
         * @param part fields to select and order by
         * @return this
         */
        @Contract("_ -> this")
        public SelectWhereOrder query(Collection<? extends QueryPart> part) {
            parts.addAll(part);
            return this;
        }

        /**
         * t.Id.desc()
         *
         * @param part fields to order by
         * @return this
         */
        @Contract("_ -> this")
        public SelectWhereOrder order(OrderField<?>... part) {
            Collections.addAll(parts, part);
            return this;
        }

        /**
         * t.Id.desc()
         *
         * @param part fields to order by
         * @return this
         */
        @Contract("_ -> this")
        public SelectWhereOrder order(Collection<? extends OrderField<?>> part) {
            parts.addAll(part);
            return this;
        }

        @NotNull
        public Condition getWhere() {
            return where == null ? DSL.noCondition() : where;
        }

        @NotNull
        public List<QueryPart> getParts() {
            return parts;
        }
    }
}
