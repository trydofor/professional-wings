package pro.fessional.wings.faceless.database.jooq.listener;

import lombok.Getter;
import lombok.Setter;
import org.jooq.ExecuteContext;
import org.jooq.Query;
import org.jooq.impl.DefaultExecuteListener;
import pro.fessional.mirana.time.StopWatch.Watch;
import pro.fessional.wings.silencer.watch.Watches;

import java.util.function.BiConsumer;

/**
 * 慢查询日志
 *
 * @author trydofor
 * @since 2021-01-14
 */

public class SlowSqlListener extends DefaultExecuteListener {

    public enum ContextKey {
        EXECUTING_STOP_WATCH
    }

    /**
     * slow阈值的毫秒数，-1表示关闭此功能
     */
    @Getter @Setter
    private long thresholdMillis = -1;

    /**
     * 取代日志，自行处理耗时与SQL
     */
    @Getter @Setter
    private BiConsumer<Long, String> costAndSqlConsumer = (c, s) -> Watches.log.warn("SLOW-SQL cost={}ms, sql={}", c, s);

    @Override
    public void start(ExecuteContext ctx) {
        if (thresholdMillis < 0) return;

        String name = "JooqSlowSql:";
        final Query query = ctx.query();
        if (query != null) {
            name = name + query.getClass().getSimpleName();
        }

        final Watch watch = Watches.acquire().start(name);
        ctx.data(ContextKey.EXECUTING_STOP_WATCH, watch);
    }

    @Override
    public void end(ExecuteContext ctx) {
        final Watch watch = (Watch) ctx.data(ContextKey.EXECUTING_STOP_WATCH);
        if (watch == null) return;

        watch.close();
        final long cost = watch.getElapseMs();
        final boolean slow = cost >= thresholdMillis;
        try {
            if (slow) {
                costAndSqlConsumer.accept(cost, ctx.sql());
            }
        }
        finally {
            Watches.release(true, slow ? "SlowSqlListener" : null);
        }
    }
}
