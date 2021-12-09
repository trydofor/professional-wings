package pro.fessional.wings.faceless.database.jooq;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jooq.Condition;
import org.jooq.Configuration;
import org.jooq.OrderField;
import org.jooq.Record;
import org.jooq.RecordMapper;
import org.jooq.Result;
import org.jooq.SelectField;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.UpdatableRecord;
import pro.fessional.wings.faceless.service.journal.JournalService;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

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
public abstract class WingsJooqDaoJournalImpl<T extends Table<R> & WingsJournalTable<T>, R extends UpdatableRecord<R>, P, K> extends WingsJooqDaoAliasImpl<T, R, P, K> {

    protected WingsJooqDaoJournalImpl(T table, Class<P> type) {
        super(table, type, null);
    }

    protected WingsJooqDaoJournalImpl(T table, Class<P> type, Configuration conf) {
        super(table, type, conf);
    }

    ///////////////// select /////////////////////

    /**
     * @see #fetch(Table, Condition)
     */
    @NotNull
    public List<P> fetchLive(Function<T, Condition> fun) {
        return fetchLive(table, fun.apply(table));
    }

    /**
     * @see #fetch(Table, Condition)
     */
    @NotNull
    public List<P> fetchLive(T table, Condition cond) {
        return fetch(table, table.onlyLive(cond));
    }

    /**
     * @see #fetch(Table, int, int, Condition, OrderField[])
     */
    @NotNull
    public List<P> fetchLive(T table, int offset, int limit, Condition cond, OrderField<?>... orderBy) {
        return fetch(table, offset, limit, table.onlyLive(cond), orderBy);
    }

    /**
     * @see #fetch(Table, Condition)
     */
    @Nullable
    public P fetchOneLive(Function<T, Condition> fun) {
        return fetchOneLive(table, fun.apply(table));
    }

    /**
     * @see #fetchOne(Table, Condition)
     */
    @Nullable
    public P fetchOneLive(T table, Condition cond) {
        return fetchOne(table, table.onlyLive(cond));
    }

    @SuppressWarnings("unchecked")
    @NotNull
    public <Z> List<P> fetchRangeLive(TableField<R, Z> field, Z lowerInclusive, Z upperInclusive) {
        final Condition cond = lowerInclusive == null
                               ? upperInclusive == null
                                 ? noCondition()
                                 : field.le(upperInclusive)
                               : upperInclusive == null
                                 ? field.ge(lowerInclusive)
                                 : field.between(lowerInclusive, upperInclusive);
        return fetchLive((T) field.getTable(), cond);
    }

    @SuppressWarnings("unchecked")
    @NotNull
    public <Z> List<P> fetchLive(TableField<R, Z> field, Z... values) {
        return fetchLive((T) field.getTable(), field.in(values));
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public <Z> P fetchOneLive(TableField<R, Z> field, Z value) {
        return fetchOneLive((T) field.getTable(), field.equal(value));
    }

    @NotNull
    public <Z> Optional<P> fetchOptionalLive(TableField<R, Z> field, Z value) {
        return Optional.ofNullable(fetchOneLive(field, value));
    }

    ///////////////// select into /////////////////////

    public <E> E fetchOneLive(Class<E> claz, T table, SelectField<?>... fields) {
        return fetchOneLive(claz, table, null, fields);
    }

    public <E> E fetchOneLive(Class<E> claz, T table, Condition cond, SelectField<?>... fields) {
        return fetchOne(claz, table, table.onlyLive(cond), fields);
    }

    public <E> E fetchOneLive(RecordMapper<? super Record, E> mapper, T table, SelectField<?>... fields) {
        return fetchOneLive(mapper, table, null, fields);
    }

    public <E> E fetchOneLive(RecordMapper<? super Record, E> mapper, T table, Condition cond, SelectField<?>... fields) {
        return fetchOne(mapper, table, table.onlyLive(cond), fields);
    }


    @NotNull
    public Result<Record> fetchLive(T table, Condition cond, SelectField<?>... fields) {
        return fetch(table, table.onlyLive(cond), fields);
    }

    @Nullable
    public Record fetchOneLive(T table, Condition cond, SelectField<?>... fields) {
        return fetchOne(table, table.onlyLive(cond), fields);
    }

    @NotNull
    public <E> List<E> fetchLive(Class<E> claz, T table, SelectField<?>... fields) {
        return fetchLive(claz, table, null, fields);
    }

    @NotNull
    public <E> List<E> fetchLive(Class<E> claz, T table, Condition cond, SelectField<?>... fields) {
        return fetch(claz, table, table.onlyLive(cond), fields);
    }

    @NotNull
    public <E> List<E> fetchLive(RecordMapper<? super Record, E> mapper, T table, SelectField<?>... fields) {
        return fetchLive(mapper, table, null, fields);
    }

    @NotNull
    public <E> List<E> fetchLive(RecordMapper<? super Record, E> mapper, T table, Condition cond, SelectField<?>... fields) {
        return fetch(mapper, table, table.onlyLive(cond), fields);
    }

    ///////////////// delete /////////////////////

    public int delete(JournalService.Journal commit, Function<T, Condition> fun) {
        return delete(commit, table, fun.apply(table));
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
     * @see #count(Table, Condition)
     */
    public long countLive(Function<T, Condition> fun) {
        return countLive(table, fun.apply(table));
    }

    /**
     * @see #count(Table, Condition)
     */
    public long countLive(T table, Condition cond) {
        return count(table, table.onlyLive(cond));
    }

    ///////////////// super read /////////////////////

    /**
     * @see #count(Table, Condition)
     */
    public long countLive() {
        return countLive(table, null);
    }

    /**
     * @see #fetchLive(Table, Condition)
     */
    @NotNull
    public List<P> findAllLive() {
        return fetchLive(table, null);
    }
}
