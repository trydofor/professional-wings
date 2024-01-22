package pro.fessional.wings.slardar.testing.spring.conf;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import pro.fessional.wings.silencer.spring.boot.ConditionalWingsEnabled;
import pro.fessional.wings.slardar.security.WingsAuthDetailsSource;
import pro.fessional.wings.slardar.security.impl.DefaultWingsAuthDetails;
import pro.fessional.wings.slardar.testing.security.handler.TestLoginHandler;
import pro.fessional.wings.slardar.testing.service.TestWingsUserDetailsService;

/**
 * @author trydofor
 * @since 2022-01-29
 */
@AutoConfiguration(before = UserDetailsServiceAutoConfiguration.class)
@ConditionalWingsEnabled
public class TestSlardarAutoConfiguration {

    @Bean
    public TestLoginHandler testLoginHandler() {
        return new TestLoginHandler();
    }

    @Bean
    public TestWingsUserDetailsService testWingsUserDetailsService() {
        return new TestWingsUserDetailsService();
    }

    @Bean
    public WingsAuthDetailsSource<DefaultWingsAuthDetails> wingsBindAuthnDetailsSource() {
        return (authType, request) -> new DefaultWingsAuthDetails();
    }
}
