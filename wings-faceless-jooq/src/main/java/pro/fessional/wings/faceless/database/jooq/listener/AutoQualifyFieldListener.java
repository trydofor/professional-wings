package pro.fessional.wings.faceless.database.jooq.listener;

import org.jooq.Context;
import org.jooq.QualifiedAsterisk;
import org.jooq.QueryPart;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.VisitContext;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultVisitListener;
import org.jooq.impl.TableImpl;

/**
 * @author trydofor
 * @since 2021-01-14
 */
public class AutoQualifyFieldListener extends DefaultVisitListener {

    @Override
    public void visitStart(VisitContext context) {
        QueryPart qp = context.queryPart();
        Context<?> ctx = context.context();
        if (qp instanceof TableField) {
            TableField<?, ?> field = (TableField<?, ?>) qp;
            if (notAlias(field.getTable(), ctx) == 0) {
                context.queryPart(DSL.field(field.getUnqualifiedName(), field.getDataType()));
            }
        } else if (qp instanceof QualifiedAsterisk) {
            QualifiedAsterisk asterisk = (QualifiedAsterisk) qp;
            if (notAlias(asterisk.qualifier(), ctx) == 0) {
                context.queryPart(DSL.sql("*"));
            }
        }
    }

    @SuppressWarnings("deprecation")
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
