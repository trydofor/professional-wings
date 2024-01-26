package pro.fessional.wings.silencer.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import pro.fessional.wings.silencer.app.conf.TestMergingProp;

/**
 * @author trydofor
 * @since 2019-07-20
 */
@SpringBootApplication
@EnableConfigurationProperties(TestMergingProp.class)
public class TestSilencerApplication {

    public static void main(String[] args) {
        SpringApplication.run(TestSilencerApplication.class, args);
    }
}
