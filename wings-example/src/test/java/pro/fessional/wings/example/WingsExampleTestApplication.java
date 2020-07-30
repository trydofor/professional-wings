package pro.fessional.wings.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.core.GrantedAuthorityDefaults;

/**
 * @author trydofor
 * @since 2019-06-26
 */
@SpringBootApplication
@EnableGlobalMethodSecurity(securedEnabled = true)
public class WingsExampleTestApplication {

    @Bean
    public GrantedAuthorityDefaults grantedAuthorityDefaults() {
        return new GrantedAuthorityDefaults("");
    }

    public static void main(String[] args) {
        SpringApplication.run(WingsExampleTestApplication.class, args);
    }
}
