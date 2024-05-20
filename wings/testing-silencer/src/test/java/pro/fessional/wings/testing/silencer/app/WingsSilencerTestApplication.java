package pro.fessional.wings.testing.silencer.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import pro.fessional.wings.testing.silencer.TestingPropertyHelper;

/**
 * @author trydofor
 * @since 2019-07-20
 */
@SpringBootApplication
public class WingsSilencerTestApplication {
    public static void main(String[] args) {
        TestingPropertyHelper.autoSetWingsRootDir();
        SpringApplication.run(WingsSilencerTestApplication.class, args);
    }
}
