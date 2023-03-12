package pro.fessional.wings.faceless;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import pro.fessional.wings.faceless.database.autogen.DefaultSchema;
import pro.fessional.wings.faceless.service.TransactionalClauseService;

/**
 * @author trydofor
 * @since 2019-06-25
 */
@SpringBootApplication
@ComponentScan(basePackageClasses = {TransactionalClauseService.class, DefaultSchema.class})
public class WingsFacelessJooqApplication {
    public static void main(String[] args) {
        SpringApplication.run(WingsFacelessJooqApplication.class, args);
    }
}
