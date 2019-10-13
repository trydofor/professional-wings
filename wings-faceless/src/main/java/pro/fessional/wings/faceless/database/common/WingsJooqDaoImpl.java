package pro.fessional.wings.faceless.database.common;

import org.jooq.Condition;
import org.jooq.Configuration;
import org.jooq.Field;
import org.jooq.OrderField;
import org.jooq.Record;
import org.jooq.Table;
import org.jooq.UniqueKey;
import org.jooq.UpdatableRecord;
import org.jooq.impl.DAOImpl;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.jooq.impl.DSL.row;
import static org.jooq.impl.DSL.using;

/**
 * @author trydofor
 * @since 2019-10-12
 */
public abstract class WingsJooqDaoImpl<S extends Table<R>, R extends UpdatableRecord<R>, P, T> extends DAOImpl<R, P, T> {

    private final S alias;
    private final S table;

    protected WingsJooqDaoImpl(S table, S alias, Class<P> type) {
        super(table, type);
        this.table = table;
        this.alias = alias;
    }

    protected WingsJooqDaoImpl(S table, S alias, Class<P> type, Configuration configuration) {
        super(table, type, configuration);
        this.table = table;
        this.alias = alias;
    }

    /**
     * 获得为了读使用的table
     *
     * @return 表
     */
    public S getTableForReader() {
        return alias;
    }

    /**
     * 获得为了写使用的table
     *
     * @return 表
     */
    public S getTableForWriter() {
        return table;
    }

    @SuppressWarnings("unchecked")
    public S as(String alias) {
        return (S) table.as(alias);
    }

    @Override
    public S getTable() {
        return table;
    }

    // ======= select =======
    public long count(Condition... condition) {
        return using(configuration())
                .selectCount()
                .from(alias)
                .where(condition)
                .fetchOne(0, Long.class);
    }

    public List<P> fetch(Condition... condition) {
        return using(configuration())
                .selectFrom(alias)
                .where(condition)
                .fetch()
                .map(mapper());
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
     * @return 结果集
     */
    public List<P> fetch(int offset, int limit, Condition... condition) {
        return using(configuration())
                .selectFrom(alias)
                .where(condition)
                .limit(offset, limit)
                .fetch()
                .map(mapper());
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
     * @param oderBy    排序字段
     * @param condition 条件
     * @return 结果集
     */
    public List<P> fetch(int offset, int limit, Collection<? extends OrderField<?>> oderBy, Condition... condition) {
        return using(configuration())
                .selectFrom(alias)
                .where(condition)
                .orderBy(oderBy)
                .limit(offset, limit)
                .fetch()
                .map(mapper());
    }

    public P fetchOne(Condition... condition) {
        R record = using(configuration())
                .selectFrom(alias)
                .where(condition)
                .fetchOne();

        return record == null ? null : mapper().map(record);
    }

    // ======= modify =======

    /**
     * 按条件删除
     *
     * @param condition 更新条件
     * @return 影响的数据条数
     */
    public int delete(Condition... condition) {
        return using(configuration())
                .delete(table)
                .where(condition)
                .execute();
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
    public int update(Map<?, ?> setter, Condition... condition) {
        return using(configuration())
                .update(table)
                .set(setter)
                .where(condition)
                .execute();
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
        return using(configuration())
                .selectFrom(alias)
                .where(field.in(values))
                .fetch()
                .map(mapper());
    }

    @Override
    public <Z> P fetchOne(Field<Z> field, Z value) {
        R record = using(configuration())
                .selectFrom(alias)
                .where(field.equal(value))
                .fetchOne();

        return record == null ? null : mapper().map(record);
    }

    // ==========
    private Field<?>[] pk() {
        UniqueKey<?> key = alias.getPrimaryKey();
        return key == null ? null : key.getFieldsArray();
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
}
