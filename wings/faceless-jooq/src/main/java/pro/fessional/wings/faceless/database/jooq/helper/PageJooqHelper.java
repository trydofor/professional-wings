package pro.fessional.wings.faceless.database.jooq.helper;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jooq.AggregateFunction;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.OrderField;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.Record10;
import org.jooq.Record11;
import org.jooq.Record12;
import org.jooq.Record13;
import org.jooq.Record14;
import org.jooq.Record15;
import org.jooq.Record16;
import org.jooq.Record17;
import org.jooq.Record18;
import org.jooq.Record19;
import org.jooq.Record2;
import org.jooq.Record20;
import org.jooq.Record21;
import org.jooq.Record22;
import org.jooq.Record3;
import org.jooq.Record4;
import org.jooq.Record5;
import org.jooq.Record6;
import org.jooq.Record7;
import org.jooq.Record8;
import org.jooq.Record9;
import org.jooq.RecordMapper;
import org.jooq.Result;
import org.jooq.SelectField;
import org.jooq.SelectFieldOrAsterisk;
import org.jooq.SelectOrderByStep;
import org.jooq.Table;
import org.jooq.TableLike;
import org.jooq.UpdatableRecord;
import org.jooq.impl.DAOImpl;
import org.jooq.impl.DSL;
import pro.fessional.mirana.page.PageQuery;
import pro.fessional.mirana.page.PageResult;
import pro.fessional.mirana.page.PageUtil;
import pro.fessional.wings.faceless.database.helper.PageJdbcHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <pre>
 * Pagination Util for jdbc and jooq.
 *
 * * total < 0 - run count, run select
 * * total = 0 - no count, no select
 * * total > 0 - no count, run select
 * </pre>
 *
 * @author trydofor
 * @link <a href="https://blog.jooq.org/2019/09/19/whats-faster-count-or-count1/">whats-faster-count-or-count1</a>
 * @since 2020-09-30
 */
public class PageJooqHelper extends PageJdbcHelper {

    @NotNull
    public static <R extends UpdatableRecord<R>, P, K> CountJooq<R> use(DAOImpl<R, P, K> dao, PageQuery page) {
        return use(dao.ctx(), page, -1);
    }

    /**
     * Page query by jooq
     *
     * @param dao   jooq Dao
     * @param page  query info
     * @param total the count cached in service level
     * @param <R>   Record
     * @param <P>   pojo
     * @param <K>   pk
     * @return step
     */
    @NotNull
    public static <R extends UpdatableRecord<R>, P, K> CountJooq<R> use(DAOImpl<R, P, K> dao, PageQuery page, int total) {
        return use(dao.ctx(), page, total);
    }

    @NotNull
    public static CountJooq<? extends Record> use(DSLContext dsl, PageQuery page) {
        return use(dsl, page, -1);
    }

    /**
     * Page query by jooq
     *
     * @param dsl   dsl
     * @param page  query info
     * @param total the count cached in service level
     * @return step
     */
    @NotNull
    public static <R extends Record> CountJooq<R> use(DSLContext dsl, PageQuery page, int total) {
        ContextJooq<R> context = new ContextJooq<>();
        context.page = page;
        context.dsl = dsl;
        context.total = total;
        return new CountJooq<>(context);
    }

    // ////////////

    private static class ContextJooq<R extends Record> {
        // in
        private DSLContext dsl;
        private PageQuery page;
        private AggregateFunction<Integer> count;
        private TableLike<?>[] from;
        private Condition where;
        private List<OrderField<?>> order = Collections.emptyList();
        private SelectOrderByStep<R> wrap;
        // out
        private Result<R> result;
        private int total = -1;

        /**
         * `PageQuery.sort` as the primary, `bys` as the mapping, and `dft` as the default
         */
        private void orderBy(Map<String, ? extends Field<?>> bys, OrderField<?>... dft) {
            final List<PageUtil.By> srt = PageUtil.sort(page.getSort());

            if (srt.isEmpty()) {
                order = Arrays.asList(dft);
            }
            else {
                order = new ArrayList<>(Math.max(dft.length, bys.size()));
                if (dft.length > 0) {
                    bys = new HashMap<>(bys);
                    @SuppressWarnings("unchecked")
                    Map<String, Field<?>> wtm = (Map<String, Field<?>>) bys;
                    for (OrderField<?> s : dft) {
                        if (s instanceof Field<?> f) {
                            wtm.putIfAbsent(f.getName(), f);
                        }
                    }
                }

                for (PageUtil.By by : srt) {
                    Field<?> fd = bys.get(by.key);
                    if (fd != null) {
                        order.add(by.asc ? fd.asc() : fd.desc());
                    }
                }
            }
        }
    }

