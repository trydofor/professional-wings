package pro.fessional.wings.example.spring.bean;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * @author trydofor
 * @since 2019-12-01
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

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
    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.httpBasic()
            .and()
            .csrf()
            .disable()
        ;
    }

    // need AuthenticationManager Bean
    // password grants are switched on by injecting an AuthenticationManager
    @Bean(BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        //
        return super.authenticationManagerBean();
    }
}
