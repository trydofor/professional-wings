package pro.fessional.wings.slardar.spring.conf;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import pro.fessional.wings.slardar.spring.bean.SlardarActuatorConfiguration;
import pro.fessional.wings.slardar.spring.bean.SlardarSecurityConfiguration;
import pro.fessional.wings.slardar.spring.prop.SlardarEnabledProp;

/**
 * @author trydofor
 * @since 2019-07-11
 */
@AutoConfiguration(before = SlardarHazelSessionAutoConfiguration.class)
@ConditionalOnProperty(name = SlardarEnabledProp.Key$autoconf, havingValue = "true")
@ImportAutoConfiguration({
        SlardarActuatorConfiguration.class,
        SlardarSecurityConfiguration.class,
})
public class SlardarSprintAutoConfiguration {
}
