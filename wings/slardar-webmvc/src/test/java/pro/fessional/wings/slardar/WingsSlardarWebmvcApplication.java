package pro.fessional.wings.slardar;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.metrics.buffering.BufferingApplicationStartup;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author trydofor
 * @since 2019-07-20
 */
@SpringBootApplication
@ComponentScan({"pro.fessional.wings.slardar.service",
                "pro.fessional.wings.slardar.controller",})
public class WingsSlardarWebmvcApplication {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(WingsSlardarWebmvcApplication.class);
        app.setApplicationStartup(new BufferingApplicationStartup(4096));
        app.run(args);
    }
}
