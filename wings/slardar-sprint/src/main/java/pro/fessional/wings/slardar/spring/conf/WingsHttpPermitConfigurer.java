package pro.fessional.wings.slardar.spring.conf;

import lombok.SneakyThrows;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationContext;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
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

    @Contract("->this")
    public WingsHttpPermitConfigurer permitLogin() {
        requestMatchers(SecurityConfigHelper.loginAntPaths()).permitAll();
        return this;
    }

    @Contract("->this")
    public WingsHttpPermitConfigurer permitOAuth2() {
        requestMatchers(SecurityConfigHelper.oauth2AntPaths()).permitAll();
        return this;
    }

    @Contract("->this")
    public WingsHttpPermitConfigurer permitSwagger() {
        requestMatchers(SecurityConfigHelper.swaggerAntPaths()).permitAll();
        return this;
    }

    @Contract("->this")
    public WingsHttpPermitConfigurer permitTest() {
        requestMatchers(SecurityConfigHelper.testAntPaths()).permitAll();
        return this;
    }

    /**
     * user mvcMatcher if exist MvcRequestMatcher.Builder, otherwise antMatcher
     *
     * @see SecurityConfigHelper#requestMatchers(MvcRequestMatcher.Builder, String...)
     */
    @SneakyThrows
    @NotNull
    public AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizedUrl requestMatchers(String... paths) {
        final HttpSecurity http = getBuilder();
        final ApplicationContext ctx = http.getSharedObject(ApplicationContext.class);
        final MvcRequestMatcher.Builder mat = ctx == null ? null : ctx.getBean(MvcRequestMatcher.Builder.class);
        return http.authorizeHttpRequests().requestMatchers(SecurityConfigHelper.requestMatchers(mat, paths));
    }
}
