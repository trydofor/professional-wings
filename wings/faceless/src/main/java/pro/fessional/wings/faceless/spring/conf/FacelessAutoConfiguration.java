package pro.fessional.wings.faceless.spring.conf;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import pro.fessional.wings.faceless.spring.bean.FacelessDataSourceConfiguration;
import pro.fessional.wings.faceless.spring.bean.FacelessEnumI18nConfiguration;
import pro.fessional.wings.faceless.spring.bean.FacelessFlakeIdConfiguration;
import pro.fessional.wings.faceless.spring.bean.FacelessJournalConfiguration;
import pro.fessional.wings.faceless.spring.bean.FacelessLightIdConfiguration;
import pro.fessional.wings.faceless.spring.prop.FacelessEnabledProp;

/**
 * @author trydofor
 * @since 2019-06-01
 */

@AutoConfiguration
@ConditionalOnProperty(name = FacelessEnabledProp.Key$autoconf, havingValue = "true")
@ImportAutoConfiguration({
        FacelessDataSourceConfiguration.class,
        FacelessEnumI18nConfiguration.class,
        FacelessFlakeIdConfiguration.class,
        FacelessJournalConfiguration.class,
        FacelessLightIdConfiguration.class,
})
public class FacelessAutoConfiguration {
}
