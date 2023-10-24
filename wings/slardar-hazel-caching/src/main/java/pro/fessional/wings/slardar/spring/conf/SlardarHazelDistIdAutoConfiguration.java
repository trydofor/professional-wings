package pro.fessional.wings.slardar.spring.conf;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import pro.fessional.wings.faceless.spring.conf.FacelessAutoConfiguration;
import pro.fessional.wings.slardar.spring.bean.HazelcastFlakeIdConfiguration;
import pro.fessional.wings.slardar.spring.bean.HazelcastLightIdConfiguration;
import pro.fessional.wings.slardar.spring.prop.SlardarEnabledProp;

/**
 * @author trydofor
 * @since 2019-07-11
 */
@AutoConfiguration(before = {SlardarAutoConfiguration.class, FacelessAutoConfiguration.class})
@ConditionalOnClass(FacelessAutoConfiguration.class)
@ConditionalOnProperty(name = SlardarEnabledProp.Key$autoconf, havingValue = "true")
@ImportAutoConfiguration({
        HazelcastFlakeIdConfiguration.class,
        HazelcastLightIdConfiguration.class,
})
public class SlardarHazelDistIdAutoConfiguration {
}
