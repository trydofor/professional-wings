package pro.fessional.wings.slardar.app.conf;

import lombok.Setter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.web.SecurityFilterChain;
import pro.fessional.wings.slardar.security.handler.TestLoginHandler;


/**
 * Used by WingsSessionTest
 *
 * @author trydofor
 * @since 2019-12-01
 */
@Configuration(proxyBeanMethods = false)
public class TestSecurityConfiguration {

    private final static Log log = LogFactory.getLog(TestSecurityConfiguration.class);

    @Setter(onMethod_ = {@Autowired})
    private TestLoginHandler testLoginHandler;

    @Setter(onMethod_ = {@Autowired})
    private SessionRegistry sessionRegistry;

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
        http.authorizeHttpRequests(conf -> conf
                    .requestMatchers("/authed/*").authenticated()
            )
            .formLogin(conf -> conf
                    .loginPage("/user/login.json") // 401 page
                    .loginProcessingUrl("/user/login-proc.json") // handle by filter, no controller
                    .usernameParameter("username")
                    .passwordParameter("password")
                    .successHandler(testLoginHandler.loginSuccess)
                    .failureHandler(testLoginHandler.loginFailure)

            )
            .logout(conf -> conf
                    .logoutUrl("/user/logout.json")
                    .clearAuthentication(true)
                    .invalidateHttpSession(true)
                    .logoutSuccessHandler(testLoginHandler.logoutSuccess)
            )
            .sessionManagement(conf -> conf
                    .maximumSessions(10)
                    .sessionRegistry(sessionRegistry)

            )
//            .exceptionHandling(conf -> conf
//                    .accessDeniedHandler()
//            )
            .requestCache().disable()
            .csrf().disable();
        return http.build();
    }
}
