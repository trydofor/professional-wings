package pro.fessional.wings.slardar.async;

import com.alibaba.ttl.threadpool.TtlExecutors;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.task.ThreadPoolTaskExecutorBuilder;
import org.springframework.core.task.AsyncTaskExecutor;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

/**
 * <a href="https://github.com/alibaba/transmittable-thread-local">...</a>
 *
 * @author trydofor
 * @see TtlExecutors
 * @since 2024-05-13
 */
public class AsyncHelper implements DisposableBean {

    private static Executor AsyncExecutor = null;
    private static AsyncTaskExecutor AppTaskExecutor = null;
    private static ThreadPoolTaskExecutorBuilder ExecutorBuilder;
    private static AsyncTaskExecutor LiteExecutor;
    private static boolean helperPrepared = false;

    protected AsyncHelper(@NotNull Executor async, @NotNull AsyncTaskExecutor appTask,
                          @NotNull ThreadPoolTaskExecutorBuilder builder, @NotNull AsyncTaskExecutor lite) {
        AsyncExecutor = Objects.requireNonNull(async);
        AppTaskExecutor = Objects.requireNonNull(appTask);
        ExecutorBuilder = Objects.requireNonNull(builder);
        LiteExecutor = Objects.requireNonNull(lite);
        helperPrepared = true;
    }

    @Override
    public void destroy() {
        helperPrepared = false;
    }

    /**
     * whether this helper is prepared
     */
    public static boolean isPrepared() {
        return helperPrepared;
    }

    /**
     * @see org.springframework.scheduling.annotation.AsyncAnnotationBeanPostProcessor#DEFAULT_TASK_EXECUTOR_BEAN_NAME
     */
    @NotNull
    public static Executor Async() {
        if (AsyncExecutor == null) {
            throw new IllegalStateException("AsyncExecutor must init before using");
        }
        return AsyncExecutor;
    }

    /**
     * just like default @Async, but not AsyncUncaughtExceptionHandler
     */
    public static CompletableFuture<Void> Async(@NotNull Runnable task) {
        return CompletableFuture.runAsync(task, AsyncExecutor);
    }

    /**
     * just like default @Async, but not AsyncUncaughtExceptionHandler
     */
    public static <T> CompletableFuture<T> Async(@NotNull Supplier<T> supplier) {
        return CompletableFuture.supplyAsync(supplier, AsyncExecutor);
    }

    /**
     * @see org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration#APPLICATION_TASK_EXECUTOR_BEAN_NAME
     */
    @NotNull
    public static AsyncTaskExecutor AppTask() {
        if (AppTaskExecutor == null) {
            throw new IllegalStateException("AppTaskExecutor must init before using");
        }

        return AppTaskExecutor;
    }

    /**
     * @see org.springframework.scheduling.annotation.AsyncAnnotationBeanPostProcessor#DEFAULT_TASK_EXECUTOR_BEAN_NAME
     */
    @NotNull
    public static AsyncTaskExecutor Lite() {
        if (LiteExecutor == null) {
            throw new IllegalStateException("LiteExecutor must init before using");
        }
        return LiteExecutor;
    }

    /**
     * Get ThreadPoolTaskExecutorBuilder, IllegalStateException if nonull but null.
     */
    @NotNull
    public static ThreadPoolTaskExecutorBuilder ExecutorBuilder() {
        if (ExecutorBuilder == null) {
            throw new IllegalStateException("LightBuilder must init before using");
        }
        return ExecutorBuilder;
    }
}
