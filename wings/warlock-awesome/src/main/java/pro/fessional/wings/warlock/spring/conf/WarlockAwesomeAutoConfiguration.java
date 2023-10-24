package pro.fessional.wings.warlock.spring.conf;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import pro.fessional.wings.warlock.spring.bean.WarlockAwesomeConfiguration;


/**
 * @author trydofor
 * @since 2019-12-01
 */
@AutoConfiguration(before = WarlockAutoConfiguration.class)
@ImportAutoConfiguration(WarlockAwesomeConfiguration.class)
public class WarlockAwesomeAutoConfiguration {
}
