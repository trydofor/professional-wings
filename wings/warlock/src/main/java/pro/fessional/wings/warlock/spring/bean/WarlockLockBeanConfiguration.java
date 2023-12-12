package pro.fessional.wings.warlock.spring.bean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import pro.fessional.mirana.lock.JvmStaticGlobalLock;
import pro.fessional.wings.faceless.concur.DatabaseGlobalLock;
import pro.fessional.wings.silencer.spring.boot.ConditionalWingsEnabled;


/**
 * @author trydofor
 * @since 2019-12-01
 */
@Configuration(proxyBeanMethods = false)
@ConditionalWingsEnabled
public class WarlockLockBeanConfiguration {

    private final static Log log = LogFactory.getLog(WarlockLockBeanConfiguration.class);

    @Bean
    @ConditionalWingsEnabled
    public DatabaseGlobalLock databaseGlobalLock(JdbcTemplate jdbcTemplate) {
        log.info("Warlock spring-bean databaseGlobalLock");
        return new DatabaseGlobalLock(jdbcTemplate);
    }

    @Bean
    @ConditionalWingsEnabled
    public JvmStaticGlobalLock jvmStaticGlobalLock() {
        log.info("Warlock spring-bean jvmStaticGlobalLock");
        return new JvmStaticGlobalLock();
    }
}
