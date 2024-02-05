package pro.fessional.wings.warlock.spring.conf;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;
import pro.fessional.wings.silencer.spring.boot.ConditionalWingsEnabled;
import pro.fessional.wings.warlock.spring.bean.WarlockAwesomeConfiguration;


/**
 * @author trydofor
 * @since 2019-12-01
 */
@AutoConfiguration
@ConditionalWingsEnabled
@Import(WarlockAwesomeConfiguration.class)
public class WarlockAwesomeAutoConfiguration {
}
