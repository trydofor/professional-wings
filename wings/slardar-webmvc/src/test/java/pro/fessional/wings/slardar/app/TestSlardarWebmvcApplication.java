package pro.fessional.wings.slardar.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.metrics.buffering.BufferingApplicationStartup;
import pro.fessional.wings.testing.silencer.TestingPropertyHelper;

/**
 * @author trydofor
 * @since 2019-07-20
 */
@SpringBootApplication
public class TestSlardarWebmvcApplication {

    public static void main(String[] args) {
        TestingPropertyHelper.autoSetWingsRootDir();
        SpringApplication app = new SpringApplication(TestSlardarWebmvcApplication.class);
        app.setApplicationStartup(new BufferingApplicationStartup(4096));
        app.run(args);
    }
}
