package pro.fessional.wings.tiny.task.service;

import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import pro.fessional.mirana.func.Lam;
import pro.fessional.mirana.time.ThreadNow;

import java.lang.reflect.Method;
import java.time.Instant;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

/**
 * <pre>
 * Task based on ThreadPoolTaskScheduler and Database.
 * `execute` is only for execution, not involve notice or databases
 * `schedule` auto trigger notice and database, task is managed by id
 * </pre>
 *
 * @author trydofor
 * @since 2022-11-29
 */
public interface TinyTaskService {

    /**
     * Get the internal ThreadPoolTaskScheduler
     */
    @NotNull
    ThreadPoolTaskScheduler referScheduler(boolean fast);

    /**
     * Get the internal ScheduledExecutorService
     */
    @NotNull
    default ScheduledExecutorService referExecutor(boolean fast) {
        return referScheduler(fast).getScheduledExecutor();
    }

    /**
     * Async execute a task immediately.
     * `fast` means a lightweight that executes quickly and completes within seconds.
     *
     * @see ThreadPoolTaskScheduler#execute(Runnable)
     */
    default void execute(boolean fast, @NotNull Runnable task) {
        referScheduler(fast).execute(task);
    }

    /**
     * Async execute a task after `delayMs` millis (ThreadNow).
     * `fast` means a lightweight that executes quickly and completes within seconds.
     *
     * @see ThreadPoolTaskScheduler#schedule(Runnable, Instant)
     */
    default ScheduledFuture<?> execute(boolean fast, long delayMs, @NotNull Runnable task) {
        return execute(fast, Instant.ofEpochMilli(ThreadNow.millis() + delayMs), task);
    }

    /**
     * Async execute a task at specified time (`fastTime` without timezone),
     * `fast` means a lightweight that executes quickly and completes within seconds.
     *
     * @see ThreadPoolTaskScheduler#schedule(Runnable, Instant)
     */
    default ScheduledFuture<?> execute(boolean fast, Instant startTime, @NotNull Runnable task) {
        return referScheduler(fast).schedule(task, startTime);
    }

    /**
     * Async execute a task by specified trigger,
     * `fast` means a lightweight that executes quickly and completes within seconds.
     * The `errorHandler` is different from other methods and does not recognize `DelegatingErrorHandlingRunnable`.
     *
     * @see ThreadPoolTaskScheduler#schedule(Runnable, Trigger)
     */
    default ScheduledFuture<?> execute(boolean fast, Trigger trigger, @NotNull Runnable task) {
        return referScheduler(fast).schedule(task, trigger);
    }

    /**
     * Take all the methods annotated with TinyTask inside the taskerBean,
     * initialize them as tasks, and execute them.
     *
     * @see ThreadPoolTaskScheduler#schedule(Runnable, Trigger)
     */
    @NotNull
    Set<Task> schedule(@NotNull Object taskerBean);

    /**
     * Take all the methods annotated with TinyTask inside the taskerBean,
     * initialize them as tasks, and execute them. taskId == `-1` means not start
     *
     * @see ThreadPoolTaskScheduler#schedule(Runnable, Trigger)
     */
    Task schedule(@NotNull Object taskerBean, @NotNull Method taskerCall, @Nullable Object taskerPara);

    /**
     * Take all the methods annotated with TinyTask inside the taskerBean,
     * initialize them as tasks, and execute them. taskId == `-1` means not start,
     * `lambdaRefer` is lambda ref, get by `Lam.ref(taskerBean::method)`
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
