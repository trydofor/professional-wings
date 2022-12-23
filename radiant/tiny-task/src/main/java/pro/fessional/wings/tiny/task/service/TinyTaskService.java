package pro.fessional.wings.tiny.task.service;

import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import pro.fessional.mirana.func.Lam;
import pro.fessional.mirana.time.ThreadNow;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

/**
 * <pre>
 * 基于 ThreadPoolTaskScheduler 和 Database 的任务。
 * execute方法仅为执行，不做通知及database
 * schedule方法会自动通知和database，通过id管理任务
 * </pre>
 *
 * @author trydofor
 * @since 2022-11-29
 */
public interface TinyTaskService {

    /**
     * 获取内部的ThreadPoolTaskScheduler
     */
    @NotNull
    ThreadPoolTaskScheduler referScheduler(boolean fast);

    /**
     * 获取内部的ScheduledExecutorService
     */
    @NotNull
    default ScheduledExecutorService referExecutor(boolean fast) {
        return referScheduler(fast).getScheduledExecutor();
    }

    /**
     * 异步立即执行一个任务，fast为轻任务，执行快，秒级完成
     *
     * @see ThreadPoolTaskScheduler#execute(Runnable)
     */
    default void execute(boolean fast, @NotNull Runnable task) {
        referScheduler(fast).execute(task);
    }

    /**
     * 在delayMs毫秒（ThreadNow）后，异步执行一个任务，fast为轻任务，执行快，秒级完成
     *
     * @see ThreadPoolTaskScheduler#schedule(Runnable, Date)
     */
    default ScheduledFuture<?> execute(boolean fast, long delayMs, @NotNull Runnable task) {
        return execute(fast, new Date(ThreadNow.millis() + delayMs), task);
    }

    /**
     * 在指定时间（fastTime构建，不考虑时区），异步执行一个任务，fast为轻任务，执行快，秒级完成
     *
     * @see ThreadPoolTaskScheduler#schedule(Runnable, Date)
     */
    default ScheduledFuture<?> execute(boolean fast, Date startTime, @NotNull Runnable task) {
        return referScheduler(fast).schedule(task, startTime);
    }

    /**
     * 指定trigger，异步执行一个任务，fast为轻任务，执行快，秒级完成
     * errorHandler不同于其他方法，不识别DelegatingErrorHandlingRunnable
     *
     * @see ThreadPoolTaskScheduler#schedule(Runnable, Trigger)
     */
    default ScheduledFuture<?> execute(boolean fast, Trigger trigger, @NotNull Runnable task) {
        return referScheduler(fast).schedule(task, trigger);
    }

    /**
     * 把taskerBean内所有TinyTask标注的方法，作为任务初始并执行。
     *
     * @see ThreadPoolTaskScheduler#schedule(Runnable, Trigger)
     */
    @NotNull
    Set<Task> schedule(@NotNull Object taskerBean);

    /**
     * 把TaskerBean中TinyTask标注的方法，作为任务初始并执行，返回TaskId，-1为未启动
     *
     * @see ThreadPoolTaskScheduler#schedule(Runnable, Trigger)
     */
    Task schedule(@NotNull Object taskerBean, @NotNull Method taskerCall, @Nullable Object taskerPara);

    /**
     * 把TaskerBean中TinyTask标注的方法，作为任务初始并执行，返回TaskId，-1为未启动
     * 通过对象引用的lambda方式获取，格式如 Lam.ref(taskerBean::method)
     *
     * @see ThreadPoolTaskScheduler#schedule(Runnable, Trigger)
     */
    default Task schedule(@NotNull Lam.Ref lambdaRefer, @Nullable Object taskerPara) {
        return schedule(lambdaRefer.object, lambdaRefer.method, taskerPara);
    }

    @Data
    class Task {
        private final long id;
        private final boolean scheduled;
    }
}
