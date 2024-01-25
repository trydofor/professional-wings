package pro.fessional.wings.testing.batrider.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author trydofor
 * @since 2019-06-25
 */
@SpringBootApplication
@ComponentScan({
        "pro.fessional.wings.testing.batrider.controller",
        "pro.fessional.wings.testing.batrider.contractor"
})
public class TestBatriderTestApplication {
    public static void main(String[] args) {
        SpringApplication.run(TestBatriderTestApplication.class, args);
    }
}
