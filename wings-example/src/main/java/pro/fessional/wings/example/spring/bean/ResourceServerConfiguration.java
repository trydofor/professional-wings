package pro.fessional.wings.example.spring.bean;

import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import pro.fessional.wings.example.enums.auto.Authority;
import pro.fessional.wings.slardar.spring.bean.WingsOAuth2xConfiguration;

/**
 * @author trydofor
 * @since 2019-12-01
 */
@Configuration
@EnableResourceServer
public class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {

    @Setter(onMethod = @__({@Autowired}))
    private WingsOAuth2xConfiguration.Helper helper;

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) {
        helper.configure(resources);
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        helper.permitAll(http)
              .antMatchers("/favicon.ico").permitAll()
              .antMatchers("/login.json", "/logout.json").permitAll()
              .antMatchers("/index.html", "/").permitAll()
              .antMatchers("/test/**/*").permitAll()
              .antMatchers("/user/create.json")
              .hasAnyAuthority(Authority.$CREATE_ROLE, Authority.$CREATE_USER)
              .anyRequest().authenticated()
        ;
    }
}