package pro.fessional.wings.slardar.spring.conf;

import lombok.SneakyThrows;
import org.jetbrains.annotations.Contract;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import pro.fessional.wings.slardar.spring.help.SecurityConfigHelper;

/**
 * @author trydofor
 * @since 2021-02-07
 */
public class WingsHttpPermitConfigurer extends AbstractHttpConfigurer<WingsHttpPermitConfigurer, HttpSecurity> {
    @SneakyThrows
    @Contract("->this")
    public WingsHttpPermitConfigurer permitCorsAll() {
        getBuilder().cors(conf -> conf.configurationSource(SecurityConfigHelper.corsPermitAll()));
        return this;
    }

    @SneakyThrows
    @Contract("->this")
    public WingsHttpPermitConfigurer permitLogin() {
        getBuilder().authorizeHttpRequests(
                c -> c.requestMatchers(SecurityConfigHelper.loginAntPaths()).permitAll()
        );
        return this;
    }

    @SneakyThrows
    @Contract("->this")
    public WingsHttpPermitConfigurer permitOAuth2() {
        getBuilder().authorizeHttpRequests(
                c -> c.requestMatchers(SecurityConfigHelper.oauth2AntPaths()).permitAll()
        );
        return this;
    }

    @SneakyThrows
    @Contract("->this")
    public WingsHttpPermitConfigurer permitSwagger() {
        getBuilder().authorizeHttpRequests(
                c -> c.requestMatchers(SecurityConfigHelper.swaggerAntPaths()).permitAll()
        );
        return this;
    }

    @SneakyThrows
    @Contract("->this")
    public WingsHttpPermitConfigurer permitTest() {
        getBuilder().authorizeHttpRequests(
                c -> c.requestMatchers(SecurityConfigHelper.testAntPaths()).permitAll()
        );
        return this;
    }
}
