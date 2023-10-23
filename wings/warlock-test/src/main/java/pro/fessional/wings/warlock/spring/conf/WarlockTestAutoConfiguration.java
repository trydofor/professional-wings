package pro.fessional.wings.warlock.spring.conf;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author trydofor
 * @since 2022-01-29
 */
@SuppressWarnings("SpringComponentScan")
@AutoConfiguration
@ComponentScan({
        "pro.fessional.wings.warlock.helper",
        "pro.fessional.wings.warlock.service",
        "pro.fessional.wings.warlock.controller",
})
public class WarlockTestAutoConfiguration {
}
