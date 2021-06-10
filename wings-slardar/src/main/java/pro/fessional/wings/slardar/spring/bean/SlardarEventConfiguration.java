package pro.fessional.wings.slardar.spring.bean;

import com.hazelcast.core.HazelcastInstance;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import pro.fessional.wings.slardar.event.EventPublishHelper;

import java.util.concurrent.Executor;

/**
 * @author trydofor
 * @since 2019-12-03
 */
@Configuration
public class SlardarEventConfiguration {

    /**
     * 轻量任务执行器
     */
    public static final String SLARDAR_EVENT_EXECUTOR = "slardarEventExecutor";

    private static final Log logger = LogFactory.getLog(SlardarEventConfiguration.class);

    @Bean(name = SLARDAR_EVENT_EXECUTOR)
    public Executor slardarEventExecutor() {
        logger.info("Wings conf slardarEventExecutor");
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setThreadNamePrefix("SlardarEvent");
        executor.setKeepAliveSeconds(30);
        executor.setMaxPoolSize(8);
        executor.setQueueCapacity(512);
        return executor;
    }

    @Autowired
    public void autowiredEventPublishHelper(ApplicationEventPublisher publisher,
                                            @Qualifier(SLARDAR_EVENT_EXECUTOR) Executor executor,
                                            ObjectProvider<HazelcastInstance> hazelcast) {
        new EventPublishHelper(publisher, executor, hazelcast.getIfAvailable()) {};
        logger.info("Wings conf autowired EventPublishHelper with " + SLARDAR_EVENT_EXECUTOR);
    }
}
