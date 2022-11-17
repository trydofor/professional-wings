package pro.fessional.wings.slardar.spring.bean;

import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerExceptionResolver;
import pro.fessional.wings.slardar.concur.impl.DoubleKillExceptionResolver;
import pro.fessional.wings.slardar.spring.prop.SlardarDoubleKillProp;
import pro.fessional.wings.slardar.spring.prop.SlardarEnabledProp;

/**
 * @author trydofor
 * @since 2019-12-03
 */
@Configuration(proxyBeanMethods = false)
@RequiredArgsConstructor
@ConditionalOnProperty(name = SlardarEnabledProp.Key$doubleKill, havingValue = "true")
@AutoConfigureAfter(SlardarDoubleKillConfiguration.class)
public class SlardarDoubleKillWebConfiguration {

    private static final Log log = LogFactory.getLog(SlardarDoubleKillWebConfiguration.class);
    private final SlardarDoubleKillProp doubleKillProp;

    @Bean
    @ConditionalOnMissingBean(name = "doubleKillExceptionResolver")
    public HandlerExceptionResolver doubleKillExceptionResolver() {
        log.info("SlardarWebmvc spring-bean doubleKillExceptionResolver");
        return new DoubleKillExceptionResolver(doubleKillProp);
    }
}
