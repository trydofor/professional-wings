package pro.fessional.wings.warlock.spring.conf;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import pro.fessional.wings.warlock.spring.bean.WarlockSecurityBeanConfiguration;
import pro.fessional.wings.warlock.spring.bean.WarlockSecurityConfConfiguration;


/**
 * @author trydofor
 * @since 2019-12-01
 */
@AutoConfiguration(before = {
        WarlockShadowAutoConfiguration.class,
        SecurityAutoConfiguration.class,
        UserDetailsServiceAutoConfiguration.class,
})
@ImportAutoConfiguration({
        WarlockSecurityBeanConfiguration.class,
        WarlockSecurityConfConfiguration.class,
})
public class WarlockShadowSecurityAutoConfiguration {
}
