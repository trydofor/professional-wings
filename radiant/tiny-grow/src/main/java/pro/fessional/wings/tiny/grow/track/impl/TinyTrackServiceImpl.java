package pro.fessional.wings.tiny.grow.track.impl;

import com.alibaba.ttl.threadpool.TtlExecutors;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import pro.fessional.mirana.time.ThreadNow;
import pro.fessional.wings.silencer.modulate.RuntimeMode;
import pro.fessional.wings.silencer.spring.boot.ConditionalWingsEnabled;
import pro.fessional.wings.silencer.spring.help.ApplicationContextHelper;
import pro.fessional.wings.tiny.grow.track.TinyTrackService;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static org.springframework.scheduling.annotation.AsyncAnnotationBeanPostProcessor.DEFAULT_TASK_EXECUTOR_BEAN_NAME;

/**
 * @author trydofor
 * @since 2024-07-26
 */
@Service
@ConditionalWingsEnabled
@Slf4j
public class TinyTrackServiceImpl implements TinyTrackService, InitializingBean {

    @Setter(onMethod_ = { @Autowired(required = false), @Qualifier(DEFAULT_TASK_EXECUTOR_BEAN_NAME) })
    private Executor executor;

    @Setter(onMethod_ = { @Autowired })
    protected List<Collector> trackCollector;


    @Override
    public void async(Runnable run) {
        executor.execute(run);
    }

    @Override
    @NotNull
    public Tracking begin(@NotNull String key, @NotNull String ref) {
        final Tracking tracking = new Tracking(ThreadNow.millis(), key, ref);

        tracking.setApp(ApplicationContextHelper.getApplicationName());
        tracking.addEnv("run", RuntimeMode.getRunMode().name());

        return tracking;
    }

    @Override
    public void track(@NotNull Tracking tracking, boolean async) {
        if (async) {
            executor.execute(() -> track(tracking));
        }
        else {
            track(tracking);
        }
    }

    @Override
    public void track(@NotNull Tracking tracking) {
        for (Collector cl : trackCollector) {
            try {
                cl.collect(tracking);
            }
            catch (Exception e) {
                log.error("tiny-track skip failed collector=" + cl.getClass(), e);
            }
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (executor == null) {
            log.warn("should reuse autowired thread pool");
            executor = TtlExecutors.getTtlExecutor(Executors.newWorkStealingPool(2));
        }
    }
}
