package pro.fessional.wings.slardar.spring.bean;

import com.hazelcast.core.HazelcastInstance;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.session.SessionAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.session.FindByIndexNameSessionRepository;
import pro.fessional.wings.slardar.session.HazelcastSessionHelper;
import pro.fessional.wings.slardar.session.WingsSessionHelper;
import pro.fessional.wings.slardar.session.WingsSessionRegistry;

/**
 * @author trydofor
 * @since 2019-12-03
 */
@Configuration(proxyBeanMethods = false)
@AutoConfigureAfter(SessionAutoConfiguration.class)
public class HazelcastSessionConfiguration {

    private static final Log log = LogFactory.getLog(HazelcastSessionConfiguration.class);

    @Bean
    @ConditionalOnMissingBean(SessionRegistry.class)
    public SessionRegistry sessionRegistry(FindByIndexNameSessionRepository<?> repository, WingsSessionHelper helper) {
        log.info("Wings conf sessionRegistry");
        return new WingsSessionRegistry<>(repository, helper);
    }

    @Bean
    public WingsSessionHelper wingsSessionHelper(
            FindByIndexNameSessionRepository<?> sessionRepository,
            HazelcastInstance hzInstance,
            @Value("${spring.session.hazelcast.map-name:spring:session:sessions}") String mapName) {

        log.info("Wings conf wingsSessionHelper");
        return new HazelcastSessionHelper(sessionRepository, hzInstance, mapName);
    }
}
