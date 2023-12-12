package pro.fessional.wings.slardar.spring.conf;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;
import pro.fessional.wings.silencer.spring.boot.ConditionalWingsEnabled;
import pro.fessional.wings.slardar.spring.bean.SlardarActuatorConfiguration;
import pro.fessional.wings.slardar.spring.bean.SlardarSecurityConfiguration;

/**
 * @author trydofor
 * @since 2019-07-11
 */
@AutoConfiguration
@ConditionalWingsEnabled
@Import({
        SlardarActuatorConfiguration.class,
        SlardarSecurityConfiguration.class,
})
public class SlardarSprintAutoConfiguration {
}
