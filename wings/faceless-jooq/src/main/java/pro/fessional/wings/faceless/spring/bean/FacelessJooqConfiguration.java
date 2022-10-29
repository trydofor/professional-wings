package pro.fessional.wings.faceless.spring.bean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jooq.ConverterProvider;
import org.jooq.ExecuteListenerProvider;
import org.jooq.VisitListenerProvider;
import org.jooq.conf.Settings;
import org.jooq.impl.DefaultExecuteListenerProvider;
import org.jooq.impl.DefaultVisitListenerProvider;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jooq.DefaultConfigurationCustomizer;
import org.springframework.boot.autoconfigure.jooq.JooqAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import pro.fessional.wings.faceless.database.WingsTableCudHandler;
import pro.fessional.wings.faceless.database.jooq.WingsJooqEnv;
import pro.fessional.wings.faceless.database.jooq.converter.JooqConverterDelegate;
import pro.fessional.wings.faceless.database.jooq.helper.JournalDiffHelper;
import pro.fessional.wings.faceless.database.jooq.listener.AutoQualifyFieldListener;
import pro.fessional.wings.faceless.database.jooq.listener.JournalDeleteListener;
import pro.fessional.wings.faceless.database.jooq.listener.TableCudListener;
import pro.fessional.wings.faceless.spring.prop.FacelessJooqCudProp;
import pro.fessional.wings.faceless.spring.prop.FacelessJooqEnabledProp;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author trydofor
 * @see JooqAutoConfiguration
 * @since 2019-08-12
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(name = FacelessJooqEnabledProp.Key$module, havingValue = "true")
@ConditionalOnClass(name = "org.jooq.conf.Settings")
public class FacelessJooqConfiguration {

    private static final Log log = LogFactory.getLog(FacelessJooqConfiguration.class);

    /**
     * workaround before Version 3.14.0
     * still opening, maybe 3.16.0 checked on 2021-06-06
     *
     * @link https://github.com/jOOQ/jOOQ/issues/8893
     * @link https://github.com/jOOQ/jOOQ/issues/9055
     * @link https://github.com/jOOQ/jOOQ/issues/7258
     */
    @Bean
    @ConditionalOnProperty(name = FacelessJooqEnabledProp.Key$autoQualify, havingValue = "true")
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public VisitListenerProvider jooqAutoQualifyFieldListener() {
        log.info("FacelessJooq spring-bean jooqAutoQualifyFieldListener");
        return new DefaultVisitListenerProvider(new AutoQualifyFieldListener());
    }

    @Bean
    @ConditionalOnProperty(name = FacelessJooqEnabledProp.Key$listenTableCud, havingValue = "true")
    @Order(Ordered.HIGHEST_PRECEDENCE + 1000)
    public VisitListenerProvider jooqTableCudListener(ObjectProvider<WingsTableCudHandler> handlers, FacelessJooqCudProp prop) {
        final List<WingsTableCudHandler> hdl = handlers.orderedStream().collect(Collectors.toList());
        final String names = hdl.stream().map(it -> it.getClass().getName()).collect(Collectors.joining(","));
        log.info("FacelessJooq spring-bean jooqTableCudListener with handler=" + names);
        final TableCudListener listener = new TableCudListener();
        listener.setHandlers(hdl);
        listener.setInsert(prop.isInsert());
        listener.setUpdate(prop.isUpdate());
        listener.setDelete(prop.isDelete());
        listener.setTableField(prop.getTable());
        return new DefaultVisitListenerProvider(listener);
    }

    @Bean
    @ConditionalOnProperty(name = FacelessJooqEnabledProp.Key$journalDelete, havingValue = "true")
    public ExecuteListenerProvider jooqJournalDeleteListener() {
        log.info("FacelessJooq spring-bean jooqJournalDeleteListener");
        return new DefaultExecuteListenerProvider(new JournalDeleteListener());
    }

    @Bean
    @ConditionalOnMissingBean(name = "facelessJooqConfiguration")
    public DefaultConfigurationCustomizer facelessJooqConfiguration(
            FacelessJooqEnabledProp config,
            ObjectProvider<ConverterProvider> providers,
            ObjectProvider<org.jooq.Converter<?, ?>> converters
    ) {
        log.info("FacelessJooq spring-bean jooqConfigurationCustomizer");
        return configuration -> {
            final Settings settings = configuration.settings();
            WingsJooqEnv.daoBatchMysql = config.isBatchMysql();
            settings.withRenderCatalog(false)
                    .withRenderSchema(false)
//                  .withParseDialect(SQLDialect.MYSQL)
//                .withRenderTable(false)
            ;
            log.info("FacelessJooq conf jooq setting, dialect=" + settings.getParseDialect());

            if (config.isConverter()) {
                log.info("FacelessJooq conf jooqConfiguration ConverterProvider");
                JooqConverterDelegate dcp = new JooqConverterDelegate();
                dcp.add(configuration.converterProvider());

                providers.orderedStream().forEach(it -> {
                    dcp.add(it);
                    log.info("   add jooqConverterProvider, class=" + it.getClass());
                });
                converters.orderedStream().forEach(it -> {
                    dcp.add(it);
                    log.info("   add jooqConverter, class=" + it.getClass());
                });
                configuration.set(dcp);
            }
        };
    }

    @Autowired
    public void initJournalDiffHelper(FacelessJooqCudProp prop) {
        log.info("FacelessJooq spring-auto initJournalDiffHelper");
        JournalDiffHelper.putDefaultIgnore(prop.getDiff());
    }
}
