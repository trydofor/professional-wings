package pro.fessional.wings.slardar.app.conf;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.RequestCacheConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import pro.fessional.mirana.data.Null;
import pro.fessional.wings.slardar.spring.conf.WingsBindLoginConfigurer;
import pro.fessional.wings.slardar.spring.conf.WingsHttpPermitConfigurer;
import pro.fessional.wings.slardar.spring.help.SecurityConfigHelper;


/**
 * @author trydofor
 * @since 2019-12-01
 */
@Configuration(proxyBeanMethods = false)
public class TestSecurityConfiguration {

    private final static Log log = LogFactory.getLog(TestSecurityConfiguration.class);

    /**
     * The URL paths provided by the framework are
     * /oauth/authorize (the authorization endpoint),
     * /oauth/token (the token endpoint),
     * /oauth/confirm_access (user posts approval for grants here),
     * /oauth/error (used to render errors in the authorization server),
     * /oauth/check_token (used by Resource Servers to decode access tokens), and
     * /oauth/token_key (exposes public key for token verification if using JWT tokens).
     * <p>
     * Note: if your Authorization Server is also a Resource Server then
     * there is another security filter chain with lower priority controlling the API resources.
     * Fo those requests to be protected by access tokens you need their paths
     * not to be matched by the ones in the main user-facing filter chain,
     * so be sure to include a request matcher that picks out
     * only non-API resources in the WebSecurityConfigurer above.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        log.info("config HttpSecurity");
        http.with(new WingsHttpPermitConfigurer(), conf -> conf
                    .permitCorsAll()
                    .permitTest()
            )
            .with(new WingsBindLoginConfigurer(), conf -> conf
                    .loginPage("/user/login.json")
                    .loginProcessingUrl("/*/login-proc.json")
                    .usernameParameter("username")
                    .passwordParameter("password")
                    .successHandler((request, response, authentication) -> log.info("successHandler"))
                    .failureHandler((request, response, exception) -> log.info("failureHandler"))
                    .bindAuthTypeToEnums("user", Null.Enm)
            )
            .authorizeHttpRequests(conf -> conf
                    .requestMatchers(new AntPathRequestMatcher("/authed/*", null)).authenticated()
            )
//            .formLogin(conf -> conf
//                    .loginPage("/user/login.json")
//                    .loginProcessingUrl("/user/login-proc.json")
//                    .usernameParameter("username")
//                    .passwordParameter("password")
//                    .successHandler(testLoginHandler.loginSuccess)
//                    .failureHandler(testLoginHandler.loginFailure)
//
//            )
            .logout(conf -> conf
                    .logoutUrl("/user/logout.json")
                    .clearAuthentication(true)
                    .invalidateHttpSession(true)
                    .logoutSuccessHandler((request, response, authentication) -> log.info("logoutSuccessHandler"))
            )
//            .exceptionHandling(conf -> conf
//                    .accessDeniedHandler()
//            )
            .requestCache(RequestCacheConfigurer::disable)
            .csrf(AbstractHttpConfigurer::disable);
        return http.build();
    }
}
