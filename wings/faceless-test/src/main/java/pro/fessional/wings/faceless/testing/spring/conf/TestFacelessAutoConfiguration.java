package pro.fessional.wings.faceless.testing.spring.conf;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import pro.fessional.wings.faceless.database.DataSourceContext;
import pro.fessional.wings.faceless.testing.database.TestingDatabaseHelper;
import pro.fessional.wings.silencer.spring.boot.ConditionalWingsEnabled;

/**
 * @author trydofor
 * @since 2023-10-23
 */
@AutoConfiguration
@ConditionalWingsEnabled
public class TestFacelessAutoConfiguration {

    @Bean
    public TestingDatabaseHelper wingsTestHelper(DataSourceContext context) {
        return new TestingDatabaseHelper(context.getCurrent(), context.getBackends());
    }

}
