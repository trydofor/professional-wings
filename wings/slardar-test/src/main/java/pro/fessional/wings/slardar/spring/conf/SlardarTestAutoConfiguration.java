package pro.fessional.wings.slardar.spring.conf;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import pro.fessional.wings.slardar.security.WingsAuthDetailsSource;
import pro.fessional.wings.slardar.security.handler.TestLoginHandler;
import pro.fessional.wings.slardar.security.impl.DefaultWingsAuthDetails;

/**
 * @author trydofor
 * @since 2022-01-29
 */
@SuppressWarnings("SpringComponentScan")
@AutoConfiguration
@ComponentScan({
        "pro.fessional.wings.slardar.helper",
        "pro.fessional.wings.slardar.service",
        "pro.fessional.wings.slardar.controller",
})
public class SlardarTestAutoConfiguration {

    @Bean
    public TestLoginHandler testLoginHandler() {
        return new TestLoginHandler();
    }

    @Bean
    public WingsAuthDetailsSource<DefaultWingsAuthDetails> wingsBindAuthnDetailsSource() {
        return (authType, request) -> new DefaultWingsAuthDetails();
    }
}
