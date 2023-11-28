package pro.fessional.wings.tiny.mail.spring.conf;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Import;
import pro.fessional.wings.silencer.spring.boot.ConditionalWingsEnabled;
import pro.fessional.wings.tiny.mail.spring.bean.TinyMailConfiguration;
import pro.fessional.wings.tiny.mail.spring.prop.TinyMailEnabledProp;

/**
 * @author trydofor
 * @since 2019-07-11
 */
@AutoConfiguration
@ConditionalWingsEnabled
@ConfigurationPropertiesScan(basePackageClasses = TinyMailEnabledProp.class)
@Import(TinyMailConfiguration.class)
public class TinyMailAutoConfiguration {
}
