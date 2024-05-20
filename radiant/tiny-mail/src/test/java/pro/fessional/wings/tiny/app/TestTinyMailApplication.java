package pro.fessional.wings.tiny.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import pro.fessional.wings.testing.silencer.TestingPropertyHelper;

/**
 * @author trydofor
 * @since 2019-07-20
 */
@SpringBootApplication
public class TestTinyMailApplication {

    public static void main(String[] args) {
        TestingPropertyHelper.autoSetWingsRootDir();
        SpringApplication.run(TestTinyMailApplication.class, args);
    }

}
