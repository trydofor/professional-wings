package pro.fessional.wings.batrider.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author trydofor
 * @since 2019-06-25
 */
@SpringBootApplication
@ComponentScan({
        "pro.fessional.wings.batrider.controller",
        "pro.fessional.wings.batrider.contractor"
})
public class WingsBatriderTestApplication {
    public static void main(String[] args) {
        SpringApplication.run(WingsBatriderTestApplication.class, args);
    }
}
