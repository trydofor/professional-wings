package pro.fessional.wings.faceless.database.jooq.listener;

import org.jooq.Context;
import org.jooq.QualifiedAsterisk;
import org.jooq.QueryPart;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.VisitContext;
import org.jooq.VisitListener;
import org.jooq.impl.DSL;
import org.jooq.impl.TableImpl;

/**
 * `visit` may be triggered multiple times, anywhere a render is needed, e.g. toString, getSQL0
 *
 * @author trydofor
 * @since 2021-01-14
 */
@SuppressWarnings("removal")
public class AutoQualifyFieldListener implements VisitListener {

    @Override
    public void visitStart(VisitContext context) {
        // only rendering
        if(context.renderContext() == null) return;

        QueryPart qp = context.queryPart();
        if (qp instanceof TableField<?, ?> field) {
            if (notAlias(field.getTable(), context.context()) == 0) {
                context.queryPart(DSL.field(field.getUnqualifiedName(), field.getDataType()));
            }
        }
        else if (qp instanceof QualifiedAsterisk asterisk) {
            if (notAlias(asterisk.qualifier(), context.context()) == 0) {
                context.queryPart(DSL.sql("*"));
            }
        }
    }

    private int notAlias(Table<?> table, Context<?> ctx) {
        if (!(table instanceof TableImpl)) return -1;

        for (org.jooq.Clause clause : ((TableImpl<?>) table).clauses(ctx)) {
            if (clause == org.jooq.Clause.TABLE_ALIAS) {
                return 1;
            }
        }
        return 0;
    }
}
