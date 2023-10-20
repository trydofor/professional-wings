package pro.fessional.wings.warlock;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

/**
 * @author trydofor
 * @since 2019-07-20
 */
@SpringBootApplication
@EnableMethodSecurity(securedEnabled = true)
public class WingsWarlockShadowApplication {
    public static void main(String[] args) {
        SpringApplication.run(WingsWarlockShadowApplication.class, args);
    }
}
