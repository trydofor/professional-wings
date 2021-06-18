package pro.fessional.wings.faceless.database.jooq.listener;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jooq.Clause;
import org.jooq.Keyword;
import org.jooq.Param;
import org.jooq.QueryPart;
import org.jooq.TableField;
import org.jooq.VisitContext;
import org.jooq.impl.DefaultVisitListener;
import org.jooq.impl.TableImpl;
import pro.fessional.mirana.data.Null;
import pro.fessional.wings.faceless.database.WingsTableCudHandler;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import static pro.fessional.wings.faceless.database.WingsTableCudHandler.Cud;

/**
 * 仅支持jooq的单表insert,update,delete。
 * 不支持merge和replace。不支持batch执行(无法获得bind值)
 * 仅支持eq,le,ge,in的where条件。
 * 注意：visit可能触发多次，任何需要render的地方，如toString, getSQL0
 *
 * @author trydofor
 * @since 2021-01-14
 */
@Slf4j
public class TableCudListener extends DefaultVisitListener {

    public static boolean WarnVisit = false;

    @Setter @Getter
    private boolean insert = true;
    @Setter @Getter
    private boolean update = true;
    @Setter @Getter
    private boolean delete = true;
    @Setter @Getter
    private List<WingsTableCudHandler> handlers = Collections.emptyList();
    @Setter @Getter
    private Map<String, Set<String>> tableField = new HashMap<>();

    private enum Key {
        EXECUTING_VISIT_CUD, // Cud
        EXECUTING_TABLE_STR, // String
        EXECUTING_FIELD_KEY, // SET<String>
        EXECUTING_FIELD_MAP, // Map<String, Set<Object>>
        EXECUTING_INSERT_IDX,
        EXECUTING_INSERT_CNT,
        EXECUTING_WHERE_KEY,
        EXECUTING_WHERE_CMP, // String 固定值 Null, WHERE_EQ, WHERE_IN
    }

    private static final String WHERE_EQ = "=";
    private static final String WHERE_IN = "in";

    @Override
    @SuppressWarnings("deprecation")
    public void clauseStart(VisitContext context) {
        if (WarnVisit) {
            final QueryPart qp = context.queryPart();
            // noinspection ConstantConditions
            log.warn(">>> clauseStart Clause={}, Query={}", context.clause(), qp == null ? "null" : qp.getClass());
        }

        if (context.renderContext() == null) return;

        final Clause clause = context.clause();
        final Cud cud;
        if (insert && clause == Clause.INSERT) {
            cud = Cud.Create;
        }
        else if (update && clause == Clause.UPDATE) {
            cud = Cud.Update;
        }
        else if (delete && clause == Clause.DELETE) {
            cud = Cud.Delete;
        }
        else {
            return;
        }

        for (Map.Entry<Object, Object> ent : context.data().entrySet()) {
            final Object key = ent.getKey();
            if (key instanceof Enum<?> && ((Enum<?>) key).name().equals("DATA_COUNT_BIND_VALUES")) {
                return;
            }
        }

        context.data(Key.EXECUTING_VISIT_CUD, cud);
    }

    @Override
    @SuppressWarnings({"unchecked", "deprecation"})
    public void clauseEnd(VisitContext context) {

        if (WarnVisit) {
            final QueryPart qp = context.queryPart();
            // noinspection ConstantConditions
            log.warn("<<< clauseEnd   Clause={}, QueryPart={}", context.clause(), qp == null ? "null" : qp.getClass());
        }

        final Cud cud = (Cud) context.data(Key.EXECUTING_VISIT_CUD);
        if (cud == null) return;

        final Clause clause = context.clause();
        if (clause != Clause.INSERT && clause != Clause.UPDATE & clause != Clause.DELETE) {
            return;
        }

        if (context.renderContext() == null) return;

        final String table = (String) context.data(Key.EXECUTING_TABLE_STR);
        if (table == null) {
            log.warn("find CUD without table, may be unsupported, sql={}", context.renderContext());
            return;
        }

        Map<String, Set<Object>> field = (Map<String, Set<Object>>) context.data(Key.EXECUTING_FIELD_MAP);
        if (field == null) field = Collections.emptyMap();

        log.info("handle CUD={}, table={}, filed-size={}", cud, table, field.size());
        for (WingsTableCudHandler hd : handlers) {
            try {
                hd.handle(cud, table, field);
            }
            catch (Exception e) {
                StringBuilder msg = new StringBuilder();
                msg.append("failed to handle cud=").append(cud);
                msg.append(", table=").append(table);
                msg.append(", handle=").append(hd.getClass());
                if (!field.isEmpty()) {
                    msg.append(", field=");
                    for (Map.Entry<String, Set<Object>> en : field.entrySet()) {
                        msg.append(",").append(en.getKey()).append(":").append(en.getValue());
                    }
                }
                log.error(msg.toString(), e);
            }
        }
    }

