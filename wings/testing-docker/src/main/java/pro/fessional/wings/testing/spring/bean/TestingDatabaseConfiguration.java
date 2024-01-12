package pro.fessional.wings.testing.spring.bean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pro.fessional.wings.silencer.spring.boot.ConditionalWingsEnabled;
import pro.fessional.wings.testing.database.TestingDataSource;

/*
 * @author trydofor
 * @since 2024-01-02
 * @see DataSourceAutoConfiguration
 * @see
 */
@Configuration(proxyBeanMethods = false)
@ConditionalWingsEnabled
public class TestingDatabaseConfiguration {

    private static final Log log = LogFactory.getLog(TestingDatabaseConfiguration.class);


    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.hikari")
    public TestingDataSource dataSource(DataSourceProperties prop) {
        log.info("TestingDatabase provide dataSource instead of docker compose");
        return new TestingDataSource(
                prop.determineDriverClassName(),
                prop.determineUrl(),
                prop.determineUsername(),
                prop.determinePassword()
        );
    }
}
