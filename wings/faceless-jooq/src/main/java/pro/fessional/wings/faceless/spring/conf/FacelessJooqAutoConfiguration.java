package pro.fessional.wings.faceless.spring.conf;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import pro.fessional.wings.faceless.spring.bean.FacelessJooqConfiguration;
import pro.fessional.wings.faceless.spring.prop.FacelessEnabledProp;

/**
 * @author trydofor
 * @since 2019-06-01
 */

@AutoConfiguration(before = FacelessAutoConfiguration.class)
@ConditionalOnProperty(name = FacelessEnabledProp.Key$autoconf, havingValue = "true")
@ImportAutoConfiguration(FacelessJooqConfiguration.class)
public class FacelessJooqAutoConfiguration {
}
