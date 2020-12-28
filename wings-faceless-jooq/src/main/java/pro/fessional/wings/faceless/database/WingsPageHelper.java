package pro.fessional.wings.faceless.database;

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
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import pro.fessional.mirana.data.Null;
import pro.fessional.mirana.page.PageQuery;
import pro.fessional.mirana.page.PageResult;
import pro.fessional.mirana.page.PageUtil;

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
public class WingsPageHelper {

    @NotNull
    public static CountJdbc use(JdbcTemplate tpl, PageQuery page) {
        return use(tpl, page, -1);
    }

    /**
     * 分页查询
     *
     * @param tpl   dsl
     * @param page  页
     * @param total service层缓存的count计数
     * @return 结果
     */
    @NotNull
    public static CountJdbc use(JdbcTemplate tpl, PageQuery page, int total) {
        ContextJdbc context = new ContextJdbc();
        context.page = page;
        context.tpl = tpl;
        context.total = total;
        return new CountJdbc(context);
    }

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

    @RequiredArgsConstructor
    public static class CountJdbc {
        private final ContextJdbc context;

        public OrderJdbc2 wrap(String select, Map<String, String> bys) {
            context.wrap = select;
            context.orderBy(bys);
            return new OrderJdbc2(context);
        }

        public OrderJdbc2 wrap(String select) {
            context.wrap = select;
            return new OrderJdbc2(context);
        }

        public FromJdbc count(String count) {
            context.count = count == null || count.isEmpty() ? "count(*)" : count;
            return new FromJdbc(context);
        }
    }

    @RequiredArgsConstructor
    public static class BindJdbc1 {
        private final ContextJdbc context;

        public FetchJdbc1 bindNone() {
            return new FetchJdbc1(context);
        }

        public FetchJdbc1 bind(Object... args) {
            if (args != null) {
                context.bind = args;
            }
            return new FetchJdbc1(context);
        }
    }

    @RequiredArgsConstructor
    public static class BindJdbc2 {
        private final ContextJdbc context;

        public IntoJdbc2 bindNone() {
            return new IntoJdbc2(context);
        }

        public IntoJdbc2 bind(Object... args) {
            if (args != null) {
                context.bind = args;
            }
            return new IntoJdbc2(context);
        }
    }

    @RequiredArgsConstructor
    public static class FetchJdbc1 {
        private final ContextJdbc context;

        @NotNull
        public IntoJdbc1 fetch(String fields) {
            context.select = fields == null || fields.isEmpty() ? "*" : fields;
            return new IntoJdbc1(context);
        }

    }

    @RequiredArgsConstructor
    public static class IntoJdbc1 {
        private final ContextJdbc context;

        @NotNull
        public <E> PageResult<E> into(Class<E> claz) {
            return into(new BeanPropertyRowMapper<>(claz));
        }

        @NotNull
        public <E> PageResult<E> into(RowMapper<E> mapper) {
            if (context.total < 0) {
                Integer total = context.tpl.queryForObject("SELECT " + context.count + " " + context.fromWhere, int.class, context.bind);
                if (total != null) {
                    context.total = total;
                }
            }
            List<E> list = null;
            if (context.total > 0) {
                StringBuilder sql = new StringBuilder(context.select.length() + context.fromWhere.length() + context.order.length() + 50);
                sql.append("SELECT ");
                sql.append(context.select);
                sql.append(" ");
                sql.append(context.fromWhere);
                context.orderLimit(sql);
                list = context.tpl.query(sql.toString(), mapper, context.bind);
            }

            return PageResult.of(context.total, list, context.page);
        }
    }

    @RequiredArgsConstructor
    public static class IntoJdbc2 {
        private final ContextJdbc context;

        @NotNull
        public <E> PageResult<E> fetchInto(Class<E> claz) {
            return fetchInto(new BeanPropertyRowMapper<>(claz));
        }

        @NotNull
        public <E> PageResult<E> fetchInto(RowMapper<E> mapper) {
            if (context.total < 0) {
                Integer total = context.tpl.queryForObject("SELECT count(*) FROM (" + context.wrap + ") WINGS_WRAP", int.class, context.bind);
                if (total != null) {
                    context.total = total;
                }
            }
            List<E> list = null;
            if (context.total > 0) {
                StringBuilder sql = new StringBuilder(context.wrap.length() + context.order.length() + 30);
                sql.append(context.wrap);
                context.orderLimit(sql);
                list = context.tpl.query(sql.toString(), mapper, context.bind);
            }

            return PageResult.of(context.total, list, context.page);
        }
    }

    // ////////////

    private static class ContextJdbc {
        // in
        private JdbcTemplate tpl;
        private PageQuery page;
        private String count;
        private String select;
        private String fromWhere;
        private String order = Null.Str;
        private String wrap;
        private Object[] bind = Null.Objects;

        // out
        private int total = -1;

        private void orderBy(Map<String, String> bys) {
            if (bys != null && bys.size() > 0) {
                StringBuilder sb = new StringBuilder();
                for (PageUtil.By by : PageUtil.sortBy(page.getSortBy())) {
                    String fd = bys.get(by.key);
                    if (fd != null) {
                        sb.append(',').append(fd);
                        if (by.asc) {
                            sb.append(" ASC");
                        } else {
                            sb.append(" DESC");
                        }
                    }
                }
                if (sb.length() > 0) {
                    order = sb.substring(1);
                }
            }
        }

        private void orderLimit(StringBuilder sql) {
            if (order.length() > 0) {
                sql.append(" order by ");
                sql.append(order);
            }
            sql.append(" limit ");
            int offset = page.toOffset();
            if (offset > 0) {
                sql.append(offset).append(",");
            }
            sql.append(page.getPageSize());
        }
    }

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
                for (PageUtil.By by : PageUtil.sortBy(page.getSortBy())) {
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
    public static class FromJdbc {
        private final ContextJdbc context;

        public OrderJdbc1 fromWhere(String fromWhere) {
            context.fromWhere = fromWhere;
            return new OrderJdbc1(context);
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
    public static class OrderJdbc1 {
        private final ContextJdbc context;

        public BindJdbc1 orderNone() {
            return new BindJdbc1(context);
        }

        public BindJdbc1 order(String bys) {
            context.order = Null.notNull(bys);
            return new BindJdbc1(context);
        }

        public BindJdbc1 order(Map<String, String> bys) {
            context.orderBy(bys);
            return new BindJdbc1(context);
        }
    }

    @RequiredArgsConstructor
    public static class OrderJdbc2 {
        private final ContextJdbc context;

        public BindJdbc2 orderNone() {
            return new BindJdbc2(context);
        }

        public BindJdbc2 order(String bys) {
            context.order = Null.notNull(bys);
            return new BindJdbc2(context);
        }

        public BindJdbc2 order(Map<String, String> bys) {
            context.orderBy(bys);
            return new BindJdbc2(context);
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
                                   .limit(context.page.toOffset(), context.page.getPageSize())
                                   .fetch()
                        :
                        context.dsl.select(select)
                                   .from(context.from)
                                   .where(context.where)
                                   .orderBy(context.order)
                                   .limit(context.page.toOffset(), context.page.getPageSize())
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
                        .limit(context.page.toOffset(), context.page.getPageSize())
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