    @RequiredArgsConstructor
    public static class CountJooq<R extends Record> {
        private final ContextJooq<R> context;

        /**
         * <a href="https://blog.jooq.org/2019/09/19/whats-faster-count-or-count1/">whats-faster-count-or-count1</a>
         */
        public FromJooq<R> count() {
            context.count = DSL.count();
            return new FromJooq<>(context);
        }

        public FromJooq<R> count(Field<?> field) {
            context.count = DSL.count(field);
            return new FromJooq<>(context);
        }

        public FromJooq<R> count(AggregateFunction<Integer> count) {
            context.count = count;
            return new FromJooq<>(context);
        }

        @SuppressWarnings("unchecked")
        public <S extends Record> WrapJooq<S> wrap(SelectOrderByStep<S> select) {
            ContextJooq<S> ctx = (ContextJooq<S>) context;
            ctx.wrap = select;
            return new WrapJooq<>(ctx);
        }

        @SuppressWarnings("unchecked")
        public <S extends Record> WrapJooq<S> wrap(SelectOrderByStep<S> select, OrderField<?>... bys) {
            ContextJooq<S> ctx = (ContextJooq<S>) context;
            ctx.wrap = select;
            ctx.orderBy(Collections.emptyMap(), bys);
            return new WrapJooq<>(ctx);
        }

        @SuppressWarnings("unchecked")
        public <S extends Record> WrapJooq<S> wrap(SelectOrderByStep<S> select, Map<String, Field<?>> bys, OrderField<?>... dft) {
            ContextJooq<S> ctx = (ContextJooq<S>) context;
            ctx.wrap = select;
            ctx.orderBy(bys, dft);
            return new WrapJooq<>(ctx);
        }
    }

    @RequiredArgsConstructor
    public static class FromJooq<R extends Record> {
        private final ContextJooq<R> context;

        public WhereJooq<R> from(TableLike<?>... tables) {
            context.from = tables;
            return new WhereJooq<>(context);
        }
    }


    @RequiredArgsConstructor
    public static class WhereJooq<R extends Record> {
        private final ContextJooq<R> context;

        public OrderJooq<R> whereTrue() {
            return new OrderJooq<>(context);
        }

        public OrderJooq<R> where(Condition where) {
            context.where = where;
            return new OrderJooq<>(context);
        }
    }

    @RequiredArgsConstructor
    public static class OrderJooq<R extends Record> {
        private final ContextJooq<R> context;

        public FetchJooq<R> orderNone() {
            return new FetchJooq<>(context);
        }

        /**
         * Specify a field or sort statement that is equivalent to the field to field mapping.
         */
        public FetchJooq<R> order(OrderField<?>... bys) {
            context.orderBy(Collections.emptyMap(), bys);
            return new FetchJooq<>(context);
        }

        /**
         * Based on the mapping of alias to filed, use PageQuery's sort to match the ordering
         */
        public FetchJooq<R> order(Map<String, Field<?>> bys, OrderField<?>... dft) {
            context.orderBy(bys, dft);
            return new FetchJooq<>(context);
        }
    }

    @RequiredArgsConstructor
    public static class FetchJooq<R extends Record> {
        private final ContextJooq<R> context;

        public <S extends Record> IntoJooq<S> fetch(Table<S> table) {
            return fetch(table.asterisk());
        }

        public IntoJooq<R> fetch() {
            if (context.from == null || context.from.length == 0) {
                throw new IllegalStateException("froms no table");
            }
            TableLike<?> tableLike = context.from[0];
            if (tableLike instanceof Table) {
                return fetch(((Table<?>) tableLike).asterisk());

            }
            else {
                return fetch(tableLike.fields());
            }
        }

        public <T1> IntoJooq<Record1<T1>> fetch(SelectField<T1> t1) {
            return fetch(new SelectField[]{t1});
        }

        public <T1, T2> IntoJooq<Record2<T1, T2>> fetch(SelectField<T1> t1, SelectField<T2> t2) {
            return fetch(new SelectField[]{t1, t2});
        }

