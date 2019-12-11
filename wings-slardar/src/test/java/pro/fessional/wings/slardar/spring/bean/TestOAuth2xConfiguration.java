package pro.fessional.wings.slardar.spring.bean;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import pro.fessional.wings.slardar.service.TestUserDetailsService;

/**
 * @author trydofor
 * @since 2019-11-14
 */
@Configuration
public class TestOAuth2xConfiguration {

    @Bean
    public UserDetailsService testUserDetailsService() {
        return new TestUserDetailsService();
    }


    @Configuration
    @EnableWebSecurity
    public static class WebSecurityConf extends WebSecurityConfigurerAdapter {

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
            http.httpBasic().and()
                .csrf().disable()
            ;
        }

        // need AuthenticationManager Bean
        // password grants are switched on by injecting an AuthenticationManager
        @Bean(BeanIds.AUTHENTICATION_MANAGER)
        @Override
        public AuthenticationManager authenticationManagerBean() throws Exception {
            return super.authenticationManagerBean();
        }
    }

    //
    @Configuration
    @EnableAuthorizationServer
    @RequiredArgsConstructor
    public static class AuthServerConf extends AuthorizationServerConfigurerAdapter {
        private final WingsOAuth2xConfiguration.Helper helper;

        @Override
        public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
            helper.configure(clients);
        }

        @Override
        public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
            helper.configure(endpoints);
        }

        @Override
        public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
            helper.configure(security);
        }
    }

    //
    @Configuration
    @EnableResourceServer
    @RequiredArgsConstructor
    public static class ResourceServerConf extends ResourceServerConfigurerAdapter {

        private final WingsOAuth2xConfiguration.Helper helper;

        @Override
        public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
            helper.configure(resources);
        }

        @Override
        public void configure(HttpSecurity http) throws Exception {
            // https://stackoverflow.com/questions/36968963
            helper.permitAll(http)
                  .antMatchers("/test/**").permitAll()
                  .antMatchers("/user.html").hasAuthority("ROLE_USER")
                  .antMatchers("/admin.html").hasAuthority("ROLE_ADMIN")
                  .anyRequest().authenticated()
            ;
        }
    }
}
