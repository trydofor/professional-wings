package pro.fessional.wings.faceless.spring.conf;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Import;
import pro.fessional.wings.faceless.spring.bean.FacelessConfiguration;
import pro.fessional.wings.faceless.spring.bean.FacelessLightIdConfiguration;
import pro.fessional.wings.faceless.spring.prop.FacelessEnabledProp;
import pro.fessional.wings.silencer.spring.boot.ConditionalWingsEnabled;

/**
 * @author trydofor
 * @since 2019-06-01
 */

@AutoConfiguration
@ConditionalWingsEnabled
@ConfigurationPropertiesScan(basePackageClasses = FacelessEnabledProp.class)
@Import({
        FacelessConfiguration.class,
        FacelessLightIdConfiguration.class
})
public class FacelessAutoConfiguration {
}
