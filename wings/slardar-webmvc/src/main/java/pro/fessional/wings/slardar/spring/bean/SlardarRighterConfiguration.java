package pro.fessional.wings.slardar.spring.bean;

import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerExceptionResolver;
import pro.fessional.wings.slardar.concur.impl.RighterExceptionResolver;
import pro.fessional.wings.slardar.concur.impl.RighterInterceptor;
import pro.fessional.wings.slardar.spring.prop.SlardarEnabledProp;
import pro.fessional.wings.slardar.spring.prop.SlardarRighterProp;

/**
 * @author trydofor
 * @since 2019-06-29
 */
@RequiredArgsConstructor
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(name = SlardarEnabledProp.Key$righter, havingValue = "true")
public class SlardarRighterConfiguration {

    private final static Log log = LogFactory.getLog(SlardarRighterConfiguration.class);
    private final SlardarRighterProp slardarRighterProp;

    @Bean
    @ConditionalOnMissingBean(RighterInterceptor.class)
    public RighterInterceptor righterInterceptor(ObjectProvider<RighterInterceptor.SecretProvider> secretProvider) {
        log.info("SlardarWebmvc spring-bean righterInterceptor");
        final RighterInterceptor bean = new RighterInterceptor(slardarRighterProp);
        secretProvider.ifAvailable(bean::setSecretProvider);
        return bean;
    }

    @Bean
    @ConditionalOnMissingBean(name = "righterExceptionResolver")
    @ConditionalOnProperty(name = SlardarEnabledProp.Key$righter, havingValue = "true")
    public HandlerExceptionResolver righterExceptionResolver() {
        log.info("SlardarWebmvc spring-bean righterExceptionResolver");
        return new RighterExceptionResolver(slardarRighterProp);
    }
}
