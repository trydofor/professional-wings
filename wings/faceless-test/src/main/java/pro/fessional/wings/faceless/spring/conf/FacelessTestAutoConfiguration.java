package pro.fessional.wings.faceless.spring.conf;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import pro.fessional.wings.faceless.helper.WingsTestHelper;

/**
 * @author trydofor
 * @since 2023-10-23
 */
@AutoConfiguration
public class FacelessTestAutoConfiguration {

    @Bean
    public WingsTestHelper wingsTestHelper() {
        return new WingsTestHelper();
    }

}
