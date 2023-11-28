package pro.fessional.wings.warlock.spring.conf;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;
import pro.fessional.wings.silencer.spring.boot.ConditionalWingsEnabled;
import pro.fessional.wings.warlock.spring.bean.WarlockBondBeanConfiguration;


/**
 * @author trydofor
 * @since 2019-12-01
 */
@AutoConfiguration
@ConditionalWingsEnabled
@Import(WarlockBondBeanConfiguration.class)
public class WarlockBondAutoConfiguration {
}
