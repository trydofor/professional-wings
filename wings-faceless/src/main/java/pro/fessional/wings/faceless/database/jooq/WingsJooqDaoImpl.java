package pro.fessional.wings.faceless.database.jooq;

import com.google.common.collect.Lists;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jooq.BatchBindStep;
import org.jooq.Condition;
import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Loader;
import org.jooq.LoaderOptionsStep;
import org.jooq.OrderField;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.RowCountQuery;
import org.jooq.Table;
import org.jooq.TableRecord;
import org.jooq.UpdatableRecord;
import org.jooq.impl.DAOImpl;
import org.jooq.impl.TableImpl;
import pro.fessional.mirana.data.CodeEnum;
import pro.fessional.mirana.data.Nulls;
import pro.fessional.mirana.pain.CodeException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;

import static org.jooq.impl.DSL.row;
import static org.jooq.impl.DSL.trueCondition;

/**
 * <pre>
 * 原则上，不希望Record携带的数据库信息扩散，因此建议Dao之外使用pojo
 *
 * 对于read方法，一律返回Pojo；对于write，同时支持 Record和Pojo。
 * 为了编码的便捷和减少数据拷贝，可以使用Record进行操作。
 * 批量处理中，一律使用了new Record，为了提升性能。
 *
 * 注意，alias 用在多表查询，filed和table需要同源，否则出现语法错误。
 * 即 T.Id 用在了非T的jooq表查询会出现语法错误。
 * </pre>
 *
 * @param <T> Table
 * @param <R> Record
 * @param <P> POJO
 * @param <K> primary Key type
 * @author trydofor
 * @since 2019-10-12
 */
public abstract class WingsJooqDaoImpl<T extends TableImpl<R>, R extends UpdatableRecord<R>, P, K> extends DAOImpl<R, P, K> {

    private final T table;
    private final T alias;
    private final Condition onlyDied;
    private final Condition onlyLive;
    private final Field<?>[] tablePks;

    protected WingsJooqDaoImpl(T table, T alias, Class<P> type) {
        this(table, alias, type, null);
    }

    protected WingsJooqDaoImpl(T table, T alias, Class<P> type, Configuration conf) {
        super(table, type, conf);
        //
        Condition d = null;
        Condition l = null;
        for (java.lang.reflect.Field f : table.getClass().getDeclaredFields()) {
            if (Condition.class.isAssignableFrom(f.getType())) {
                try {
                    if (f.getName().equals("onlyDiedData")) {
                        d = (Condition) f.get(table);
                    } else if (f.getName().equals("onlyLiveData")) {
                        l = (Condition) f.get(table);
                    }
                } catch (IllegalAccessException e) {
                    // ignore
                }
            }
        }

        this.table = table;
        this.alias = alias == null ? table : alias;
        this.onlyDied = d;
        this.onlyLive = l;
        this.tablePks = WingsJooqUtil.primaryKeys(table);
    }

    /**
     * 相同表结构，构造一个新表名，有在分表的场景
     *
     * @param name 新表名
     * @return 新表
     * @see TableImpl#rename(String)
     */
    @SuppressWarnings("unchecked")
    public T newTable(String name) {
        return (T) table.rename(name);
    }

    /**
     * 以当前表名为基础，增加前缀，后缀
     *
     * @param prefix  前缀
     * @param postfix 后缀
     * @return 新表
     */
    public T newTable(String prefix, String postfix) {
        return newTable(prefix + table.getName() + postfix);
    }

    @SuppressWarnings("unchecked")
    @NotNull
    public T as(String alias) {
        return (T) table.as(alias);
    }

