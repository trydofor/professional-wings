package pro.fessional.wings.slardar.spring.bean;

import com.hazelcast.core.HazelcastInstance;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.Session;
import org.springframework.session.security.SpringSessionBackedSessionRegistry;
import pro.fessional.wings.silencer.spring.boot.ConditionalWingsEnabled;
import pro.fessional.wings.slardar.session.HazelcastSessionHelper;
import pro.fessional.wings.slardar.session.WingsSessionHelper;

/**
 * @author trydofor
 * @since 2019-12-03
 */
@Configuration(proxyBeanMethods = false)
@ConditionalWingsEnabled
public class SlardarHazelSessionConfiguration {

    private static final Log log = LogFactory.getLog(SlardarHazelSessionConfiguration.class);

    @Bean
    @ConditionalWingsEnabled
    public SessionRegistry sessionRegistry(FindByIndexNameSessionRepository<?> repository) {
        log.info("SlardarHazelSession spring-bean sessionRegistry");
        return new SpringSessionBackedSessionRegistry<>(repository);
    }

    @Bean
    @ConditionalWingsEnabled
    public WingsSessionHelper wingsSessionHelper(
            FindByIndexNameSessionRepository<Session> sessionRepository,
            HazelcastInstance hazelcastInstance,
            @Value("${spring.session.hazelcast.map-name:spring:session:sessions}") String mapName) {

        log.info("SlardarHazelSession spring-bean wingsSessionHelper");
        return new HazelcastSessionHelper(sessionRepository, hazelcastInstance, mapName);
    }
}
