package pro.fessional.wings.testing.spring.bean;

import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pro.fessional.wings.silencer.spring.boot.ConditionalWingsEnabled;

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
    HikariDataSource dataSource(DataSourceProperties properties) {
        log.info("TestingDatabase provide dataSource instead of docker compose");
        return DataSourceBuilder
                .create(properties.getClassLoader())
                .type(HikariDataSource.class)
                .driverClassName(properties.determineDriverClassName())
                .url(properties.determineUrl())
                .username(properties.determineUsername())
                .password(properties.determinePassword())
                .build();
    }
}
