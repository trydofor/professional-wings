package pro.fessional.wings.slardar.spring.conf;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.session.SessionAutoConfiguration;
import pro.fessional.wings.slardar.spring.bean.HazelcastSessionConfiguration;
import pro.fessional.wings.slardar.spring.prop.SlardarEnabledProp;

/**
 * @author trydofor
 * @since 2019-07-11
 */
@AutoConfiguration(after = SessionAutoConfiguration.class)
@ConditionalOnProperty(name = SlardarEnabledProp.Key$autoconf, havingValue = "true")
@ImportAutoConfiguration(HazelcastSessionConfiguration.class)
public class SlardarHazelSessionAutoConfiguration {
}
