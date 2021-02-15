package pro.fessional.wings.slardar.spring.bean;

import com.hazelcast.core.HazelcastInstance;
import lombok.Setter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.Session;
import org.springframework.session.hazelcast.config.annotation.web.http.HazelcastHttpSessionConfiguration;
import org.springframework.session.security.SpringSessionBackedSessionRegistry;
import pro.fessional.wings.slardar.spring.prop.SlardarEnabledProp;

/**
 * 通过 session-hazelcast.xml 配置好 spring session用的map，主要是index和serial
 * https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#boot-features-session
 * https://docs.spring.io/spring-session/docs/2.4.2/reference/html5/#spring-security
 * https://guides.hazelcast.org/spring-session-hazelcast/
 *
 * @author trydofor
 * @since 2019-06-26
 */
@Configuration
@ConditionalOnClass(HazelcastInstance.class)
@ConditionalOnProperty(name = SlardarEnabledProp.Key$sessionHazelcast, havingValue = "true")
public class SlardarHazelcastConfiguration extends HazelcastHttpSessionConfiguration {

    private static final Log logger = LogFactory.getLog(SlardarHazelcastConfiguration.class);

    @Setter(onMethod = @__({@Autowired}))
    private FindByIndexNameSessionRepository<? extends Session> sessionRepository;

    @Bean
    @ConditionalOnMissingBean
    @Override
    public FindByIndexNameSessionRepository<?> sessionRepository() {
        logger.info("Wings conf sessionRepository : FindByIndexNameSessionRepository");
        return (FindByIndexNameSessionRepository<? extends Session>) super.sessionRepository();
    }

    // concurrent session
    @Bean
    @ConditionalOnMissingBean
    public SessionRegistry sessionRegistry() {
        logger.info("Wings conf sessionRegistry");
        return new SpringSessionBackedSessionRegistry<>(sessionRepository);
    }
}
