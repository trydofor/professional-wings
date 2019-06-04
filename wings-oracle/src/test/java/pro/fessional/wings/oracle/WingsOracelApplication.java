package pro.fessional.wings.oracle;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import pro.fessional.mirana.id.LightIdProvider;

/**
 * @author trydofor
 * @since 2019-05-23
 */
@SpringBootApplication
public class WingsOracelApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(WingsOracelApplication.class, args);
        LightIdProvider bean = context.getBean(LightIdProvider.class);
        System.out.println(bean);
        System.out.println(context.isActive());
    }
}
