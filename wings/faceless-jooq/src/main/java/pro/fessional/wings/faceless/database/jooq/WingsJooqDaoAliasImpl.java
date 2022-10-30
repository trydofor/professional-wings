package pro.fessional.wings.faceless.database.jooq;

import com.google.common.collect.Lists;
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
import pro.fessional.mirana.best.StateAssert;
import pro.fessional.mirana.cast.TypedCastUtil;
import pro.fessional.mirana.data.Null;
import pro.fessional.mirana.data.U;
import pro.fessional.mirana.pain.IORuntimeException;
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
import java.util.stream.Collectors;

/**
 * <pre>
 * 原则上，不希望Record携带的数据库信息扩散，因此建议Dao之外使用pojo
 *
 * 对于read方法，一律返回Pojo；对于write，同时支持 Record和Pojo。
 * 为了编码的便捷和减少数据拷贝，可以使用Record进行操作。
 * 批量处理中，一律使用了new Record，为了提升性能。
 *
 * 注意，alias 用在多表查询，filed/condition和table需要同名，否则出现语法错误。
 * 即，不能是表名和字段不能一个是table，一个是alias的。
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
    protected volatile int tableExist = -1; // -1:未检出 | 0:不存：1:存在

    protected WingsJooqDaoAliasImpl(T table, Class<P> type) {
        this(table, type, null);
    }

    protected WingsJooqDaoAliasImpl(T table, Class<P> type, Configuration conf) {
        super(table, type, conf);
        this.table = table;
        this.pkeys = WingsJooqUtil.primaryKeys(table);
    }

    /**
     * -1:未检出 | 0:不存：1:存在
     *
     * @param type -1|0|1
     */
    public void setTableExist(int type) {
        tableExist = type;
    }

    /**
     * 默认以select count(*) from table where 1 = 0检查数据库中是否存在此表
     *
     * @return 存在与否
     */
    public boolean notTableExist() {
        if (tableExist < 0) {
            synchronized (this) {
                if (tableExist < 0) {
                    try {
                        ctx().selectCount()
                             .from(table)
                             .where(DSL.falseCondition())
                             .execute();
                        tableExist = 1;
                    }
                    catch (Exception e) {
                        tableExist = 0;
                    }
                }
            }
        }
        //
        return tableExist == 0;
    }

    /**
     * 相同表结构，构造一个新表名，有在分表，影子表的场景
     *
     * @param name 新表名
     * @return 新表
     * @see TableImpl#rename(String)
     */
    @SuppressWarnings("unchecked")
    @NotNull
    public T newTable(String name) {
        return (T) ((TableImpl<?>) table).rename(name);
    }

    /**
     * 以当前表名为基础，增加前缀，后缀
     *
     * @param prefix  前缀
     * @param postfix 后缀
     * @return 新表
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
     * 获得系统默认的table别名
     *
     * @return 表
     */
    @NotNull
    public T getAlias() {
        return table.getAliasTable();
    }

    /**
     * 通过 mapping 构造一个 record
     *
     * @param obj 具有相同mapping规则
     * @return record
     */
    @NotNull
    public R newRecord(Object obj) {
        return ctx().newRecord(table, obj);
    }

    /**
     * 把一组 po 构造为 record，可供batch系列使用
     *
     * @param pos po
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
     * 一次性导入新记录，对重复记录忽略或更新。
     * ignore时，采用了先查询 from dual where exists select * where `id` = ?
     * replace时，使用了 on duplicate key update
     *
     * @param records         所有记录
     * @param ignoreOrReplace 唯一冲突时，忽略还是替换
     * @return 执行结果，使用 ModifyAssert判断
     * @see DSLContext#loadInto(Table)
     */
    @NotNull
    public Loader<R> batchLoad(Collection<R> records, boolean ignoreOrReplace) {
        checkBatchMysql();

        DSLContext dsl = ctx();
        LoaderOptionsStep<R> ldi = dsl.loadInto(table);
        if (ignoreOrReplace) {
            ldi.onDuplicateKeyIgnore();
        }
        else {
            ldi.onDuplicateKeyUpdate();
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
            throw new IllegalStateException("请使用#batchInsert(Collection<R>, int, boolean)，以使用insert ignore 和 replace into的mysql高效语法。避免使用from dual where exists 和 on duplicate key update");
        }
    }


    /**
     * 插入新记录，使用mysql的 insert ignore或 replace into。
     * 注意 jooq的mergeInto，不完美，必须都有值，而replace不会。
     *
     * @param pojo            记录
     * @param ignoreOrReplace 唯一冲突时，忽略还是替换
     * @return 执行结果，使用 ModifyAssert判断
     */
    public int insertInto(P pojo, boolean ignoreOrReplace) {
        return insertInto(pojo, ignoreOrReplace, null);
    }

    /**
     * 以ignoreOrReplace=false 插入，并获取diff
     *
     * @see #diffInsert(Object, boolean)
     */
    @NotNull
    public JournalDiff diffInsert(P pojo) {
        return diffInsert(pojo, false);
    }

    /**
     * 插入，并获取diff
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
        final @NotNull Field<?>[] fields = table.fields();
        final @NotNull Object[] values = record.intoArray();
        if (ignoreOrReplace) {
            // insert ignore
            rc = dsl.insertInto(table)
                    .columns(fields)
                    .values(values)
                    .onDuplicateKeyIgnore()
                    .execute();

        }
        else {
//            RowCountQuery query = WingsJooqUtil.replaceInto(record);
//            return dsl.execute(query);
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
            StateAssert.aEqb(1, rs2.size(), "should find 1 record after insert");
            JournalDiffHelper.helpInsert(diff, rs2);
        }

        return rc;
    }

    /**
     * batchInsert record的语法糖
     *
     * @param pos             记录
     * @param ignoreOrReplace 唯一冲突时，忽略还是替换
     * @return 执行结果，使用 ModifyAssert判断
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
     * 插入新记录，默认使用①insert into DuplicateKey update，
     * 也可以②先select，在insert或update
     *
     * @param table        与 updateFields 同名表
     * @param pojo         记录
     * @param updateFields 唯一约束存在时更新的字段，确保不使用别名
     * @return 执行结果，使用 ModifyAssert判断
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
     * 先select，在insert或update
     *
     * @param table        与 updateFields 同名表
     * @param records      所有记录
     * @param size         每批的数量，小于等于0时，表示不分批
     * @param updateFields 唯一约束存在时更新的字段
     * @return 执行结果，使用 ModifyAssert判断
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
                batch.bind(vals);
            }

            return batch.execute();
        };

        return batchExecute(records, size, batchMergeExec);
    }

    /**
     * 当不使用db中的唯一约束时，使用此方法
     * 先根据keys进行分批select，再根据记录情况进行insert或update。
     * 字符串比较忽略大小写
     *
     * @param table        与 updateFields 同名表
     * @param keys         唯一索引字段
     * @param records      所有记录
     * @param size         每批的数量，小于等于0时，表示不分批
     * @param updateFields 唯一约束存在时更新的字段
     * @return 执行结果，使用 ModifyAssert判断
     */
    public int @NotNull [] batchMerge(T table, Field<?>[] keys, Collection<R> records, int size, Field<?>... updateFields) {
        return batchMerge(table, keys, caseIgnore, records, size, updateFields);
    }

    private final BiPredicate<Object, Object> caseIgnore = (o1, o2) -> {
        if (o1 instanceof String && o2 instanceof String) {
            String s1 = (String) o1;
            String s2 = (String) o2;
            return s1.equalsIgnoreCase(s2);
        }
        else {
            return o1.equals(o2);
        }
    };

    /**
     * 当不使用db中的唯一约束时，使用此方法
     * 先根据keys进行分批select，再根据记录情况进行insert或update
     *
     * @param table        与 updateFields 同名表
     * @param keys         唯一索引字段
     * @param equals       判断字段相等的方法
     * @param records      所有记录
     * @param size         每批的数量，小于等于0时，表示不分批
     * @param updateFields 唯一约束存在时更新的字段
     * @return 执行结果，使用 ModifyAssert判断
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
     * 分配批量插入新记录，使用mysql的 insert ignore或 replace into。
     * 注意 jooq的mergeInto，不完美，必须都有值，而replace不会。
     *
     * @param records         所有记录
     * @param size            每批的数量，小于等于0时，表示不分批
     * @param ignoreOrReplace 唯一冲突时，忽略还是替换
     * @return 执行结果，使用 ModifyAssert判断
     * @see DSLContext#mergeInto(Table)
     */
    @SuppressWarnings("unchecked")
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
     * 分配批量插入记录
     *
     * @param records 所有记录
     * @param size    每批的数量，小于等于0时，表示不分批
     * @return 执行结果，使用 ModifyAssert判断
     * @see DSLContext#batchInsert(TableRecord[])
     */
    public int @NotNull [] batchInsert(Collection<R> records, int size) {
        return batchExecute(records, size, batchInsertExec);
    }

    private final BiFunction<DSLContext, Collection<R>, int[]> batchInsertExec = (dsl, rs) -> dsl.batchInsert(rs).execute();

    /**
     * 分配批量插入或更新记录
     *
     * @param records 所有记录
     * @param size    每批的数量，小于等于0时，表示不分批
     * @return 执行结果，使用 ModifyAssert判断
     * @see DSLContext#batchStore(UpdatableRecord[])
     */
    public int @NotNull [] batchStore(Collection<R> records, int size) {
        return batchExecute(records, size, batchStoreExec);
    }

    private final BiFunction<DSLContext, Collection<R>, int[]> batchStoreExec = (dsl, rs) -> dsl.batchStore(rs).execute();

    /**
     * 分配批量更新数据
     *
     * @param table        与 updateFields 同名表
     * @param whereFields  where条件
     * @param records      记录
     * @param size         批次大小
     * @param updateFields 更新字段
     * @return 执行结果，使用 ModifyAssert判断
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
                batch.bind(vals);
            }

            return batch.execute();
        };

        return batchExecute(records, size, batchMergeExec);
    }

    /**
     * 分配批量更新记录
     *
     * @param records 所有记录
     * @param size    每批的数量，小于等于0时，表示不分批
     * @return 执行结果，使用 ModifyAssert判断
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
    public List<P> fetch(T table, int limit, QueryPart... selectsOrders) {
        return fetch(table, 0, limit, null, selectsOrders);
    }

    @NotNull
    public List<P> fetch(T table, int offset, int limit, QueryPart... selectsOrders) {
        return fetch(table, offset, limit, null, selectsOrders);
    }

    @NotNull
    public List<P> fetch(T table, int limit, Condition cond, QueryPart... selectsOrders) {
        return fetch(table, 0, limit, cond, selectsOrders);
    }

    @NotNull
    public List<P> fetch(T table, int offset, int limit, Condition cond, QueryPart... selectsOrders) {
        return fetch(getType(), offset, limit, table, cond, selectsOrders);
    }

    @NotNull
    public List<P> fetch(T table, int offset, int limit, Condition cond, Collection<SelectFieldOrAsterisk> selects, Collection<OrderField<?>> orderBy) {
        return fetch(getType(), offset, limit, table, cond, selects, orderBy);
    }

    ////////
    @NotNull
    public <E> List<E> fetch(Class<E> claz, T table, QueryPart... selectsOrders) {
        return fetch(claz, table, null, selectsOrders);
    }

    @NotNull
    public <E> List<E> fetch(Class<E> claz, T table, Condition cond, QueryPart... selectsOrders) {
        return fetch(claz, -1, -1, table, cond, selectsOrders);
    }

    @NotNull
    public <E> List<E> fetch(Class<E> claz, int limit, T table, QueryPart... selectsOrders) {
        return fetch(claz, 0, limit, table, null, selectsOrders);
    }

    @NotNull
    public <E> List<E> fetch(Class<E> claz, int offset, int limit, T table, QueryPart... selectsOrders) {
        return fetch(claz, offset, limit, table, null, selectsOrders);
    }

    @NotNull
    public <E> List<E> fetch(Class<E> claz, int limit, T table, Condition cond, QueryPart... selectsOrders) {
        return fetch(claz, 0, limit, table, cond, selectsOrders);
    }

    @NotNull
    public <E> List<E> fetch(Class<E> claz, int offset, int limit, T table, Condition cond, QueryPart... selectsOrders) {
        final U.Two<Collection<SelectFieldOrAsterisk>, Collection<OrderField<?>>> two = selectAndOrders(selectsOrders);
        return fetch(claz, offset, limit, table, cond, two.one(), two.two());
    }

    @NotNull
    public <E> List<E> fetch(Class<E> claz, int offset, int limit, T table, Condition cond, Collection<SelectFieldOrAsterisk> selects, Collection<OrderField<?>> orderBy) {
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
    public <E> List<E> fetch(RecordMapper<? super Record, E> mapper, T table, Condition cond, QueryPart... selectsOrders) {
        return fetch(mapper, -1, -1, table, cond, selectsOrders);
    }

    @NotNull
    public <E> List<E> fetch(RecordMapper<? super Record, E> mapper, int limit, T table, QueryPart... selectsOrders) {
        return fetch(mapper, 0, limit, table, null, selectsOrders);
    }

    @NotNull
    public <E> List<E> fetch(RecordMapper<? super Record, E> mapper, int offset, int limit, T table, QueryPart... selectsOrders) {
        return fetch(mapper, offset, limit, table, null, selectsOrders);
    }

    @NotNull
    public <E> List<E> fetch(RecordMapper<? super Record, E> mapper, int limit, T table, Condition cond, QueryPart... selectsOrders) {
        return fetch(mapper, 0, limit, table, cond, selectsOrders);
    }

    @NotNull
    public <E> List<E> fetch(RecordMapper<? super Record, E> mapper, int offset, int limit, T table, Condition cond, QueryPart... selectsOrders) {
        final U.Two<Collection<SelectFieldOrAsterisk>, Collection<OrderField<?>>> two = selectAndOrders(selectsOrders);
        return fetch(mapper, offset, limit, table, cond, two.one(), two.two());
    }

    @NotNull
    public <E> List<E> fetch(RecordMapper<? super Record, E> mapper, int offset, int limit, T table, Condition cond, Collection<SelectFieldOrAsterisk> selects, Collection<OrderField<?>> orderBy) {
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

    /////////////////
    @Nullable
    public P fetchOne(T table, QueryPart... selectsOrders) {
        return fetchOne(table, null, selectsOrders);
    }

    @Nullable
    public P fetchLimitOne(T table, QueryPart... selectsOrders) {
        return fetchLimitOne(table, null, selectsOrders);
    }

    @NotNull
    public Optional<P> fetchOptional(T table, QueryPart... selectsOrders) {
        return Optional.ofNullable(fetchOne(table, null, selectsOrders));
    }

    @NotNull
    public Optional<P> fetchLimitOptional(T table, QueryPart... selectsOrders) {
        return Optional.ofNullable(fetchLimitOne(table, null, selectsOrders));
    }

    public P fetchOne(T table, Condition cond, QueryPart... selectsOrders) {
        final U.Two<Collection<SelectFieldOrAsterisk>, Collection<OrderField<?>>> two = selectAndOrders(selectsOrders);
        return fetchOne(table, cond, two.one(), two.two(), false);
    }

    @Nullable
    public P fetchLimitOne(T table, Condition cond, QueryPart... selectsOrders) {
        final U.Two<Collection<SelectFieldOrAsterisk>, Collection<OrderField<?>>> two = selectAndOrders(selectsOrders);
        return fetchOne(table, cond, two.one(), two.two(), true);
    }

    @NotNull
    public Optional<P> fetchOptional(T table, Condition cond, QueryPart... selectsOrders) {
        return Optional.ofNullable(fetchOne(table, cond, selectsOrders));
    }

    @NotNull
    public Optional<P> fetchLimitOptional(T table, Condition cond, QueryPart... selectsOrders) {
        return Optional.ofNullable(fetchLimitOne(table, cond, selectsOrders));
    }

    @Nullable
    public P fetchOne(T table, Condition cond, Collection<SelectFieldOrAsterisk> selects, Collection<OrderField<?>> orderBy, boolean limit) {
        return fetchOne(getType(), table, cond, selects, orderBy, limit);
    }

    /////////////////
    @Nullable
    public <E> E fetchOne(Class<E> claz, T table, QueryPart... selectsOrders) {
        return fetchOne(claz, table, null, selectsOrders);
    }

    @Nullable
    public <E> E fetchLimitOne(Class<E> claz, T table, QueryPart... selectsOrders) {
        return fetchLimitOne(claz, table, null, selectsOrders);
    }

    @NotNull
    public <E> Optional<E> fetchOptional(Class<E> claz, T table, QueryPart... selectsOrders) {
        return Optional.ofNullable(fetchOne(claz, table, null, selectsOrders));
    }

    @NotNull
    public <E> Optional<E> fetchLimitOptional(Class<E> claz, T table, QueryPart... selectsOrders) {
        return Optional.ofNullable(fetchLimitOne(claz, table, null, selectsOrders));
    }

    public <E> E fetchOne(Class<E> claz, T table, Condition cond, QueryPart... selectsOrders) {
        final U.Two<Collection<SelectFieldOrAsterisk>, Collection<OrderField<?>>> two = selectAndOrders(selectsOrders);
        return fetchOne(claz, table, cond, two.one(), two.two(), false);
    }

    @Nullable
    public <E> E fetchLimitOne(Class<E> claz, T table, Condition cond, QueryPart... selectsOrders) {
        final U.Two<Collection<SelectFieldOrAsterisk>, Collection<OrderField<?>>> two = selectAndOrders(selectsOrders);
        return fetchOne(claz, table, cond, two.one(), two.two(), true);
    }

    @NotNull
    public <E> Optional<E> fetchOptional(Class<E> claz, T table, Condition cond, QueryPart... selectsOrders) {
        return Optional.ofNullable(fetchOne(claz, table, cond, selectsOrders));
    }

    @NotNull
    public <E> Optional<E> fetchLimitOptional(Class<E> claz, T table, Condition cond, QueryPart... selectsOrders) {
        return Optional.ofNullable(fetchLimitOne(claz, table, cond, selectsOrders));
    }

    @Nullable
    public <E> E fetchOne(Class<E> claz, T table, Condition cond, Collection<SelectFieldOrAsterisk> selects, Collection<OrderField<?>> orderBy, boolean limit) {
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
    public <E> E fetchLimitOne(RecordMapper<? super Record, E> mapper, T table, QueryPart... selectsOrders) {
        return fetchLimitOne(mapper, table, null, selectsOrders);
    }

    @NotNull
    public <E> Optional<E> fetchOptional(RecordMapper<? super Record, E> mapper, T table, QueryPart... selectsOrders) {
        return Optional.ofNullable(fetchOne(mapper, table, null, selectsOrders));
    }

    @NotNull
    public <E> Optional<E> fetchLimitOptional(RecordMapper<? super Record, E> mapper, T table, QueryPart... selectsOrders) {
        return Optional.ofNullable(fetchLimitOne(mapper, table, null, selectsOrders));
    }

    public <E> E fetchOne(RecordMapper<? super Record, E> mapper, T table, Condition cond, QueryPart... selectsOrders) {
        final U.Two<Collection<SelectFieldOrAsterisk>, Collection<OrderField<?>>> two = selectAndOrders(selectsOrders);
        return fetchOne(mapper, table, cond, two.one(), two.two(), false);
    }

    @Nullable
    public <E> E fetchLimitOne(RecordMapper<? super Record, E> mapper, T table, Condition cond, QueryPart... selectsOrders) {
        final U.Two<Collection<SelectFieldOrAsterisk>, Collection<OrderField<?>>> two = selectAndOrders(selectsOrders);
        return fetchOne(mapper, table, cond, two.one(), two.two(), true);
    }

    @NotNull
    public <E> Optional<E> fetchOptional(RecordMapper<? super Record, E> mapper, T table, Condition cond, QueryPart... selectsOrders) {
        return Optional.ofNullable(fetchOne(mapper, table, cond, selectsOrders));
    }

    @NotNull
    public <E> Optional<E> fetchLimitOptional(RecordMapper<? super Record, E> mapper, T table, Condition cond, QueryPart... selectsOrders) {
        return Optional.ofNullable(fetchLimitOne(mapper, table, cond, selectsOrders));
    }

    @Nullable
    public <E> E fetchOne(RecordMapper<? super Record, E> mapper, T table, Condition cond, Collection<SelectFieldOrAsterisk> selects, Collection<OrderField<?>> orderBy, boolean limit) {
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
     * 按条件删除
     *
     * @param table 表
     * @param cond  条件
     * @return 影响的数据条数
     */
    public int delete(T table, Condition cond) {
        return ctx().delete(table)
                    .where(cond)
                    .execute();
    }

    /**
     * 删除一条记录，并获取Diff
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

        StateAssert.aEqb(rc, size, "delete mismatched records. cond={}", cond);
        JournalDiffHelper.helpDelete(diff, rs1);
        return diff;
    }

    ///////////////// update /////////////////////

    /**
     * 更新记录，并获取Diff
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

        StateAssert.aEqb(rc, size, "update mismatched records. cond={}", cond);
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
     * @param table    同源表
     * @param setter   更新的字段-值
     * @param cond     更新条件
     * @param skipNull 忽略null值，true时需要map可编辑
     * @return 影响的数据条数
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
     * 按对象和条件更新，null被忽略
     *
     * @param table 同源表
     * @param pojo  对象
     * @param cond  条件
     * @return 更新数量
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
     * 按对象和主键更新
     *
     * @param pojo     对象
     * @param skipNull null字段不被更新
     * @return 更新数量
     */
    public int update(P pojo, boolean skipNull) {
        DSLContext dsl = ctx();
        R record = dsl.newRecord(table, pojo);
        skipPkAndNull(record, skipNull);
        return record.update();
    }


    /**
     * 按对象组更新
     *
     * @param objects  对象组
     * @param skipNull null字段不被更新
     * @return 更新数量
     */
    public int @NotNull [] update(Collection<P> objects, boolean skipNull) {
        List<R> records = new ArrayList<>(objects.size());
        DSLContext dsl = ctx();
        for (P object : objects) {
            R record = dsl.newRecord(table, object);
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
     * 按表count，要求table和cond中的字段必须同源
     *
     * @param table 表
     * @param cond  条件
     * @return 结果
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
    private U.Two<Collection<SelectFieldOrAsterisk>, Collection<OrderField<?>>> selectAndOrders(QueryPart[] selectsOrders) {
        if (selectsOrders == null || selectsOrders.length == 0) {
            return U.of(Collections.emptyList(), Collections.emptyList());
        }

        final ArrayList<SelectFieldOrAsterisk> fields = new ArrayList<>(selectsOrders.length);
        final ArrayList<OrderField<?>> orders = new ArrayList<>(selectsOrders.length);

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

    private SelectConditionStep<R> selectWhere(T table, Condition cond, Collection<SelectFieldOrAsterisk> selects) {
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

        private static final QueryPart[] EMPTY = new QueryPart[0];

        private Condition where = null;
        private QueryPart[] parts = null;

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
        public SelectWhereOrder order(QueryPart... part) {
            parts = part;
            return this;
        }

        @NotNull
        public Condition getWhere() {
            return where == null ? DSL.noCondition() : where;
        }

        @NotNull
        public QueryPart[] getParts() {
            return parts == null ? EMPTY : parts;
        }
    }
}
