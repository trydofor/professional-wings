package pro.fessional.wings.warlock.spring.bean;

import com.hazelcast.core.HazelcastInstance;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.HandlerExceptionResolver;
import pro.fessional.wings.slardar.concur.HazelcastGlobalLock;
import pro.fessional.wings.slardar.concur.impl.RighterInterceptor;
import pro.fessional.wings.slardar.context.SecurityContextUtil;
import pro.fessional.wings.warlock.errorhandle.AllExceptionResolver;
import pro.fessional.wings.warlock.errorhandle.CodeExceptionResolver;
import pro.fessional.wings.warlock.errorhandle.auto.BindExceptionAdvice;
import pro.fessional.wings.warlock.spring.prop.WarlockEnabledProp;
import pro.fessional.wings.warlock.spring.prop.WarlockErrorProp;
import pro.fessional.wings.warlock.spring.prop.WarlockLockProp;


/**
 * @author trydofor
 * @since 2019-12-01
 */
@Configuration(proxyBeanMethods = false)
public class WarlockOtherBeanConfiguration {

    private final static Log log = LogFactory.getLog(WarlockOtherBeanConfiguration.class);

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnProperty(name = WarlockEnabledProp.Key$bindExceptionAdvice, havingValue = "true")
    @ComponentScan(basePackageClasses = BindExceptionAdvice.class)
    public static class BindingErrorConfig {
    }

    @Bean
    @ConditionalOnMissingBean(name = "codeExceptionResolver")
    @ConditionalOnProperty(name = WarlockEnabledProp.Key$codeExceptionHandler, havingValue = "true")
    public HandlerExceptionResolver codeExceptionResolver(MessageSource messageSource, WarlockErrorProp prop) {
        log.info("WarlockShadow spring-bean codeExceptionResolver");
        final WarlockErrorProp.CodeException cp = prop.getCodeException();
        final CodeExceptionResolver bean = new CodeExceptionResolver(messageSource, cp.getHttpStatus(), cp.getContentType(), cp.getResponseBody());
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE + 1000);
        return bean;
    }

    @Bean
    @ConditionalOnMissingBean(name = "allExceptionResolver")
    @ConditionalOnProperty(name = WarlockEnabledProp.Key$allExceptionHandler, havingValue = "true")
    public HandlerExceptionResolver allExceptionResolver(WarlockErrorProp prop) {
        log.info("WarlockShadow spring-bean allExceptionResolver");
        final WarlockErrorProp.CodeException cp = prop.getAllException();
        final AllExceptionResolver bean = new AllExceptionResolver(cp.getHttpStatus(), cp.getContentType(), cp.getResponseBody());
        bean.setOrder(Ordered.LOWEST_PRECEDENCE);
        return bean;
    }

    @Bean
    @ConditionalOnMissingBean
    public RighterInterceptor.SecretProvider righterInterceptorSecretProvider() {
        log.info("WarlockShadow spring-bean righterInterceptorSecretProvider");
        return ss -> {
            final Long uid = SecurityContextUtil.getUserId(false);
            return uid == null ? null : ss.getId() + uid;
        };
    }

    @Bean
    @ConditionalOnMissingBean(HazelcastGlobalLock.class)
    @ConditionalOnProperty(name = WarlockEnabledProp.Key$globalLock, havingValue = "true")
    public HazelcastGlobalLock hazelcastGlobalLock(HazelcastInstance hazelcastInstance, WarlockLockProp warlockLockProp) {
        final boolean hcp = warlockLockProp.isHazelcastCp();
        log.info("WarlockShadow spring-bean hazelcastGlobalLock, useCpIfSafe=" + hcp);
        return new HazelcastGlobalLock(hazelcastInstance, hcp);
    }
}
