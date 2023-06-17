package pro.fessional.wings.warlock.spring.bean;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerExceptionResolver;
import pro.fessional.wings.slardar.concur.impl.RighterInterceptor;
import pro.fessional.wings.slardar.context.SecurityContextUtil;
import pro.fessional.wings.spring.consts.OrderedWarlockConst;
import pro.fessional.wings.warlock.errorhandle.DefaultExceptionResolver;
import pro.fessional.wings.warlock.errorhandle.auto.BindExceptionAdvice;
import pro.fessional.wings.warlock.spring.prop.WarlockEnabledProp;
import pro.fessional.wings.warlock.spring.prop.WarlockErrorProp;

import static pro.fessional.wings.spring.consts.NamingWarlockConst.defaultExceptionResolver;


/**
 * @author trydofor
 * @since 2019-12-01
 */
@Configuration(proxyBeanMethods = false)
@ComponentScan("pro.fessional.wings.warlock.controller")
@AutoConfigureOrder(OrderedWarlockConst.OtherBeanConfiguration)
public class WarlockOtherBeanConfiguration {

    private final static Log log = LogFactory.getLog(WarlockOtherBeanConfiguration.class);

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnProperty(name = WarlockEnabledProp.Key$bindExceptionAdvice, havingValue = "true")
    @ComponentScan(basePackageClasses = BindExceptionAdvice.class)
    public static class BindingErrorConfig {
    }

    @Bean(name = defaultExceptionResolver)
    @ConditionalOnMissingBean(name = defaultExceptionResolver)
    @ConditionalOnProperty(name = WarlockEnabledProp.Key$defaultExceptionHandler, havingValue = "true")
    public HandlerExceptionResolver defaultExceptionResolver(WarlockErrorProp prop, MessageSource messageSource, ObjectMapper objectMapper) {
        log.info("WarlockShadow spring-bean " + defaultExceptionResolver);
        return new DefaultExceptionResolver(prop.getDefaultException(), messageSource, objectMapper);
    }

    @Bean
    @ConditionalOnMissingBean(RighterInterceptor.SecretProvider.class)
    public RighterInterceptor.SecretProvider righterInterceptorSecretProvider() {
        log.info("WarlockShadow spring-bean righterInterceptorSecretProvider");
        return ss -> {
            final Long uid = SecurityContextUtil.getUserId(false);
            return uid == null ? null : ss.getId() + uid;
        };
    }
}
