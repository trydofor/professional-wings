package pro.fessional.wings.tiny.grow.track.impl;

import com.alibaba.ttl.threadpool.TtlExecutors;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import pro.fessional.mirana.time.ThreadNow;
import pro.fessional.wings.silencer.spring.boot.ConditionalWingsEnabled;
import pro.fessional.wings.tiny.grow.track.TinyTrackService;
import pro.fessional.wings.tiny.grow.track.TinyTracking;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import static org.springframework.scheduling.annotation.AsyncAnnotationBeanPostProcessor.DEFAULT_TASK_EXECUTOR_BEAN_NAME;

/**
 * @author trydofor
 * @since 2024-07-26
 */
@Service
@ConditionalWingsEnabled
@Slf4j
public class TinyTrackServiceImpl implements TinyTrackService, InitializingBean, DisposableBean {

    @Setter(onMethod_ = { @Autowired(required = false), @Qualifier(DEFAULT_TASK_EXECUTOR_BEAN_NAME) })
    private Executor executor;
    private boolean innerExecutor = false;

    @Setter(onMethod_ = { @Autowired })
    protected List<Collector> trackCollector;

    @Setter(onMethod_ = { @Autowired })
    protected List<Preparer> trackPreparer;

    @Override
    public FutureTask<Void> async(Runnable run) {
        FutureTask<Void> future = new FutureTask<>(run, null);
        executor.execute(future);
        return future;
    }

    @Override
    @NotNull
    public TinyTracking begin(@NotNull String key, @NotNull String ref) {
        final TinyTracking tracking = new TinyTracking(ThreadNow.millis(), key, ref);

        for (Preparer pr : trackPreparer) {
            try {
                pr.prepare(tracking);
            }
            catch (Exception e) {
                log.error("tiny-track skip failed preparer=" + pr.getClass(), e);
            }
        }

        return tracking;
    }

    @Override
    public void track(@NotNull TinyTracking tracking, boolean async) {
        if (async) {
            executor.execute(() -> track(tracking));
        }
        else {
            track(tracking);
        }
    }

    @Override
    public void track(@NotNull TinyTracking tracking) {
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
    public void afterPropertiesSet() {
        if (executor == null) {
            log.warn("should reuse autowired thread pool");
            executor = TtlExecutors.getTtlExecutorService(Executors.newWorkStealingPool(2));
            innerExecutor = true;
        }
    }

    @Override
    public void destroy() {
        if (innerExecutor && executor instanceof ExecutorService es) {
            es.shutdown();
        }
    }
}