    @Override
    public void visitStart(VisitContext context) {
        if (WarnVisit) {
            final QueryPart qp = context.queryPart();
            // noinspection ConstantConditions
            log.warn("==> visitStart  Clause={}, Query={}, Context={}", context.clause(), qp == null ? "null" : qp.getClass(), context.context().getClass());
        }

        if (handlers.isEmpty() || tableField.isEmpty()) return;
        if (context.renderContext() == null) return;

        final Cud cud = (Cud) context.data(Key.EXECUTING_VISIT_CUD);
        if (cud == null) return;

        if (cud == Cud.Create) {
            handleInsert(context);
        }
        else if (cud == Cud.Update) {
            handleUpdate(context);
        }
        else if (cud == Cud.Delete) {
            handleDelete(context);
        }
    }

    @SuppressWarnings({"deprecation"})
    private void handleDelete(VisitContext context) {
        final Clause clause = context.clause();
        final QueryPart query = context.queryPart();
        if (clause == Clause.TABLE_REFERENCE && query instanceof TableImpl) {
            handleTable(context, (TableImpl<?>) query);
        }
        else if (clause == Clause.DELETE_WHERE && query instanceof Keyword) {
            context.data(Key.EXECUTING_WHERE_CMP, Null.Str);
        }
        else {
            handleWhere(context, clause, query);
        }
    }

    @SuppressWarnings({"deprecation", "unchecked"})
    private void handleUpdate(VisitContext context) {
        final Clause clause = context.clause();
        final QueryPart query = context.queryPart();

        // FieldMapForUpdate:AbstractQueryPartMap
        if (clause == Clause.TABLE_REFERENCE && query instanceof TableImpl) {
            handleTable(context, (TableImpl<?>) query);
        }
        else if (clause == Clause.UPDATE_SET && query instanceof Map) {
            final Set<String> fds = (Set<String>) context.data(Key.EXECUTING_FIELD_KEY);
            if (fds == null) return;

            final Map<String, Set<Object>> map = (Map<String, Set<Object>>) context.data(Key.EXECUTING_FIELD_MAP);
            if (map == null) return;

            final Map<?, ?> updSet = (Map<?, ?>) query;
            for (Map.Entry<?, ?> en : updSet.entrySet()) {
                final Object ky = en.getKey();
                final Object vl = en.getValue();
                if (ky instanceof TableField && (vl == null || vl instanceof Param)) {
                    final String fd = ((TableField<?, ?>) ky).getName();
                    if (fds.contains(fd)) {
                        final Set<Object> set = map.computeIfAbsent(fd, k -> new HashSet<>());
                        set.add(vl == null ? null : ((Param<?>) vl).getValue());
                    }
                }
            }
        }
        else if (clause == Clause.UPDATE_WHERE && query instanceof Keyword) {
            context.data(Key.EXECUTING_WHERE_CMP, Null.Str);
        }
        else {
            handleWhere(context, clause, query);
        }
    }