        public <T1, T2, T3> IntoJooq<Record3<T1, T2, T3>> fetch(SelectField<T1> t1, SelectField<T2> t2, SelectField<T3> t3) {
            return fetch(new SelectField[]{t1, t2, t3});
        }

        public <T1, T2, T3, T4> IntoJooq<Record4<T1, T2, T3, T4>> fetch(SelectField<T1> t1, SelectField<T2> t2, SelectField<T3> t3, SelectField<T4> t4) {
            return fetch(new SelectField[]{t1, t2, t3, t4});
        }

        public <T1, T2, T3, T4, T5> IntoJooq<Record5<T1, T2, T3, T4, T5>> fetch(SelectField<T1> t1, SelectField<T2> t2, SelectField<T3> t3, SelectField<T4> t4, SelectField<T5> t5) {
            return fetch(new SelectField[]{t1, t2, t3, t4, t5});
        }

        public <T1, T2, T3, T4, T5, T6> IntoJooq<Record6<T1, T2, T3, T4, T5, T6>>
        fetch(SelectField<T1> t1, SelectField<T2> t2, SelectField<T3> t3, SelectField<T4> t4, SelectField<T5> t5,
              SelectField<T6> t6) {
            return fetch(new SelectField[]{t1, t2, t3, t4, t5, t6});
        }

        public <T1, T2, T3, T4, T5, T6, T7> IntoJooq<Record7<T1, T2, T3, T4, T5, T6, T7>>
        fetch(SelectField<T1> t1, SelectField<T2> t2, SelectField<T3> t3, SelectField<T4> t4, SelectField<T5> t5,
              SelectField<T6> t6, SelectField<T7> t7) {
            return fetch(new SelectField[]{t1, t2, t3, t4, t5, t6, t7});
        }

        public <T1, T2, T3, T4, T5, T6, T7, T8> IntoJooq<Record8<T1, T2, T3, T4, T5, T6, T7, T8>>
        fetch(SelectField<T1> t1, SelectField<T2> t2, SelectField<T3> t3, SelectField<T4> t4, SelectField<T5> t5,
              SelectField<T6> t6, SelectField<T7> t7, SelectField<T8> t8) {
            return fetch(new SelectField[]{t1, t2, t3, t4, t5, t6, t7, t8});
        }

        public <T1, T2, T3, T4, T5, T6, T7, T8, T9> IntoJooq<Record9<T1, T2, T3, T4, T5, T6, T7, T8, T9>>
        fetch(SelectField<T1> t1, SelectField<T2> t2, SelectField<T3> t3, SelectField<T4> t4, SelectField<T5> t5,
              SelectField<T6> t6, SelectField<T7> t7, SelectField<T8> t8, SelectField<T9> t9) {
            return fetch(new SelectField[]{t1, t2, t3, t4, t5, t6, t7, t8, t9});
        }

        public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> IntoJooq<Record10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>>
        fetch(SelectField<T1> t1, SelectField<T2> t2, SelectField<T3> t3, SelectField<T4> t4, SelectField<T5> t5,
              SelectField<T6> t6, SelectField<T7> t7, SelectField<T8> t8, SelectField<T9> t9, SelectField<T10> t10) {
            return fetch(new SelectField[]{t1, t2, t3, t4, t5, t6, t7, t8, t9, t10});
        }

        public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>
        IntoJooq<Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>>
        fetch(SelectField<T1> t1, SelectField<T2> t2, SelectField<T3> t3, SelectField<T4> t4, SelectField<T5> t5,
              SelectField<T6> t6, SelectField<T7> t7, SelectField<T8> t8, SelectField<T9> t9, SelectField<T10> t10,
              SelectField<T11> t11) {
            return fetch(new SelectField[]{t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11});
        }

        public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12>
        IntoJooq<Record12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12>>
        fetch(SelectField<T1> t1, SelectField<T2> t2, SelectField<T3> t3, SelectField<T4> t4, SelectField<T5> t5,
              SelectField<T6> t6, SelectField<T7> t7, SelectField<T8> t8, SelectField<T9> t9, SelectField<T10> t10,
              SelectField<T11> t11, SelectField<T12> t12) {
            return fetch(new SelectField[]{t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12});
        }

