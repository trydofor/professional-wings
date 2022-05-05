package pro.fessional.wings.slardar.spring.bean;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pro.fessional.wings.slardar.security.WingsAuthDetailsSource;
import pro.fessional.wings.slardar.security.handler.TestLoginHandler;
import pro.fessional.wings.slardar.security.impl.DefaultWingsAuthDetails;

/**
 * @author trydofor
 * @since 2022-01-29
 */
@Configuration(proxyBeanMethods = false)
public class TestSecurityBeans {

    @Bean
    public TestLoginHandler testLoginHandler() {
        return new TestLoginHandler();
    }

    @Bean
    public WingsAuthDetailsSource<DefaultWingsAuthDetails> wingsBindAuthnDetailsSource() {
        return (authType, request) -> new DefaultWingsAuthDetails();
    }
}
