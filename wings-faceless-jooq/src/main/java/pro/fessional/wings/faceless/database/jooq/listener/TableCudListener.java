package pro.fessional.wings.faceless.database.jooq.listener;

import lombok.extern.slf4j.Slf4j;
import org.jooq.Clause;
import org.jooq.Context;
import org.jooq.QueryPart;
import org.jooq.VisitContext;
import org.jooq.impl.DefaultVisitListener;

/**
 * @author trydofor
 * @since 2021-01-14
 */
@Slf4j
public class TableCudListener extends DefaultVisitListener {

    @Override
    public void visitStart(VisitContext context) {
        QueryPart qp = context.queryPart();
        final Clause clause = context.clause();
        Context<?> ctx = context.context();
        log.info("=== QueryPart={}, clause={}", qp, clause);
    }
}
