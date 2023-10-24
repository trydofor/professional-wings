package pro.fessional.wings.slardar.spring.conf;

import de.codecentric.boot.admin.client.config.SpringBootAdminClientAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import pro.fessional.wings.slardar.spring.bean.SlardarBootAdminConfiguration;
import pro.fessional.wings.slardar.spring.prop.SlardarEnabledProp;

/**
 * @author trydofor
 * @since 2019-07-11
 */
@AutoConfiguration(before = {SlardarHazelSessionAutoConfiguration.class, SpringBootAdminClientAutoConfiguration.class})
@ConditionalOnClass(SpringBootAdminClientAutoConfiguration.class)
@ConditionalOnProperty(name = SlardarEnabledProp.Key$autoconf, havingValue = "true")
@ImportAutoConfiguration(SlardarBootAdminConfiguration.class)
public class SlardarBootAdminAutoConfiguration {
}
