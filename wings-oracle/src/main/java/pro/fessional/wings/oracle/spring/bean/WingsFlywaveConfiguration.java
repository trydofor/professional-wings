package pro.fessional.wings.oracle.spring.bean;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pro.fessional.wings.oracle.flywave.FlywaveDataSources;
import pro.fessional.wings.oracle.flywave.SchemaDefinitionLoader;
import pro.fessional.wings.oracle.flywave.impl.MysqlDefinitionLoader;
import pro.fessional.wings.oracle.spring.conf.WingsFlywaveSqlProperties;
import pro.fessional.wings.oracle.flywave.impl.MySqlStatementParser;
import pro.fessional.wings.oracle.flywave.impl.DefaultVersionManger;
import pro.fessional.wings.oracle.flywave.SqlSegmentProcessor;
import pro.fessional.wings.oracle.flywave.SqlStatementParser;

/**
 * @author trydofor
 * @since 2019-06-01
 */
@Configuration
@ConditionalOnProperty(prefix = "wings.flywave", name = "enabled", havingValue = "true", matchIfMissing = false)
public class WingsFlywaveConfiguration {

    @Bean
    public DefaultVersionManger schemaVersionManger(FlywaveDataSources sources,
                                                    SqlSegmentProcessor segmentParser,
                                                    SqlStatementParser statementParser,
                                                    SchemaDefinitionLoader schemaDefinitionLoader) {
        return new DefaultVersionManger(sources, segmentParser, statementParser, schemaDefinitionLoader);
    }

    @Bean
    public SqlStatementParser sqlStatementParser(WingsFlywaveSqlProperties properties) {
        if (properties.getDialect().equalsIgnoreCase("mysql")) {
            return new MySqlStatementParser();
        } else {
            throw new IllegalArgumentException("only support mysql");
        }
    }

    @Bean
    public SqlSegmentProcessor sqlSegmentParser(WingsFlywaveSqlProperties properties) {
        if (properties.getDialect().equalsIgnoreCase("mysql")) {
            return new SqlSegmentProcessor(properties.getCommentSingle(),
                    properties.getCommentMultiple(),
                    properties.getDelimiterDefault(),
                    properties.getDelimiterCommand());
        } else {
            throw new IllegalArgumentException("only support mysql");
        }
    }

    @Bean
    public SchemaDefinitionLoader schemaDefinitionLoader(WingsFlywaveSqlProperties properties) {
        if (properties.getDialect().equalsIgnoreCase("mysql")) {
            return new MysqlDefinitionLoader();
        } else {
            throw new IllegalArgumentException("only support mysql");
        }
    }

}
