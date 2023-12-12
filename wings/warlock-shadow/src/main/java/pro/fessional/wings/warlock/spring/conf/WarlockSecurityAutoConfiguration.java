package pro.fessional.wings.warlock.spring.conf;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.context.annotation.Import;
import pro.fessional.wings.silencer.spring.boot.ConditionalWingsEnabled;
import pro.fessional.wings.warlock.spring.bean.WarlockSecurityBeanConfiguration;
import pro.fessional.wings.warlock.spring.bean.WarlockSecurityConfConfiguration;
import pro.fessional.wings.warlock.spring.bean.WarlockSecurityDummyConfiguration;


/**
 * @author trydofor
 * @since 2019-12-01
 */
@AutoConfiguration(before = {
        SecurityAutoConfiguration.class,
        UserDetailsServiceAutoConfiguration.class,
})
@ConditionalWingsEnabled
@Import({
        WarlockSecurityBeanConfiguration.class,
        WarlockSecurityConfConfiguration.class,
        WarlockSecurityDummyConfiguration.class,
})
public class WarlockSecurityAutoConfiguration {
}
