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
import pro.fessional.wings.slardar.context.GlobalAttributeHolder;
import pro.fessional.wings.slardar.security.WingsUserDetails;
import pro.fessional.wings.warlock.errorhandle.AllExceptionResolver;
import pro.fessional.wings.warlock.errorhandle.CodeExceptionResolver;
import pro.fessional.wings.warlock.errorhandle.auto.BindExceptionAdvice;
import pro.fessional.wings.warlock.spring.prop.WarlockEnabledProp;
import pro.fessional.wings.warlock.spring.prop.WarlockErrorProp;
import pro.fessional.wings.warlock.spring.prop.WarlockLockProp;

import static pro.fessional.wings.warlock.service.user.WarlockUserAttribute.SaltByUid;


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
        log.info("Wings conf codeExceptionResolver");
        final WarlockErrorProp.CodeException cp = prop.getCodeException();
        final CodeExceptionResolver bean = new CodeExceptionResolver(messageSource, cp.getHttpStatus(), cp.getContentType(), cp.getResponseBody());
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE + 1000);
        return bean;
    }

    @Bean
    @ConditionalOnMissingBean(name = "allExceptionResolver")
    @ConditionalOnProperty(name = WarlockEnabledProp.Key$allExceptionHandler, havingValue = "true")
    public HandlerExceptionResolver allExceptionResolver(WarlockErrorProp prop) {
        log.info("Wings conf allExceptionResolver");
        final WarlockErrorProp.CodeException cp = prop.getAllException();
        final AllExceptionResolver bean = new AllExceptionResolver(cp.getHttpStatus(), cp.getContentType(), cp.getResponseBody());
        bean.setOrder(Ordered.LOWEST_PRECEDENCE);
        return bean;
    }

    @Bean
    @ConditionalOnMissingBean
    public RighterInterceptor.SecretProvider righterInterceptorSecretProvider() {
        log.info("Wings conf righterInterceptorSecretProvider");
        return auth -> {
            final Object dtl = auth.getDetails();
            if (dtl instanceof WingsUserDetails) {
                return GlobalAttributeHolder.getAttr(SaltByUid, ((WingsUserDetails) dtl).getUserId());
            }
            return null;
        };
    }

    @Bean
    @ConditionalOnMissingBean(HazelcastGlobalLock.class)
    @ConditionalOnProperty(name = WarlockEnabledProp.Key$globalLock, havingValue = "true")
    public HazelcastGlobalLock hazelcastGlobalLock(HazelcastInstance hazelcastInstance, WarlockLockProp warlockLockProp) {
        final boolean hcp = warlockLockProp.isHazelcastCp();
        log.info("Wings conf hazelcastGlobalLock, useCpIfSafe=" + hcp);
        return new HazelcastGlobalLock(hazelcastInstance, hcp);
    }
}
