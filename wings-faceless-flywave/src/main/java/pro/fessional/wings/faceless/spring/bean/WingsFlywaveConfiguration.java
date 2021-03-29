package pro.fessional.wings.faceless.spring.bean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
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
import pro.fessional.wings.faceless.spring.prop.FlywaveEnabledProp;
import pro.fessional.wings.faceless.spring.prop.FlywaveSqlProp;
import pro.fessional.wings.faceless.spring.prop.FlywaveVerProp;

import static pro.fessional.wings.faceless.flywave.SchemaJournalManager.JournalDdl;

/**
 * @author trydofor
 * @since 2019-06-01
 */
@Configuration
@ConditionalOnClass(name = "pro.fessional.wings.faceless.database.DataSourceContext")
@ConditionalOnProperty(name = FlywaveEnabledProp.Key$module, havingValue = "true")
public class WingsFlywaveConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(WingsFlywaveConfiguration.class);

    @Bean
    public SchemaJournalManager schemaJournalManager(
            DataSourceContext facelessDs,
            SqlStatementParser statementParser,
            SchemaDefinitionLoader schemaDefinitionLoader,
            FlywaveVerProp properties) {

        JournalDdl ddl = new JournalDdl(
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
            FlywaveVerProp properties) {
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
    public SqlStatementParser sqlStatementParser(FlywaveSqlProp conf) {
        if ("mysql".equalsIgnoreCase(conf.getDialect())) {
            return new MySqlStatementParser();
        }
        else {
            throw new IllegalArgumentException("only support mysql");
        }
    }

    @Bean
    public SqlSegmentProcessor sqlSegmentParser(FlywaveSqlProp conf) {
        if ("mysql".equalsIgnoreCase(conf.getDialect())) {
            return new SqlSegmentProcessor(conf.getCommentSingle(),
                    conf.getCommentMultiple(),
                    conf.getDelimiterDefault(),
                    conf.getDelimiterCommand());
        }
        else {
            throw new IllegalArgumentException("only support mysql");
        }
    }

    @Bean
    public SchemaDefinitionLoader schemaDefinitionLoader(FlywaveSqlProp conf) {
        if ("mysql".equalsIgnoreCase(conf.getDialect())) {
            return new MysqlDefinitionLoader();
        }
        else {
            throw new IllegalArgumentException("only support mysql");
        }
    }
}
