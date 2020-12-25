package pro.fessional.wings.slardar.security;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

/**
 * @author trydofor
 * @since 2020-08-10
 */
public class SecurityConfigHelper extends AbstractHttpConfigurer<SecurityConfigHelper, HttpSecurity> {

    private HttpSecurity notDslHttp;
    private ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry notDslRegistry;

    private boolean flagAllCors = false;
    private boolean flagLogin = false;
    private boolean flagOAuth2 = false;
    private boolean flagSwagger2 = false;
    private boolean flagTest = false;

    public SecurityConfigHelper permitAllCors() throws Exception {
        if (notDslHttp != null) {
            permitAllCors(notDslHttp);
        } else {
            this.flagAllCors = true;
        }
        return this;
    }

    public SecurityConfigHelper permitLogin() {
        if (notDslRegistry != null) {
            permitLogin(notDslRegistry);
        } else {
            this.flagLogin = true;
        }
        return this;
    }

    public SecurityConfigHelper permitOAuth2() {
        if (notDslRegistry != null) {
            permitOAuth2(notDslRegistry);
        } else {
            this.flagOAuth2 = true;
        }
        return this;
    }

    public SecurityConfigHelper permitSwagger2() {
        if (notDslRegistry != null) {
            permitSwagger2(notDslRegistry);
        } else {
            this.flagSwagger2 = true;
        }
        return this;
    }

    public SecurityConfigHelper permitTest() {
        if (notDslRegistry != null) {
            permitTest(notDslRegistry);
        } else {
            this.flagTest = true;
        }
        return this;
    }

    @Override
    public void init(HttpSecurity http) throws Exception {
        if (notDslRegistry != null) return;

        if (flagAllCors) {
            permitAllCors(http);
        }
        ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry registry = http.authorizeRequests();
        if (flagLogin) {
            permitLogin(registry);
        }
        if (flagOAuth2) {
            permitOAuth2(registry);
        }
        if (flagSwagger2) {
            permitSwagger2(registry);
        }
        if (flagTest) {
            permitTest(registry);
        }
    }

    // ////

    /**
     * wrap HttpSecurity
     *
     * @param http HttpSecurity
     * @return SecurityConfigHelper
     * @throws Exception exception
     */
    public static SecurityConfigHelper wingsSecurityConfig(HttpSecurity http) throws Exception {
        SecurityConfigHelper helper = new SecurityConfigHelper();
        helper.notDslHttp = http;
        helper.notDslRegistry = http.authorizeRequests();
        return helper;
    }

    /**
     * <pre>
     * public class Config extends WebSecurityConfigurerAdapter {
     *     protected void configure(HttpSecurity http) throws Exception {
     *         http
     *             .apply(wingsSecurityConfig())
     *                 .permitAllCors(true)
     *                 .and()
     *             ...;
     *     }
     * }
     * </pre>
     *
     * @return customer dsl
     */
    public static SecurityConfigHelper wingsSecurityConfig() {
        return new SecurityConfigHelper();
    }

    // ////
    public static ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry permitAllCors(HttpSecurity http) throws Exception {
        // https://stackoverflow.com/questions/36968963
        // CorsConfiguration#applyPermitDefaultValues
        return http
                .cors().configurationSource(corsAllowAll())
                .and()
                .authorizeRequests();
    }

    public static ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry permitLogin(ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry registry) {
        registry.antMatchers(loginAntPaths()).permitAll()
        ;
        return registry;
    }

    public static ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry permitOAuth2(ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry registry) {
        registry.antMatchers(oauth2AntPaths()).permitAll()
        ;
        return registry;
    }

    public static ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry permitSwagger2(ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry registry) {
        registry.antMatchers(swagger2AntPaths()).permitAll()
        ;
        return registry;
    }

    public static ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry permitTest(ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry registry) {
        registry.antMatchers(testAntPaths()).permitAll()
        ;
        return registry;
    }

    // ////
    public static CorsConfigurationSource corsAllowAll() {
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

    public static String[] swagger2AntPaths() {
        return new String[]{"/v2/api-docs", "/configuration/**", "/swagger*/**", "/webjars/**"};
    }
}
