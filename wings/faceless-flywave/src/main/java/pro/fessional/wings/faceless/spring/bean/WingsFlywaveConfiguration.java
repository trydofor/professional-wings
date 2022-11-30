package pro.fessional.wings.faceless.spring.bean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import pro.fessional.wings.faceless.database.DataSourceContext;
import pro.fessional.wings.faceless.flywave.RevisionFitness;
import pro.fessional.wings.faceless.flywave.SchemaDefinitionLoader;
import pro.fessional.wings.faceless.flywave.SchemaFulldumpManager;
import pro.fessional.wings.faceless.flywave.SchemaJournalManager;
import pro.fessional.wings.faceless.flywave.SchemaShardingManager;
import pro.fessional.wings.faceless.flywave.SqlSegmentProcessor;
import pro.fessional.wings.faceless.flywave.SqlStatementParser;
import pro.fessional.wings.faceless.flywave.impl.DefaultRevisionManager;
import pro.fessional.wings.faceless.flywave.impl.MySqlStatementParser;
import pro.fessional.wings.faceless.flywave.impl.MysqlDefinitionLoader;
import pro.fessional.wings.faceless.spring.prop.FlywaveEnabledProp;
import pro.fessional.wings.faceless.spring.prop.FlywaveFitProp;
import pro.fessional.wings.faceless.spring.prop.FlywaveSqlProp;
import pro.fessional.wings.faceless.spring.prop.FlywaveVerProp;

import java.util.TreeSet;

import static pro.fessional.wings.faceless.flywave.SchemaJournalManager.JournalDdl;

/**
 * @author trydofor
 * @since 2019-06-01
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(name = "pro.fessional.wings.faceless.database.DataSourceContext")
@ConditionalOnProperty(name = FlywaveEnabledProp.Key$module, havingValue = "true")
public class WingsFlywaveConfiguration {

    private static final Log log = LogFactory.getLog(WingsFlywaveConfiguration.class);

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public SchemaJournalManager schemaJournalManager(
            DataSourceContext facelessDs,
            SqlStatementParser statementParser,
            SchemaDefinitionLoader schemaDefinitionLoader,
            FlywaveVerProp properties) {

        JournalDdl ddl = new JournalDdl(
                properties.getJournalInsert(),
                properties.getTriggerInsert(),
                properties.getJournalUpdate(),
                properties.getTriggerUpdate(),
                properties.getJournalDelete(),
                properties.getTriggerDelete()
        );
        log.info("FacelessFlywave spring-bean schemaJournalManager");
        return new SchemaJournalManager(facelessDs.getPlains(), statementParser, schemaDefinitionLoader, ddl, properties.getSchemaJournalTable());
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public DefaultRevisionManager schemaVersionManger(
            DataSourceContext sources,
            SqlStatementParser statementParser,
            SqlSegmentProcessor segmentProcessor,
            SchemaDefinitionLoader schemaDefinitionLoader,
            FlywaveVerProp properties) {
        DefaultRevisionManager bean = new DefaultRevisionManager(
                sources.getPlains(), sources.getSharding(),
                statementParser, segmentProcessor, schemaDefinitionLoader,
                properties.getSchemaVersionTable());
        for (String s : new TreeSet<>(properties.getDropReg().values())) {
            if (s != null && !s.isEmpty()) {
                bean.addDropRegexp(s);
            }
        }
        log.info("FacelessFlywave spring-bean schemaVersionManger");
        return bean;
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public SchemaShardingManager schemaShardingManager(
            DataSourceContext sources,
            SqlStatementParser statementParser,
            SchemaDefinitionLoader schemaDefinitionLoader) {
        log.info("FacelessFlywave spring-bean schemaShardingManager");
        return new SchemaShardingManager(sources.getPlains(), sources.getSharding(),
                statementParser, schemaDefinitionLoader);
    }

    @Bean
    public SchemaFulldumpManager schemaFulldumpManager(
            SqlStatementParser statementParser,
            SchemaDefinitionLoader schemaDefinitionLoader) {
        log.info("FacelessFlywave spring-bean schemaFulldumpManager");
        return new SchemaFulldumpManager(statementParser, schemaDefinitionLoader);
    }

    @Bean
    public SqlStatementParser sqlStatementParser(FlywaveSqlProp conf) {
        if ("mysql".equalsIgnoreCase(conf.getDialect())) {
            log.info("FacelessFlywave spring-bean sqlStatementParser");
            return new MySqlStatementParser();
        }
        else {
            throw new IllegalArgumentException("only support mysql");
        }
    }

    @Bean
    public SqlSegmentProcessor sqlSegmentProcessor(FlywaveSqlProp conf) {
        if ("mysql".equalsIgnoreCase(conf.getDialect())) {
            final String fs = conf.getFormatShard();
            if (fs != null && !fs.isEmpty()) {
                log.info("FacelessFlywave spring-bean static ShardFormat=" + fs);
                SqlSegmentProcessor.setShardFormat(fs);
            }
            final String ft = conf.getFormatTrace();
            if (ft != null && !ft.isEmpty()) {
                log.info("FacelessFlywave spring-bean static TraceFormat=" + ft);
                SqlSegmentProcessor.setTraceFormat(ft);
            }
            log.info("FacelessFlywave spring-bean sqlSegmentParser");
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
            log.info("FacelessFlywave spring-bean schemaDefinitionLoader");
            return new MysqlDefinitionLoader();
        }
        else {
            throw new IllegalArgumentException("only support mysql");
        }
    }

    @Bean
    @ConditionalOnProperty(name = FlywaveEnabledProp.Key$checker, havingValue = "true")
    public CommandLineRunner runnerRevisionChecker(DefaultRevisionManager manager, FlywaveFitProp prop) {
        log.info("FacelessFlywave spring-runs runnerRevisionChecker");
        return args -> {
            final RevisionFitness fits = new RevisionFitness();
            fits.addFits(prop.getFit());
            fits.checkRevision(manager);
        };
    }
}