    @Override
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
        return alias;
    }

    // ============

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
    public Loader<R> batchLoad(Collection<R> records, boolean ignoreOrReplace) throws IOException {
        checkBatchMysql();

        DSLContext dsl = ctx();
        LoaderOptionsStep<R> ldi = dsl.loadInto(table);
        if (ignoreOrReplace) {
            ldi.onDuplicateKeyIgnore();
        } else {
            ldi.onDuplicateKeyUpdate();
        }
        return ldi.loadRecords(records)
                  .fields(table.fields())
                  .execute();
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

        DSLContext dsl = ctx();
        R record = dsl.newRecord(table, pojo);
        if (ignoreOrReplace) {
            // insert ignore
            return dsl.insertInto(table)
                      .columns(table.fields())
                      .values(record.intoArray())
                      .onDuplicateKeyIgnore()
                      .execute();

        } else {
            RowCountQuery query = WingsJooqUtil.replaceInto(record);
            return dsl.execute(query);
        }
    }


    /**
     * 插入新记录，默认使用①insert into DuplicateKey update，
     * 也可以②先select，在insert或update
     *
     * @param pojo         记录
     * @param updateFields 唯一约束存在时更新的字段
     * @return 执行结果，使用 ModifyAssert判断
     */
    public int mergeInto(P pojo, Field<?>... updateFields) {
        HashMap<Field<?>, Object> map = new HashMap<>();
        DSLContext dsl = ctx();
        R record = dsl.newRecord(table, pojo);

        for (Field<?> field : updateFields) {
            Object t = record.get(field);
            map.put(field, t);
        }

        return dsl
                .insertInto(table)
                .columns(table.fields())
                .values(record.intoArray())
                .onDuplicateKeyUpdate()
                .set(map)
                .execute();
    }

    /**
     * 先select，在insert或update
     *
     * @param records      所有记录
     * @param size         每批的数量，小于等于0时，表示不分批
     * @param updateFields 唯一约束存在时更新的字段
     * @return 执行结果，使用 ModifyAssert判断
     */
    public int[] batchMerge(Collection<R> records, int size, Field<?>... updateFields) {
        if (records == null || records.isEmpty()) return Nulls.Ints;

        BiFunction<DSLContext, Collection<R>, int[]> batchMergeExec = (dsl, rs) -> {
            HashMap<Field<?>, Object> map = new HashMap<>();
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
     * @param keys         唯一索引字段
     * @param records      所有记录
     * @param size         每批的数量，小于等于0时，表示不分批
     * @param updateFields 唯一约束存在时更新的字段
     * @return 执行结果，使用 ModifyAssert判断
     */
    public int[] batchMerge(Field<?>[] keys, Collection<R> records, int size, Field<?>... updateFields) {
        return batchMerge(keys, caseIgnore, records, size, updateFields);
    }

    private final BiPredicate<Object, Object> caseIgnore = (o1, o2) -> {
        if (o1 instanceof String && o2 instanceof String) {
            String s1 = (String) o1;
            String s2 = (String) o2;
            return s1.equalsIgnoreCase(s2);
        } else {
            return o1.equals(o2);
        }
    };

    /**
     * 当不使用db中的唯一约束时，使用此方法
     * 先根据keys进行分批select，再根据记录情况进行insert或update
     *
     * @param keys         唯一索引字段
     * @param equals       判断字段相等的方法
     * @param records      所有记录
     * @param size         每批的数量，小于等于0时，表示不分批
     * @param updateFields 唯一约束存在时更新的字段
     * @return 执行结果，使用 ModifyAssert判断
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public int[] batchMerge(Field<?>[] keys, BiPredicate<Object, Object> equals, Collection<R> records, int size, Field<?>... updateFields) {
        if (records == null || records.isEmpty()) return Nulls.Ints;

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
                    } else {
                        cand = cand.and(key.eq(o));
                    }
                }
                if (where == null) {
                    where = cand;
                } else {
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
                } else {
                    ins.add(rcd);
                }
            }

            if (!ins.isEmpty()) {
                int[] r = batchInsert(ins, size);
                System.arraycopy(r, 0, result, off, r.length);
                off += r.length;
            }

            if (!upd.isEmpty()) {
                int[] r = batchUpdate(keys, upd, size, updateFields);
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
    public int[] batchInsert(Collection<R> records, int size, boolean ignoreOrReplace) {
        if (records == null || records.isEmpty()) return Nulls.Ints;

        BiFunction<DSLContext, Collection<R>, int[]> batchIgnoreExec = (dsl, rs) -> {
            Field<?>[] fields = table.fields();
            BatchBindStep batch;
            if (ignoreOrReplace) {
                // insert ignore
                batch = dsl.batch(
                        dsl.insertInto(table)
                           .columns(fields)
                           .values(new Object[fields.length])
                           .onDuplicateKeyIgnore()
                );

            } else {
                batch = dsl.batch(WingsJooqUtil.replaceInto(table, fields));
            }

            for (R r : rs) {
                Object[] vals = new Object[fields.length];
                for (int i = 0; i < vals.length; i++) {
                    vals[i] = r.get(i);
                }
                batch.bind(vals);
            }

            return batch.execute();
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
    public int[] batchInsert(Collection<R> records, int size) {
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
    public int[] batchStore(Collection<R> records, int size) {
        return batchExecute(records, size, batchStoreExec);
    }

    private final BiFunction<DSLContext, Collection<R>, int[]> batchStoreExec = (dsl, rs) -> dsl.batchStore(rs).execute();

    /**
     * 分配批量更新数据
     *
     * @param whereFields  where条件
     * @param records      记录
     * @param size         批次大小
     * @param updateFields 更新字段
     * @return 执行结果，使用 ModifyAssert判断
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public int[] batchUpdate(Field<?>[] whereFields, Collection<R> records, int size, Field<?>... updateFields) {
        if (records == null || records.isEmpty()) return Nulls.Ints;

        BiFunction<DSLContext, Collection<R>, int[]> batchMergeExec = (dsl, rs) -> {
            HashMap<Field<?>, Object> map = new HashMap<>();
            for (Field<?> uf : updateFields) {
                map.put(uf, null);
            }
            Condition where = null;
            for (Field wf : whereFields) {
                if (where == null) {
                    where = wf.eq((Object) null);
                } else {
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

    public int[] batchUpdate(Collection<R> records, int size) {
        return batchExecute(records, size, batchUpdateExec);
    }

    private final BiFunction<DSLContext, Collection<R>, int[]> batchUpdateExec = (dsl, rs) -> dsl.batchUpdate(rs).execute();

    //
    public int[] batchExecute(Collection<R> records, int size, BiFunction<DSLContext, Collection<R>, int[]> exec) {
        if (records == null || records.isEmpty()) return Nulls.Ints;

        DSLContext dsl = ctx();
        if (size <= 0 || records.size() <= size) {
            return exec.apply(dsl, records);
        }

        int[] rst = new int[records.size()];
        int off = 0;
        List<R> rds;
        if (records instanceof List) {
            rds = (List<R>) records;
        } else {
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
        } else {
            rds = new ArrayList<>(records);
        }
        return Lists.partition(rds, size);
    }

    // ===== override =====

    /**
     * 只选择未标记删除的
     */
    public boolean existsByIdLive(K id) {
        if (tablePks.length > 0) {
            return count(table, onlyLive(condEqual(id, tablePks))) > 0;
        } else {
            return false;
        }
    }

    /**
     * 只选择未标记删除的
     */
    public long countLive() {
        return count(table, onlyLive);
    }

    /**
     * 只选择未标记删除的
     */
    public List<P> findAllLive() {
        return fetch(table, onlyLive);
    }

    /**
     * 只选择未标记删除的
     */
    public P findByIdLive(K id) {
        if (tablePks.length == 0) return null;
        return fetchOne(table, onlyLive(condEqual(id, tablePks)));
    }


    public <Z> List<P> fetchRangeLive(Field<Z> field, Z lowerInclusive, Z upperInclusive) {
        Condition cond = condRange(field, lowerInclusive, upperInclusive);

        return ctx()
                .selectFrom(table)
                .where(onlyLive(cond))
                .fetch(mapper());
    }

    /**
     * 只选择未标记删除的
     */
    @SuppressWarnings("unchecked")
    public <Z> List<P> fetchLive(Field<Z> field, Z... values) {
        return fetch(table, onlyLive(field.in(values)));
    }


    /**
     * 只选择未标记删除的
     */
    public <Z> P fetchOneLive(Field<Z> field, Z value) {
        return fetchOne(table, onlyLive(field.eq(value)));
    }

    // ======= select =======

    public long count(Condition condition) {
        return count(table, condition);
    }

    @NotNull
    public List<P> fetch(Condition condition, OrderField<?>... orderBy) {
        return fetch(table, condition, orderBy);
    }

    /**
     * <pre>
     * val a = dao.tableForReader
     * val c = a.Id.eq(1L).and(a.CommitId.eq(2L))
     *
     * val i = dao.count(c)
     * val fetch = dao.fetch(0, 10, c)
     * </pre>
     *
     * @param offset  offset
     * @param limit   size
     * @param orderBy 排序字段
     * @return 结果集
     */
    @NotNull
    public List<P> fetch(int offset, int limit, OrderField<?>... orderBy) {
        return fetch(table, offset, limit, null, orderBy);
    }

    /**
     * <pre>
     * val a = dao.tableForReader
     * val c = a.Id.eq(1L).and(a.CommitId.eq(2L))
     *
     * val i = dao.count(c)
     * val fetch = dao.fetch(0, 10, c)
     * </pre>
     *
     * @param offset    offset
     * @param limit     size
     * @param condition 条件
     * @param orderBy   排序字段
     * @return 结果集
     */
    @NotNull
    public List<P> fetch(int offset, int limit, Condition condition, OrderField<?>... orderBy) {
        return fetch(table, offset, limit, condition, orderBy);
    }

    @Nullable
    public P fetchOne(Condition condition) {
        return fetchOne(table, condition);
    }

    // ======= modify =======

    /**
     * 必须插入一个，否则CodeException(orError)
     *
     * @param object  对象
     * @param orError 异常code
     */
    public int insertOne(P object, CodeEnum orError) {
        int rc = ctx().newRecord(table, object).insert();
        if (rc != 1) throw new CodeException(orError);
        return rc;
    }

    /**
     * 批量插入N，必须N条，否则CodeException(orError)
     *
     * @param records 批量对象
     * @param orError 异常code
     */
    public int[] insertEqN(Collection<R> records, CodeEnum orError) {
        DSLContext dsl = ctx();
        int[] rc = dsl.batchInsert(records).execute();
        for (int v : rc) {
            if (v != 1) throw new CodeException(orError);
        }
        return rc;
    }

    /**
     * 按条件删除
     *
     * @param condition 更新条件
     * @return 影响的数据条数
     */
    public int delete(Condition condition) {
        return ctx()
                .delete(table)
                .where(condition)
                .execute();
    }

    /**
     * 必须删除一个，否则CodeException(orError)
     *
     * @param condition 条件
     * @param orError   异常code
     */
    public int deleteOne(Condition condition, CodeEnum orError) {
        return deleteEqN(condition, 1, orError);
    }

    /**
     * 必须删除N个，否则CodeException(orError)
     *
     * @param condition 条件
     * @param n         数字
     * @param orError   异常code
     */
    public int deleteEqN(Condition condition, int n, CodeEnum orError) {
        int rc = delete(condition);
        if (rc != n) throw new CodeException(orError);
        return rc;
    }

    /**
     * 必须删除不多于N个，否则CodeException(orError)
     *
     * @param condition 条件
     * @param n         数字
     * @param orError   异常code
     */
    public int deleteLeN(Condition condition, int n, CodeEnum orError) {
        int rc = delete(condition);
        if (rc > n) throw new CodeException(orError);
        return rc;
    }

    /**
     * <pre>
     * val t = dao.tableForWriter
     * val setter = hashMapOf<Any, Any>()
     * setter.put(t.Id, 1L)
     * setter.put(t.CommitId, t.Id)
     * val ui = dao.update(setter, t.Id.eq(2L))
     * </pre>
     *
     * @param setter    更新的字段-值
     * @param condition 更新条件
     * @return 影响的数据条数
     */
    public int update(Map<?, ?> setter, Condition condition) {
        return ctx()
                .update(table)
                .set(setter)
                .where(condition)
                .execute();
    }


    /**
     * 按对象和条件更新，null被忽略
     *
     * @param pojo      对象
     * @param condition 条件
     * @return 更新数量
     */
    public int update(P pojo, Condition condition) {
        DSLContext dsl = ctx();
        R record = dsl.newRecord(table, pojo);

        Map<Field<?>, Object> setter = new LinkedHashMap<>();
        int size = record.size();
        for (int i = 0; i < size; i++) {
            if (record.get(i) != null) {
                setter.put(record.field(i), record.get(i));
            }
        }

        return dsl.update(table)
                  .set(setter)
                  .where(condition)
                  .execute();
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
        dealPkAndNull(record, skipNull);
        return record.update();
    }


    /**
     * 按对象组更新
     *
     * @param objects  对象组
     * @param skipNull null字段不被更新
     * @return 更新数量
     */
    public int[] update(Collection<P> objects, boolean skipNull) {
        List<R> records = new ArrayList<>(objects.size());
        DSLContext dsl = ctx();
        for (P object : objects) {
            R record = dsl.newRecord(table, object);
            dealPkAndNull(record, skipNull);
            records.add(record);
        }
        return dsl.batchUpdate(records).execute();
    }

    /**
     * 必须更新一个，否则CodeException(orError)
     *
     * @param setter    变更新
     * @param condition 条件
     * @param orError   异常code
     */
    public int updateOne(Map<?, ?> setter, Condition condition, CodeEnum orError) {
        return updateEqN(setter, condition, 1, orError);
    }

    /**
     * 必须更新N个，否则CodeException(orError)
     *
     * @param setter    变更新
     * @param condition 条件
     * @param n         数量
     * @param orError   异常code
     */
    public int updateEqN(Map<?, ?> setter, Condition condition, int n, CodeEnum orError) {
        int rc = update(setter, condition);
        if (rc != n) throw new CodeException(orError);
        return rc;
    }

    /**
     * 必须更新不多于N个，否则CodeException(orError)
     *
     * @param setter    变更新
     * @param condition 条件
     * @param n         数量
     * @param orError   异常code
     */
    public int updateLeN(Map<?, ?> setter, Condition condition, int n, CodeEnum orError) {
        int rc = update(setter, condition);
        if (rc > n) throw new CodeException(orError);
        return rc;
    }

    /**
     * 把 object 中非null，非主键字段按setter更新。
     * 必须更新一个，否则CodeException(orError)
     *
     * @param object    变更新
     * @param condition 条件
     * @param orError   异常code
     */
    public int updateOne(P object, Condition condition, CodeEnum orError) {
        return updateEqN(object, condition, 1, orError);
    }

    /**
     * 把 object 中非null，非主键字段按setter更新。
     * 必须更新N个，否则CodeException(orError)
     *
     * @param object    变更新
     * @param condition 条件
     * @param n         数量
     * @param orError   异常code
     */
    public int updateEqN(P object, Condition condition, int n, CodeEnum orError) {
        int rc = update(object, condition);
        if (rc != n) throw new CodeException(orError);
        return rc;
    }

    /**
     * 把 object 中非null，非主键字段按setter更新。
     * 必须更新不多于N个，否则CodeException(orError)
     *
     * @param object    变更新
     * @param condition 条件
     * @param n         数量
     * @param orError   异常code
     */
    public int updateLeN(P object, Condition condition, int n, CodeEnum orError) {
        int rc = update(object, condition);
        if (rc > n) throw new CodeException(orError);
        return rc;
    }

    /**
     * 必须更新一个，否则CodeException(orError)
     *
     * @param object   被更新对象
     * @param skipNull null值忽略
     * @param orError  异常code
     */
    public int updateOne(P object, boolean skipNull, CodeEnum orError) {
        int rc = update(object, skipNull);
        if (rc != 1) throw new CodeException(orError);
        return rc;
    }

    /**
     * 必须更新N个，否则CodeException(orError)
     *
     * @param objects  被更新对象
     * @param skipNull null值忽略
     * @param orError  异常code
     */
    public int[] updateEqN(Collection<P> objects, boolean skipNull, CodeEnum orError) {
        int[] rc = update(objects, skipNull);
        for (int v : rc) {
            if (v != 1) throw new CodeException(orError);
        }
        return rc;
    }

    /**
     * 必须更新不多于N个，否则CodeException(orError)
     *
     * @param objects  被更新对象
     * @param skipNull null值忽略
     * @param orError  异常code
     */
    public int[] updateLeN(Collection<P> objects, boolean skipNull, CodeEnum orError) {
        int[] rc = update(objects, skipNull);
        for (int v : rc) {
            if (v > 1) throw new CodeException(orError);
        }
        return rc;
    }

    // ===== Condition =====

    /**
     * 数据标记删除了
     * 默认 DeleteDt.gt(EmptyValue.DATE_TIME);
     *
     * @return Condition 或 null
     */
    @Nullable
    public Condition onlyDied() {
        return onlyDied;
    }

    /**
     * 数据是有效数据
     * 默认 DeleteDt.eq(EmptyValue.DATE_TIME);
     *
     * @return Condition 或 null
     */
    @Nullable
    public Condition onlyLive() {
        return onlyLive;
    }

    /**
     * 组合其他条件 and onlyDiedData
     *
     * @param cond 其他条件
     * @return Condition
     */
    @NotNull
    public Condition onlyDied(@NotNull Condition cond) {
        return onlyDied == null ? cond : onlyDied.and(cond);
    }

    /**
     * 组合其他条件 and onlyLiveData
     *
     * @param cond 其他条件
     * @return Condition
     */
    @NotNull
    public Condition onlyLive(@NotNull Condition cond) {
        return onlyLive == null ? cond : onlyLive.and(cond);
    }

    /**
     * 构造一个between的条件
     *
     * @param field          字段
     * @param lowerInclusive 小值，包含
     * @param upperInclusive 大值，包含
     * @param <Z>            类型
     * @return 条件
     */
    public <Z> Condition condRange(Field<Z> field, Z lowerInclusive, Z upperInclusive) {
        if (lowerInclusive == null) {
            if (upperInclusive == null) {
                return trueCondition();
            } else {
                return field.le(upperInclusive);
            }
        } else {
            if (upperInclusive == null) {
                return field.ge(lowerInclusive);
            } else {
                return field.between(lowerInclusive, upperInclusive);
            }
        }
    }

    /**
     * Composite key T types are of type Record[N]
     * 复合条件 RowN = RecordN
     *
     * @param valN 复合值
     * @param rowN 复合Col
     * @return 条件
     */
    @SuppressWarnings("unchecked")
    public Condition condEqual(Object valN, Field<?>... rowN) {
        if (rowN.length == 1) {
            return ((Field<Object>) rowN[0]).equal(rowN[0].getDataType().convert(valN));
        }
        // [#2573] Composite key T types are of type Record[N]
        else {
            return row(rowN).equal((Record) valN);
        }
    }

    // ===== common ======

    /**
     * 按表count，要求table和cond中的字段必须同源
     *
     * @param table 表
     * @param cond  条件
     * @return 结果
     */
    public long count(T table, Condition cond) {
        return ctx()
                .selectCount()
                .from(table)
                .where(cond)
                .fetchOne(0, Long.class);
    }

    /**
     * 按表取一个，要求table和cond中的字段必须同源
     *
     * @param table 表
     * @param cond  条件
     * @return 结果
     */
    public P fetchOne(T table, Condition cond) {
        R record = ctx()
                .selectFrom(table)
                .where(cond)
                .fetchOne();

        return record == null ? null : mapper().map(record);
    }

    /**
     * 按表分页排序查询，要求table和cond中的字段必须同源
     *
     * @param table   表
     * @param offset  offset
     * @param limit   limit
     * @param cond    条件
     * @param orderBy order by
     * @return 结果
     */
    public List<P> fetch(T table, int offset, int limit, Condition cond, OrderField<?>[] orderBy) {
        DSLContext dsl = ctx();
        if (orderBy == null || orderBy.length == 0) {
            return dsl
                    .selectFrom(table)
                    .where(cond)
                    .limit(offset, limit)
                    .fetch()
                    .map(mapper());
        } else {
            return dsl
                    .selectFrom(table)
                    .where(cond)
                    .orderBy(orderBy)
                    .limit(offset, limit)
                    .fetch()
                    .map(mapper());
        }
    }

    /**
     * 按表查询，要求table和cond中的字段必须同源
     *
     * @param table 表
     * @param cond  条件
     * @return 结果
     */
    public List<P> fetch(T table, Condition cond) {
        DSLContext dsl = ctx();
        return dsl
                .selectFrom(table)
                .where(cond)
                .fetch()
                .map(mapper());
    }

    /**
     * 按表排序查询，要求table和cond中的字段必须同源
     *
     * @param table   表
     * @param cond    条件
     * @param orderBy order by
     * @return 结果
     */
    public List<P> fetch(T table, Condition cond, OrderField<?>[] orderBy) {
        DSLContext dsl = ctx();
        if (orderBy == null || orderBy.length == 0) {
            return dsl
                    .selectFrom(table)
                    .where(cond)
                    .fetch()
                    .map(mapper());
        } else {
            return dsl
                    .selectFrom(table)
                    .where(cond)
                    .orderBy(orderBy)
                    .fetch()
                    .map(mapper());
        }
    }

    //
    private void dealPkAndNull(R record, boolean skipNull) {
        WingsJooqUtil.skipFields(record, tablePks);

        if (skipNull) {
            WingsJooqUtil.skipNullVals(record);
        }
    }
}
