package pro.fessional.wings.silencer.spring.bean;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * @author trydofor
 * @since 2019-07-09
 */
@Configuration
@ConditionalOnProperty(prefix = "wings.security", name = "enabled", havingValue = "true")
@ConditionalOnClass(WebSecurityConfigurerAdapter.class)
public class WingsSecurityConfiguration {

//    @Configuration
//    public static class DisableSecurityConfigurationAdapter extends WebSecurityConfigurerAdapter {
//
//        @Override
//        public void configure(HttpSecurity http) throws Exception {
//            http
//                    .csrf().disable()
//                    .requestCache().disable()
//                    .formLogin().disable()
//                    .httpBasic().disable();
//        }
//    }
}
