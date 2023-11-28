package pro.fessional.wings.faceless.spring.conf;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;
import pro.fessional.wings.faceless.spring.bean.FlywaveConfiguration;
import pro.fessional.wings.faceless.spring.prop.FacelessEnabledProp;
import pro.fessional.wings.silencer.spring.boot.ConditionalWingsEnabled;

/**
 * @author trydofor
 * @since 2019-06-01
 */

@AutoConfiguration
@ConditionalWingsEnabled(abs = FacelessEnabledProp.Key$flywave, value = false)
@Import(FlywaveConfiguration.class)
public class FlywaveAutoConfiguration {
}
