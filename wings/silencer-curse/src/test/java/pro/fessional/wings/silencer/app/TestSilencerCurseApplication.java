package pro.fessional.wings.silencer.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import pro.fessional.wings.silencer.app.conf.TestMergingProp;

/**
 * @author trydofor
 * @since 2019-07-20
 */
@SpringBootApplication
@EnableConfigurationProperties(TestMergingProp.class)
public class TestSilencerCurseApplication {

    public interface InnerFace {
    }

    @Bean
    public InnerFace innerFace() {
        return new InnerFace() {};
    }


    public static void main(String[] args) {
        SpringApplication.run(TestSilencerCurseApplication.class, args);
    }
}
