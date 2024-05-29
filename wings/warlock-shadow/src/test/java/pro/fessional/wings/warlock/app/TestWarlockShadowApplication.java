package pro.fessional.wings.warlock.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import pro.fessional.wings.testing.silencer.TestingPropertyHelper;

/**
 * @author trydofor
 * @since 2019-07-20
 */
@SpringBootApplication
@EnableMethodSecurity(securedEnabled = true)
public class TestWarlockShadowApplication {
    public static void main(String[] args) {
        TestingPropertyHelper.autoSetWingsRootDir();
        SpringApplication.run(TestWarlockShadowApplication.class, args);
    }
}
