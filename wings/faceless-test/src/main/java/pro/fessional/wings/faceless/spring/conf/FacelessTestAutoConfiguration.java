package pro.fessional.wings.faceless.spring.conf;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author trydofor
 * @since 2023-10-23
 */
@SuppressWarnings("SpringComponentScan")
@AutoConfiguration
@ComponentScan({
        "pro.fessional.wings.faceless.helper",
        "pro.fessional.wings.faceless.service",
        "pro.fessional.wings.faceless.database.autogen",
        "pro.fessional.wings.faceless.autoconf",
})
public class FacelessTestAutoConfiguration {
}
