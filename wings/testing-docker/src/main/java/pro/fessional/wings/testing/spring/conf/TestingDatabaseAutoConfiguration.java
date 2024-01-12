package pro.fessional.wings.testing.spring.conf;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Import;
import pro.fessional.wings.silencer.spring.boot.ConditionalWingsEnabled;
import pro.fessional.wings.testing.spring.bean.TestingDatabaseConfiguration;

/**
 * @author trydofor
 * @since 2019-07-11
 */
@AutoConfiguration(before = DataSourceAutoConfiguration.class)
@ConditionalWingsEnabled
@Import(TestingDatabaseConfiguration.class)
public class TestingDatabaseAutoConfiguration {
}
