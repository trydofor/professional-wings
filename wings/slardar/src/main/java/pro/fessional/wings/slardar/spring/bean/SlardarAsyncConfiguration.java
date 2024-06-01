package pro.fessional.wings.slardar.spring.bean;

import com.alibaba.ttl.threadpool.TtlExecutors;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration;
import org.springframework.boot.autoconfigure.task.TaskSchedulingAutoConfiguration;
import org.springframework.boot.autoconfigure.task.TaskSchedulingProperties;
import org.springframework.boot.task.ThreadPoolTaskExecutorBuilder;
import org.springframework.boot.task.ThreadPoolTaskSchedulerBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.TaskDecorator;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ConcurrentTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import pro.fessional.wings.silencer.spring.boot.ConditionalWingsEnabled;
import pro.fessional.wings.slardar.async.AsyncHelper;
import pro.fessional.wings.slardar.async.TaskSchedulerHelper;
import pro.fessional.wings.slardar.async.TtlTaskDecorator;
import pro.fessional.wings.slardar.async.TtlThreadPoolTaskScheduler;
import pro.fessional.wings.slardar.spring.prop.SlardarAsyncProp;

import java.util.List;
import java.util.concurrent.Executor;

import static org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration.APPLICATION_TASK_EXECUTOR_BEAN_NAME;
import static org.springframework.scheduling.annotation.AsyncAnnotationBeanPostProcessor.DEFAULT_TASK_EXECUTOR_BEAN_NAME;
import static org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor.DEFAULT_TASK_SCHEDULER_BEAN_NAME;

/**
 * <pre>
 * <a href="https://docs.spring.io/spring-boot/docs/3.0.3/reference/htmlsingle/#features.task-execution-and-scheduling">Task Execution and Scheduling</a>
 * https://github.com/alibaba/transmittable-thread-local
 * </pre>
 *
 * @author trydofor
 * @see TaskExecutionAutoConfiguration
 * @see TaskSchedulingAutoConfiguration
 * @since 2019-12-03
 */
@EnableAsync
@Configuration(proxyBeanMethods = false)
@ConditionalWingsEnabled
public class SlardarAsyncConfiguration {
    public static final String slardarFastScheduler = "slardarFastScheduler";

    private static final Log log = LogFactory.getLog(SlardarAsyncConfiguration.class);

    private final SlardarAsyncProp asyncProp;
    private final ThreadPoolTaskSchedulerBuilder fastSchedulerBuilder;

    public SlardarAsyncConfiguration(SlardarAsyncProp asyncProp) {
        this.asyncProp = asyncProp;

        ThreadPoolTaskSchedulerBuilder builder = new ThreadPoolTaskSchedulerBuilder();
        TaskSchedulingProperties fast = asyncProp.getFast();
        builder = builder.poolSize(fast.getPool().getSize());
        TaskSchedulingProperties.Shutdown shutdown = fast.getShutdown();
        builder = builder.awaitTermination(shutdown.isAwaitTermination());
        builder = builder.awaitTerminationPeriod(shutdown.getAwaitTerminationPeriod());
        builder = builder.threadNamePrefix(fast.getThreadNamePrefix());
        fastSchedulerBuilder = builder;
    }

    @Bean
    @Primary
    @ConditionalWingsEnabled
    public TaskDecorator ttlTaskDecorator(List<TaskDecorator> others) {
        log.info("Slardar spring-bean ttlTaskDecorator, others count=" + others.size());
        return new TtlTaskDecorator(others);
    }

    /**
     * Executor in the context, regular (@Async) execution (that is @EnableAsync) will use it transparently
     */
    @Bean(name = DEFAULT_TASK_EXECUTOR_BEAN_NAME)
    @ConditionalWingsEnabled
    public Executor taskExecutor(ThreadPoolTaskExecutorBuilder builder) {
        final ThreadPoolTaskExecutor bean = builder.build();
        log.info("Slardar spring-bean taskExecutor of @Async, prefix=" + bean.getThreadNamePrefix());
        return bean;
    }

    /**
     * Spring (Callable) MVC requires an AsyncTaskExecutor implementation (named applicationTaskExecutor)
     */
    @Bean(name = APPLICATION_TASK_EXECUTOR_BEAN_NAME)
    @ConditionalWingsEnabled
    public AsyncTaskExecutor applicationTaskExecutor(ThreadPoolTaskExecutorBuilder builder) {
        final ThreadPoolTaskExecutor bean = builder.build();
        bean.setThreadNamePrefix(asyncProp.getExecPrefix().getApplication());
        log.info("Slardar spring-bean applicationTaskExecutor of Callable MVC, prefix=" + bean.getThreadNamePrefix());
        return bean;
    }

    // Do NOT use @Primary to avoid overwriting the @Async thread pool.
    @Bean(name = DEFAULT_TASK_SCHEDULER_BEAN_NAME)
    @ConditionalWingsEnabled
    public ThreadPoolTaskScheduler taskScheduler(ThreadPoolTaskSchedulerBuilder builder) {
        final TtlThreadPoolTaskScheduler bean = TaskSchedulerHelper.Ttl(builder);
        log.info("Slardar spring-bean taskScheduler of @Scheduled, prefix=" + bean.getThreadNamePrefix());
        return bean;
    }


    @Bean(name = slardarFastScheduler)
    @ConditionalWingsEnabled
    public ThreadPoolTaskScheduler slardarFastScheduler() {
        TtlThreadPoolTaskScheduler bean = TaskSchedulerHelper.Ttl(fastSchedulerBuilder);
        log.info("Slardar spring-bean slardarFastScheduler of fast Scheduled, prefix=" + bean.getThreadNamePrefix());
        return bean;
    }

    @Bean
    @ConditionalWingsEnabled
    public TaskSchedulerHelper taskSchedulerHelper(
        @Qualifier(slardarFastScheduler) ThreadPoolTaskScheduler fast,
        @Qualifier(DEFAULT_TASK_SCHEDULER_BEAN_NAME) ThreadPoolTaskScheduler scheduled,
        ThreadPoolTaskSchedulerBuilder scheduledBuilder) {
        log.info("Slardar spring-bean taskSchedulerHelper");
        return new TaskSchedulerHelper(scheduled, fast) {{
            FastBuilder = fastSchedulerBuilder;
            ScheduledBuilder = scheduledBuilder;
        }};
    }

    @Bean
    @ConditionalWingsEnabled
    public AsyncHelper asyncHelper(
        @Qualifier(DEFAULT_TASK_EXECUTOR_BEAN_NAME) Executor asyncExec,
        @Qualifier(APPLICATION_TASK_EXECUTOR_BEAN_NAME) AsyncTaskExecutor appExec,
        ThreadPoolTaskExecutorBuilder executorBuilder
    ) {
        log.info("Slardar spring-bean asyncHelper");
        final ThreadPoolTaskExecutor executor = executorBuilder.build();
        executor.setThreadNamePrefix(asyncProp.getExecPrefix().getLite());
        executor.initialize();
        final Executor exec = TtlExecutors.getTtlExecutor(executor);
        AsyncTaskExecutor liteExecutor = new ConcurrentTaskExecutor(exec);

        return new AsyncHelper(asyncExec, appExec) {{
            ExecutorBuilder = executorBuilder;
            LiteExecutor = liteExecutor;
        }};
    }
}
