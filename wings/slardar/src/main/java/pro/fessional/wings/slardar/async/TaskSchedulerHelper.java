package pro.fessional.wings.slardar.async;

import org.jetbrains.annotations.NotNull;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import pro.fessional.mirana.time.ThreadNow;

import java.util.Date;
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
     * @see pro.fessional.wings.slardar.constants.SlardarNameConst#SlardarHeavySchedulerBean
     */
    @NotNull
    public static ThreadPoolTaskScheduler Heavy() {
        if (HeavyTasker == null) {
            throw new IllegalStateException("HeavyTasker must init before using");
        }

        return HeavyTasker;
    }

    /**
     * 异步立即执行一个任务，fast表示此任务很快会结束，如10s
     *
     * @see ThreadPoolTaskScheduler#execute(Runnable)
     */
    public static void execute(boolean fast, @NotNull Runnable task) {
        ThreadPoolTaskScheduler scheduler = fast ? Light() : Heavy();
        scheduler.execute(task);
    }

    /**
     * 在delayMs毫秒（ThreadNow）后，异步执行一个任务，fast表示此任务很快会结束，如10s
     *
     * @see ThreadPoolTaskScheduler#schedule(Runnable, Date)
     */
    public static ScheduledFuture<?> execute(boolean fast, long delayMs, @NotNull Runnable task) {
        return execute(fast, new Date(ThreadNow.millis() + delayMs), task);
    }

    /**
     * 在指定时间（fastTime构建，系统默认时区），异步执行一个任务，fast表示此任务很快会结束，如10s
     *
     * @see ThreadPoolTaskScheduler#schedule(Runnable, Date)
     */
    public static ScheduledFuture<?> execute(boolean fast, Date start, @NotNull Runnable task) {
        ThreadPoolTaskScheduler scheduler = fast ? Light() : Heavy();
        return scheduler.schedule(task, start);
    }
}
