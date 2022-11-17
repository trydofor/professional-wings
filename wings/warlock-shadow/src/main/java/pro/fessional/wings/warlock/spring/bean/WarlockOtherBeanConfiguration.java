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
import org.springframework.web.servlet.HandlerExceptionResolver;
import pro.fessional.wings.slardar.concur.HazelcastGlobalLock;
import pro.fessional.wings.slardar.concur.impl.RighterInterceptor;
import pro.fessional.wings.slardar.context.SecurityContextUtil;
import pro.fessional.wings.slardar.webmvc.MessageResponse;
import pro.fessional.wings.warlock.errorhandle.CodeExceptionResolver;
import pro.fessional.wings.warlock.errorhandle.DefaultExceptionResolver;
import pro.fessional.wings.warlock.errorhandle.auto.BindExceptionAdvice;
import pro.fessional.wings.warlock.service.auth.WarlockTicketService;
import pro.fessional.wings.warlock.service.auth.impl.SimpleTicketServiceImpl;
import pro.fessional.wings.warlock.spring.prop.WarlockEnabledProp;
import pro.fessional.wings.warlock.spring.prop.WarlockErrorProp;
import pro.fessional.wings.warlock.spring.prop.WarlockLockProp;
import pro.fessional.wings.warlock.spring.prop.WarlockTicketProp;

import java.util.Map;


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
        final MessageResponse cp = prop.getCodeException();
        prop.defaultIfAbsent(cp);
        return new CodeExceptionResolver(cp, messageSource);
    }

    @Bean
    @ConditionalOnMissingBean(name = "defaultExceptionResolver")
    @ConditionalOnProperty(name = WarlockEnabledProp.Key$defaultExceptionHandler, havingValue = "true")
    public HandlerExceptionResolver defaultExceptionResolver(WarlockErrorProp prop) {
        log.info("WarlockShadow spring-bean defaultExceptionResolver");
        final MessageResponse cp = prop.getDefaultException();
        return new DefaultExceptionResolver(cp);
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

    @Bean
    @ConditionalOnMissingBean(WarlockTicketService.class)
    public WarlockTicketService warlockTicketService(WarlockTicketProp warlockTicketProp) {
        log.info("WarlockShadow spring-bean warlockTicketService");
        SimpleTicketServiceImpl bean = new SimpleTicketServiceImpl();
        bean.setAuthorizeCodeMax(warlockTicketProp.getCodeMax());
        bean.setAccessTokenMax(warlockTicketProp.getTokenMax());

        for (Map.Entry<String, WarlockTicketService.Pass> en : warlockTicketProp.getClient().entrySet()) {
            final WarlockTicketService.Pass pass = en.getValue();
            final String client = en.getKey();
            final String secret = pass.getSecret();
            if (secret == null || secret.isEmpty()) {
                log.warn("WarlockShadow spring-conf skip warlockTicketService.client=" + client + " for empty secret");
                continue;
            }
            pass.setClient(client);
            log.info("WarlockShadow spring-conf warlockTicketService.client=" + client);
            bean.addClient(pass);
        }
        return bean;
    }
}
