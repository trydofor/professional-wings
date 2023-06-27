package pro.fessional.wings.silencer.spring.boot;

import org.jetbrains.annotations.NotNull;
import org.springframework.boot.context.event.ApplicationPreparedEvent;
import org.springframework.boot.logging.DeferredLog;
import org.springframework.context.ApplicationListener;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Logging information is recorded before the logging system is initialized,
 * and redirected to the logging system when ApplicationPreparedEvent.
 *
 * @author trydofor
 * @since 2019-05-30
 */
public class DeferredLogFactory implements ApplicationListener<ApplicationPreparedEvent> {

    private static final ConcurrentHashMap<Class<?>, DeferredLog> logs = new ConcurrentHashMap<>();

    public static DeferredLog getLog(Class<?> claz) {
        return logs.computeIfAbsent(claz, k -> new DeferredLog());
    }

    @Override
    public void onApplicationEvent(@NotNull ApplicationPreparedEvent event) {
        logs.forEach(Integer.MAX_VALUE, (k, v) -> v.replayTo(k));
    }
}
