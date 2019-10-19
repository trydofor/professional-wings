package pro.fessional.wings.faceless.database.common;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jooq.Condition;
import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.OrderField;
import org.jooq.Record;
import org.jooq.Table;
import org.jooq.UniqueKey;
import org.jooq.UpdatableRecord;
import org.jooq.impl.DAOImpl;
import pro.fessional.mirana.data.CodeEnum;
import pro.fessional.mirana.pain.CodeException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.jooq.impl.DSL.row;
import static org.jooq.impl.DSL.using;

/**
 * @author trydofor
 * @since 2019-10-12
 */
public abstract class WingsJooqDaoImpl<S extends Table<R>, R extends UpdatableRecord<R>, P, T> extends DAOImpl<R, P, T> {

    private final S table;
    private final S alias;
    private final S deleted;
    private final S updated;

    protected WingsJooqDaoImpl(S table, S alias, Class<P> type) {
        super(table, type);
        this.table = table;
        this.alias = alias;
        this.deleted = null;
        this.updated = null;
    }

    protected WingsJooqDaoImpl(S table, S alias, Class<P> type, Configuration conf) {
        super(table, type, conf);
        this.table = table;
        this.alias = alias;
        this.deleted = null;
        this.updated = null;
    }

    protected WingsJooqDaoImpl(S table, S alias, Class<P> type, Configuration conf, S deleted, S updated) {
        super(table, type, conf);
        this.table = table;
        this.alias = alias;
        this.deleted = deleted;
        this.updated = updated;
    }

    // ======= select =======

    @SuppressWarnings("unchecked")
    @NotNull
    public S as(String alias) {
        return (S) table.as(alias);
    }

    /**
     * 获得为了读使用的table
     *
     * @return 表
     */
    @NotNull
    public S getAliasForReader() {
        return alias;
    }


    public long count(Condition condition) {
        return count(alias, condition);
    }

    @NotNull
    public List<P> fetch(Condition condition, OrderField<?>... orderBy) {
        return fetch(alias, condition, orderBy);
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
        return fetch(alias, offset, limit, orderBy);
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
        return fetch(alias, offset, limit, condition, orderBy);
    }

    @Nullable
    public P fetchOne(Condition condition) {
        return fetch(alias, condition);
    }

    // ======= trace deleted =======

    /**
     * 获得删除影子表
     *
     * @return 表
     */
    @Nullable
    public S getTraceOfDeleted() {
        return deleted;
    }

    public long countDeleted(Condition condition) {
        return count(deleted, condition);
    }

    @NotNull
    public List<P> fetchDeleted(Condition condition, OrderField<?>... orderBy) {
        return fetch(deleted, condition, orderBy);
    }

    @NotNull
    public List<P> fetchDeleted(int offset, int limit, OrderField<?>... orderBy) {
        return fetch(deleted, offset, limit, orderBy);
    }

    @NotNull
    public List<P> fetchDeleted(int offset, int limit, Condition condition, OrderField<?>... orderBy) {
        return fetch(deleted, offset, limit, condition, orderBy);
    }

    @Nullable
    public P fetchOneDeleted(Condition condition) {
        return fetch(deleted, condition);
    }

    // ======= trace updated =======

    /**
     * 获得更新影子表
     *
     * @return 表
     */
    @Nullable
    public S getTraceOfUpdated() {
        return updated;
    }

    public long countUpdated(Condition condition) {
        return count(updated, condition);
    }

    @NotNull
    public List<P> fetchUpdated(Condition condition, OrderField<?>... orderBy) {
        return fetch(updated, condition, orderBy);
    }

    @NotNull
    public List<P> fetchUpdated(int offset, int limit, OrderField<?>... orderBy) {
        return fetch(updated, offset, limit, orderBy);
    }

    @NotNull
    public List<P> fetchUpdated(int offset, int limit, Condition condition, OrderField<?>... orderBy) {
        return fetch(updated, offset, limit, condition, orderBy);
    }

    @Nullable
    public P fetchOneUpdated(Condition condition) {
        return fetch(updated, condition);
    }

    // ======= modify =======

    /**
     * 获得为了写使用的table
     *
     * @return 表
     */
    @NotNull
    public S getTableForWriter() {
        return table;
    }

    /**
     * 必须插入一个，否则CodeException(orError)
     *
     * @param object  对象
     * @param orError 异常code
     */
    public int insertOne(P object, CodeEnum orError) {
        int rc = using(configuration()).newRecord(table, object).insert();
        if (rc != 1) throw new CodeException(orError);
        return rc;
    }

