package pro.fessional.wings.faceless.spring.bean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pro.fessional.wings.faceless.database.DataSourceContext;
import pro.fessional.wings.faceless.flywave.SchemaDefinitionLoader;
import pro.fessional.wings.faceless.flywave.SchemaFulldumpManager;
import pro.fessional.wings.faceless.flywave.SchemaJournalManager;
import pro.fessional.wings.faceless.flywave.SchemaRevisionManager.AskType;
import pro.fessional.wings.faceless.flywave.SchemaShardingManager;
import pro.fessional.wings.faceless.flywave.SqlSegmentProcessor;
import pro.fessional.wings.faceless.flywave.SqlStatementParser;
import pro.fessional.wings.faceless.flywave.impl.DefaultRevisionManager;
import pro.fessional.wings.faceless.flywave.impl.MySqlStatementParser;
import pro.fessional.wings.faceless.flywave.impl.MysqlDefinitionLoader;
import pro.fessional.wings.faceless.spring.conf.WingsFlywaveSqlProperties;
import pro.fessional.wings.faceless.spring.conf.WingsFlywaveVerProperties;

/**
 * @author trydofor
 * @since 2019-06-01
 */
@Configuration
@ConditionalOnClass(name = "pro.fessional.wings.faceless.database.DataSourceContext")
@ConditionalOnProperty(name = "spring.wings.faceless.flywave.enabled", havingValue = "true")
public class WingsFlywaveConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(WingsFlywaveConfiguration.class);

    @Bean
    public SchemaJournalManager schemaJournalManager(
            DataSourceContext facelessDs,
            SqlStatementParser statementParser,
            SchemaDefinitionLoader schemaDefinitionLoader,
            WingsFlywaveVerProperties properties) {

        SchemaJournalManager.JournalDdl ddl = new SchemaJournalManager.JournalDdl(
                properties.getJournalUpdate(),
                properties.getTriggerUpdate(),
                properties.getJournalDelete(),
                properties.getTriggerDelete()
        );
        logger.info("config schemaJournalManager");
        return new SchemaJournalManager(facelessDs.getPlains(), statementParser, schemaDefinitionLoader, ddl, properties.getSchemaJournalTable());
    }

    @Bean
    public DefaultRevisionManager schemaVersionManger(
            DataSourceContext sources,
            SqlStatementParser statementParser,
            SqlSegmentProcessor segmentProcessor,
            SchemaDefinitionLoader schemaDefinitionLoader,
            WingsFlywaveVerProperties properties) {
        DefaultRevisionManager revisionManager = new DefaultRevisionManager(
                sources.getPlains(), sources.getSharding(),
                statementParser, segmentProcessor, schemaDefinitionLoader,
                properties.getSchemaVersionTable());
        revisionManager.confirmAsk(AskType.Mark, properties.isAskMark());
        revisionManager.confirmAsk(AskType.Undo, properties.isAskUndo());
        revisionManager.confirmAsk(AskType.Drop, properties.isAskDrop());
        for (String s : properties.getDropReg()) {
            revisionManager.addDropRegexp(s);
        }
        logger.info("config schemaVersionManger");
        return revisionManager;
    }

    @Bean
    public SchemaShardingManager schemaShardingManager(
            DataSourceContext sources,
            SqlStatementParser statementParser,
            SchemaDefinitionLoader schemaDefinitionLoader) {
        logger.info("config schemaShardingManager");
        return new SchemaShardingManager(sources.getPlains(), sources.getSharding(),
                statementParser, schemaDefinitionLoader);
    }

    @Bean
    public SchemaFulldumpManager schemaFulldumpManager(
            SqlStatementParser statementParser,
            SchemaDefinitionLoader schemaDefinitionLoader) {
        logger.info("config schemaFulldumpManager");
        return new SchemaFulldumpManager(statementParser, schemaDefinitionLoader);
    }

    @Bean
    public SqlStatementParser sqlStatementParser(WingsFlywaveSqlProperties conf) {
        if ("mysql".equalsIgnoreCase(conf.getDialect())) {
            return new MySqlStatementParser();
        } else {
            throw new IllegalArgumentException("only support mysql");
        }
    }

    @Bean
    public SqlSegmentProcessor sqlSegmentParser(WingsFlywaveSqlProperties conf) {
        if ("mysql".equalsIgnoreCase(conf.getDialect())) {
            return new SqlSegmentProcessor(conf.getCommentSingle(),
                    conf.getCommentMultiple(),
                    conf.getDelimiterDefault(),
                    conf.getDelimiterCommand());
        } else {
            throw new IllegalArgumentException("only support mysql");
        }
    }

    @Bean
    public SchemaDefinitionLoader schemaDefinitionLoader(WingsFlywaveSqlProperties conf) {
        if ("mysql".equalsIgnoreCase(conf.getDialect())) {
            return new MysqlDefinitionLoader();
        } else {
            throw new IllegalArgumentException("only support mysql");
        }
    }

    @Bean
    @ConfigurationProperties("wings.faceless.flywave.sql")
    public WingsFlywaveSqlProperties sqlProperties() {
        return new WingsFlywaveSqlProperties();
    }

    @Bean
    @ConfigurationProperties("wings.faceless.flywave.ver")
    public WingsFlywaveVerProperties verProperties() {
        return new WingsFlywaveVerProperties();
    }
}
