package pro.fessional.wings.faceless.database.jooq.listener;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jooq.Clause;
import org.jooq.Configuration;
import org.jooq.Context;
import org.jooq.Keyword;
import org.jooq.Param;
import org.jooq.QueryPart;
import org.jooq.TableField;
import org.jooq.VisitContext;
import org.jooq.VisitListener;
import org.jooq.impl.QOM;
import org.jooq.impl.TableImpl;
import pro.fessional.mirana.data.Null;
import pro.fessional.mirana.pain.DebugException;
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
import java.util.function.Supplier;

import static pro.fessional.wings.faceless.database.WingsTableCudHandler.Cud;

/**
 * <pre>
 * Only support for single table insert,update,delete in jooq.
 * No support for merge and replace.
 * No support for batch execution (cannot get bind values)
 * Only support where conditions for eq,le,ge,in.
 * INSERT_ON_DUPLICATE_KEY_UPDATE as UPDATE
 * </pre>
 *
 * @author trydofor
 * @since 2021-01-14
 */
@SuppressWarnings("removal")
@Slf4j
public class TableCudListener implements VisitListener, WingsTableCudHandler.Auto {

    /**
     * for debug only
     */
    public static boolean WarnVisit = false;

    @Setter @Getter
    private boolean create = true;
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
        EXECUTING_INSERT_UPD,
        EXECUTING_WHERE_KEY,
        EXECUTING_WHERE_CMP, // String fixed Null, WHERE_EQ, WHERE_IN
    }

    private static final String WHERE_EQ = "=";
    private static final String WHERE_IN = "in";

    @Override
    public void clauseStart(VisitContext context) {
        if (WarnVisit) {
            final String clz = scn(context.queryPart());
            final Clause clause = context.clause();
            if (clause == Clause.INSERT || clause == Clause.UPDATE || clause == Clause.DELETE) {
                log.warn(">>> clauseStart Clause=" + clause + ", Query=" + clz
                        , new DebugException("debug for call stack"));
            }
            else {
                log.warn(">>> clauseStart Clause={}, Query={}", clause, clz);
            }
        }

        if (handlers.isEmpty() || tableField.isEmpty()) return;
        if (context.renderContext() == null) return;

        final Clause clause = context.clause();
        final Cud cud;
        if (create && clause == Clause.INSERT) {
            // on duplicate key update
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
            // org.jooq.impl.Tools.BooleanDataKey#DATA_COUNT_BIND_VALUES;
            if (key instanceof Enum<?> em && "DATA_COUNT_BIND_VALUES".equals(em.name())) {
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
    @SuppressWarnings({"unchecked"})
    public void clauseEnd(VisitContext context) {

        if (WarnVisit) {
            final String clz = scn(context.queryPart());
            final Clause clause = context.clause();
            if (clause == Clause.INSERT || clause == Clause.UPDATE || clause == Clause.DELETE) {
                log.warn("<<< clauseEnd   Clause={}, Query={}\n\n", clause, clz);
            }
            else {
                log.warn(">>> clauseStart Clause={}, Query={}", clause, clz);
            }
        }

        if (handlers.isEmpty() || tableField.isEmpty()) return;

        Cud cud = (Cud) context.data(ContextKey.EXECUTING_VISIT_CUD);
        if (cud == null) return;

        final Clause clause = context.clause();
        if (clause != Clause.INSERT && clause != Clause.UPDATE & clause != Clause.DELETE) {
            return;
        }

        if (context.renderContext() == null) return;

        final String tbl = (String) context.data(ContextKey.EXECUTING_TABLE_STR);
        if (tbl == null) {
            log.warn("find CUD without table, may be unsupported, sql={}", context.renderContext());
            return;
        }

        final Map<String, List<?>> field = (Map<String, List<?>>) context.data(ContextKey.EXECUTING_FIELD_MAP);

        final Object upd = context.data(ContextKey.EXECUTING_INSERT_UPD);
        if (upd == Boolean.TRUE) {
            log.debug("find INSERT_ON_DUPLICATE_KEY_UPDATE, set CUD to update");
            cud = Cud.Update;
        }

        log.debug("handle CUD={}, table={}, filed={}", cud, tbl, field);
        final Class<?> src = this.getClass();
        final Supplier<Map<String, List<?>>> sup = field == null ? Collections::emptyMap : () -> field;

        for (WingsTableCudHandler hd : handlers) {
            try {
                hd.handle(src, cud, tbl, sup);
            }
            catch (Exception e) {
                StringBuilder msg = new StringBuilder();
                msg.append("failed to handle cud=").append(cud);
                msg.append(", table=").append(tbl);
                msg.append(", handle=").append(hd.getClass());
                if (field != null && !field.isEmpty()) {
                    msg.append(", field=");
                    for (Map.Entry<String, List<?>> en : field.entrySet()) {
                        msg.append(',').append(en.getKey()).append(':').append(en.getValue());
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

    @SuppressWarnings({"unchecked"})
    private void handleUpdate(VisitContext context) {
        final Clause clause = context.clause();
        final QueryPart query = context.queryPart();

        // FieldMapForUpdate:AbstractQueryPartMap
        if (clause == Clause.TABLE_REFERENCE && query instanceof TableImpl) {
            handleTable(context, (TableImpl<?>) query);
        }
        else if (clause == Clause.UPDATE_SET && query instanceof final Map<?, ?> updSet) {
            final Set<String> fds = (Set<String>) context.data(ContextKey.EXECUTING_FIELD_KEY);
            if (fds == null) {
                log.warn("should not be here, update-table without key");
                return;
            }
            if (fds.isEmpty()) {
                log.debug("skip careless field in update");
                return;
            }

            final Map<String, List<Object>> field = (Map<String, List<Object>>) context.data(ContextKey.EXECUTING_FIELD_MAP);
            if (field == null) {
                log.warn("should not be here, update-table without field");
                return;
            }

            // handle set
            for (Map.Entry<?, ?> en : updSet.entrySet()) {
                final Object ky = en.getKey();
                final Object vl = en.getValue();
                if (ky instanceof TableField && (vl == null || vl instanceof Param)) {
                    final String fd = ((TableField<?, ?>) ky).getName();
                    if (fds.contains(fd)) {
                        final List<Object> lst = field.computeIfAbsent(fd, k -> new ArrayList<>());
                        lst.add(vl == null ? null : ((Param<?>) vl).getValue());
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

    @SuppressWarnings({"unchecked", "UnstableApiUsage"})
    private void handleWhere(VisitContext context, Clause clause, QueryPart query) {
        if (clause == Clause.FIELD_REFERENCE && query instanceof TableField<?, ?> field) {
            if (context.data(ContextKey.EXECUTING_WHERE_CMP) == null) {
                log.debug("skip where without where-clause");
                return;
            }

            final Set<String> fds = (Set<String>) context.data(ContextKey.EXECUTING_FIELD_KEY);
            if (fds == null) {
                log.warn("should not be here, table without key");
                return;
            }

            final String fd = field.getName();
            if (fds.contains(fd)) {
                log.debug("handle where-field={}", fd);
                context.data(ContextKey.EXECUTING_WHERE_KEY, fd);
            }
            else {
                log.debug("skip careless where-field={}", fd);
                // remove the old key
                context.data(ContextKey.EXECUTING_WHERE_KEY, null);
            }
        }
        // 3.14 use query instanceof Keyword
//        else if ((clause == Clause.CONDITION_COMPARISON || clause == Clause.CONDITION_IN) && query instanceof Keyword) {
//            if (context.data(ContextKey.EXECUTING_WHERE_KEY) == null) {
//                log.debug("skip comparison without where-key or careless");
//                return;
//            }
//
//            final String cmp = query.toString();
//            if (cmp.equals("=") || cmp.equals(">=") || cmp.equals("<=")) {
//                log.debug("handle comparison. key={}", cmp);
//                context.data(ContextKey.EXECUTING_WHERE_CMP, WHERE_EQ);
//            }
//            else if (cmp.equalsIgnoreCase("in")) {
//                log.debug("handle comparison. key=in");
//                context.data(ContextKey.EXECUTING_WHERE_CMP, WHERE_IN);
//            }
//            else {
//                log.debug("skip comparison. key={}", cmp);
//            }
//        }
        // 3.16 use QOM
        else if ((clause == Clause.CONDITION_COMPARISON || clause == Clause.CONDITION_IN)) {
            if (query instanceof QOM.Eq || query instanceof QOM.Ge || query instanceof QOM.Le) {
                log.debug("handle comparison. key={}", query);
                context.data(ContextKey.EXECUTING_WHERE_CMP, WHERE_EQ);
            }
            else if (query instanceof QOM.In || query instanceof QOM.InList) {
                log.debug("handle comparison. key=in");
                context.data(ContextKey.EXECUTING_WHERE_CMP, WHERE_IN);
            }
        }
        else if (clause == Clause.FIELD_VALUE && query instanceof Param<?> param) {
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
                final List<Object> lst = map.computeIfAbsent(fd, k -> new ArrayList<>());
                lst.add(param.getValue());
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

    @SuppressWarnings({"unchecked"})
    private void handleInsert(VisitContext context) {
        final Clause clause = context.clause();
        final QueryPart query = context.queryPart();

        if (clause == Clause.TABLE_REFERENCE && query instanceof TableImpl) {
            handleTable(context, (TableImpl<?>) query);
        }
        // QueryPartCollectionView
        else if (clause == Clause.INSERT_INSERT_INTO && query instanceof Collection<?> col) {
            final Set<String> fds = (Set<String>) context.data(ContextKey.EXECUTING_FIELD_KEY);
            if (fds == null) {
                log.warn("should not be here, insert-table without key");
                return;
            }
            if (fds.isEmpty()) {
                log.debug("skip careless field in insert");
                return;
            }

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
                context.data(ContextKey.EXECUTING_INSERT_IDX, idx);
                context.data(ContextKey.EXECUTING_INSERT_CNT, new AtomicInteger(0));
            }
        }
        else if (clause == Clause.FIELD_VALUE && query instanceof Param<?> param) {
            final Map<Integer, String> idx = (Map<Integer, String>) context.data(ContextKey.EXECUTING_INSERT_IDX);
            if (idx == null) {
                log.debug("skip careless insert-fields without index");
                return;
            }

            final AtomicInteger cnt = (AtomicInteger) context.data(ContextKey.EXECUTING_INSERT_CNT);
            if (cnt == null) {
                log.warn("should not be here, insert-fields without cnt");
                return;
            }

            final String name = idx.get(cnt.incrementAndGet());
            if (name == null) {
                log.debug("skip careless insert-field not in index");
            }
            else {
                final Map<String, List<Object>> field = (Map<String, List<Object>>) context.data(ContextKey.EXECUTING_FIELD_MAP);
                if (field == null) {
                    log.warn("should not be here, insert-field without field");
                }
                else {
                    final List<Object> lst = field.computeIfAbsent(name, k -> new ArrayList<>());
                    lst.add(param.getValue());
                    log.debug("handle insert-field={} with value", name);
                }
            }
        }
        else if (clause == Clause.INSERT_ON_DUPLICATE_KEY_UPDATE && query instanceof Keyword) {
            context.data(ContextKey.EXECUTING_INSERT_UPD, Boolean.TRUE);
        }
    }

    @Override
    public boolean accept(@NotNull Class<?> source, @NotNull Cud cud, @NotNull String table) {
        // this class or no handler
        if (source == this.getClass() || handlers.isEmpty()) return false;
        // careless table
        final Set<String> fld = tableField.get(table);
        if (fld == null) return false;

        // cud type matching
        if (create && (cud == Cud.Create || cud == Cud.Unsure)) return true;
        if (update && (cud == Cud.Update || cud == Cud.Unsure)) return true;
        if (delete && (cud == Cud.Delete || cud == Cud.Unsure)) return true;

        // default
        return false;
    }

    @Nullable
    private String scn(QueryPart obj) {
        if (obj == null) return null;

        if (obj instanceof TableImpl<?> f) {
            return obj.getClass().getSimpleName() + ":" + f.getName();
        }

        if (obj instanceof TableField<?, ?> f) {
            return obj.getClass().getSimpleName() + ":" + f.getName();
        }

        if (obj instanceof Param<?> p) {
            return obj.getClass().getSimpleName() + ":name=" + p.getParamName() + ",value=" + p.getValue();
        }

        if (obj instanceof Keyword k) {
            return obj.getClass().getSimpleName() + ":" + k;
        }

        return obj.getClass().getSimpleName();
    }
}
