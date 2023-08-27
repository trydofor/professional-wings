package pro.fessional.wings.slardar.spring.conf;

import lombok.val;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationContext;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import pro.fessional.wings.slardar.spring.help.SecurityConfigHelper;

/**
 * @author trydofor
 * @since 2021-02-07
 */
public class WingsHttpPermitConfigurer extends AbstractHttpConfigurer<WingsHttpPermitConfigurer, HttpSecurity> {
    private boolean flagCorsAll = false;
    private boolean flagLogin = false;
    private boolean flagOAuth2 = false;
    private boolean flagSwagger = false;
    private boolean flagTest = false;

    @Contract("->this")
    public WingsHttpPermitConfigurer permitCorsAll() {
        this.flagCorsAll = true;
        return this;
    }

    @Contract("->this")
    public WingsHttpPermitConfigurer permitLogin() {
        this.flagLogin = true;
        return this;
    }

    @Contract("->this")
    public WingsHttpPermitConfigurer permitOAuth2() {
        this.flagOAuth2 = true;
        return this;
    }

    @Contract("->this")
    public WingsHttpPermitConfigurer permitSwagger() {
        this.flagSwagger = true;
        return this;
    }

    @Contract("->this")
    public WingsHttpPermitConfigurer permitTest() {
        this.flagTest = true;
        return this;
    }

    @Override
    public void init(HttpSecurity http) throws Exception {
        if (flagCorsAll) {
            http.cors().configurationSource(corsPermitAll());
        }

        ApplicationContext ctx = http.getSharedObject(ApplicationContext.class);
        final MvcRequestMatcher.Builder mat = ctx == null ? null : ctx.getBean(MvcRequestMatcher.Builder.class);

        val registry = http.authorizeHttpRequests();
        if (flagLogin) {
            registry.requestMatchers(SecurityConfigHelper.requestMatchers(mat, loginAntPaths())).permitAll();
        }
        if (flagOAuth2) {
            registry.requestMatchers(SecurityConfigHelper.requestMatchers(mat, oauth2AntPaths())).permitAll();
        }
        if (flagSwagger) {
            registry.requestMatchers(SecurityConfigHelper.requestMatchers(mat, swaggerAntPaths())).permitAll();
        }
        if (flagTest) {
            registry.requestMatchers(SecurityConfigHelper.requestMatchers(mat, testAntPaths())).permitAll();
        }
    }

    // ////
    public static CorsConfigurationSource corsPermitAll() {
        return request -> {
            CorsConfiguration conf = new CorsConfiguration();
            conf.addAllowedHeader("*");
            conf.addAllowedOrigin("*");
            conf.addAllowedMethod("*");
            conf.setMaxAge(1800L);
            return conf;
        };
    }

    @NotNull
    public static String[] oauth2AntPaths() {
        return new String[]{"/oauth/**", "/error"};
    }

    @NotNull
    public static String[] testAntPaths() {
        return new String[]{"/test/**"};
    }

    @NotNull
    public static String[] loginAntPaths() {
        return new String[]{"/login", "/login/**", "/logout"};
    }

    @NotNull
    public static String[] swaggerAntPaths() {
        return new String[]{"/swagger*/**", "/webjars/**"};
    }
}
