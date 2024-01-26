package pro.fessional.wings.slardar.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.metrics.buffering.BufferingApplicationStartup;
import pro.fessional.wings.faceless.spring.conf.FacelessAutoConfiguration;

/**
 * @author trydofor
 * @since 2019-07-20
 */
@SpringBootApplication(exclude = {
        FacelessAutoConfiguration.class,
        DataSourceAutoConfiguration.class
})
public class TestSlardarHazelCachingApplication {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(TestSlardarHazelCachingApplication.class);
        app.setApplicationStartup(new BufferingApplicationStartup(4096));
        app.run(args);
    }
}
