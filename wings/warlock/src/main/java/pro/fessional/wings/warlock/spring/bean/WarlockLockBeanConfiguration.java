package pro.fessional.wings.warlock.spring.bean;

import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import pro.fessional.mirana.lock.JvmStaticGlobalLock;
import pro.fessional.wings.faceless.concur.DatabaseGlobalLock;
import pro.fessional.wings.spring.consts.OrderedWarlockConst;
import pro.fessional.wings.warlock.spring.prop.WarlockEnabledProp;


/**
 * @author trydofor
 * @since 2019-12-01
 */
@Configuration(proxyBeanMethods = false)
@RequiredArgsConstructor
@AutoConfigureOrder(OrderedWarlockConst.LockBeanConfiguration)
public class WarlockLockBeanConfiguration {

    private final static Log log = LogFactory.getLog(WarlockLockBeanConfiguration.class);

    @Bean
    @ConditionalOnMissingBean(JvmStaticGlobalLock.class)
    @ConditionalOnProperty(name = WarlockEnabledProp.Key$globalLock, havingValue = "true")
    public JvmStaticGlobalLock jvmStaticGlobalLock() {
        log.info("Warlock spring-bean jvmStaticGlobalLock");
        return new JvmStaticGlobalLock();
    }

    @Bean
    @ConditionalOnMissingBean(DatabaseGlobalLock.class)
    @ConditionalOnProperty(name = WarlockEnabledProp.Key$globalLock, havingValue = "true")
    public DatabaseGlobalLock databaseGlobalLock(JdbcTemplate jdbcTemplate) {
        log.info("Warlock spring-bean databaseGlobalLock");
        return new DatabaseGlobalLock(jdbcTemplate);
    }
}
