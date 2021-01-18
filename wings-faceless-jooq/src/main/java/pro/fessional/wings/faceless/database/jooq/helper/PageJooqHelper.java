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
import org.jooq.RecordMapper;
import org.jooq.Result;
import org.jooq.SelectConditionStep;
import org.jooq.SelectFieldOrAsterisk;
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
import java.util.List;
import java.util.Map;

/**
 * 提供基于jdbc和jooq的分页查询工具。
 * <pre>
 * total < 0，DB执行count和select
 * total = 0，DB不count，不select
 * total > 0，DB不count，但select
 * </pre>
 *
 * @author trydofor
 * @link https://blog.jooq.org/2019/09/19/whats-faster-count-or-count1/
 * @since 2020-09-30
 */
public class PageJooqHelper extends PageJdbcHelper {

    @NotNull
    public static <R extends UpdatableRecord<R>, P, K> CountJooq use(DAOImpl<R, P, K> dao, PageQuery page) {
        return use(dao.ctx(), page, -1);
    }

    /**
     * @param dao   jooq Dao
     * @param page  页
     * @param total service层缓存的count计数
     * @param <R>   Record
     * @param <P>   pojo
     * @param <K>   主键
     * @return 结果
     */
    @NotNull
    public static <R extends UpdatableRecord<R>, P, K> CountJooq use(DAOImpl<R, P, K> dao, PageQuery page, int total) {
        return use(dao.ctx(), page, total);
    }

    @NotNull
    public static CountJooq use(DSLContext dsl, PageQuery page) {
        return use(dsl, page, -1);
    }

    /**
     * 分页查询
     *
     * @param dsl   dsl
     * @param page  页
     * @param total service层缓存的count计数
     * @return 结果
     */
    @NotNull
    public static CountJooq use(DSLContext dsl, PageQuery page, int total) {
        ContextJooq context = new ContextJooq();
        context.page = page;
        context.dsl = dsl;
        context.total = total;
        return new CountJooq(context);
    }

    // ////////////

    private static class ContextJooq {
        // in
        private DSLContext dsl;
        private PageQuery page;
        private AggregateFunction<Integer> count;
        private TableLike<?>[] from;
        private Condition where;
        private List<OrderField<?>> order = Collections.emptyList();
        private SelectConditionStep<Record> wrap;
        // out
        private Result<Record> result;
        private int total = -1;

        //
        private void orderBy(OrderField<?>... bys) {
            if (bys != null && bys.length > 0) {
                order = Arrays.asList(bys);
            }
        }

        private void orderBy(Map<String, Field<?>> bys) {
            if (bys != null && bys.size() > 0) {
                order = new ArrayList<>();
                for (PageUtil.By by : PageUtil.sort(page.getSort())) {
                    Field<?> fd = bys.get(by.key);
                    if (fd != null) {
                        order.add(by.asc ? fd.asc() : fd.desc());
                    }
                }
            }
        }
    }

    @RequiredArgsConstructor
    public static class CountJooq {
        private final ContextJooq context;

        /**
         * https://blog.jooq.org/2019/09/19/whats-faster-count-or-count1/
         */
        public FromJooq count() {
            context.count = DSL.count();
            return new FromJooq(context);
        }

        public FromJooq count(Field<?> field) {
            context.count = DSL.count(field);
            return new FromJooq(context);
        }

        public FromJooq count(AggregateFunction<Integer> count) {
            context.count = count;
            return new FromJooq(context);
        }

        public WrapJooq wrap(SelectConditionStep<Record> select) {
            context.wrap = select;
            return new WrapJooq(context);
        }

        public WrapJooq wrap(SelectConditionStep<Record> select, OrderField<?>... bys) {
            context.wrap = select;
            context.orderBy(bys);
            return new WrapJooq(context);
        }

        public WrapJooq wrap(SelectConditionStep<Record> select, Map<String, Field<?>> bys) {
            context.wrap = select;
            context.orderBy(bys);
            return new WrapJooq(context);
        }
    }

    @RequiredArgsConstructor
    public static class FromJooq {
        private final ContextJooq context;

        public WhereJooq from(TableLike<?>... tables) {
            context.from = tables;
            return new WhereJooq(context);
        }
    }


    @RequiredArgsConstructor
    public static class WhereJooq {
        private final ContextJooq context;

        public OrderJooq whereTrue() {
            return new OrderJooq(context);
        }

        public OrderJooq where(Condition where) {
            context.where = where;
            return new OrderJooq(context);
        }
    }

    @RequiredArgsConstructor
    public static class OrderJooq {
        private final ContextJooq context;

        public FetchJooq orderNone() {
            return new FetchJooq(context);
        }

        public FetchJooq order(OrderField<?>... bys) {
            context.orderBy(bys);
            return new FetchJooq(context);
        }

        public FetchJooq order(Map<String, Field<?>> bys) {
            context.orderBy(bys);
            return new FetchJooq(context);
        }
    }

    @RequiredArgsConstructor
    public static class FetchJooq {
        private final ContextJooq context;

        public <R extends Record> IntoJooq fetch(Table<R> table) {
            return fetch(table.asterisk());
        }

        public <R extends Record> IntoJooq fetch() {
            if (context.from == null || context.from.length == 0) {
                throw new IllegalStateException("froms no table");
            }
            TableLike<?> tableLike = context.from[0];
            if (tableLike instanceof Table) {
                return fetch(((Table<?>) tableLike).asterisk());

            } else {
                return fetch(tableLike.fields());
            }
        }

        public IntoJooq fetch(SelectFieldOrAsterisk... select) {
            if (context.total < 0) {
                Record1<Integer> cnt;
                if (context.where == null) {
                    cnt = context.dsl.select(context.count)
                                     .from(context.from)
                                     .fetchOne();
                } else {
                    cnt = context.dsl.select(context.count)
                                     .from(context.from)
                                     .where(context.where)
                                     .fetchOne();
                }
                context.total = cnt == null ? 0 : cnt.value1();
            }

            if (context.total > 0) {
                context.result = context.where == null ?
                        context.dsl.select(select)
                                   .from(context.from)
                                   .orderBy(context.order)
                                   .limit(context.page.toOffset(), context.page.getSize())
                                   .fetch()
                        :
                        context.dsl.select(select)
                                   .from(context.from)
                                   .where(context.where)
                                   .orderBy(context.order)
                                   .limit(context.page.toOffset(), context.page.getSize())
                                   .fetch();
            }

            return new IntoJooq(context);
        }
    }


    @RequiredArgsConstructor
    public static class WrapJooq {
        private final ContextJooq context;

        public IntoJooq fetch() {
            if (context.total < 0) {
                context.total = context.dsl.fetchCount(context.wrap);
            }

            if (context.total > 0) {
                context.result = context.wrap
                        .orderBy(context.order)
                        .limit(context.page.toOffset(), context.page.getSize())
                        .fetch();
            }
            return new IntoJooq(context);
        }
    }

    @RequiredArgsConstructor
    public static class IntoJooq {
        private final ContextJooq context;

        @NotNull
        public <E> PageResult<E> into(Class<E> claz) {
            final List<E> data = context.result == null ? null : context.result.into(claz);
            return PageResult.of(context.total, data, context.page);
        }

        @NotNull
        public <E> PageResult<E> into(RecordMapper<? super Record, E> mapper) {
            final List<E> data = context.result == null ? null : context.result.map(mapper);
            return PageResult.of(context.total, data, context.page);
        }

        @Nullable
        public Result<Record> result() {
            return context.result;
        }
    }
}
