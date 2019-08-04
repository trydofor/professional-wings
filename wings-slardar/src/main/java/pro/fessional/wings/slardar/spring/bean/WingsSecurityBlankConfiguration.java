package pro.fessional.wings.slardar.spring.bean;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * @author trydofor
 * @since 2019-07-09
 */
@Configuration
@ConditionalOnProperty(prefix = "spring.wings.security.blank", name = "enabled", havingValue = "true")
public class WingsSecurityBlankConfiguration {

    @Configuration
    @Order(SecurityProperties.BASIC_AUTH_ORDER - 7)
    public class DisableSecurityConfigurationAdapter extends WebSecurityConfigurerAdapter {

        @Override
        public void configure(HttpSecurity http) throws Exception {
            http
                    .csrf().disable()
                    .requestCache().disable()
                    .formLogin().disable()
                    .httpBasic().disable();
        }
    }
}
