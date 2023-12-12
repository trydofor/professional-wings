package pro.fessional.wings.slardar.spring.bean;

import com.alibaba.ttl.threadpool.TtlExecutors;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.task.TaskExecutionProperties;
import org.springframework.boot.task.TaskExecutorBuilder;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import pro.fessional.wings.silencer.runner.ApplicationStartedEventRunner;
import pro.fessional.wings.silencer.spring.WingsOrdered;
import pro.fessional.wings.silencer.spring.boot.ConditionalWingsEnabled;
import pro.fessional.wings.slardar.event.EventPublishHelper;
import pro.fessional.wings.slardar.event.attr.AttributeEventListener;
import pro.fessional.wings.slardar.spring.prop.SlardarAsyncProp;

import java.lang.reflect.Method;
import java.util.concurrent.Executor;

/**
 * <pre>
 * @author trydofor
 * @since 2023-10-31
 */
@Configuration(proxyBeanMethods = false)
@ConditionalWingsEnabled
public class SlardarEventConfiguration {
    private static final Log log = LogFactory.getLog(SlardarEventConfiguration.class);

    public static final String slardarEventExecutor = "slardarEventExecutor";

    @Bean(name = slardarEventExecutor)
    @ConditionalWingsEnabled
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

    @Bean
    @ConditionalWingsEnabled
    public ApplicationStartedEventRunner eventPublishHelperRunner(
            ApplicationEventPublisher publisher,
            ApplicationEventMulticaster multicaster,
            @Qualifier(slardarEventExecutor) Executor executor) {
        log.info("Slardar spring-runs eventPublishHelperRunner");
        return new ApplicationStartedEventRunner(WingsOrdered.Lv4Application, ignored -> {
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
    @ConditionalWingsEnabled
    public AttributeEventListener attributeEventListener() {
        log.info("Slardar spring-bean AttributeEventListener");
        return new AttributeEventListener();
    }
}
