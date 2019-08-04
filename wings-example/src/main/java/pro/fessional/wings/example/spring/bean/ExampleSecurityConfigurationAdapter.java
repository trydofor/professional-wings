package pro.fessional.wings.example.spring.bean;

import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * 自己配置SecurityConfiguration 要么禁止
 * @author trydofor
 * @since 2019-08-08
 */
@Configuration
@Order(SecurityProperties.BASIC_AUTH_ORDER - 6)
public class ExampleSecurityConfigurationAdapter extends WebSecurityConfigurerAdapter {

    @Override
    public void configure(HttpSecurity http) throws Exception {
        System.out.println("ExampleSecurityConfigurationAdapter.configure");
    }
}