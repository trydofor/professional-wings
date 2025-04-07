package pro.fessional.wings.faceless.database.jooq.listener;

import lombok.Getter;
import lombok.Setter;
import org.jooq.ExecuteContext;
import org.jooq.ExecuteListener;
import org.jooq.Query;
import pro.fessional.wings.silencer.watch.Watches;

import java.util.function.BiConsumer;

/**
 * Log the Slow Sql
 *
 * @author trydofor
 * @since 2021-01-14
 */
@Getter @Setter
public class SlowSqlListener implements ExecuteListener {

    public enum ContextKey {
        EXECUTING_STOP_WATCH
    }


    private String token = "SlowSqlListener";

    /**
     * threshold of slow in mills, `-1` means disable
     */
    private volatile long thresholdMillis = -1;

    /**
     * Handle time-consuming and SQL instead of logger
     */
    private BiConsumer<Long, String> costAndSqlConsumer = (c, s) -> Watches.log.warn("SLOW-SQL cost={}ms, sql={}", c, s);

    @Override
    public void start(ExecuteContext ctx) {
        if (thresholdMillis < 0) return;

        String name = "JooqSlowSql:";
        final Query query = ctx.query();
        if (query != null) {
            name = name + query.getClass().getSimpleName();
        }

        final Watches.Threshold threshold = Watches.threshold(name, thresholdMillis);
        ctx.data(ContextKey.EXECUTING_STOP_WATCH, threshold);
    }

    @Override
    public void end(ExecuteContext ctx) {
        final Watches.Threshold threshold = (Watches.Threshold) ctx.data(ContextKey.EXECUTING_STOP_WATCH);
        if (threshold == null) return;

        final boolean slow = threshold.reach();
        try {
            if (slow) {
                costAndSqlConsumer.accept(threshold.elapse(), ctx.sql());
            }
        }
        finally {
            Watches.release(true, slow ? token : null);
        }
    }
}
