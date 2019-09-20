package pro.fessional.wings.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * @author trydofor
 * @since 2019-06-26
 */
@SpringBootApplication
@EnableRedisHttpSession
public class WingsExampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(WingsExampleApplication.class, args);
    }
}
