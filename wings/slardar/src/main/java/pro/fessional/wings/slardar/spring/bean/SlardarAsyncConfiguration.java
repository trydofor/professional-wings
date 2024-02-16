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
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ConcurrentTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import pro.fessional.wings.silencer.spring.boot.ConditionalWingsEnabled;
import pro.fessional.wings.slardar.async.TaskSchedulerHelper;
import pro.fessional.wings.slardar.async.TtlThreadPoolTaskScheduler;
import pro.fessional.wings.slardar.spring.prop.SlardarAsyncProp;

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
    public static final String slardarHeavyScheduler = "slardarHeavyScheduler";

    private static final Log log = LogFactory.getLog(SlardarAsyncConfiguration.class);

    @Bean(name = DEFAULT_TASK_EXECUTOR_BEAN_NAME)
    @ConditionalWingsEnabled
    public Executor taskExecutor(ThreadPoolTaskExecutorBuilder builder) {
        final ThreadPoolTaskExecutor executor = builder.build();
        executor.initialize();
        log.info("Slardar spring-bean taskExecutor via ttlExecutor, prefix=" + executor.getThreadNamePrefix());
        return TtlExecutors.getTtlExecutor(executor);
    }

    @Bean(name = APPLICATION_TASK_EXECUTOR_BEAN_NAME)
    @ConditionalWingsEnabled
    public AsyncTaskExecutor applicationTaskExecutor(ThreadPoolTaskExecutorBuilder builder) {
        final ThreadPoolTaskExecutor executor = builder.build();
        executor.initialize();
        final Executor ttlExecutor = TtlExecutors.getTtlExecutor(executor);
        log.info("Slardar spring-bean applicationTaskExecutor via ttlExecutor, prefix=" + executor.getThreadNamePrefix());
        return new ConcurrentTaskExecutor(ttlExecutor);
    }

    // Do NOT use @Primary to avoid overwriting the @Async thread pool.
    @Bean(name = DEFAULT_TASK_SCHEDULER_BEAN_NAME)
    @ConditionalWingsEnabled
    public ThreadPoolTaskScheduler taskScheduler(ThreadPoolTaskSchedulerBuilder builder) {
        final TtlThreadPoolTaskScheduler scheduler = new TtlThreadPoolTaskScheduler();
        final TtlThreadPoolTaskScheduler bean = builder.configure(scheduler);
        log.info("Slardar spring-bean taskScheduler via TtlThreadPoolTaskScheduler, prefix=" + bean.getThreadNamePrefix());
        return bean;
    }


    @Bean(name = slardarHeavyScheduler)
    @ConditionalWingsEnabled
    public ThreadPoolTaskScheduler slardarHeavyScheduler(SlardarAsyncProp prop) {
        final TtlThreadPoolTaskScheduler scheduler = new TtlThreadPoolTaskScheduler();
        ThreadPoolTaskSchedulerBuilder builder = new ThreadPoolTaskSchedulerBuilder();
        final TaskSchedulingProperties heavy = prop.getHeavy();
        builder = builder.poolSize(heavy.getPool().getSize());
        TaskSchedulingProperties.Shutdown shutdown = heavy.getShutdown();
        builder = builder.awaitTermination(shutdown.isAwaitTermination());
        builder = builder.awaitTerminationPeriod(shutdown.getAwaitTerminationPeriod());
        builder = builder.threadNamePrefix(heavy.getThreadNamePrefix());
        log.info("Slardar spring-bean slardarHeavyScheduler via TtlThreadPoolTaskExecutor, prefix=" + heavy.getThreadNamePrefix());
        return builder.configure(scheduler);
    }

    @Bean
    @ConditionalWingsEnabled
    public TaskSchedulerHelper taskSchedulerHelper(
            @Qualifier(DEFAULT_TASK_SCHEDULER_BEAN_NAME) ThreadPoolTaskScheduler light,
            @Qualifier(slardarHeavyScheduler) ThreadPoolTaskScheduler heavy) {
        log.info("Slardar spring-bean taskSchedulerHelper");
        return new TaskSchedulerHelper(light, heavy) {};
    }
}
