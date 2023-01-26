package pro.fessional.wings.slardar.async;

import org.jetbrains.annotations.NotNull;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import pro.fessional.mirana.time.ThreadNow;
import pro.fessional.wings.spring.consts.NamingSlardarConst;

import java.time.Instant;
import java.util.concurrent.ScheduledFuture;

/**
 * @author trydofor
 * @since 2022-12-05
 */
public class TaskSchedulerHelper {

    protected static ThreadPoolTaskScheduler LightTasker;
    protected static ThreadPoolTaskScheduler HeavyTasker;

    /**
     * @see org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor#DEFAULT_TASK_SCHEDULER_BEAN_NAME
     */
    @NotNull
    public static ThreadPoolTaskScheduler Light() {
        if (LightTasker == null) {
            throw new IllegalStateException("LightTasker must init before using");
        }
        return LightTasker;
    }

    /**
     * @see NamingSlardarConst#SlardarHeavySchedulerBean
     */
    @NotNull
    public static ThreadPoolTaskScheduler Heavy() {
        if (HeavyTasker == null) {
            throw new IllegalStateException("HeavyTasker must init before using");
        }

        return HeavyTasker;
    }

    /**
     * 获取Light或Heavy
     */
    @NotNull
    public static ThreadPoolTaskScheduler referScheduler(boolean fast) {
        return fast ? Light() : Heavy();
    }

    /**
     * 异步立即执行一个任务，fast表示此任务很快会结束，如10s
     *
     * @see ThreadPoolTaskScheduler#execute(Runnable)
     */
    public static void execute(boolean fast, @NotNull Runnable task) {
        referScheduler(fast).execute(task);
    }

    /**
     * 在delayMs毫秒（ThreadNow）后，异步执行一个任务，fast表示此任务很快会结束，如10s
     *
     * @see ThreadPoolTaskScheduler#schedule(Runnable, Instant)
     */
    public static ScheduledFuture<?> execute(boolean fast, long delayMs, @NotNull Runnable task) {
        return referScheduler(fast).schedule(task, Instant.ofEpochMilli(ThreadNow.millis() + delayMs));
    }

    /**
     * 在指定时间（fastTime构建，系统默认时区），异步执行一个任务，fast表示此任务很快会结束，如10s
     *
     * @see ThreadPoolTaskScheduler#schedule(Runnable, Instant)
     */
    public static ScheduledFuture<?> execute(boolean fast, Instant start, @NotNull Runnable task) {
        return referScheduler(fast).schedule(task, start);
    }

    /**
     * 指定trigger，异步执行一个任务，fast表示此任务很快会结束，如10s，
     * errorHandler不同于其他方法，不识别DelegatingErrorHandlingRunnable
     *
     * @see ThreadPoolTaskScheduler#schedule(Runnable, Trigger)
     */
    public static ScheduledFuture<?> execute(boolean fast, Trigger trigger, @NotNull Runnable task) {
        return referScheduler(fast).schedule(task, trigger);
    }
}
