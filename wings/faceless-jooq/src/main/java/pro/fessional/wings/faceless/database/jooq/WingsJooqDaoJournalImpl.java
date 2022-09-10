package pro.fessional.wings.faceless.database.jooq;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jooq.Condition;
import org.jooq.Configuration;
import org.jooq.Field;
import org.jooq.QueryPart;
import org.jooq.Record;
import org.jooq.RecordMapper;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.UpdatableRecord;
import pro.fessional.wings.faceless.service.journal.JournalService;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static org.jooq.impl.DSL.noCondition;
import static org.jooq.impl.DSL.row;

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

    ///////////////// fields /////////////////////
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

    ///////////////// select list /////////////////////

    @NotNull
    public List<P> fetchLive(Function<T, Condition> fun) {
        final Condition cond = fun.apply(table);
        return fetchLive(table, table.onlyLive(cond), SelectOrderCondition.getSelectsOrders(cond));
    }

    @NotNull
    public List<P> fetchLive(int limit, Function<T, Condition> fun) {
        return fetchLive(0, limit, fun);
    }

    @NotNull
    public List<P> fetchLive(int offset, int limit, Function<T, Condition> fun) {
        final Condition cond = fun.apply(table);
        return fetch(table, offset, limit, table.onlyLive(cond), SelectOrderCondition.getSelectsOrders(cond));
    }

    ////////
    @NotNull
    public List<P> fetchLive(T table, Condition cond) {
        return fetchLive(table, -1, -1, table.onlyLive(cond));
    }

    @NotNull
    public List<P> fetchLive(T table, Condition cond, QueryPart... selectsOrders) {
        return fetch(table, -1, -1, table.onlyLive(cond), selectsOrders);
    }

    @NotNull
    public List<P> fetchLive(T table, int limit, QueryPart... selectsOrders) {
        return fetch(table, 0, limit, table.getOnlyLive(), selectsOrders);
    }

    @NotNull
    public List<P> fetchLive(T table, int offset, int limit, QueryPart... selectsOrders) {
        return fetch(table, offset, limit, table.getOnlyLive(), selectsOrders);
    }

    @NotNull
    public List<P> fetchLive(T table, int limit, Condition cond, QueryPart... selectsOrders) {
        return fetch(table, 0, limit, table.onlyLive(cond), selectsOrders);
    }

    @NotNull
    public List<P> fetchLive(T table, int offset, int limit, Condition cond, QueryPart... selectsOrders) {
        return fetch(table, offset, limit, table.onlyLive(cond), selectsOrders);
    }

    ////////
    @NotNull
    public <E> List<E> fetchLive(Class<E> claz, T table, QueryPart... selectsOrders) {
        return fetch(claz, table, table.getOnlyLive(), selectsOrders);
    }

    @NotNull
    public <E> List<E> fetchLive(Class<E> claz, T table, Condition cond, QueryPart... selectsOrders) {
        return fetch(claz, -1, -1, table, table.onlyLive(cond), selectsOrders);
    }

    @NotNull
    public <E> List<E> fetchLive(Class<E> claz, int limit, T table, QueryPart... selectsOrders) {
        return fetch(claz, 0, limit, table, table.getOnlyLive(), selectsOrders);
    }

    @NotNull
    public <E> List<E> fetchLive(Class<E> claz, int offset, int limit, T table, QueryPart... selectsOrders) {
        return fetch(claz, offset, limit, table, table.getOnlyLive(), selectsOrders);
    }

    @NotNull
    public <E> List<E> fetchLive(Class<E> claz, int limit, T table, Condition cond, QueryPart... selectsOrders) {
        return fetch(claz, 0, limit, table, table.onlyLive(cond), selectsOrders);
    }

    @NotNull
    public <E> List<E> fetchLive(Class<E> claz, int offset, int limit, T table, Condition cond, QueryPart... selectsOrders) {
        return fetch(claz, offset, limit, table, table.onlyLive(cond), selectsOrders);
    }

    ////////
    @NotNull
    public <E> List<E> fetchLive(RecordMapper<? super Record, E> mapper, T table, QueryPart... selectsOrders) {
        return fetch(mapper, table, table.getOnlyLive(), selectsOrders);
    }

    @NotNull
    public <E> List<E> fetchLive(RecordMapper<? super Record, E> mapper, T table, Condition cond, QueryPart... selectsOrders) {
        return fetch(mapper, -1, -1, table, table.onlyLive(cond), selectsOrders);
    }

    @NotNull
    public <E> List<E> fetchLive(RecordMapper<? super Record, E> mapper, int limit, T table, QueryPart... selectsOrders) {
        return fetch(mapper, 0, limit, table, table.getOnlyLive(), selectsOrders);
    }

    @NotNull
    public <E> List<E> fetchLive(RecordMapper<? super Record, E> mapper, int offset, int limit, T table, QueryPart... selectsOrders) {
        return fetch(mapper, offset, limit, table, table.getOnlyLive(), selectsOrders);
    }

    @NotNull
    public <E> List<E> fetchLive(RecordMapper<? super Record, E> mapper, int limit, T table, Condition cond, QueryPart... selectsOrders) {
        return fetch(mapper, 0, limit, table, table.onlyLive(cond), selectsOrders);
    }

    @NotNull
    public <E> List<E> fetchLive(RecordMapper<? super Record, E> mapper, int offset, int limit, T table, Condition cond, QueryPart... selectsOrders) {
        return fetch(mapper, offset, limit, table, table.onlyLive(cond), selectsOrders);
    }

    ///////////////// select one /////////////////////
    @Nullable
    public P fetchOneLive(Function<T, Condition> fun) {
        final Condition cond = fun.apply(table);
        return fetchOne(table, table.onlyLive(cond), SelectOrderCondition.getSelectsOrders(cond));
    }

    @Nullable
    public P fetchLimitOneLive(Function<T, Condition> fun) {
        final Condition cond = fun.apply(table);
        return fetchLimitOne(table, table.onlyLive(cond), SelectOrderCondition.getSelectsOrders(cond));
    }

    @NotNull
    public Optional<P> fetchOptionalLive(Function<T, Condition> fun) {
        return Optional.ofNullable(fetchOne(fun));
    }

    @NotNull
    public Optional<P> fetchLimitOptionalLive(Function<T, Condition> fun) {
        return Optional.ofNullable(fetchLimitOne(fun));
    }

    /////////////////
    @Nullable
    public P fetchOneLive(T table, QueryPart... selectsOrders) {
        return fetchOne(table, table.getOnlyLive(), selectsOrders);
    }

    @Nullable
    public P fetchLimitOneLive(T table, QueryPart... selectsOrders) {
        return fetchLimitOne(table, table.getOnlyLive(), selectsOrders);
    }

    @NotNull
    public Optional<P> fetchOptionalLive(T table, QueryPart... selectsOrders) {
        return Optional.ofNullable(fetchOne(table, table.getOnlyLive(), selectsOrders));
    }

    @NotNull
    public Optional<P> fetchLimitOptionalLive(T table, QueryPart... selectsOrders) {
        return Optional.ofNullable(fetchLimitOne(table, table.getOnlyLive(), selectsOrders));
    }

    public P fetchOneLive(T table, Condition cond, QueryPart... selectsOrders) {
        return fetchOne(table, table.onlyLive(cond), selectsOrders);
    }

    @Nullable
    public P fetchLimitOneLive(T table, Condition cond, QueryPart... selectsOrders) {
        return fetchOne(table, table.onlyLive(cond), selectsOrders);
    }

    @NotNull
    public Optional<P> fetchOptionalLive(T table, Condition cond, QueryPart... selectsOrders) {
        return Optional.ofNullable(fetchOne(table, table.onlyLive(cond), selectsOrders));
    }

    @NotNull
    public Optional<P> fetchLimitOptionalLive(T table, Condition cond, QueryPart... selectsOrders) {
        return Optional.ofNullable(fetchLimitOne(table, table.onlyLive(cond), selectsOrders));
    }

    /////////////////
    @Nullable
    public <E> E fetchOneLive(Class<E> claz, T table, QueryPart... selectsOrders) {
        return fetchOne(claz, table, table.getOnlyLive(), selectsOrders);
    }

    @Nullable
    public <E> E fetchLimitOneLive(Class<E> claz, T table, QueryPart... selectsOrders) {
        return fetchLimitOne(claz, table, table.getOnlyLive(), selectsOrders);
    }

    @NotNull
    public <E> Optional<E> fetchOptionalLive(Class<E> claz, T table, QueryPart... selectsOrders) {
        return Optional.ofNullable(fetchOne(claz, table, table.getOnlyLive(), selectsOrders));
    }

    @NotNull
    public <E> Optional<E> fetchLimitOptionalLive(Class<E> claz, T table, QueryPart... selectsOrders) {
        return Optional.ofNullable(fetchLimitOne(claz, table, table.getOnlyLive(), selectsOrders));
    }

    public <E> E fetchOneLive(Class<E> claz, T table, Condition cond, QueryPart... selectsOrders) {
        return fetchOne(claz, table, table.onlyLive(cond), selectsOrders);
    }

    @Nullable
    public <E> E fetchLimitOneLive(Class<E> claz, T table, Condition cond, QueryPart... selectsOrders) {
        return fetchOne(claz, table, table.onlyLive(cond), selectsOrders);
    }

    @NotNull
    public <E> Optional<E> fetchOptionalLive(Class<E> claz, T table, Condition cond, QueryPart... selectsOrders) {
        return Optional.ofNullable(fetchOne(claz, table, table.onlyLive(cond), selectsOrders));
    }

    @NotNull
    public <E> Optional<E> fetchLimitOptionalLive(Class<E> claz, T table, Condition cond, QueryPart... selectsOrders) {
        return Optional.ofNullable(fetchLimitOne(claz, table, table.onlyLive(cond), selectsOrders));
    }

    /////////////////
    @Nullable
    public <E> E fetchOneLive(RecordMapper<? super Record, E> mapper, T table, QueryPart... selectsOrders) {
        return fetchOne(mapper, table, table.getOnlyLive(), selectsOrders);
    }

    @Nullable
    public <E> E fetchLimitOneLive(RecordMapper<? super Record, E> mapper, T table, QueryPart... selectsOrders) {
        return fetchLimitOne(mapper, table, table.getOnlyLive(), selectsOrders);
    }

    @NotNull
    public <E> Optional<E> fetchOptionalLive(RecordMapper<? super Record, E> mapper, T table, QueryPart... selectsOrders) {
        return Optional.ofNullable(fetchOne(mapper, table, table.getOnlyLive(), selectsOrders));
    }

    @NotNull
    public <E> Optional<E> fetchLimitOptionalLive(RecordMapper<? super Record, E> mapper, T table, QueryPart... selectsOrders) {
        return Optional.ofNullable(fetchLimitOne(mapper, table, table.getOnlyLive(), selectsOrders));
    }

    public <E> E fetchOneLive(RecordMapper<? super Record, E> mapper, T table, Condition cond, QueryPart... selectsOrders) {
        return fetchOne(mapper, table, table.onlyLive(cond), selectsOrders);
    }

    @Nullable
    public <E> E fetchLimitOneLive(RecordMapper<? super Record, E> mapper, T table, Condition cond, QueryPart... selectsOrders) {
        return fetchOne(mapper, table, table.onlyLive(cond), selectsOrders);
    }

    @NotNull
    public <E> Optional<E> fetchOptionalLive(RecordMapper<? super Record, E> mapper, T table, Condition cond, QueryPart... selectsOrders) {
        return Optional.ofNullable(fetchOne(mapper, table, table.onlyLive(cond), selectsOrders));
    }

    @NotNull
    public <E> Optional<E> fetchLimitOptionalLive(RecordMapper<? super Record, E> mapper, T table, Condition cond, QueryPart... selectsOrders) {
        return Optional.ofNullable(fetchLimitOne(mapper, table, table.onlyLive(cond), selectsOrders));
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
        return deleteById(commit, Arrays.asList(ids));
    }

    private static final Record[] EMPTY_RECORD = {};

    /**
     * 按id逻辑删除
     *
     * @param commit journal
     * @param ids    ids
     * @return 影响的数据条数
     */
    public int deleteById(JournalService.Journal commit, Collection<K> ids) {
        // 参考DAOImpl deleteById
        final Condition cond;
        if (pkeys.length == 1) {
            @SuppressWarnings("unchecked")
            final Field<Object> pk = (Field<Object>) pkeys[0];
            if (ids.size() == 1) {
                cond = pk.eq(pk.getDataType().convert(ids.iterator().next()));
            }
            else {
                cond = pk.in(pk.getDataType().convert(ids));
            }
        }
        // [#2573] Composite key T types are of type Record[N]
        else {
            @SuppressWarnings("SuspiciousToArrayCall")
            final Record[] rn = ids.toArray(EMPTY_RECORD);
            cond = row(pkeys).in(rn);
        }

        return delete(commit, table, cond);
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
        return fetch(table, table.getOnlyLive());
    }
}
