package pro.fessional.wings.slardar.async;

import com.alibaba.ttl.TtlCallable;
import com.alibaba.ttl.TtlRunnable;
import com.alibaba.ttl.threadpool.TtlExecutors;
import org.jetbrains.annotations.NotNull;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.util.concurrent.ListenableFuture;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;

/**
 * use Ttl ThreadPool
 *
 * @author trydofor
 * @since 2022-12-03
 */
public class TtlThreadPoolTaskScheduler extends ThreadPoolTaskScheduler {

    protected final boolean releaseTtlValueReferenceAfterRun;
    protected final boolean idempotent;

    public TtlThreadPoolTaskScheduler() {
        this(false, true);
    }

    public TtlThreadPoolTaskScheduler(boolean releaseTtlValueReferenceAfterRun, boolean idempotent) {
        this.idempotent = idempotent;
        this.releaseTtlValueReferenceAfterRun = releaseTtlValueReferenceAfterRun;
    }

    @Override
    @NotNull
    protected ExecutorService initializeExecutor(
            @NotNull ThreadFactory threadFactory,
            @NotNull RejectedExecutionHandler rejectedExecutionHandler
    ) {
        final ExecutorService es = super.initializeExecutor(threadFactory, rejectedExecutionHandler);
        return TtlExecutors.getTtlExecutorService(es);
    }

    @Override
    public void execute(@NotNull Runnable task) {
        super.execute(TtlRunnable.get(task, releaseTtlValueReferenceAfterRun, idempotent));
    }

    @Override
    @NotNull
    public Future<?> submit(@NotNull Runnable task) {
        return super.submit(TtlRunnable.get(task, releaseTtlValueReferenceAfterRun, idempotent));
    }

    @Override
    @NotNull
    public <T> Future<T> submit(@NotNull Callable<T> task) {
        return super.submit(TtlCallable.get(task, releaseTtlValueReferenceAfterRun, idempotent));
    }

    @Override
    @NotNull
    @Deprecated
    public ListenableFuture<?> submitListenable(@NotNull Runnable task) {
        return super.submitListenable(TtlRunnable.get(task, releaseTtlValueReferenceAfterRun, idempotent));
    }

    @Override
    @NotNull
    @Deprecated
    public <T> ListenableFuture<T> submitListenable(@NotNull Callable<T> task) {
        return super.submitListenable(TtlCallable.get(task, releaseTtlValueReferenceAfterRun, idempotent));
    }

    @Override
    public ScheduledFuture<?> schedule(@NotNull Runnable task, @NotNull Trigger trigger) {
        return super.schedule(TtlRunnable.get(task, releaseTtlValueReferenceAfterRun, idempotent), trigger);
    }

    @Override
    @NotNull
    @Deprecated
    public ScheduledFuture<?> schedule(@NotNull Runnable task, @NotNull Date startTime) {
        return schedule(task, startTime.toInstant());
    }

    @Override
    @NotNull
    @Deprecated
    public ScheduledFuture<?> scheduleAtFixedRate(@NotNull Runnable task, @NotNull Date startTime, long period) {
        return scheduleAtFixedRate(task, startTime.toInstant(), Duration.ofMillis(period));
    }

    @Override
    @NotNull
    @Deprecated
    public ScheduledFuture<?> scheduleAtFixedRate(@NotNull Runnable task, long period) {
        return scheduleAtFixedRate(task, Duration.ofMillis(period));
    }

    @Override
    @NotNull
    @Deprecated
    public ScheduledFuture<?> scheduleWithFixedDelay(@NotNull Runnable task, @NotNull Date startTime, long delay) {
        return scheduleWithFixedDelay(task, startTime.toInstant(), Duration.ofMillis(delay));
    }

    @Override
    @NotNull
    @Deprecated
    public ScheduledFuture<?> scheduleWithFixedDelay(@NotNull Runnable task, long delay) {
        return scheduleWithFixedDelay(task, Duration.ofMillis(delay));
    }

    @Override
    @NotNull
    public ScheduledFuture<?> schedule(@NotNull Runnable task, @NotNull Instant startTime) {
        return super.schedule(TtlRunnable.get(task, releaseTtlValueReferenceAfterRun, idempotent), startTime);
    }

    @Override
    @NotNull
    public ScheduledFuture<?> scheduleAtFixedRate(@NotNull Runnable task, @NotNull Instant startTime, @NotNull Duration period) {
        return super.scheduleAtFixedRate(TtlRunnable.get(task, releaseTtlValueReferenceAfterRun, idempotent), startTime, period);
    }

    @Override
    @NotNull
    public ScheduledFuture<?> scheduleAtFixedRate(@NotNull Runnable task, @NotNull Duration period) {
        return super.scheduleAtFixedRate(TtlRunnable.get(task, releaseTtlValueReferenceAfterRun, idempotent), period);
    }

    @Override
    @NotNull
    public ScheduledFuture<?> scheduleWithFixedDelay(@NotNull Runnable task, @NotNull Instant startTime, @NotNull Duration delay) {
        return super.scheduleWithFixedDelay(TtlRunnable.get(task, releaseTtlValueReferenceAfterRun, idempotent), startTime, delay);
    }

    @Override
    @NotNull
    public ScheduledFuture<?> scheduleWithFixedDelay(@NotNull Runnable task, @NotNull Duration delay) {
        return super.scheduleWithFixedDelay(TtlRunnable.get(task, releaseTtlValueReferenceAfterRun, idempotent), delay);
    }

    @Override
    @NotNull
    public Thread newThread(@NotNull Runnable task) {
        return super.newThread(TtlRunnable.get(task, releaseTtlValueReferenceAfterRun, idempotent));
    }

    @Override
    @NotNull
    public Thread createThread(@NotNull Runnable task) {
        return super.createThread(TtlRunnable.get(task, releaseTtlValueReferenceAfterRun, idempotent));
    }
}
