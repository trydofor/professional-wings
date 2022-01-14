package pro.fessional.wings.slardar.spring.bean;

import com.alibaba.ttl.threadpool.TtlExecutors;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration;
import org.springframework.boot.task.TaskExecutorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncAnnotationBeanPostProcessor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import pro.fessional.wings.slardar.spring.prop.SlardarEnabledProp;

import java.util.concurrent.Executor;

/**
 * https://docs.spring.io/spring-boot/docs/2.4.2/reference/html/spring-boot-features.html#boot-features-task-execution-scheduling
 *
 * @author trydofor
 * @see TaskExecutionAutoConfiguration
 * @since 2019-12-03
 */
@EnableAsync
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(name = SlardarEnabledProp.Key$async, havingValue = "true")
public class SlardarAsyncConfiguration {

    private static final Log logger = LogFactory.getLog(SlardarAsyncConfiguration.class);


    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Bean(name = {TaskExecutionAutoConfiguration.APPLICATION_TASK_EXECUTOR_BEAN_NAME,
                  AsyncAnnotationBeanPostProcessor.DEFAULT_TASK_EXECUTOR_BEAN_NAME})
    public Executor applicationTaskExecutor(TaskExecutorBuilder builder) {
        logger.info("Wings conf TtlExecutor");
        final ThreadPoolTaskExecutor executor = builder.build();
        executor.initialize();
        return TtlExecutors.getTtlExecutor(executor);
    }
}
