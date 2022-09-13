package pro.fessional.wings.slardar.spring.conf;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

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

    public WingsHttpPermitConfigurer permitCorsAll() {
        this.flagCorsAll = true;
        return this;
    }

    public WingsHttpPermitConfigurer permitLogin() {
        this.flagLogin = true;
        return this;
    }

    public WingsHttpPermitConfigurer permitOAuth2() {
        this.flagOAuth2 = true;
        return this;
    }

    public WingsHttpPermitConfigurer permitSwagger() {
        this.flagSwagger = true;
        return this;
    }

    public WingsHttpPermitConfigurer permitTest() {
        this.flagTest = true;
        return this;
    }

    @Override
    public void init(HttpSecurity http) throws Exception {

        if (flagCorsAll) {
            http.cors().configurationSource(corsPermitAll());
        }

        ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry registry = http.authorizeRequests();
        if (flagLogin) {
            registry.antMatchers(loginAntPaths()).permitAll();
        }
        if (flagOAuth2) {
            registry.antMatchers(oauth2AntPaths()).permitAll();
        }
        if (flagSwagger) {
            registry.antMatchers(swaggerAntPaths()).permitAll();
        }
        if (flagTest) {
            registry.antMatchers(testAntPaths()).permitAll();
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

    public static String[] oauth2AntPaths() {
        return new String[]{"/oauth/**", "/error"};
    }

    public static String[] testAntPaths() {
        return new String[]{"/test/**"};
    }

    public static String[] loginAntPaths() {
        return new String[]{"/login", "/login/**", "/logout"};
    }

    public static String[] swaggerAntPaths() {
        return new String[]{"/swagger*/**", "/webjars/**"};
    }

}
