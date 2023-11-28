package pro.fessional.wings.slardar.spring.bean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerExceptionResolver;
import pro.fessional.wings.silencer.spring.boot.ConditionalWingsEnabled;
import pro.fessional.wings.slardar.concur.impl.DoubleKillExceptionResolver;
import pro.fessional.wings.slardar.spring.prop.SlardarDoubleKillProp;
import pro.fessional.wings.slardar.spring.prop.SlardarEnabledProp;

/**
 * @author trydofor
 * @since 2019-12-03
 */
@Configuration(proxyBeanMethods = false)
@ConditionalWingsEnabled(abs = SlardarEnabledProp.Key$doubleKill)
public class SlardarDoubleKillWebConfiguration {

    private static final Log log = LogFactory.getLog(SlardarDoubleKillWebConfiguration.class);

    @Bean
    @ConditionalWingsEnabled
    public HandlerExceptionResolver doubleKillExceptionResolver(SlardarDoubleKillProp prop) {
        log.info("SlardarWebmvc spring-bean doubleKillExceptionResolver");
        return new DoubleKillExceptionResolver(prop);
    }
}
