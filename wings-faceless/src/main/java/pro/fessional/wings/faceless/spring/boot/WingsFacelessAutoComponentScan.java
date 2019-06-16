package pro.fessional.wings.faceless.spring.boot;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author trydofor
 * @since 2019-06-01
 */
@ComponentScan({"pro.fessional.wings.faceless.spring.bean",
                "pro.fessional.wings.faceless.spring.conf",
                "pro.fessional.wings.faceless.service",
                "pro.fessional.wings.faceless.database"})

@Configuration
public class WingsFacelessAutoComponentScan {

}
