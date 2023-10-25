package pro.fessional.wings.warlock.spring.conf;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import pro.fessional.wings.warlock.spring.bean.WarlockBondBeanConfiguration;


/**
 * @author trydofor
 * @since 2019-12-01
 */
@AutoConfiguration(before = WarlockShadowSecurityAutoConfiguration.class)
@ImportAutoConfiguration(WarlockBondBeanConfiguration.class)
public class WarlockBondAutoConfiguration {
}
