package pro.fessional.wings.faceless.database.jooq.listener;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import org.jooq.Clause;
import org.jooq.Configuration;
import org.jooq.Context;
import org.jooq.Keyword;
import org.jooq.Param;
import org.jooq.QueryPart;
import org.jooq.TableField;
import org.jooq.VisitContext;
import org.jooq.impl.DefaultVisitListener;
import org.jooq.impl.TableImpl;
import pro.fessional.mirana.data.Null;
import pro.fessional.wings.faceless.database.WingsTableCudHandler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import static pro.fessional.wings.faceless.database.WingsTableCudHandler.Cud;

/**
 * 仅支持jooq的单表insert,update,delete。<p>
 * 不支持merge和replace。不支持batch执行(无法获得bind值)<p>
 * 仅支持eq,le,ge,in的where条件。<p>
 * 注意：visit可能触发多次，任何需要render的地方，如日志debug，toString等<p>
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

    public enum ContextKey {
        EXECUTING_VISIT_CUD, // Cud
        EXECUTING_TABLE_STR, // String
        EXECUTING_FIELD_KEY, // SET<String>
        EXECUTING_FIELD_MAP, // Map<String, List<Object>>
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
            final String clz = scn(context.queryPart());
            final Clause clause = context.clause();
            if (clause == Clause.INSERT || clause == Clause.UPDATE || clause == Clause.DELETE) {
                log.warn(">>> clauseStart Clause=" + clause + ", Query=" + clz
                        , new RuntimeException("debug for call stack"));
            }
            else {
                log.warn(">>> clauseStart Clause=" + clause + ", Query=" + clz);
            }
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
                if (WarnVisit) {
                    log.warn(">>> got DATA_COUNT_BIND_VALUES");
                }
                context.data(ContextKey.EXECUTING_VISIT_CUD, cud);
                return;
            }
        }

        context.data(ContextKey.EXECUTING_VISIT_CUD, null);
    }

    @Override
    @SuppressWarnings({"unchecked", "deprecation"})
    public void clauseEnd(VisitContext context) {

        if (WarnVisit) {
            final String clz = scn(context.queryPart());
            final Clause clause = context.clause();
            if (clause == Clause.INSERT || clause == Clause.UPDATE || clause == Clause.DELETE) {
                log.warn("<<< clauseEnd   Clause=" + clause + ", Query=" + clz + "\n\n");
            }
            else {
                log.warn(">>> clauseStart Clause=" + clause + ", Query=" + clz);
            }
        }

        final Cud cud = (Cud) context.data(ContextKey.EXECUTING_VISIT_CUD);
        if (cud == null) return;

        final Clause clause = context.clause();
        if (clause != Clause.INSERT && clause != Clause.UPDATE & clause != Clause.DELETE) {
            return;
        }

        if (context.renderContext() == null) return;

        final String table = (String) context.data(ContextKey.EXECUTING_TABLE_STR);
        if (table == null) {
            log.warn("find CUD without table, may be unsupported, sql={}", context.renderContext());
            return;
        }

        Map<String, List<?>> field = (Map<String, List<?>>) context.data(ContextKey.EXECUTING_FIELD_MAP);
        if (field == null) field = Collections.emptyMap();

        log.debug("handle CUD={}, table={}, filed={}", cud, table, field);
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
                    for (Map.Entry<String, List<?>> en : field.entrySet()) {
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
            final Context<?> ctx = context.context();
            final Configuration cnf = ctx.configuration();
            log.warn("==> visitStart  Clause={}, Query={}, Context={}, Config={}",
                    context.clause(),
                    scn(context.queryPart()),
                    ctx.getClass().getSimpleName() + "@" + System.identityHashCode(ctx),
                    cnf.getClass().getSimpleName() + "@" + System.identityHashCode(cnf));
        }

        if (handlers.isEmpty() || tableField.isEmpty()) return;
        if (context.renderContext() == null) return;

        final Cud cud = (Cud) context.data(ContextKey.EXECUTING_VISIT_CUD);
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
            log.debug("handle delete-where");
            context.data(ContextKey.EXECUTING_WHERE_CMP, Null.Str);
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
            final Set<String> fds = (Set<String>) context.data(ContextKey.EXECUTING_FIELD_KEY);
            if (fds == null) {
                log.debug("should not be here, update-table without key");
                return;
            }

            final Map<String, List<Object>> map = (Map<String, List<Object>>) context.data(ContextKey.EXECUTING_FIELD_MAP);
            if (map == null) {
                log.debug("should not be here, update-table without map");
                return;
            }

            final Map<?, ?> updSet = (Map<?, ?>) query;
            for (Map.Entry<?, ?> en : updSet.entrySet()) {
                final Object ky = en.getKey();
                final Object vl = en.getValue();
                if (ky instanceof TableField && (vl == null || vl instanceof Param)) {
                    final String fd = ((TableField<?, ?>) ky).getName();
                    if (fds.contains(fd)) {
                        final List<Object> set = map.computeIfAbsent(fd, k -> new ArrayList<>());
                        set.add(vl == null ? null : ((Param<?>) vl).getValue());
                        log.debug("handle update-field, name={}", fd);
                    }
                    else {
                        log.debug("skip careless update-field, name={}", fd);
                    }
                }
            }
        }
        else if (clause == Clause.UPDATE_WHERE && query instanceof Keyword) {
            log.debug("handle update-where");
            context.data(ContextKey.EXECUTING_WHERE_CMP, Null.Str);
        }
        else {
            handleWhere(context, clause, query);
        }
    }

    @SuppressWarnings({"deprecation", "unchecked"})
    private void handleWhere(VisitContext context, Clause clause, QueryPart query) {
        if (clause == Clause.FIELD_REFERENCE && query instanceof TableField) {
            if (context.data(ContextKey.EXECUTING_WHERE_CMP) == null) {
                log.debug("skip where without where-clause");
                return;
            }

            final Set<String> fds = (Set<String>) context.data(ContextKey.EXECUTING_FIELD_KEY);
            if (fds == null) {
                log.debug("should not be here, table without key");
                return;
            }
            final String fd = ((TableField<?, ?>) query).getName();
            if (fds.contains(fd)) {
                log.debug("handle where-field={}", fd);
                context.data(ContextKey.EXECUTING_WHERE_KEY, fd);
            }
            else {
                log.debug("skip careless where-field={}", fd);
                context.data(ContextKey.EXECUTING_WHERE_KEY, null);
            }
        }
        // 3.16后使用QOM，3.14为query instanceof Keyword
        else if ((clause == Clause.CONDITION_COMPARISON || clause == Clause.CONDITION_IN) && query instanceof Keyword) {
            if (context.data(ContextKey.EXECUTING_WHERE_KEY) == null) {
                log.debug("skip comparison without where-key or careless");
                return;
            }

            final String cmp = query.toString();
            if (cmp.equals("=") || cmp.equals(">=") || cmp.equals("<=")) {
                log.debug("handle comparison. key={}", cmp);
                context.data(ContextKey.EXECUTING_WHERE_CMP, WHERE_EQ);
            }
            else if (cmp.equalsIgnoreCase("in")) {
                log.debug("handle comparison. key=in");
                context.data(ContextKey.EXECUTING_WHERE_CMP, WHERE_IN);
            }
            else {
                log.debug("skip comparison. key={}", cmp);
            }
        }
        else if (clause == Clause.FIELD_VALUE && query instanceof Param) {
            final String fd = (String) context.data(ContextKey.EXECUTING_WHERE_KEY);
            if (fd == null) {
                log.debug("skip where-field without where-key or careless");
                return;
            }

            final Map<String, List<Object>> map = (Map<String, List<Object>>) context.data(ContextKey.EXECUTING_FIELD_MAP);
            if (map == null) {
                log.debug("skip where-field without where-table or careless");
                return;
            }

            final Object cmp = context.data(ContextKey.EXECUTING_WHERE_CMP);
            if (cmp == WHERE_EQ || cmp == WHERE_IN) {
                log.debug("handle where-value key={}", cmp);
                final List<Object> set = map.computeIfAbsent(fd, k -> new ArrayList<>());
                set.add(((Param<?>) query).getValue());
            }
        }
    }

    private void handleTable(VisitContext context, TableImpl<?> query) {
        final String tbl = query.getName();
        final Set<String> fds = tableField.get(tbl);
        if (fds == null) {
            if (WarnVisit) {
                log.warn("skip careless table={}", tbl);
            }
            else {
                log.debug("skip careless table={}", tbl);
            }
            context.data(ContextKey.EXECUTING_VISIT_CUD, null);
        }
        else {
            log.debug("handle table={}", tbl);
            context.data(ContextKey.EXECUTING_TABLE_STR, tbl);
            context.data(ContextKey.EXECUTING_FIELD_KEY, fds);
            context.data(ContextKey.EXECUTING_FIELD_MAP, new LinkedHashMap<>());
        }
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
            final Set<String> fds = (Set<String>) context.data(ContextKey.EXECUTING_FIELD_KEY);
            if (fds == null) {
                log.debug("should not be here, insert-table without key");
                return;
            }

            final Collection<?> col = (Collection<?>) query;
            int cnt = 0;
            final Map<Integer, String> idx = new HashMap<>();
            for (Object o : col) {
                if (o instanceof TableField) {
                    cnt++;
                    final String name = ((TableField<?, ?>) o).getName();
                    if (fds.contains(name)) {
                        log.debug("handle insert-field index={}, name={}", cnt, name);
                        idx.put(cnt, name);
                    }
                }
            }
            if (cnt > 0) {
                log.debug("handle insert-fields. count={}", cnt);
                context.data(ContextKey.EXECUTING_INSERT_CNT, new AtomicInteger(0));
                context.data(ContextKey.EXECUTING_INSERT_IDX, idx);
            }
        }
        else if (clause == Clause.FIELD_VALUE && query instanceof Param) {
            final AtomicInteger cnt = (AtomicInteger) context.data(ContextKey.EXECUTING_INSERT_CNT);
            if (cnt == null) {
                log.debug("should not be here, insert-fields without cnt");
                return;
            }
            final Map<Integer, String> idx = (Map<Integer, String>) context.data(ContextKey.EXECUTING_INSERT_IDX);
            if (idx == null) {
                log.debug("skip careless insert-fields without index");
                return;
            }
            final String name = idx.get(cnt.incrementAndGet());
            if (name == null) {
                log.debug("skip careless insert-field not in index");
            }
            else {
                final Map<String, List<Object>> map = (Map<String, List<Object>>) context.data(ContextKey.EXECUTING_FIELD_MAP);
                if (map == null) {
                    log.debug("should not be here, insert-field without map");
                }
                else {
                    final List<Object> set = map.computeIfAbsent(name, k -> new ArrayList<>());
                    set.add(((Param<?>) query).getValue());
                    log.debug("handle insert-field={} with value", name);
                }
            }
        }
    }

    @Nullable
    private String scn(Object obj) {
        if (obj == null) return null;

        if (obj instanceof TableImpl) {
            TableImpl<?> f = (TableImpl<?>) obj;
            return obj.getClass().getSimpleName() + ":" + f.getName();
        }

        if (obj instanceof TableField) {
            TableField<?, ?> f = (TableField<?, ?>) obj;
            return obj.getClass().getSimpleName() + ":" + f.getName();
        }

        if (obj instanceof Param) {
            Param<?> p = (Param<?>) obj;
            return obj.getClass().getSimpleName() + ":name=" + p.getParamName() + ",value=" + p.getValue();
        }

        if (obj instanceof Keyword) {
            Keyword k = (Keyword) obj;
            return obj.getClass().getSimpleName() + ":" + k;
        }

        return obj.getClass().getSimpleName();
    }
}
