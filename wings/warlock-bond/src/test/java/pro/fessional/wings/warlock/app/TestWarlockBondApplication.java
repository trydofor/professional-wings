package pro.fessional.wings.warlock.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import pro.fessional.wings.testing.silencer.TestingPropertyHelper;

/**
 * @author trydofor
 * @since 2019-07-20
 */
@SpringBootApplication
public class TestWarlockBondApplication {

    public static void main(String[] args) {
        TestingPropertyHelper.autoSetWingsRootDir();
        SpringApplication.run(TestWarlockBondApplication.class, args);
    }
}
