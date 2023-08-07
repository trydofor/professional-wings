package pro.fessional.wings.slardar.spring.bean;

import com.alibaba.ttl.threadpool.TtlExecutors;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration;
import org.springframework.boot.autoconfigure.task.TaskExecutionProperties;
import org.springframework.boot.autoconfigure.task.TaskSchedulingAutoConfiguration;
import org.springframework.boot.autoconfigure.task.TaskSchedulingProperties;
import org.springframework.boot.task.TaskExecutorBuilder;
import org.springframework.boot.task.TaskSchedulerBuilder;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ConcurrentTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import pro.fessional.wings.silencer.runner.ApplicationStartedEventRunner;
import pro.fessional.wings.slardar.async.TaskSchedulerHelper;
import pro.fessional.wings.slardar.async.TtlThreadPoolTaskScheduler;
import pro.fessional.wings.slardar.event.EventPublishHelper;
import pro.fessional.wings.slardar.spring.prop.SlardarAsyncProp;
import pro.fessional.wings.slardar.spring.prop.SlardarEnabledProp;
import pro.fessional.wings.spring.consts.OrderedSlardarConst;

import java.lang.reflect.Method;
import java.util.concurrent.Executor;

import static org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration.APPLICATION_TASK_EXECUTOR_BEAN_NAME;
import static org.springframework.scheduling.annotation.AsyncAnnotationBeanPostProcessor.DEFAULT_TASK_EXECUTOR_BEAN_NAME;
import static org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor.DEFAULT_TASK_SCHEDULER_BEAN_NAME;
import static pro.fessional.wings.spring.consts.NamingSlardarConst.slardarEventExecutor;
import static pro.fessional.wings.spring.consts.NamingSlardarConst.slardarHeavyScheduler;

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
@ConditionalOnProperty(name = SlardarEnabledProp.Key$async, havingValue = "true")
@AutoConfigureOrder(OrderedSlardarConst.AsyncConfiguration)
public class SlardarAsyncConfiguration {

    private static final Log log = LogFactory.getLog(SlardarAsyncConfiguration.class);

    @Bean(name = DEFAULT_TASK_EXECUTOR_BEAN_NAME)
    public Executor taskExecutor(TaskExecutorBuilder builder) {
        final ThreadPoolTaskExecutor executor = builder.build();
        executor.initialize();
        log.info("Slardar spring-bean taskExecutor via ttlExecutor, prefix=" + executor.getThreadNamePrefix());
        return TtlExecutors.getTtlExecutor(executor);
    }

    @Bean(name = APPLICATION_TASK_EXECUTOR_BEAN_NAME)
    public AsyncTaskExecutor applicationTaskExecutor(TaskExecutorBuilder builder) {
        final ThreadPoolTaskExecutor executor = builder.build();
        executor.initialize();
        final Executor ttlExecutor = TtlExecutors.getTtlExecutor(executor);
        log.info("Slardar spring-bean applicationTaskExecutor via ttlExecutor, prefix=" + executor.getThreadNamePrefix());
        return new ConcurrentTaskExecutor(ttlExecutor);
    }

    // Do NOT use @Primary to avoid overwriting the @Async thread pool.
    @Bean(name = DEFAULT_TASK_SCHEDULER_BEAN_NAME)
    public ThreadPoolTaskScheduler taskScheduler(TaskSchedulerBuilder builder) {
        final TtlThreadPoolTaskScheduler scheduler = new TtlThreadPoolTaskScheduler();
        final TtlThreadPoolTaskScheduler bean = builder.configure(scheduler);
        log.info("Slardar spring-bean taskScheduler via TtlThreadPoolTaskScheduler, prefix=" + bean.getThreadNamePrefix());
        return bean;
    }

