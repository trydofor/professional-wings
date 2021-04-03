package pro.fessional.wings.faceless.database.jooq;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jooq.Condition;
import org.jooq.Configuration;
import org.jooq.Field;
import org.jooq.OrderField;
import org.jooq.Record;
import org.jooq.RecordMapper;
import org.jooq.SelectField;
import org.jooq.UpdatableRecord;
import org.jooq.impl.TableImpl;
import pro.fessional.wings.faceless.service.journal.JournalService;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.jooq.impl.DSL.noCondition;

/**
 * <pre>
 * 原则上，不希望Record携带的数据库信息扩散，因此建议Dao之外使用pojo
 *
 * 对于read方法，一律返回Pojo；对于write，同时支持 Record和Pojo。
 * 为了编码的便捷和减少数据拷贝，可以使用Record进行操作。
 * 批量处理中，一律使用了new Record，为了提升性能。
 *
 * 注意，alias 用在多表查询，filed和table需要同源，否则出现语法错误。
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
public abstract class WingsJooqDaoJournalImpl<T extends TableImpl<R> & WingsJournalTable<T>, R extends UpdatableRecord<R>, P, K> extends WingsJooqDaoAliasImpl<T, R, P, K> {

    protected WingsJooqDaoJournalImpl(T table, Class<P> type) {
        this(table, type, null);
    }

    protected WingsJooqDaoJournalImpl(T table, Class<P> type, Configuration conf) {
        super(table, type, conf);
    }

    ///////////////// select /////////////////////

    /**
     * @see #fetch(TableImpl, Condition)
     */
    public List<P> fetchLive(Condition cond) {
        return fetchLive(table, cond);
    }

    /**
     * @see #fetch(TableImpl, Condition)
     */
    public List<P> fetchLive(T table, Condition cond) {
        return fetch(table, table.onlyLive(cond));
    }

    /**
     * @see #fetch(TableImpl, Condition, OrderField[])
     */
    @NotNull
    public List<P> fetchLive(Condition cond, OrderField<?>... orderBy) {
        return fetchLive(table, cond, orderBy);
    }

    /**
     * @see #fetch(TableImpl, Condition, OrderField[])
     */
    @NotNull
    public List<P> fetchLive(T table, Condition cond, OrderField<?>... orderBy) {
        return fetch(table, table.onlyLive(cond), orderBy);
    }

    /**
     * @see #fetch(TableImpl, int, int, Condition, OrderField[])
     */
    @NotNull
    public List<P> fetchLive(int offset, int limit, OrderField<?>... orderBy) {
        return fetchLive(table, offset, limit, null, orderBy);
    }

    /**
     * @see #fetch(TableImpl, int, int, Condition, OrderField[])
     */
    @NotNull
    public List<P> fetchLive(int offset, int limit, Condition condition, OrderField<?>... orderBy) {
        return fetchLive(table, offset, limit, condition, orderBy);
    }

    /**
     * @see #fetch(TableImpl, int, int, Condition, OrderField[])
     */
    @NotNull
    public List<P> fetchLive(T table, int offset, int limit, Condition cond, OrderField<?>... orderBy) {
        return fetch(table, offset, limit, table.onlyLive(cond), orderBy);
    }

    /**
     * @see #fetchOne(TableImpl, Condition)
     */
    @Nullable
    public P fetchOneLive(Condition cond) {
        return fetchOneLive(table, cond);
    }

    /**
     * @see #fetchOne(TableImpl, Condition)
     */
    @Nullable
    public P fetchOneLive(T table, Condition cond) {
        return fetchOne(table, table.onlyLive(cond));
    }

    @NotNull
    public <Z> List<P> fetchRangeLive(Field<Z> field, Z lowerInclusive, Z upperInclusive) {
        final Condition cond = lowerInclusive == null
                               ? upperInclusive == null
                                 ? noCondition()
                                 : field.le(upperInclusive)
                               : upperInclusive == null
                                 ? field.ge(lowerInclusive)
                                 : field.between(lowerInclusive, upperInclusive);
        return fetchLive(table, cond);
    }

    @SuppressWarnings("unchecked")
    @NotNull
    public <Z> List<P> fetchLive(Field<Z> field, Z... values) {
        return fetchLive(table, field.in(values));
    }

    @Nullable
    public <Z> P fetchOneLive(Field<Z> field, Z value) {
        return fetchOneLive(table, field.equal(value));
    }

    @NotNull
    public <Z> Optional<P> fetchOptionalLive(Field<Z> field, Z value) {
        return Optional.ofNullable(fetchOneLive(field, value));
    }

    ///////////////// select into /////////////////////

    public <E> E fetchOneLive(Class<E> claz, SelectField<?>... fields) {
        return fetchOneLive(claz, table, null, fields);
    }

    public <E> E fetchOneLive(Class<E> claz, T table, SelectField<?>... fields) {
        return fetchOneLive(claz, table, null, fields);
    }

    public <E> E fetchOneLive(Class<E> claz, T table, Condition cond, SelectField<?>... fields) {
        return fetchOne(claz, table, table.onlyLive(cond), fields);
    }

    public <E> E fetchOneLive(RecordMapper<? super Record, E> mapper, SelectField<?>... fields) {
        return fetchOneLive(mapper, table, null, fields);
    }

    public <E> E fetchOneLive(RecordMapper<? super Record, E> mapper, T table, SelectField<?>... fields) {
        return fetchOneLive(mapper, table, null, fields);
    }

    public <E> E fetchOneLive(RecordMapper<? super Record, E> mapper, T table, Condition cond, SelectField<?>... fields) {
        return fetchOne(mapper, table, table.onlyLive(cond), fields);
    }

    public <E> List<E> fetchLive(Class<E> claz, SelectField<?>... fields) {
        return fetchLive(claz, table, null, fields);
    }

    public <E> List<E> fetchLive(Class<E> claz, T table, SelectField<?>... fields) {
        return fetchLive(claz, table, null, fields);
    }

    public <E> List<E> fetchLive(Class<E> claz, T table, Condition cond, SelectField<?>... fields) {
        return fetch(claz, table, table.onlyLive(cond), fields);
    }

    public <E> List<E> fetchLive(RecordMapper<? super Record, E> mapper, SelectField<?>... fields) {
        return fetchLive(mapper, table, null, fields);
    }

    public <E> List<E> fetchLive(RecordMapper<? super Record, E> mapper, T table, SelectField<?>... fields) {
        return fetchLive(mapper, table, null, fields);
    }

    public <E> List<E> fetchLive(RecordMapper<? super Record, E> mapper, T table, Condition cond, SelectField<?>... fields) {
        return fetch(mapper, table, table.onlyLive(cond), fields);
    }

    ///////////////// delete /////////////////////

    /**
     * 逻辑删除
     *
     * @see #delete(TableImpl, Condition)
     */
    public int delete(JournalService.Journal commit, Condition cond) {
        return delete(table, cond);
    }

    /**
     * 按条件逻辑删除
     *
     * @param commit journal
     * @param table  表
     * @param cond   条件
     * @return 影响的数据条数
     */
    public int delete(JournalService.Journal commit, T table, Condition cond) {
        return ctx()
                       .update(table)
                       .set(table.markDelete(commit))
                       .where(cond)
                       .execute();
    }

    /**
     * 按id逻辑删除
     *
     * @param commit journal
     * @param ids    ids
     * @return 影响的数据条数
     */
    @SafeVarargs
    public final int deleteById(JournalService.Journal commit, K... ids) {
        return delete(commit, table, WingsJooqUtil.condCombo(ids, pkeys));
    }

    /**
     * 按id逻辑删除
     *
     * @param commit journal
     * @param ids    ids
     * @return 影响的数据条数
     */
    public int deleteById(JournalService.Journal commit, Collection<K> ids) {
        return delete(commit, table, WingsJooqUtil.condCombo(ids, pkeys));
    }

    ///////////////// update /////////////////////

    ///////////////// count /////////////////////

    /**
     * @see #count(TableImpl, Condition)
     */
    public long countLive(Condition cond) {
        return countLive(table, cond);
    }

    /**
     * @see #count(TableImpl, Condition)
     */
    public long countLive(T table) {
        return countLive(table, null);
    }

    /**
     * @see #count(TableImpl, Condition)
     */
    public long countLive(T table, Condition cond) {
        return count(table, table.onlyLive(cond));
    }

    ///////////////// super read /////////////////////

    /**
     * @see #count(TableImpl, Condition)
     */
    public long countLive() {
        return countLive(table, null);
    }

    /**
     * @see #fetchLive(Condition)
     */
    public List<P> findAllLive() {
        return fetchLive(null);
    }
}
