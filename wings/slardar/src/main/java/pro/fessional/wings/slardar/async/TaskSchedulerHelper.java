package pro.fessional.wings.slardar.async;

import org.jetbrains.annotations.NotNull;
import org.springframework.boot.task.ThreadPoolTaskSchedulerBuilder;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import pro.fessional.mirana.time.ThreadNow;

import java.time.Instant;
import java.util.Objects;
import java.util.concurrent.ScheduledFuture;

/**
 * @author trydofor
 * @since 2022-12-05
 */
public class TaskSchedulerHelper {

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

    /**
     * @see org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor#DEFAULT_TASK_SCHEDULER_BEAN_NAME
     */
    @NotNull
    public static ThreadPoolTaskScheduler Fast() {
        if (FastScheduler == null) {
            throw new IllegalStateException("FastScheduler must init before using");
        }
        return FastScheduler;
    }

    /**
     * see NamingSlardarConst#slardarHeavyScheduler
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
     * @see org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor.DEFAULT_TASK_SCHEDULER_BEAN_NAME
     */
    public static void Scheduled(@NotNull Runnable task) {
        Scheduled().execute(task);
    }

    /**
     * just like default @Scheduled
     *
     * @see org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor.DEFAULT_TASK_SCHEDULER_BEAN_NAME
     */
    public static ScheduledFuture<?> Scheduled(long delayMs, @NotNull Runnable task) {
        return Scheduled().schedule(task, Instant.ofEpochMilli(ThreadNow.millis() + delayMs));
    }

    /**
     * just like default @Scheduled
     *
     * @see org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor.DEFAULT_TASK_SCHEDULER_BEAN_NAME
     */
    public static ScheduledFuture<?> Scheduled(Instant start, @NotNull Runnable task) {
        return Scheduled().schedule(task, start);
    }

    /**
     * just like default @Scheduled
     *
     * @see org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor.DEFAULT_TASK_SCHEDULER_BEAN_NAME
     */
    public static ScheduledFuture<?> Scheduled(Trigger trigger, @NotNull Runnable task) {
        return Scheduled().schedule(task, trigger);
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
