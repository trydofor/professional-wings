package pro.fessional.wings.slardar.spring.bean;

import com.alibaba.ttl.threadpool.TtlExecutors;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration;
import org.springframework.boot.autoconfigure.task.TaskSchedulingAutoConfiguration;
import org.springframework.boot.task.TaskExecutorBuilder;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ConcurrentTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import pro.fessional.wings.slardar.event.EventPublishHelper;
import pro.fessional.wings.slardar.spring.prop.SlardarEnabledProp;

import java.lang.reflect.Method;
import java.util.concurrent.Executor;

import static org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration.APPLICATION_TASK_EXECUTOR_BEAN_NAME;
import static org.springframework.scheduling.annotation.AsyncAnnotationBeanPostProcessor.DEFAULT_TASK_EXECUTOR_BEAN_NAME;

/**
 * https://docs.spring.io/spring-boot/docs/2.6.6/reference/htmlsingle/#features.task-execution-and-scheduling
 * https://github.com/alibaba/transmittable-thread-local
 *
 * @author trydofor
 * @see TaskExecutionAutoConfiguration
 * @see TaskSchedulingAutoConfiguration
 * @since 2019-12-03
 */
@EnableAsync
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(name = SlardarEnabledProp.Key$async, havingValue = "true")
public class SlardarAsyncConfiguration {

    public static final String SLARDAR_EVENT_EXECUTOR_BEAN_NAME = "slardarEventExecutor";
    private static final Log log = LogFactory.getLog(SlardarAsyncConfiguration.class);

    @Bean(name = DEFAULT_TASK_EXECUTOR_BEAN_NAME)
    public Executor taskExecutor(TaskExecutorBuilder builder) {
        log.info("Slardar spring-bean taskExecutor via ttlExecutor");
        return ttlExecutor(builder);
    }

    @Bean(name = APPLICATION_TASK_EXECUTOR_BEAN_NAME)
    public AsyncTaskExecutor applicationTaskExecutor(TaskExecutorBuilder builder) {
        log.info("Slardar spring-bean applicationTaskExecutor via ttlExecutor");
        final Executor executor = ttlExecutor(builder);
        return new ConcurrentTaskExecutor(executor);
    }

    private Executor ttlExecutor(TaskExecutorBuilder builder) {
        final ThreadPoolTaskExecutor executor = builder.build();
        executor.initialize();
        return TtlExecutors.getTtlExecutor(executor);
    }

    @Bean(name = SLARDAR_EVENT_EXECUTOR_BEAN_NAME)
    public Executor slardarEventExecutor() {
        log.info("Slardar spring-bean slardarEventExecutor");
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setThreadNamePrefix("slardar-event-");
        executor.setKeepAliveSeconds(30);
        executor.setMaxPoolSize(8);
        executor.setQueueCapacity(512);
        return executor;
    }

    @Bean
    public CommandLineRunner eventPublishHelperRunner(ApplicationEventPublisher publisher,
                                                      ApplicationEventMulticaster multicaster,
                                                      @Qualifier(SLARDAR_EVENT_EXECUTOR_BEAN_NAME) Executor executor) {
        log.info("Slardar spring-runs eventPublishHelperRunner");
        return (arg) -> {
            EventPublishHelper.setExecutor(executor);
            log.info("Slardar conf eventPublishHelper ApplicationEventPublisher=" + publisher.getClass());
            EventPublishHelper.setSpringPublisher(publisher);
            log.info("Slardar conf eventPublishHelper ApplicationEventMulticaster=" + multicaster.getClass());
            if (multicaster instanceof SimpleApplicationEventMulticaster) {
                SimpleApplicationEventMulticaster mc = (SimpleApplicationEventMulticaster) multicaster;
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
        };
    }
}
