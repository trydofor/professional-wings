package pro.fessional.wings.silencer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * @author trydofor
 * @since 2019-07-20
 */
@SpringBootApplication
public class WingsSilencerCurseApplication {

    public interface InnerFace {
    }

    @Bean
    public InnerFace innerFace() {
        return new InnerFace() {};
    }


    public static void main(String[] args) {
        SpringApplication.run(WingsSilencerCurseApplication.class, args);
    }
}