        public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13>
        IntoJooq<Record13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13>>
        fetch(SelectField<T1> t1, SelectField<T2> t2, SelectField<T3> t3, SelectField<T4> t4, SelectField<T5> t5,
              SelectField<T6> t6, SelectField<T7> t7, SelectField<T8> t8, SelectField<T9> t9, SelectField<T10> t10,
              SelectField<T11> t11, SelectField<T12> t12, SelectField<T13> t13) {
            return fetch(new SelectField[]{t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13});
        }

        public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14>
        IntoJooq<Record14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14>>
        fetch(SelectField<T1> t1, SelectField<T2> t2, SelectField<T3> t3, SelectField<T4> t4, SelectField<T5> t5,
              SelectField<T6> t6, SelectField<T7> t7, SelectField<T8> t8, SelectField<T9> t9, SelectField<T10> t10,
              SelectField<T11> t11, SelectField<T12> t12, SelectField<T13> t13, SelectField<T14> t14) {
            return fetch(new SelectField[]{t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14});
        }

        public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15>
        IntoJooq<Record15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15>>
        fetch(SelectField<T1> t1, SelectField<T2> t2, SelectField<T3> t3, SelectField<T4> t4, SelectField<T5> t5,
              SelectField<T6> t6, SelectField<T7> t7, SelectField<T8> t8, SelectField<T9> t9, SelectField<T10> t10,
              SelectField<T11> t11, SelectField<T12> t12, SelectField<T13> t13, SelectField<T14> t14, SelectField<T15> t15) {
            return fetch(new SelectField[]{t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15});
        }

        public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16>
        IntoJooq<Record16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16>>
        fetch(SelectField<T1> t1, SelectField<T2> t2, SelectField<T3> t3, SelectField<T4> t4, SelectField<T5> t5,
              SelectField<T6> t6, SelectField<T7> t7, SelectField<T8> t8, SelectField<T9> t9, SelectField<T10> t10,
              SelectField<T11> t11, SelectField<T12> t12, SelectField<T13> t13, SelectField<T14> t14, SelectField<T15> t15,
              SelectField<T16> t16) {
            return fetch(new SelectField[]{t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16});
        }

        public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17>
        IntoJooq<Record17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17>>
        fetch(SelectField<T1> t1, SelectField<T2> t2, SelectField<T3> t3, SelectField<T4> t4, SelectField<T5> t5,
              SelectField<T6> t6, SelectField<T7> t7, SelectField<T8> t8, SelectField<T9> t9, SelectField<T10> t10,
              SelectField<T11> t11, SelectField<T12> t12, SelectField<T13> t13, SelectField<T14> t14, SelectField<T15> t15,
              SelectField<T16> t16, SelectField<T17> t17) {
            return fetch(new SelectField[]{t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17});
        }

        public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18>
        IntoJooq<Record18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18>>
        fetch(SelectField<T1> t1, SelectField<T2> t2, SelectField<T3> t3, SelectField<T4> t4, SelectField<T5> t5,
              SelectField<T6> t6, SelectField<T7> t7, SelectField<T8> t8, SelectField<T9> t9, SelectField<T10> t10,
              SelectField<T11> t11, SelectField<T12> t12, SelectField<T13> t13, SelectField<T14> t14, SelectField<T15> t15,
              SelectField<T16> t16, SelectField<T17> t17, SelectField<T17> t18) {
            return fetch(new SelectField[]{t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17, t18});
        }

        public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19>
        IntoJooq<Record19<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19>>
        fetch(SelectField<T1> t1, SelectField<T2> t2, SelectField<T3> t3, SelectField<T4> t4, SelectField<T5> t5,
              SelectField<T6> t6, SelectField<T7> t7, SelectField<T8> t8, SelectField<T9> t9, SelectField<T10> t10,
              SelectField<T11> t11, SelectField<T12> t12, SelectField<T13> t13, SelectField<T14> t14, SelectField<T15> t15,
              SelectField<T16> t16, SelectField<T17> t17, SelectField<T17> t18, SelectField<T19> t19) {
            return fetch(new SelectField[]{t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17, t18, t19});
        }

        public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20>
        IntoJooq<Record20<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20>>
        fetch(SelectField<T1> t1, SelectField<T2> t2, SelectField<T3> t3, SelectField<T4> t4, SelectField<T5> t5,
              SelectField<T6> t6, SelectField<T7> t7, SelectField<T8> t8, SelectField<T9> t9, SelectField<T10> t10,
              SelectField<T11> t11, SelectField<T12> t12, SelectField<T13> t13, SelectField<T14> t14, SelectField<T15> t15,
              SelectField<T16> t16, SelectField<T17> t17, SelectField<T17> t18, SelectField<T19> t19, SelectField<T20> t20) {
            return fetch(new SelectField[]{t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17, t18, t19, t20});
        }

