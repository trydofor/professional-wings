package pro.fessional.wings.slardar.spring.bean;

import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pro.fessional.wings.slardar.concur.impl.DoubleKillAround;
import pro.fessional.wings.spring.consts.OrderedSlardarConst;
import pro.fessional.wings.slardar.spring.prop.SlardarEnabledProp;

/**
 * @author trydofor
 * @since 2019-12-03
 */
@Configuration(proxyBeanMethods = false)
@RequiredArgsConstructor
@ConditionalOnProperty(name = SlardarEnabledProp.Key$doubleKill, havingValue = "true")
@AutoConfigureOrder(OrderedSlardarConst.DoubleKillConfiguration)
public class SlardarDoubleKillConfiguration {

    private static final Log log = LogFactory.getLog(SlardarDoubleKillConfiguration.class);

    @Bean
    public DoubleKillAround doubleKillAround() {
        log.info("Slardar spring-bean doubleKillAround");
        return new DoubleKillAround();
    }

}
