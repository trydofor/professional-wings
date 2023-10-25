package pro.fessional.wings.silencer.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import pro.fessional.wings.silencer.app.conf.MergingProp;

/**
 * @author trydofor
 * @since 2019-07-20
 */
@SpringBootApplication
@EnableConfigurationProperties(MergingProp.class)
public class WingsSilencerApplication {

    public static void main(String[] args) {
        SpringApplication.run(WingsSilencerApplication.class, args);
    }
}