        public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21>
        IntoJooq<Record21<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21>>
        fetch(SelectField<T1> t1, SelectField<T2> t2, SelectField<T3> t3, SelectField<T4> t4, SelectField<T5> t5,
              SelectField<T6> t6, SelectField<T7> t7, SelectField<T8> t8, SelectField<T9> t9, SelectField<T10> t10,
              SelectField<T11> t11, SelectField<T12> t12, SelectField<T13> t13, SelectField<T14> t14, SelectField<T15> t15,
              SelectField<T16> t16, SelectField<T17> t17, SelectField<T17> t18, SelectField<T19> t19, SelectField<T20> t20,
              SelectField<T21> t21) {
            return fetch(new SelectField[]{t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17, t18, t19, t20, t21});
        }

        public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22>
        IntoJooq<Record22<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22>>
        fetch(SelectField<T1> t1, SelectField<T2> t2, SelectField<T3> t3, SelectField<T4> t4, SelectField<T5> t5,
              SelectField<T6> t6, SelectField<T7> t7, SelectField<T8> t8, SelectField<T9> t9, SelectField<T10> t10,
              SelectField<T11> t11, SelectField<T12> t12, SelectField<T13> t13, SelectField<T14> t14, SelectField<T15> t15,
              SelectField<T16> t16, SelectField<T17> t17, SelectField<T17> t18, SelectField<T19> t19, SelectField<T20> t20,
              SelectField<T21> t21, SelectField<T22> t22) {
            return fetch(new SelectField[]{t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17, t18, t19, t20, t21, t22});
        }

        @SuppressWarnings("unchecked")
        public <S extends Record> IntoJooq<S> fetch(SelectFieldOrAsterisk... select) {
            ContextJooq<S> ctx = (ContextJooq<S>) context;

            if (ctx.total < 0) {
                Record1<Integer> cnt;
                if (ctx.where == null) {
                    cnt = ctx.dsl.select(ctx.count)
                                 .from(ctx.from)
                                 .fetchOne();
                }
                else {
                    cnt = ctx.dsl.select(ctx.count)
                                 .from(ctx.from)
                                 .where(ctx.where)
                                 .fetchOne();
                }
                ctx.total = cnt == null ? 0 : cnt.value1();
            }

            if (ctx.total > 0) {
                ctx.result = (Result<S>) (ctx.where == null ?
                                          ctx.dsl.select(select)
                                                 .from(ctx.from)
                                                 .orderBy(ctx.order)
                                                 .limit(ctx.page.toOffset(), ctx.page.getSize())
                                                 .fetch()
                                                            :
                                          ctx.dsl.select(select)
                                                 .from(ctx.from)
                                                 .where(ctx.where)
                                                 .orderBy(ctx.order)
                                                 .limit(ctx.page.toOffset(), ctx.page.getSize())
                                                 .fetch()
                );
            }

            return new IntoJooq<>(ctx);
        }
    }


    @RequiredArgsConstructor
    public static class WrapJooq<R extends Record> {
        private final ContextJooq<R> context;

        public IntoJooq<R> fetch() {
            if (context.total < 0) {
                context.total = context.dsl.fetchCount(context.wrap);
            }

            if (context.total > 0) {
                context.result = context.wrap
                        .orderBy(context.order)
                        .limit(context.page.toOffset(), context.page.getSize())
                        .fetch();
            }
            return new IntoJooq<>(context);
        }
    }

    @RequiredArgsConstructor
    public static class IntoJooq<R extends Record> {
        private final ContextJooq<R> context;

        @NotNull
        public <E> PageResult<E> into(Class<E> claz) {
            final List<E> data = context.result == null ? null : context.result.into(claz);
            return PageResult.ok(context.total, data, context.page);
        }

        @NotNull
        public <E> PageResult<E> into(RecordMapper<R, E> mapper) {
            final List<E> data = context.result == null ? null : context.result.map(mapper);
            return PageResult.ok(context.total, data, context.page);
        }

        @Nullable
        public Result<R> result() {
            return context.result;
        }
    }
}
