package pro.fessional.wings.oracle.spring.boot;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author trydofor
 * @since 2019-06-01
 */
@ComponentScan({"pro.fessional.wings.oracle.spring.bean",
                "pro.fessional.wings.oracle.spring.conf",
                "pro.fessional.wings.oracle.service",
                "pro.fessional.wings.oracle.database"})

@Configuration
public class WingsOracleAutoComponentScan {

}
