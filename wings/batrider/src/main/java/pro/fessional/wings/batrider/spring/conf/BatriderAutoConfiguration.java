package pro.fessional.wings.batrider.spring.conf;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Import;
import pro.fessional.wings.batrider.spring.bean.BatriderServcombConfiguration;
import pro.fessional.wings.batrider.spring.prop.BatriderEnabledProp;
import pro.fessional.wings.silencer.spring.boot.ConditionalWingsEnabled;

/**
 * @author trydofor
 * @since 2019-07-11
 */
@AutoConfiguration
@ConditionalWingsEnabled
@ConfigurationPropertiesScan(basePackageClasses = BatriderEnabledProp.class)
@Import(BatriderServcombConfiguration.class)
public class BatriderAutoConfiguration {
}