    @SuppressWarnings({"deprecation", "unchecked"})
    private void handleWhere(VisitContext context, Clause clause, QueryPart query) {
        if (clause == Clause.FIELD_REFERENCE && query instanceof TableField) {
            if (context.data(Key.EXECUTING_WHERE_CMP) == null) return;

            final Set<String> fds = (Set<String>) context.data(Key.EXECUTING_FIELD_KEY);
            if (fds == null) return;
            final String fd = ((TableField<?, ?>) query).getName();
            if (fds.contains(fd)) {
                context.data(Key.EXECUTING_WHERE_KEY, fd);
            }
            else {
                context.data(Key.EXECUTING_WHERE_KEY, null);
            }
        }
        else if ((clause == Clause.CONDITION_COMPARISON || clause == Clause.CONDITION_IN) && query instanceof Keyword) {
            if (context.data(Key.EXECUTING_WHERE_KEY) == null) return;

            final String cmp = query.toString();
            if (cmp.equals("=") || cmp.equals(">=") || cmp.equals("<=")) {
                context.data(Key.EXECUTING_WHERE_CMP, WHERE_EQ);
            }
            else if (cmp.equalsIgnoreCase("in")) {
                context.data(Key.EXECUTING_WHERE_CMP, WHERE_IN);
            }
        }
        else if (clause == Clause.FIELD_VALUE && query instanceof Param) {
            final String fd = (String) context.data(Key.EXECUTING_WHERE_KEY);
            if (fd == null) return;

            final Map<String, Set<Object>> map = (Map<String, Set<Object>>) context.data(Key.EXECUTING_FIELD_MAP);
            if (map == null) return;

            final Object cmp = context.data(Key.EXECUTING_WHERE_CMP);
            if (cmp == WHERE_EQ || cmp == WHERE_IN) {
                final Set<Object> set = map.computeIfAbsent(fd, k -> new HashSet<>());
                set.add(((Param<?>) query).getValue());
            }
        }
    }

    private void handleTable(VisitContext context, TableImpl<?> query) {
        final String tbl = query.getName();
        final Set<String> fds = tableField.get(tbl);
        if (fds == null) return;

        context.data(Key.EXECUTING_TABLE_STR, tbl);
        context.data(Key.EXECUTING_FIELD_KEY, fds);
        context.data(Key.EXECUTING_FIELD_MAP, new HashMap<>());
    }

    @SuppressWarnings({"deprecation", "unchecked"})
    private void handleInsert(VisitContext context) {
        final Clause clause = context.clause();
        final QueryPart query = context.queryPart();

        if (clause == Clause.TABLE_REFERENCE && query instanceof TableImpl) {
            handleTable(context, (TableImpl<?>) query);
        }
        // QueryPartCollectionView
        else if (clause == Clause.INSERT_INSERT_INTO && query instanceof Collection) {
            final Set<String> fds = (Set<String>) context.data(Key.EXECUTING_FIELD_KEY);
            if (fds == null) return;

            final Collection<?> col = (Collection<?>) query;
            int cnt = 0;
            final Map<Integer, String> idx = new HashMap<>();
            for (Object o : col) {
                if (o instanceof TableField) {
                    cnt++;
                    final String name = ((TableField<?, ?>) o).getName();
                    if (fds.contains(name)) {
                        idx.put(cnt, name);
                    }
                }
            }
            if (cnt > 0) {
                context.data(Key.EXECUTING_INSERT_CNT, new AtomicInteger(0));
                context.data(Key.EXECUTING_INSERT_IDX, idx);
            }
        }
        else if (clause == Clause.FIELD_VALUE && query instanceof Param) {
            final AtomicInteger cnt = (AtomicInteger) context.data(Key.EXECUTING_INSERT_CNT);
            if (cnt == null) return;
            final Map<Integer, String> idx = (Map<Integer, String>) context.data(Key.EXECUTING_INSERT_IDX);
            if (idx == null) return;
            final String name = idx.get(cnt.incrementAndGet());
            if (name != null) {
                final Map<String, Set<Object>> map = (Map<String, Set<Object>>) context.data(Key.EXECUTING_FIELD_MAP);
                if (map == null) return;
                final Set<Object> set = map.computeIfAbsent(name, k -> new HashSet<>());
                set.add(((Param<?>) query).getValue());
            }
        }
    }
}
