package pro.fessional.wings.warlock.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

/**
 * @author trydofor
 * @since 2019-07-20
 */
@SpringBootApplication
@EnableMethodSecurity(securedEnabled = true)
public class TestWarlockShadowApplication {
    public static void main(String[] args) {
        SpringApplication.run(TestWarlockShadowApplication.class, args);
    }
}
