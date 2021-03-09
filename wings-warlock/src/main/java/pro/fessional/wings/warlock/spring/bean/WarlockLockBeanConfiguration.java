package pro.fessional.wings.warlock.spring.bean;

import com.hazelcast.core.HazelcastInstance;
import lombok.Setter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import pro.fessional.wings.faceless.concur.DatabaseGlobalLock;
import pro.fessional.wings.silencer.concur.JvmStaticGlobalLock;
import pro.fessional.wings.slardar.concur.HazelcastGlobalLock;
import pro.fessional.wings.warlock.spring.prop.WarlockEnabledProp;
import pro.fessional.wings.warlock.spring.prop.WarlockLockProp;


/**
 * @author trydofor
 * @since 2019-12-01
 */
@Configuration
public class WarlockLockBeanConfiguration {

    private final static Log logger = LogFactory.getLog(WarlockLockBeanConfiguration.class);

    @Setter(onMethod_ = {@Autowired})
    private WarlockLockProp warlockLockProp;


    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(name = WarlockEnabledProp.Key$globalLock, havingValue = "true")
    public JvmStaticGlobalLock jvmStaticGlobalLock() {
        logger.info("Wings conf jvmStaticGlobalLock");
        return new JvmStaticGlobalLock();
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(name = WarlockEnabledProp.Key$globalLock, havingValue = "true")
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public DatabaseGlobalLock databaseGlobalLock(JdbcTemplate jdbcTemplate) {
        logger.info("Wings conf databaseGlobalLock");
        return new DatabaseGlobalLock(jdbcTemplate);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(name = WarlockEnabledProp.Key$globalLock, havingValue = "true")
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public HazelcastGlobalLock hazelcastGlobalLock(HazelcastInstance hazelcastInstance) {
        final boolean hcp = warlockLockProp.isHazelcastCp();
        logger.info("Wings conf hazelcastGlobalLock, useCpIfSafe=" + hcp);
        return new HazelcastGlobalLock(hazelcastInstance, hcp);
    }
}
