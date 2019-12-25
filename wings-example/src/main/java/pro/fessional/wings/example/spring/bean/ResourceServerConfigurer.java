package pro.fessional.wings.example.spring.bean;

import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.RemoteTokenServices;
import pro.fessional.wings.slardar.spring.bean.WingsOAuth2xConfiguration;

/**
 * @author trydofor
 * @since 2019-12-01
 */
@Configuration
@EnableResourceServer
public class ResourceServerConfigurer extends ResourceServerConfigurerAdapter {

    @Setter(onMethod = @__({@Autowired}))
    private WingsOAuth2xConfiguration.Helper helper;

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        helper.configure(resources);
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        helper.permitAll(http)
              .antMatchers("/test/").permitAll()
              .antMatchers("/flywave-*.json").permitAll()
              .antMatchers("/index.html","/").permitAll()
              .antMatchers("/string.json", "/journal.json").permitAll()
              .antMatchers("/test.json").permitAll()
              .antMatchers("/sleep.html").permitAll()
              .antMatchers("/speed.html").permitAll()
              .antMatchers("/oauth2-login.html").permitAll()
              .antMatchers("/oauth2.html").hasAuthority("ROLE_ADMIN")
              .anyRequest().authenticated()
        ;
    }

    @Bean
    public RemoteTokenServices tokenService() {
        RemoteTokenServices tokenService = new RemoteTokenServices();
        tokenService.setCheckTokenEndpointUrl("http://localhost:8081/oauth/check_token");
        tokenService.setClientId("wings-slardar-id");
        tokenService.setClientSecret("wings-slardar-secret");
        return tokenService;
    }
}