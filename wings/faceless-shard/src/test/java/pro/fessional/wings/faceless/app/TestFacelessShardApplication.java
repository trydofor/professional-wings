package pro.fessional.wings.faceless.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import pro.fessional.wings.testing.silencer.TestingPropertyHelper;

/**
 * @author trydofor
 * @since 2019-06-25
 */
@SpringBootApplication
public class TestFacelessShardApplication {
    public static void main(String[] args) {
        TestingPropertyHelper.autoSetWingsRootDir();
        SpringApplication.run(TestFacelessShardApplication.class, args);
    }
}
