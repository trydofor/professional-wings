package pro.fessional.wings.slardar.app.conf;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author trydofor
 * @since 2019-12-03
 */
@Configuration(proxyBeanMethods = false)
@ComponentScan({
        "pro.fessional.wings.slardar.service",
        "pro.fessional.wings.slardar.controller",
})
public class SlardarAutoScanConfiguration {
}
