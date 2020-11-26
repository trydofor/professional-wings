package pro.fessional.wings.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author trydofor
 * @since 2019-06-26
 */
@SpringBootApplication
@EnableScheduling
public class WingsExampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(WingsExampleApplication.class, args);
    }
}
