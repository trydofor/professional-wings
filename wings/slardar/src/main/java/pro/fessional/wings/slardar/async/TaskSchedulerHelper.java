package pro.fessional.wings.slardar.async;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import pro.fessional.mirana.time.ThreadNow;

import java.time.Instant;
import java.util.concurrent.ScheduledFuture;

/**
 * @author trydofor
 * @since 2022-12-05
 */
public class TaskSchedulerHelper {

    protected static ThreadPoolTaskScheduler LightTasker;
    protected static ThreadPoolTaskScheduler HeavyTasker;

    protected TaskSchedulerHelper(ThreadPoolTaskScheduler light, ThreadPoolTaskScheduler heavy) {
        LightTasker = light;
        HeavyTasker = heavy;
    }

    /**
     * @see org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor#DEFAULT_TASK_SCHEDULER_BEAN_NAME
     */
    @Contract("true->!null")
    public static ThreadPoolTaskScheduler Light(boolean nonnull) {
        if (nonnull && LightTasker == null) {
            throw new IllegalStateException("LightTasker must init before using");
        }
        return LightTasker;
    }

    /**
     * see NamingSlardarConst#slardarHeavyScheduler
     */
    @Contract("true->!null")
    public static ThreadPoolTaskScheduler Heavy(boolean nonnull) {
        if (nonnull && HeavyTasker == null) {
            throw new IllegalStateException("HeavyTasker must init before using");
        }

        return HeavyTasker;
    }

    /**
     * Get Light Scheduler if fast, otherwise Heavy.
     */
    @NotNull
    public static ThreadPoolTaskScheduler referScheduler(boolean fast) {
        return fast ? Light(true) : Heavy(true);
    }

    /**
     * Execute an async task immediately, `fast` means that the task will be finished soon, e.g. 10s.
     *
     * @see ThreadPoolTaskScheduler#execute(Runnable)
     */
    public static void execute(boolean fast, @NotNull Runnable task) {
        referScheduler(fast).execute(task);
    }

    /**
     * Execute an async task after delayMs millis (ThreadNow), `fast` means that the task will be finished soon, e.g. 10s.
     *
     * @see ThreadPoolTaskScheduler#schedule(Runnable, Instant)
     */
    public static ScheduledFuture<?> execute(boolean fast, long delayMs, @NotNull Runnable task) {
        return referScheduler(fast).schedule(task, Instant.ofEpochMilli(ThreadNow.millis() + delayMs));
    }

    /**
     * Execute an async task at specified instant, `fast` means that the task will be finished soon, e.g. 10s.
     *
     * @see ThreadPoolTaskScheduler#schedule(Runnable, Instant)
     */
    public static ScheduledFuture<?> execute(boolean fast, Instant start, @NotNull Runnable task) {
        return referScheduler(fast).schedule(task, start);
    }

    /**
     * Execute an async task by given trigger, `fast` means that the task will be finished soon, e.g. 10s.
     * Note, errorHandler, unlike other methods, does not handle DelegatingErrorHandlingRunnable.
     *
     * @see ThreadPoolTaskScheduler#schedule(Runnable, Trigger)
     */
    public static ScheduledFuture<?> execute(boolean fast, Trigger trigger, @NotNull Runnable task) {
        return referScheduler(fast).schedule(task, trigger);
    }
}