    /**
     * 批量插入N，必须N条，否则CodeException(orError)
     *
     * @param objects 批量对象
     * @param orError 异常code
     */
    public int[] insertEqN(Collection<P> objects, CodeEnum orError) {
        DSLContext dsl = using(configuration());
        List<R> records = new ArrayList<>(objects.size());
        for (P po : objects) {
            records.add(dsl.newRecord(table, po));
        }

        int[] rc = dsl.batchInsert(records).execute();
        for (int i = 0; i < rc.length; i++) {
            if (rc[i] != 1) throw new CodeException(orError);
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
        return using(configuration())
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
        return using(configuration())
                .update(table)
                .set(setter)
                .where(condition)
                .execute();
    }


    /**
     * 按对象和条件更新，null被忽略
     *
     * @param object    对象
     * @param condition 条件
     * @return 更新数量
     */
    public int update(P object, Condition condition) {
        DSLContext dsl = using(configuration());
        R record = dsl.newRecord(table, object);

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
     * @param object   对象
     * @param skipNull null字段不被更新
     * @return 更新数量
     */
    public int update(P object, boolean skipNull) {
        DSLContext dsl = using(configuration());
        R record = dsl.newRecord(table, object);
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
        DSLContext dsl = using(configuration());
        for (P object : objects) {
            R record = dsl.newRecord(table, object);
            dealPkAndNull(record, skipNull);
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
        for (int i = 0; i < rc.length; i++) {
            if (rc[i] != 1) throw new CodeException(orError);
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
        for (int i = 0; i < rc.length; i++) {
            if (rc[i] > 1) throw new CodeException(orError);
        }
        return rc;
    }


    // ============

    @Override
    public boolean existsById(T id) {
        Field<?>[] pk = pk();

        if (pk != null) {
            return using(configuration())
                    .selectCount()
                    .from(alias)
                    .where(equal(pk, id))
                    .fetchOne(0, Integer.class) > 0;
        } else {
            return false;
        }
    }

    @Override
    public long count() {
        return using(configuration())
                .selectCount()
                .from(alias)
                .fetchOne(0, Long.class);
    }

    @Override
    public List<P> findAll() {
        return using(configuration())
                .selectFrom(alias)
                .fetch()
                .map(mapper());
    }

    @Override
    public P findById(T id) {
        Field<?>[] pk = pk();
        R record = null;

        if (pk != null) {
            record = using(configuration())
                    .selectFrom(alias)
                    .where(equal(pk, id))
                    .fetchOne();
        }

        return record == null ? null : mapper().map(record);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <Z> List<P> fetch(Field<Z> field, Z... values) {
        return fetch(alias, field, values);
    }


    @Override
    public <Z> P fetchOne(Field<Z> field, Z value) {
        return fetch(alias, field.equal(value));
    }

    // ==========
    private static final Field<?>[] EMPTY_PK = new Field<?>[0];

    private Field<?>[] pk() {
        UniqueKey<?> key = alias.getPrimaryKey();
        return key == null ? EMPTY_PK : key.getFieldsArray();
    }

    @SuppressWarnings("unchecked")
    private Condition equal(Field<?>[] pk, T id) {
        if (pk.length == 1) {
            return ((Field<Object>) pk[0]).equal(pk[0].getDataType().convert(id));
        }

        // [#2573] Composite key T types are of type Record[N]
        else {
            return row(pk).equal((Record) id);
        }
    }

    private void dealPkAndNull(R record, boolean skipNull) {
        for (Field<?> field : pk()) {
            record.changed(field, false);
        }

        if (!skipNull) return;

        int size = record.size();
        for (int i = 0; i < size; i++) {
            if (record.get(i) == null) {
                record.changed(i, false);
            }
        }
    }

    // ===========

    private P fetch(S alias, Condition condition) {
        R record = using(configuration())
                .selectFrom(alias)
                .where(condition)
                .fetchOne();

        return record == null ? null : mapper().map(record);
    }

    private <Z> List<P> fetch(S t, Field<Z> field, Z[] values) {
        return using(configuration())
                .selectFrom(t)
                .where(field.in(values))
                .fetch()
                .map(mapper());
    }

    private Long count(S alias, Condition condition) {
        return using(configuration())
                .selectCount()
                .from(alias)
                .where(condition)
                .fetchOne(0, Long.class);
    }

    private List<P> fetch(S t, int offset, int limit, OrderField<?>[] orderBy) {
        DSLContext dsl = using(configuration());
        if (orderBy != null && orderBy.length > 0) {
            return dsl
                    .selectFrom(t)
                    .orderBy(orderBy)
                    .limit(offset, limit)
                    .fetch()
                    .map(mapper());
        } else {
            return dsl
                    .selectFrom(t)
                    .limit(offset, limit)
                    .fetch()
                    .map(mapper());
        }
    }

    private List<P> fetch(S t, int offset, int limit, Condition condition, OrderField<?>[] orderBy) {
        DSLContext dsl = using(configuration());
        if (orderBy != null && orderBy.length > 0) {
            return dsl
                    .selectFrom(t)
                    .where(condition)
                    .orderBy(orderBy)
                    .limit(offset, limit)
                    .fetch()
                    .map(mapper());
        } else {
            return dsl
                    .selectFrom(t)
                    .where(condition)
                    .limit(offset, limit)
                    .fetch()
                    .map(mapper());
        }
    }

    private List<P> fetch(S t, Condition condition, OrderField<?>[] orderBy) {
        DSLContext dsl = using(configuration());
        if (orderBy != null && orderBy.length > 0) {
            return dsl
                    .selectFrom(t)
                    .where(condition)
                    .orderBy(orderBy)
                    .fetch()
                    .map(mapper());
        } else {
            return dsl
                    .selectFrom(t)
                    .where(condition)
                    .fetch()
                    .map(mapper());
        }
    }
}
