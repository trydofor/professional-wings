package pro.fessional.wings.faceless.testing.spring.conf;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import pro.fessional.wings.faceless.database.DataSourceContext;
import pro.fessional.wings.testing.database.WingsTestHelper;

/**
 * @author trydofor
 * @since 2023-10-23
 */
@AutoConfiguration
public class TestFacelessAutoConfiguration {

    @Bean
    public WingsTestHelper wingsTestHelper(DataSourceContext context) {
        return new WingsTestHelper(context.getCurrent(), context.getBackends());
    }

}
