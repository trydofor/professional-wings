package pro.fessional.wings.warlock.app;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import pro.fessional.wings.silencer.spring.boot.ConditionalWingsEnabled;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author trydofor
 * @since 2024-07-02
 */
@Configuration
@EnableAsync
@ConditionalWingsEnabled(abs = "test.plain-async", value = false)
public class TestPlainAsyncConfigurer implements AsyncConfigurer {

    @Bean("plainPoolTaskExecutor")
    public ThreadPoolTaskExecutor plainPoolTaskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(5);
        taskExecutor.setMaxPoolSize(5);
        taskExecutor.setQueueCapacity(10);
        taskExecutor.setKeepAliveSeconds(60);
        taskExecutor.setThreadNamePrefix("raw-");
        taskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        taskExecutor.setWaitForTasksToCompleteOnShutdown(true);
        taskExecutor.setAwaitTerminationSeconds(60);
        taskExecutor.initialize();
        return taskExecutor;
    }
}
