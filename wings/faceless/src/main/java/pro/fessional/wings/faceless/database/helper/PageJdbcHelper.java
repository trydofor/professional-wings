package pro.fessional.wings.faceless.database.helper;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import pro.fessional.mirana.data.Null;
import pro.fessional.mirana.page.PageQuery;
import pro.fessional.mirana.page.PageResult;
import pro.fessional.mirana.page.PageUtil;
import pro.fessional.wings.faceless.converter.WingsConverter;

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
public class PageJdbcHelper {

    @NotNull
    public static CountJdbc use(JdbcTemplate tpl, PageQuery page) {
        return use(tpl, page, -1);
    }

    /**
     * Page query by jdbc
     *
     * @param tpl   jdbc template
     * @param page  query info
     * @param total the count cached in service level
     */
    @NotNull
    public static CountJdbc use(JdbcTemplate tpl, PageQuery page, int total) {
        ContextJdbc context = new ContextJdbc();
        context.page = page;
        context.tpl = tpl;
        context.total = total;
        return new CountJdbc(context);
    }

    @RequiredArgsConstructor
    public static class CountJdbc {
        private final ContextJdbc context;

        public OrderJdbc2 wrap(String select, Map<String, String> bys, String... dft) {
            context.wrap = select;
            context.orderBy(bys, dft);
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
            return into(RowMapperHelper.of(claz));
        }

        @NotNull
        public <E> PageResult<E> into(Class<E> claz, WingsConverter<?, ?>... converters) {
            return into(RowMapperHelper.of(claz, converters));
        }

        @NotNull
        public <E> PageResult<E> into(RowMapper<E> mapper) {
            if (context.total < 0) {
                Integer total = context.tpl.queryForObject("SELECT " + context.count + " " + context.fromWhere, Integer.class, context.bind);
                if (total != null) {
                    context.total = total;
                }
            }
            List<E> list = null;
            if (context.total > 0) {
                StringBuilder sql = new StringBuilder(context.select.length() + context.fromWhere.length() + context.order.length() + 50);
                sql.append("SELECT ");
                sql.append(context.select);
                sql.append(' ');
                sql.append(context.fromWhere);
                context.orderLimit(sql);
                list = context.tpl.query(sql.toString(), mapper, context.bind);
            }

            return PageResult.ok(context.total, list, context.page);
        }
    }

    @RequiredArgsConstructor
    public static class IntoJdbc2 {
        private final ContextJdbc context;

        @NotNull
        public <E> PageResult<E> fetchInto(Class<E> claz) {
            return fetchInto(RowMapperHelper.of(claz));
        }

        @NotNull
        public <E> PageResult<E> fetchInto(Class<E> claz, WingsConverter<?, ?>... converters) {
            return fetchInto(RowMapperHelper.of(claz, converters));
        }

        @NotNull
        public <E> PageResult<E> fetchInto(RowMapper<E> mapper) {
            if (context.total < 0) {
                Integer total = context.tpl.queryForObject("SELECT count(*) FROM (" + context.wrap + ") WINGS_WRAP", Integer.class, context.bind);
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

            return PageResult.ok(context.total, list, context.page);
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

        /**
         * `PageQuery.sort` as the primary, `bys` as the mapping, and `dft` as the default
         */
        private void orderBy(Map<String, String> bys, String... dft) {
            final List<PageUtil.By> srt = PageUtil.sort(page.getSort());
            StringBuilder sb = new StringBuilder();
            if (srt.isEmpty()) {
                for (String s : dft) {
                    sb.append(',').append(s);
                }
            }
            else {
                if (dft.length > 0) {
                    bys = new HashMap<>(bys);
                    for (String s : dft) {
                        bys.putIfAbsent(s, s);
                    }
                }

                for (PageUtil.By by : srt) {
                    String fd = bys.get(by.key);
                    if (fd != null) {
                        sb.append(',').append(fd);
                        if (by.asc) {
                            sb.append(" ASC");
                        }
                        else {
                            sb.append(" DESC");
                        }
                    }
                }
            }

            if (!sb.isEmpty()) {
                order = sb.substring(1);
            }
        }

        private void orderLimit(StringBuilder sql) {
            if (!order.isEmpty()) {
                sql.append(" order by ");
                sql.append(order);
            }
            sql.append(" limit ");
            int offset = page.toOffset();
            if (offset > 0) {
                sql.append(offset).append(',');
            }
            sql.append(page.getSize());
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
    public static class OrderJdbc1 {
        private final ContextJdbc context;

        public BindJdbc1 orderNone() {
            return new BindJdbc1(context);
        }

        /**
         * Specify a field or sort statement that is equivalent to the field to field mapping.
         */
        public BindJdbc1 order(String bys) {
            context.orderBy(Collections.emptyMap(), bys);
            return new BindJdbc1(context);
        }

        /**
         * Based on the mapping of alias to filed, use PageQuery's sort to match the ordering
         */
        public BindJdbc1 order(Map<String, String> bys, String... dft) {
            context.orderBy(bys, dft);
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
            context.orderBy(Collections.emptyMap(), bys);
            return new BindJdbc2(context);
        }

        public BindJdbc2 order(Map<String, String> bys, String... dft) {
            context.orderBy(bys, dft);
            return new BindJdbc2(context);
        }
    }
}
