package pro.fessional.wings.warlock.spring.bean;

import com.hazelcast.core.HazelcastInstance;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import pro.fessional.mirana.lock.JvmStaticGlobalLock;
import pro.fessional.wings.faceless.concur.DatabaseGlobalLock;
import pro.fessional.wings.slardar.concur.HazelcastGlobalLock;
import pro.fessional.wings.warlock.spring.prop.WarlockEnabledProp;
import pro.fessional.wings.warlock.spring.prop.WarlockLockProp;


/**
 * @author trydofor
 * @since 2019-12-01
 */
@Configuration(proxyBeanMethods = false)
@RequiredArgsConstructor
public class WarlockLockBeanConfiguration {

    private final static Log log = LogFactory.getLog(WarlockLockBeanConfiguration.class);
    private final WarlockLockProp warlockLockProp;

    @Bean
    @ConditionalOnMissingBean(JvmStaticGlobalLock.class)
    @ConditionalOnProperty(name = WarlockEnabledProp.Key$globalLock, havingValue = "true")
    public JvmStaticGlobalLock jvmStaticGlobalLock() {
        log.info("Wings conf jvmStaticGlobalLock");
        return new JvmStaticGlobalLock();
    }

    @Bean
    @ConditionalOnMissingBean(DatabaseGlobalLock.class)
    @ConditionalOnProperty(name = WarlockEnabledProp.Key$globalLock, havingValue = "true")
    public DatabaseGlobalLock databaseGlobalLock(JdbcTemplate jdbcTemplate) {
        log.info("Wings conf databaseGlobalLock");
        return new DatabaseGlobalLock(jdbcTemplate);
    }

    @Bean
    @ConditionalOnMissingBean(HazelcastGlobalLock.class)
    @ConditionalOnProperty(name = WarlockEnabledProp.Key$globalLock, havingValue = "true")
    public HazelcastGlobalLock hazelcastGlobalLock(HazelcastInstance hazelcastInstance) {
        final boolean hcp = warlockLockProp.isHazelcastCp();
        log.info("Wings conf hazelcastGlobalLock, useCpIfSafe=" + hcp);
        return new HazelcastGlobalLock(hazelcastInstance, hcp);
    }
}
