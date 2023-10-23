package pro.fessional.wings.faceless.spring.conf;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jooq.JooqAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import pro.fessional.wings.faceless.spring.bean.FacelessJooqConfiguration;
import pro.fessional.wings.faceless.spring.prop.FacelessEnabledProp;
import pro.fessional.wings.faceless.spring.prop.FacelessJooqEnabledProp;

/**
 * @author trydofor
 * @since 2019-06-01
 */

@AutoConfiguration(after = JooqAutoConfiguration.class)
@ConditionalOnProperty(name = FacelessEnabledProp.Key$autoconf, havingValue = "true")
@EnableConfigurationProperties(FacelessJooqEnabledProp.class)
@ImportAutoConfiguration(FacelessJooqConfiguration.class)
public class FacelessJooqAutoConfiguration {
}
