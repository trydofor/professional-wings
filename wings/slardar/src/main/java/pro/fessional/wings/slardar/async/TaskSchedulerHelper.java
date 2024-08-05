package pro.fessional.wings.slardar.async;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.task.ThreadPoolTaskSchedulerBuilder;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import pro.fessional.mirana.time.ThreadNow;

import java.time.Instant;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author trydofor
 * @since 2022-12-05
 */
public class TaskSchedulerHelper implements DisposableBean {

    private static ThreadPoolTaskScheduler FastScheduler;
    private static ThreadPoolTaskScheduler ScheduledScheduler;
    private static ThreadPoolTaskSchedulerBuilder FastBuilder;
    private static ThreadPoolTaskSchedulerBuilder ScheduledBuilder;
    private static boolean helperPrepared = false;


    protected TaskSchedulerHelper(@NotNull ThreadPoolTaskScheduler fast, @NotNull ThreadPoolTaskScheduler scheduled,
                                  @NotNull ThreadPoolTaskSchedulerBuilder fastBuilder, @NotNull ThreadPoolTaskSchedulerBuilder scheduledBuilder) {
        FastScheduler = Objects.requireNonNull(fast);
        ScheduledScheduler = Objects.requireNonNull(scheduled);
        FastBuilder = Objects.requireNonNull(fastBuilder);
        ScheduledBuilder = Objects.requireNonNull(scheduledBuilder);
        helperPrepared = true;
    }

    @Override
    public void destroy() {
        helperPrepared = false;
        for (var task : TaskRun.values()) {
            task.cancel(false);
        }
        TaskRun.clear();
    }

    /**
     * whether this helper is prepared
     */
    public static boolean isPrepared() {
        return helperPrepared;
    }

    /**
     * configure TtlThreadPoolTaskScheduler by builder
     */
    public static TtlThreadPoolTaskScheduler Ttl(ThreadPoolTaskSchedulerBuilder builder) {
        return builder.configure(new TtlThreadPoolTaskScheduler());
    }

    @NotNull
    public static ThreadPoolTaskScheduler Scheduler(boolean fast) {
        return fast ? Fast() : Scheduled();
    }

    @NotNull
    public static ThreadPoolTaskScheduler Fast() {
        if (FastScheduler == null) {
            throw new IllegalStateException("FastScheduler must init before using");
        }
        return FastScheduler;
    }

    /**
     * @see org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor#DEFAULT_TASK_SCHEDULER_BEAN_NAME
     */
    @NotNull
    public static ThreadPoolTaskScheduler Scheduled() {
        if (ScheduledScheduler == null) {
            throw new IllegalStateException("ScheduledScheduler must init before using");
        }

        return ScheduledScheduler;
    }

    /**
     * just like default @Scheduled
     *
     * @see org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor#DEFAULT_TASK_SCHEDULER_BEAN_NAME
     */
    public static void Scheduled(@NotNull Runnable task) {
        Scheduled().execute(task);
    }

    /**
     * just like default @Scheduled
     *
     * @see org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor#DEFAULT_TASK_SCHEDULER_BEAN_NAME
     * @see ThreadPoolTaskScheduler#schedule(Runnable, Instant)
     */
    @NotNull
    public static ScheduledFuture<?> Scheduled(long delayMs, @NotNull Runnable task) {
        return Scheduled(Scheduled(), delayMs, task);
    }

    /**
     * just like default @Scheduled
     *
     * @see org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor#DEFAULT_TASK_SCHEDULER_BEAN_NAME
     * @see ThreadPoolTaskScheduler#schedule(Runnable, Instant)
     */
    @NotNull
    public static ScheduledFuture<?> Scheduled(@NotNull Instant start, @NotNull Runnable task) {
        return Scheduled(Scheduled(), start, task);
    }

    /**
     * just like default @Scheduled
     *
     * @see org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor#DEFAULT_TASK_SCHEDULER_BEAN_NAME
     * @see ThreadPoolTaskScheduler#schedule(Runnable, Trigger)
     */
    @Nullable
    public static ScheduledFuture<?> Scheduled(@NotNull Trigger trigger, @NotNull Runnable task) {
        return Scheduled(Scheduled(), trigger, task);
    }


    /**
     * just like default @Scheduled
     *
     * @see org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor#DEFAULT_TASK_SCHEDULER_BEAN_NAME
     */
    public static void Scheduled(boolean fast, @NotNull Runnable task) {
        Scheduler(fast).execute(task);
    }

