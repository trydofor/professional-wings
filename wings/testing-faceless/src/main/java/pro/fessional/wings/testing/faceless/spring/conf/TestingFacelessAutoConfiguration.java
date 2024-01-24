package pro.fessional.wings.testing.faceless.spring.conf;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import pro.fessional.wings.faceless.database.DataSourceContext;
import pro.fessional.wings.silencer.spring.boot.ConditionalWingsEnabled;
import pro.fessional.wings.testing.faceless.database.TestingDatabaseHelper;

/**
 * @author trydofor
 * @since 2023-10-23
 */
@AutoConfiguration
@ConditionalWingsEnabled
public class TestingFacelessAutoConfiguration {

    @Bean
    public TestingDatabaseHelper wingsTestHelper(DataSourceContext context) {
        return new TestingDatabaseHelper(context);
    }

}
