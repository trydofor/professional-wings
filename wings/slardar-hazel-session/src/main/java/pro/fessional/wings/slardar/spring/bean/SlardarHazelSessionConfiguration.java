package pro.fessional.wings.slardar.spring.bean;

import com.hazelcast.config.SerializationConfig;
import com.hazelcast.config.SerializerConfig;
import com.hazelcast.core.HazelcastInstance;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.hazelcast.HazelcastConfigCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.MapSession;
import org.springframework.session.Session;
import org.springframework.session.hazelcast.HazelcastSessionSerializer;
import org.springframework.session.security.SpringSessionBackedSessionRegistry;
import pro.fessional.wings.silencer.spring.boot.ConditionalWingsEnabled;
import pro.fessional.wings.slardar.session.HazelcastSessionHelper;

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
    public SpringSessionBackedSessionRegistry<?> sessionRegistry(FindByIndexNameSessionRepository<?> repository) {
        log.info("SlardarHazelSession spring-bean sessionRegistry");
        return new SpringSessionBackedSessionRegistry<>(repository);
    }

    @Bean
    @ConditionalWingsEnabled
    public HazelcastSessionHelper wingsSessionHelper(
            FindByIndexNameSessionRepository<Session> sessionRepository,
            HazelcastInstance hazelcastInstance,
            @Value("${spring.session.hazelcast.map-name:spring:session:sessions}") String mapName) {

        log.info("SlardarHazelSession spring-bean wingsSessionHelper");
        return new HazelcastSessionHelper(sessionRepository, hazelcastInstance, mapName);
    }

    /**
     * spring FindByIndexNameSessionRepository
     */
    @Bean
    @ConditionalWingsEnabled
    public HazelcastConfigCustomizer wingsHazelcastSessionSerializer() {
        log.info("SlardarHazelSession spring-bean wingsHazelcastSessionSerializer");
        return config -> {
            String msc = MapSession.class.getName();
            SerializationConfig serialization = config.getSerializationConfig();
            for (SerializerConfig ss : serialization.getSerializerConfigs()) {
                String tcn = ss.getTypeClassName();
                if (tcn == null) {
                    Class<?> tc = ss.getTypeClass();
                    if (tc != null) {
                        tcn = tc.getName();
                    }
                }

                if (msc.equals(tcn)) {
                    log.warn("Wings hazelcast addSerializerConfig skipped, current=" + ss);
                    return;
                }
            }

            log.info("Wings hazelcast addSerializerConfig type=MapSession");
            SerializerConfig sessionSerializer = new SerializerConfig();
            sessionSerializer.setClass(HazelcastSessionSerializer.class);
            sessionSerializer.setTypeClass(MapSession.class);
            serialization.addSerializerConfig(sessionSerializer);
        };
    }
}
