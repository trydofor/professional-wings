package pro.fessional.wings.testing.slardar.spring.conf;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import pro.fessional.wings.silencer.spring.boot.ConditionalWingsEnabled;
import pro.fessional.wings.slardar.security.WingsAuthDetailsSource;
import pro.fessional.wings.slardar.security.impl.DefaultWingsAuthDetails;
import pro.fessional.wings.testing.slardar.security.handler.TestingLoginHandler;
import pro.fessional.wings.testing.slardar.service.TestingWingsUserDetailsService;

/**
 * @author trydofor
 * @since 2022-01-29
 */
@AutoConfiguration(before = UserDetailsServiceAutoConfiguration.class)
@ConditionalWingsEnabled
public class TestingSlardarAutoConfiguration {

    @Bean
    public TestingLoginHandler testLoginHandler() {
        return new TestingLoginHandler();
    }

    @Bean
    public TestingWingsUserDetailsService testWingsUserDetailsService() {
        return new TestingWingsUserDetailsService();
    }

    @Bean
    public WingsAuthDetailsSource<DefaultWingsAuthDetails> wingsBindAuthnDetailsSource() {
        return (authType, request) -> new DefaultWingsAuthDetails();
    }
}
