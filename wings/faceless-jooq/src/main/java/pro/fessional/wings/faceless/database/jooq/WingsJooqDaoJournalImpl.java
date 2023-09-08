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
import java.util.function.BiConsumer;
import java.util.function.Function;

import static org.jooq.impl.DSL.noCondition;
import static org.jooq.impl.DSL.row;

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

    @SuppressWarnings("unchecked")
    @NotNull
    public <Z> List<P> fetchLive(TableField<R, Z> field, Collection<? extends Z> values) {
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
        return fetchLive(table, table.onlyLive(cond));
    }

    @NotNull
    public List<P> fetchLive(int limit, Function<T, Condition> fun) {
        return fetchLive(0, limit, fun);
    }

    @NotNull
    public List<P> fetchLive(int offset, int limit, Function<T, Condition> fun) {
        final Condition cond = fun.apply(table);
        return fetch(table, offset, limit, table.onlyLive(cond));
    }

    @NotNull
    public List<P> fetchLive(BiConsumer<T, SelectWhereOrder> fun) {
        final SelectWhereOrder soc = new SelectWhereOrder();
        fun.accept(table, soc);
        return fetchLive(table, table.onlyLive(soc.getWhere()), soc.getParts());
    }

    @NotNull
    public List<P> fetchLive(int limit, BiConsumer<T, SelectWhereOrder> fun) {
        return fetchLive(0, limit, fun);
    }

    @NotNull
    public List<P> fetchLive(int offset, int limit, BiConsumer<T, SelectWhereOrder> fun) {
        final SelectWhereOrder soc = new SelectWhereOrder();
        fun.accept(table, soc);
        return fetch(table, offset, limit, table.onlyLive(soc.getWhere()), soc.getParts());
    }

    @NotNull
    public <E> List<E> fetchLive(Class<E> claz, BiConsumer<T, SelectWhereOrder> fun) {
        final SelectWhereOrder soc = new SelectWhereOrder();
        fun.accept(table, soc);
        return fetchLive(claz, table, table.onlyLive(soc.getWhere()), soc.getParts());
    }

    @NotNull
    public <E> List<E> fetchLive(Class<E> claz, int limit, BiConsumer<T, SelectWhereOrder> fun) {
        return fetchLive(claz, 0, limit, fun);
    }

    @NotNull
    public <E> List<E> fetchLive(Class<E> claz, int offset, int limit, BiConsumer<T, SelectWhereOrder> fun) {
        final SelectWhereOrder soc = new SelectWhereOrder();
        fun.accept(table, soc);
        return fetch(claz, offset, limit, table, table.onlyLive(soc.getWhere()), soc.getParts());
    }


    @NotNull
    public <E> List<E> fetchLive(RecordMapper<? super Record, E> mapper, BiConsumer<T, SelectWhereOrder> fun) {
        final SelectWhereOrder soc = new SelectWhereOrder();
        fun.accept(table, soc);
        return fetchLive(mapper, table, table.onlyLive(soc.getWhere()), soc.getParts());
    }

    @NotNull
    public <E> List<E> fetchLive(RecordMapper<? super Record, E> mapper, int limit, BiConsumer<T, SelectWhereOrder> fun) {
        return fetchLive(mapper, 0, limit, fun);
    }

    @NotNull
    public <E> List<E> fetchLive(RecordMapper<? super Record, E> mapper, int offset, int limit, BiConsumer<T, SelectWhereOrder> fun) {
        final SelectWhereOrder soc = new SelectWhereOrder();
        fun.accept(table, soc);
        return fetch(mapper, offset, limit, table, table.onlyLive(soc.getWhere()), soc.getParts());
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
    public List<P> fetchLive(T table, Condition cond, Collection<? extends QueryPart> selectsOrders) {
        return fetch(table, -1, -1, table.onlyLive(cond), selectsOrders);
    }

    @NotNull
    public List<P> fetchLive(T table, int limit, QueryPart... selectsOrders) {
        return fetch(table, 0, limit, table.getOnlyLive(), selectsOrders);
    }

    @NotNull
    public List<P> fetchLive(T table, int limit, Collection<? extends QueryPart> selectsOrders) {
        return fetch(table, 0, limit, table.getOnlyLive(), selectsOrders);
    }

    @NotNull
    public List<P> fetchLive(T table, int offset, int limit, QueryPart... selectsOrders) {
        return fetch(table, offset, limit, table.getOnlyLive(), selectsOrders);
    }

    @NotNull
    public List<P> fetchLive(T table, int offset, int limit, Collection<? extends QueryPart> selectsOrders) {
        return fetch(table, offset, limit, table.getOnlyLive(), selectsOrders);
    }

    @NotNull
    public List<P> fetchLive(T table, int limit, Condition cond, QueryPart... selectsOrders) {
        return fetch(table, 0, limit, table.onlyLive(cond), selectsOrders);
    }

    @NotNull
    public List<P> fetchLive(T table, int limit, Condition cond, Collection<? extends QueryPart> selectsOrders) {
        return fetch(table, 0, limit, table.onlyLive(cond), selectsOrders);
    }

    @NotNull
    public List<P> fetchLive(T table, int offset, int limit, Condition cond, QueryPart... selectsOrders) {
        return fetch(table, offset, limit, table.onlyLive(cond), selectsOrders);
    }

    @NotNull
    public List<P> fetchLive(T table, int offset, int limit, Condition cond, Collection<? extends QueryPart> selectsOrders) {
        return fetch(table, offset, limit, table.onlyLive(cond), selectsOrders);
    }

    ////////
    @NotNull
    public <E> List<E> fetchLive(Class<E> claz, T table, QueryPart... selectsOrders) {
        return fetch(claz, table, table.getOnlyLive(), selectsOrders);
    }

     @NotNull
    public <E> List<E> fetchLive(Class<E> claz, T table, Collection<? extends QueryPart> selectsOrders) {
        return fetch(claz, table, table.getOnlyLive(), selectsOrders);
    }

    @NotNull
    public <E> List<E> fetchLive(Class<E> claz, T table, Condition cond, QueryPart... selectsOrders) {
        return fetch(claz, -1, -1, table, table.onlyLive(cond), selectsOrders);
    }

    @NotNull
    public <E> List<E> fetchLive(Class<E> claz, T table, Condition cond, Collection<? extends QueryPart> selectsOrders) {
        return fetch(claz, -1, -1, table, table.onlyLive(cond), selectsOrders);
    }

    @NotNull
    public <E> List<E> fetchLive(Class<E> claz, int limit, T table, QueryPart... selectsOrders) {
        return fetch(claz, 0, limit, table, table.getOnlyLive(), selectsOrders);
    }

    @NotNull
    public <E> List<E> fetchLive(Class<E> claz, int limit, T table, Collection<? extends QueryPart> selectsOrders) {
        return fetch(claz, 0, limit, table, table.getOnlyLive(), selectsOrders);
    }

    @NotNull
    public <E> List<E> fetchLive(Class<E> claz, int offset, int limit, T table, QueryPart... selectsOrders) {
        return fetch(claz, offset, limit, table, table.getOnlyLive(), selectsOrders);
    }

    @NotNull
    public <E> List<E> fetchLive(Class<E> claz, int offset, int limit, T table, Collection<? extends QueryPart> selectsOrders) {
        return fetch(claz, offset, limit, table, table.getOnlyLive(), selectsOrders);
    }

    @NotNull
    public <E> List<E> fetchLive(Class<E> claz, int limit, T table, Condition cond, QueryPart... selectsOrders) {
        return fetch(claz, 0, limit, table, table.onlyLive(cond), selectsOrders);
    }

    @NotNull
    public <E> List<E> fetchLive(Class<E> claz, int limit, T table, Condition cond, Collection<? extends QueryPart> selectsOrders) {
        return fetch(claz, 0, limit, table, table.onlyLive(cond), selectsOrders);
    }

    @NotNull
    public <E> List<E> fetchLive(Class<E> claz, int offset, int limit, T table, Condition cond, QueryPart... selectsOrders) {
        return fetch(claz, offset, limit, table, table.onlyLive(cond), selectsOrders);
    }

    @NotNull
    public <E> List<E> fetchLive(Class<E> claz, int offset, int limit, T table, Condition cond, Collection<? extends QueryPart> selectsOrders) {
        return fetch(claz, offset, limit, table, table.onlyLive(cond), selectsOrders);
    }

    ////////
    @NotNull
    public <E> List<E> fetchLive(RecordMapper<? super Record, E> mapper, T table, QueryPart... selectsOrders) {
        return fetch(mapper, table, table.getOnlyLive(), selectsOrders);
    }

    @NotNull
    public <E> List<E> fetchLive(RecordMapper<? super Record, E> mapper, T table, Collection<? extends QueryPart> selectsOrders) {
        return fetch(mapper, table, table.getOnlyLive(), selectsOrders);
    }

    @NotNull
    public <E> List<E> fetchLive(RecordMapper<? super Record, E> mapper, T table, Condition cond, QueryPart... selectsOrders) {
        return fetch(mapper, -1, -1, table, table.onlyLive(cond), selectsOrders);
    }

    @NotNull
    public <E> List<E> fetchLive(RecordMapper<? super Record, E> mapper, T table, Condition cond, Collection<? extends QueryPart> selectsOrders) {
        return fetch(mapper, -1, -1, table, table.onlyLive(cond), selectsOrders);
    }

    @NotNull
    public <E> List<E> fetchLive(RecordMapper<? super Record, E> mapper, int limit, T table, QueryPart... selectsOrders) {
        return fetch(mapper, 0, limit, table, table.getOnlyLive(), selectsOrders);
    }

    @NotNull
    public <E> List<E> fetchLive(RecordMapper<? super Record, E> mapper, int limit, T table, Collection<? extends QueryPart> selectsOrders) {
        return fetch(mapper, 0, limit, table, table.getOnlyLive(), selectsOrders);
    }

    @NotNull
    public <E> List<E> fetchLive(RecordMapper<? super Record, E> mapper, int offset, int limit, T table, QueryPart... selectsOrders) {
        return fetch(mapper, offset, limit, table, table.getOnlyLive(), selectsOrders);
    }

    @NotNull
    public <E> List<E> fetchLive(RecordMapper<? super Record, E> mapper, int offset, int limit, T table, Collection<? extends QueryPart> selectsOrders) {
        return fetch(mapper, offset, limit, table, table.getOnlyLive(), selectsOrders);
    }

    @NotNull
    public <E> List<E> fetchLive(RecordMapper<? super Record, E> mapper, int limit, T table, Condition cond, QueryPart... selectsOrders) {
        return fetch(mapper, 0, limit, table, table.onlyLive(cond), selectsOrders);
    }

    @NotNull
    public <E> List<E> fetchLive(RecordMapper<? super Record, E> mapper, int limit, T table, Condition cond, Collection<? extends QueryPart> selectsOrders) {
        return fetch(mapper, 0, limit, table, table.onlyLive(cond), selectsOrders);
    }

    @NotNull
    public <E> List<E> fetchLive(RecordMapper<? super Record, E> mapper, int offset, int limit, T table, Condition cond, QueryPart... selectsOrders) {
        return fetch(mapper, offset, limit, table, table.onlyLive(cond), selectsOrders);
    }
    @NotNull
    public <E> List<E> fetchLive(RecordMapper<? super Record, E> mapper, int offset, int limit, T table, Condition cond, Collection<? extends QueryPart> selectsOrders) {
        return fetch(mapper, offset, limit, table, table.onlyLive(cond), selectsOrders);
    }

    ///////////////// select one /////////////////////
    @Nullable
    public P fetchOneLive(Function<T, Condition> fun) {
        final Condition cond = fun.apply(table);
        return fetchOne(table, table.onlyLive(cond));
    }

    @Nullable
    public P fetchLimitOneLive(Function<T, Condition> fun) {
        final Condition cond = fun.apply(table);
        return fetchLimitOne(table, table.onlyLive(cond));
    }

    @NotNull
    public Optional<P> fetchOptionalLive(Function<T, Condition> fun) {
        return Optional.ofNullable(fetchOne(fun));
    }

    @NotNull
    public Optional<P> fetchLimitOptionalLive(Function<T, Condition> fun) {
        return Optional.ofNullable(fetchLimitOne(fun));
    }

    @Nullable
    public P fetchOneLive(BiConsumer<T, SelectWhereOrder> fun) {
        final SelectWhereOrder soc = new SelectWhereOrder();
        fun.accept(table, soc);
        return fetchOne(table, table.onlyLive(soc.getWhere()), soc.getParts());
    }

    @Nullable
    public P fetchLimitOneLive(BiConsumer<T, SelectWhereOrder> fun) {
        final SelectWhereOrder soc = new SelectWhereOrder();
        fun.accept(table, soc);
        return fetchLimitOne(table, table.onlyLive(soc.getWhere()), soc.getParts());
    }

    @NotNull
    public Optional<P> fetchOptionalLive(BiConsumer<T, SelectWhereOrder> fun) {
        return Optional.ofNullable(fetchOne(fun));
    }

    @NotNull
    public Optional<P> fetchLimitOptionalLive(BiConsumer<T, SelectWhereOrder> fun) {
        return Optional.ofNullable(fetchLimitOne(fun));
    }

    @Nullable
    public <E> E fetchOneLive(Class<E> claz, BiConsumer<T, SelectWhereOrder> fun) {
        final SelectWhereOrder soc = new SelectWhereOrder();
        fun.accept(table, soc);
        return fetchOne(claz, table, table.onlyLive(soc.getWhere()), soc.getParts());
    }

    @Nullable
    public <E> E fetchLimitOneLive(Class<E> claz, BiConsumer<T, SelectWhereOrder> fun) {
        final SelectWhereOrder soc = new SelectWhereOrder();
        fun.accept(table, soc);
        return fetchLimitOne(claz, table, table.onlyLive(soc.getWhere()), soc.getParts());
    }

    @NotNull
    public <E> Optional<E> fetchOptionalLive(Class<E> claz, BiConsumer<T, SelectWhereOrder> fun) {
        return Optional.ofNullable(fetchOne(claz, fun));
    }

    @NotNull
    public <E> Optional<E> fetchLimitOptionalLive(Class<E> claz, BiConsumer<T, SelectWhereOrder> fun) {
        return Optional.ofNullable(fetchLimitOne(claz, fun));
    }

    @Nullable
    public <E> E fetchOneLive(RecordMapper<? super Record, E> mapper, BiConsumer<T, SelectWhereOrder> fun) {
        final SelectWhereOrder soc = new SelectWhereOrder();
        fun.accept(table, soc);
        return fetchOne(mapper, table, table.onlyLive(soc.getWhere()), soc.getParts());
    }

    @Nullable
    public <E> E fetchLimitOneLive(RecordMapper<? super Record, E> mapper, BiConsumer<T, SelectWhereOrder> fun) {
        final SelectWhereOrder soc = new SelectWhereOrder();
        fun.accept(table, soc);
        return fetchLimitOne(mapper, table, table.onlyLive(soc.getWhere()), soc.getParts());
    }

    @NotNull
    public <E> Optional<E> fetchOptionalLive(RecordMapper<? super Record, E> mapper, BiConsumer<T, SelectWhereOrder> fun) {
        return Optional.ofNullable(fetchOne(mapper, fun));
    }

    @NotNull
    public <E> Optional<E> fetchLimitOptionalLive(RecordMapper<? super Record, E> mapper, BiConsumer<T, SelectWhereOrder> fun) {
        return Optional.ofNullable(fetchLimitOne(mapper, fun));
    }

    /////////////////
    @Nullable
    public P fetchOneLive(T table, QueryPart... selectsOrders) {
        return fetchOne(table, table.getOnlyLive(), selectsOrders);
    }

    @Nullable
    public P fetchOneLive(T table, Collection<? extends QueryPart> selectsOrders) {
        return fetchOne(table, table.getOnlyLive(), selectsOrders);
    }

    @Nullable
    public P fetchLimitOneLive(T table, QueryPart... selectsOrders) {
        return fetchLimitOne(table, table.getOnlyLive(), selectsOrders);
    }

    @Nullable
    public P fetchLimitOneLive(T table, Collection<? extends QueryPart> selectsOrders) {
        return fetchLimitOne(table, table.getOnlyLive(), selectsOrders);
    }

    @NotNull
    public Optional<P> fetchOptionalLive(T table, QueryPart... selectsOrders) {
        return Optional.ofNullable(fetchOne(table, table.getOnlyLive(), selectsOrders));
    }

    @NotNull
    public Optional<P> fetchOptionalLive(T table, Collection<? extends QueryPart> selectsOrders) {
        return Optional.ofNullable(fetchOne(table, table.getOnlyLive(), selectsOrders));
    }

    @NotNull
    public Optional<P> fetchLimitOptionalLive(T table, QueryPart... selectsOrders) {
        return Optional.ofNullable(fetchLimitOne(table, table.getOnlyLive(), selectsOrders));
    }

    @NotNull
    public Optional<P> fetchLimitOptionalLive(T table, Collection<? extends QueryPart> selectsOrders) {
        return Optional.ofNullable(fetchLimitOne(table, table.getOnlyLive(), selectsOrders));
    }

    public P fetchOneLive(T table, Condition cond, QueryPart... selectsOrders) {
        return fetchOne(table, table.onlyLive(cond), selectsOrders);
    }

    public P fetchOneLive(T table, Condition cond, Collection<? extends QueryPart> selectsOrders) {
        return fetchOne(table, table.onlyLive(cond), selectsOrders);
    }

    @Nullable
    public P fetchLimitOneLive(T table, Condition cond, QueryPart... selectsOrders) {
        return fetchOne(table, table.onlyLive(cond), selectsOrders);
    }

    @Nullable
    public P fetchLimitOneLive(T table, Condition cond, Collection<? extends QueryPart> selectsOrders) {
        return fetchOne(table, table.onlyLive(cond), selectsOrders);
    }

    @NotNull
    public Optional<P> fetchOptionalLive(T table, Condition cond, QueryPart... selectsOrders) {
        return Optional.ofNullable(fetchOne(table, table.onlyLive(cond), selectsOrders));
    }

    @NotNull
    public Optional<P> fetchOptionalLive(T table, Condition cond, Collection<? extends QueryPart> selectsOrders) {
        return Optional.ofNullable(fetchOne(table, table.onlyLive(cond), selectsOrders));
    }

    @NotNull
    public Optional<P> fetchLimitOptionalLive(T table, Condition cond, QueryPart... selectsOrders) {
        return Optional.ofNullable(fetchLimitOne(table, table.onlyLive(cond), selectsOrders));
    }

    @NotNull
    public Optional<P> fetchLimitOptionalLive(T table, Condition cond, Collection<? extends QueryPart> selectsOrders) {
        return Optional.ofNullable(fetchLimitOne(table, table.onlyLive(cond), selectsOrders));
    }

    /////////////////
    @Nullable
    public <E> E fetchOneLive(Class<E> claz, T table, QueryPart... selectsOrders) {
        return fetchOne(claz, table, table.getOnlyLive(), selectsOrders);
    }

    @Nullable
    public <E> E fetchOneLive(Class<E> claz, T table, Collection<? extends QueryPart> selectsOrders) {
        return fetchOne(claz, table, table.getOnlyLive(), selectsOrders);
    }

    @Nullable
    public <E> E fetchLimitOneLive(Class<E> claz, T table, QueryPart... selectsOrders) {
        return fetchLimitOne(claz, table, table.getOnlyLive(), selectsOrders);
    }

    @Nullable
    public <E> E fetchLimitOneLive(Class<E> claz, T table, Collection<? extends QueryPart> selectsOrders) {
        return fetchLimitOne(claz, table, table.getOnlyLive(), selectsOrders);
    }

    @NotNull
    public <E> Optional<E> fetchOptionalLive(Class<E> claz, T table, QueryPart... selectsOrders) {
        return Optional.ofNullable(fetchOne(claz, table, table.getOnlyLive(), selectsOrders));
    }

    @NotNull
    public <E> Optional<E> fetchOptionalLive(Class<E> claz, T table, Collection<? extends QueryPart> selectsOrders) {
        return Optional.ofNullable(fetchOne(claz, table, table.getOnlyLive(), selectsOrders));
    }

    @NotNull
    public <E> Optional<E> fetchLimitOptionalLive(Class<E> claz, T table, QueryPart... selectsOrders) {
        return Optional.ofNullable(fetchLimitOne(claz, table, table.getOnlyLive(), selectsOrders));
    }

    @NotNull
    public <E> Optional<E> fetchLimitOptionalLive(Class<E> claz, T table, Collection<? extends QueryPart> selectsOrders) {
        return Optional.ofNullable(fetchLimitOne(claz, table, table.getOnlyLive(), selectsOrders));
    }

    public <E> E fetchOneLive(Class<E> claz, T table, Condition cond, QueryPart... selectsOrders) {
        return fetchOne(claz, table, table.onlyLive(cond), selectsOrders);
    }

    public <E> E fetchOneLive(Class<E> claz, T table, Condition cond, Collection<? extends QueryPart> selectsOrders) {
        return fetchOne(claz, table, table.onlyLive(cond), selectsOrders);
    }

    @Nullable
    public <E> E fetchLimitOneLive(Class<E> claz, T table, Condition cond, QueryPart... selectsOrders) {
        return fetchOne(claz, table, table.onlyLive(cond), selectsOrders);
    }

    @Nullable
    public <E> E fetchLimitOneLive(Class<E> claz, T table, Condition cond, Collection<? extends QueryPart> selectsOrders) {
        return fetchOne(claz, table, table.onlyLive(cond), selectsOrders);
    }

    @NotNull
    public <E> Optional<E> fetchOptionalLive(Class<E> claz, T table, Condition cond, QueryPart... selectsOrders) {
        return Optional.ofNullable(fetchOne(claz, table, table.onlyLive(cond), selectsOrders));
    }

    @NotNull
    public <E> Optional<E> fetchOptionalLive(Class<E> claz, T table, Condition cond, Collection<? extends QueryPart> selectsOrders) {
        return Optional.ofNullable(fetchOne(claz, table, table.onlyLive(cond), selectsOrders));
    }

    @NotNull
    public <E> Optional<E> fetchLimitOptionalLive(Class<E> claz, T table, Condition cond, QueryPart... selectsOrders) {
        return Optional.ofNullable(fetchLimitOne(claz, table, table.onlyLive(cond), selectsOrders));
    }

    @NotNull
    public <E> Optional<E> fetchLimitOptionalLive(Class<E> claz, T table, Condition cond, Collection<? extends QueryPart> selectsOrders) {
        return Optional.ofNullable(fetchLimitOne(claz, table, table.onlyLive(cond), selectsOrders));
    }

    /////////////////
    @Nullable
    public <E> E fetchOneLive(RecordMapper<? super Record, E> mapper, T table, QueryPart... selectsOrders) {
        return fetchOne(mapper, table, table.getOnlyLive(), selectsOrders);
    }

    @Nullable
    public <E> E fetchOneLive(RecordMapper<? super Record, E> mapper, T table, Collection<? extends QueryPart> selectsOrders) {
        return fetchOne(mapper, table, table.getOnlyLive(), selectsOrders);
    }

    @Nullable
    public <E> E fetchLimitOneLive(RecordMapper<? super Record, E> mapper, T table, QueryPart... selectsOrders) {
        return fetchLimitOne(mapper, table, table.getOnlyLive(), selectsOrders);
    }

    @Nullable
    public <E> E fetchLimitOneLive(RecordMapper<? super Record, E> mapper, T table, Collection<? extends QueryPart> selectsOrders) {
        return fetchLimitOne(mapper, table, table.getOnlyLive(), selectsOrders);
    }

    @NotNull
    public <E> Optional<E> fetchOptionalLive(RecordMapper<? super Record, E> mapper, T table, QueryPart... selectsOrders) {
        return Optional.ofNullable(fetchOne(mapper, table, table.getOnlyLive(), selectsOrders));
    }

    @NotNull
    public <E> Optional<E> fetchOptionalLive(RecordMapper<? super Record, E> mapper, T table, Collection<? extends QueryPart> selectsOrders) {
        return Optional.ofNullable(fetchOne(mapper, table, table.getOnlyLive(), selectsOrders));
    }

    @NotNull
    public <E> Optional<E> fetchLimitOptionalLive(RecordMapper<? super Record, E> mapper, T table, QueryPart... selectsOrders) {
        return Optional.ofNullable(fetchLimitOne(mapper, table, table.getOnlyLive(), selectsOrders));
    }

    @NotNull
    public <E> Optional<E> fetchLimitOptionalLive(RecordMapper<? super Record, E> mapper, T table, Collection<? extends QueryPart> selectsOrders) {
        return Optional.ofNullable(fetchLimitOne(mapper, table, table.getOnlyLive(), selectsOrders));
    }

    public <E> E fetchOneLive(RecordMapper<? super Record, E> mapper, T table, Condition cond, QueryPart... selectsOrders) {
        return fetchOne(mapper, table, table.onlyLive(cond), selectsOrders);
    }

    public <E> E fetchOneLive(RecordMapper<? super Record, E> mapper, T table, Condition cond, Collection<? extends QueryPart> selectsOrders) {
        return fetchOne(mapper, table, table.onlyLive(cond), selectsOrders);
    }

    @Nullable
    public <E> E fetchLimitOneLive(RecordMapper<? super Record, E> mapper, T table, Condition cond, QueryPart... selectsOrders) {
        return fetchOne(mapper, table, table.onlyLive(cond), selectsOrders);
    }

    @Nullable
    public <E> E fetchLimitOneLive(RecordMapper<? super Record, E> mapper, T table, Condition cond, Collection<? extends QueryPart> selectsOrders) {
        return fetchOne(mapper, table, table.onlyLive(cond), selectsOrders);
    }

    @NotNull
    public <E> Optional<E> fetchOptionalLive(RecordMapper<? super Record, E> mapper, T table, Condition cond, QueryPart... selectsOrders) {
        return Optional.ofNullable(fetchOne(mapper, table, table.onlyLive(cond), selectsOrders));
    }
    @NotNull
    public <E> Optional<E> fetchOptionalLive(RecordMapper<? super Record, E> mapper, T table, Condition cond, Collection<? extends QueryPart> selectsOrders) {
        return Optional.ofNullable(fetchOne(mapper, table, table.onlyLive(cond), selectsOrders));
    }

    @NotNull
    public <E> Optional<E> fetchLimitOptionalLive(RecordMapper<? super Record, E> mapper, T table, Condition cond, QueryPart... selectsOrders) {
        return Optional.ofNullable(fetchLimitOne(mapper, table, table.onlyLive(cond), selectsOrders));
    }
    @NotNull
    public <E> Optional<E> fetchLimitOptionalLive(RecordMapper<? super Record, E> mapper, T table, Condition cond, Collection<? extends QueryPart> selectsOrders) {
        return Optional.ofNullable(fetchLimitOne(mapper, table, table.onlyLive(cond), selectsOrders));
    }

    ///////////////// delete /////////////////////

    public int delete(JournalService.Journal commit, Function<T, Condition> fun) {
        return delete(commit, table, fun.apply(table));
    }

    /**
     * Delete record by condition
     *
     * @param commit journal
     * @param table  count table by condition, requires table with the same name as condition
     * @param cond   condition
     * @return affected records
     */
    public int delete(JournalService.Journal commit, T table, Condition cond) {
        return ctx()
                .update(table)
                .set(table.markDelete(commit))
                .where(cond)
                .execute();
    }

    /**
     * Logic delete record by ids
     *
     * @param commit journal
     * @param ids    ids
     * @return affected records
     */
    @SafeVarargs
    public final int deleteById(JournalService.Journal commit, K... ids) {
        return deleteById(commit, Arrays.asList(ids));
    }

    private static final Record[] EMPTY_RECORD = {};

    /**
     * Logic delete record by ids
     *
     * @param commit journal
     * @param ids    ids
     * @return affected records
     */
    public int deleteById(JournalService.Journal commit, Collection<K> ids) {
        // see DAOImpl deleteById
        final Condition cond;
        if (pkeys.length == 1) {
            @SuppressWarnings("unchecked") final Field<Object> pk = (Field<Object>) pkeys[0];
            if (ids.size() == 1) {
                cond = pk.eq(pk.getDataType().convert(ids.iterator().next()));
            }
            else {
                cond = pk.in(pk.getDataType().convert(ids));
            }
        }
        // [#2573] Composite key T types are of type Record[N]
        else {
            @SuppressWarnings("SuspiciousToArrayCall") final Record[] rn = ids.toArray(EMPTY_RECORD);
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