    /**
     * just like default @Scheduled
     *
     * @see org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor#DEFAULT_TASK_SCHEDULER_BEAN_NAME
     * @see ThreadPoolTaskScheduler#schedule(Runnable, Instant)
     */
    @NotNull
    public static ScheduledFuture<?> Scheduled(boolean fast, long delayMs, @NotNull Runnable task) {
        return Scheduled(Scheduler(fast), delayMs, task);
    }

    /**
     * just like default @Scheduled
     *
     * @see org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor#DEFAULT_TASK_SCHEDULER_BEAN_NAME
     * @see ThreadPoolTaskScheduler#schedule(Runnable, Instant)
     */
    @NotNull
    public static ScheduledFuture<?> Scheduled(boolean fast, @NotNull Instant start, @NotNull Runnable task) {
        return Scheduled(Scheduler(fast), start, task);
    }

    /**
     * just like default @Scheduled
     *
     * @see org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor#DEFAULT_TASK_SCHEDULER_BEAN_NAME
     * @see ThreadPoolTaskScheduler#schedule(Runnable, Trigger)
     */
    @Nullable
    public static ScheduledFuture<?> Scheduled(boolean fast, @NotNull Trigger trigger, @NotNull Runnable task) {
        return Scheduled(Scheduler(fast), trigger, task);
    }

    /**
     * just like default @Scheduled
     *
     * @see org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor#DEFAULT_TASK_SCHEDULER_BEAN_NAME
     * @see ThreadPoolTaskScheduler#schedule(Runnable, Instant)
     */
    @NotNull
    public static ScheduledFuture<?> Scheduled(@NotNull ThreadPoolTaskScheduler scheduler, long delayMs, @NotNull Runnable task) {
        return Scheduled(scheduler, Instant.ofEpochMilli(ThreadNow.millis() + delayMs), task);
    }

    /**
     * just like default @Scheduled
     *
     * @see org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor#DEFAULT_TASK_SCHEDULER_BEAN_NAME
     * @see ThreadPoolTaskScheduler#schedule(Runnable, Instant)
     */
    @NotNull
    public static ScheduledFuture<?> Scheduled(@NotNull ThreadPoolTaskScheduler scheduler, @NotNull Instant start, @NotNull Runnable task) {
        final Task tsk = new Task(task);
        final var future = scheduler.schedule(tsk, start);
        if (tsk.run) {
            TaskRun.put(tsk.seq, future);
        }
        return future;
    }

    /**
     * just like default @Scheduled
     *
     * @see org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor#DEFAULT_TASK_SCHEDULER_BEAN_NAME
     * @see ThreadPoolTaskScheduler#schedule(Runnable, Trigger)
     */
    @Nullable
    public static ScheduledFuture<?> Scheduled(@NotNull ThreadPoolTaskScheduler scheduler, @NotNull Trigger trigger, @NotNull Runnable task) {
        final Task tsk = new Task(task);
        final var future = scheduler.schedule(tsk, trigger);
        if (future != null && tsk.run) {
            TaskRun.put(tsk.seq, future);
        }
        return future;
    }

    /**
     * clean done task and get the running size
     */
    public static int runningSize() {
        TaskRun.entrySet().removeIf(en -> en.getValue().isDone());
        return TaskRun.size();
    }

    private static final ConcurrentHashMap<Long, ScheduledFuture<?>> TaskRun = new ConcurrentHashMap<>();
    private static final AtomicLong TaskSeq = new AtomicLong(0);

    private static class Task implements Runnable {
        private final Long seq = TaskSeq.incrementAndGet();
        private volatile boolean run = true;
        private final Runnable runnable;

        public Task(Runnable run) {
            runnable = run;
        }

        @Override
        public void run() {
            try {
                runnable.run();
            }
            finally {
                run = false;
                TaskRun.remove(seq);
            }
        }
    }

    /**
     * Get Light ThreadPoolTaskSchedulerBuilder, IllegalStateException if nonull but null.
     */
    @NotNull
    public static ThreadPoolTaskSchedulerBuilder FastBuilder() {
        if (FastBuilder == null) {
            throw new IllegalStateException("FastBuilder must init before using");
        }
        return FastBuilder;
    }

    /**
     * Get Light ThreadPoolTaskSchedulerBuilder, IllegalStateException if nonull but null.
     */
    @NotNull
    public static ThreadPoolTaskSchedulerBuilder ScheduledBuilder() {
        if (ScheduledBuilder == null) {
            throw new IllegalStateException("ScheduledBuilder must init before using");
        }
        return ScheduledBuilder;
    }
}
