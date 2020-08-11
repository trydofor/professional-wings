package pro.fessional.wings.slardar.security;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

/**
 * @author trydofor
 * @since 2020-08-10
 */
public class SecurityConfigHelper {

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
