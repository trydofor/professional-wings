package pro.fessional.wings.faceless.spring.bean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
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
import pro.fessional.wings.faceless.spring.prop.FlywaveFitProp;
import pro.fessional.wings.faceless.spring.prop.FlywaveSqlProp;
import pro.fessional.wings.faceless.spring.prop.FlywaveVerProp;
import pro.fessional.wings.silencer.runner.ApplicationRunnerOrdered;
import pro.fessional.wings.silencer.spring.WingsOrdered;
import pro.fessional.wings.silencer.spring.boot.ConditionalWingsEnabled;

import java.util.TreeSet;

import static pro.fessional.wings.faceless.flywave.SchemaJournalManager.JournalDdl;

/**
 * @author trydofor
 * @since 2019-06-01
 */
@Configuration(proxyBeanMethods = false)
@ConditionalWingsEnabled
@ConditionalOnClass(DataSourceContext.class)
public class FlywaveConfiguration {

    private static final Log log = LogFactory.getLog(FlywaveConfiguration.class);

    @Bean
    @ConditionalWingsEnabled
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
        return new SchemaJournalManager(facelessDs.getBackends(), statementParser, schemaDefinitionLoader, ddl, properties.getSchemaJournalTable());
    }

    @Bean
    @ConditionalWingsEnabled
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public DefaultRevisionManager schemaVersionManger(
            DataSourceContext sources,
            SqlStatementParser statementParser,
            SqlSegmentProcessor segmentProcessor,
            SchemaDefinitionLoader schemaDefinitionLoader,
            FlywaveVerProp properties) {
        DefaultRevisionManager bean = new DefaultRevisionManager(
                sources.getBackends(), sources.getCurrent(),
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
    @ConditionalWingsEnabled
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public SchemaShardingManager schemaShardingManager(
            DataSourceContext sources,
            SqlStatementParser statementParser,
            SchemaDefinitionLoader schemaDefinitionLoader) {
        log.info("FacelessFlywave spring-bean schemaShardingManager");
        return new SchemaShardingManager(sources.getBackends(), sources.getCurrent(),
                statementParser, schemaDefinitionLoader);
    }

    @Bean
    @ConditionalWingsEnabled
    public SchemaFulldumpManager schemaFulldumpManager(
            SqlStatementParser statementParser,
            SchemaDefinitionLoader schemaDefinitionLoader) {
        log.info("FacelessFlywave spring-bean schemaFulldumpManager");
        return new SchemaFulldumpManager(statementParser, schemaDefinitionLoader);
    }

    @Bean
    @ConditionalWingsEnabled
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
    @ConditionalWingsEnabled
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
    @ConditionalWingsEnabled
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
    @ConditionalWingsEnabled(abs = FlywaveFitProp.Key$checker)
    public ApplicationRunnerOrdered revisionCheckerRunner(DefaultRevisionManager manager, FlywaveFitProp prop) {
        log.info("FacelessFlywave spring-runs runnerRevisionChecker");
        return new ApplicationRunnerOrdered(WingsOrdered.Lv5Supervisor, ignored -> {
            log.info("FacelessFlywave check RevisionFitness");
            final RevisionFitness fits = new RevisionFitness();
            fits.addFits(prop.getFit());
            fits.checkRevision(manager, prop.isAutoInit());
        });
    }
}
