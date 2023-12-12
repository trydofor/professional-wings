package pro.fessional.wings.slardar.spring.bean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pro.fessional.wings.silencer.spring.boot.ConditionalWingsEnabled;
import pro.fessional.wings.slardar.concur.impl.DoubleKillAround;
import pro.fessional.wings.slardar.spring.prop.SlardarEnabledProp;

/**
 * @author trydofor
 * @since 2019-12-03
 */
@Configuration(proxyBeanMethods = false)
@ConditionalWingsEnabled(abs = SlardarEnabledProp.Key$doubleKill)
public class SlardarDoubleKillConfiguration {

    private static final Log log = LogFactory.getLog(SlardarDoubleKillConfiguration.class);

    @Bean
    @ConditionalWingsEnabled
    public DoubleKillAround doubleKillAround() {
        log.info("Slardar spring-bean doubleKillAround");
        return new DoubleKillAround();
    }

}