    @Bean(name = slardarEventExecutor)
    public Executor slardarEventExecutor(SlardarAsyncProp prop) {
        TaskExecutorBuilder builder = new TaskExecutorBuilder();
        final TaskExecutionProperties event = prop.getEvent();
        final TaskExecutionProperties.Pool pool = event.getPool();
        builder = builder.queueCapacity(pool.getQueueCapacity());
        builder = builder.corePoolSize(pool.getCoreSize());
        builder = builder.maxPoolSize(pool.getMaxSize());
        builder = builder.allowCoreThreadTimeOut(pool.isAllowCoreThreadTimeout());
        builder = builder.keepAlive(pool.getKeepAlive());
        TaskExecutionProperties.Shutdown shutdown = event.getShutdown();
        builder = builder.awaitTermination(shutdown.isAwaitTermination());
        builder = builder.awaitTerminationPeriod(shutdown.getAwaitTerminationPeriod());
        builder = builder.threadNamePrefix(event.getThreadNamePrefix());
        log.info("Slardar spring-bean slardarEventExecutor via TtlThreadPoolTaskExecutor, prefix=" + event.getThreadNamePrefix());
        final ThreadPoolTaskExecutor executor = builder.build();
        executor.initialize();
        return TtlExecutors.getTtlExecutor(executor);
    }

    @Bean(name = slardarHeavyScheduler)
    public ThreadPoolTaskScheduler slardarHeavyScheduler(SlardarAsyncProp prop) {
        final TtlThreadPoolTaskScheduler scheduler = new TtlThreadPoolTaskScheduler();
        TaskSchedulerBuilder builder = new TaskSchedulerBuilder();
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
    public ApplicationStartedEventRunner runnerEventPublishHelper(
            ApplicationEventPublisher publisher,
            ApplicationEventMulticaster multicaster,
            @Qualifier(slardarEventExecutor) Executor executor) {
        log.info("Slardar spring-runs runnerEventPublishHelper");
        return new ApplicationStartedEventRunner(OrderedSlardarConst.RunnerEventPublishHelper, ignored -> {
            EventPublishHelper.setExecutor(executor);
            log.info("Slardar conf eventPublishHelper ApplicationEventPublisher=" + publisher.getClass());
            EventPublishHelper.setSpringPublisher(publisher);
            log.info("Slardar conf eventPublishHelper ApplicationEventMulticaster=" + multicaster.getClass());
            if (multicaster instanceof SimpleApplicationEventMulticaster mc) {
                try {
                    final Method getTaskExecutor = BeanUtils.findMethod(SimpleApplicationEventMulticaster.class, "getTaskExecutor");
                    if (getTaskExecutor != null) {
                        getTaskExecutor.setAccessible(true);
                        final Object te = getTaskExecutor.invoke(mc);
                        if (te != null) {
                            log.warn("Slardar conf eventPublishHelper SimpleApplicationEventMulticaster should without TaskExecutor");
                        }
                    }

                    final Method getErrorHandler = BeanUtils.findMethod(SimpleApplicationEventMulticaster.class, "getErrorHandler");
                    if (getErrorHandler != null) {
                        getErrorHandler.setAccessible(true);
                        final Object eh = getErrorHandler.invoke(mc);
                        if (eh != null) {
                            log.warn("Slardar conf eventPublishHelper SimpleApplicationEventMulticaster should without ErrorHandler");
                        }
                    }
                }
                catch (Exception e) {
                    log.info("failed to check SimpleApplicationEventMulticaster", e);
                }
            }
        });
    }

    @Bean
    public TaskSchedulerHelper taskSchedulerHelper(
            @Qualifier(DEFAULT_TASK_SCHEDULER_BEAN_NAME) ThreadPoolTaskScheduler light,
            @Qualifier(slardarHeavyScheduler) ThreadPoolTaskScheduler heavy) {
        log.info("Slardar spring-bean taskSchedulerHelper");
        return new TaskSchedulerHelper() {{
            log.info("Slardar conf TaskSchedulerHelper");
            LightTasker = light;
            HeavyTasker = heavy;
        }};
    }
}
