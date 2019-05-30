package pro.fessional.wings.oracle.spring.boot;

import org.springframework.boot.logging.DeferredLog;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author trydofor
 * @since 2019-05-30
 */
public class WingsDeferredLogFactory implements ApplicationListener<ApplicationEvent> {

    private static ConcurrentHashMap<Class<?>, DeferredLog> loggers = new ConcurrentHashMap<>();

    public static DeferredLog getLog(Class<?> claz) {
        return loggers.computeIfAbsent(claz, k -> new DeferredLog());
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        loggers.forEach(Integer.MAX_VALUE, (k, v) -> v.replayTo(k));
    }
}
