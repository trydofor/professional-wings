package pro.fessional.wings.faceless.spring.bean;

import org.jooq.ConverterProvider;
import org.jooq.ExecuteListenerProvider;
import org.jooq.SQLDialect;
import org.jooq.VisitListenerProvider;
import org.jooq.conf.Settings;
import org.jooq.impl.DefaultExecuteListenerProvider;
import org.jooq.impl.DefaultVisitListenerProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import pro.fessional.wings.faceless.database.jooq.WingsJooqEnv;
import pro.fessional.wings.faceless.database.jooq.converter.JooqConverterDelegate;
import pro.fessional.wings.faceless.database.jooq.listener.AutoQualifyFieldListener;
import pro.fessional.wings.faceless.database.jooq.listener.JournalDeleteListener;
import pro.fessional.wings.faceless.spring.prop.FacelessJooqEnabledProp;

/**
 * @author trydofor
 * @since 2019-08-12
 */
@Configuration
@ConditionalOnProperty(name = FacelessJooqEnabledProp.Key$module, havingValue = "true")
@ConditionalOnClass(name = "org.jooq.conf.Settings")
public class FacelessJooqConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(FacelessJooqConfiguration.class);

    /**
     * workaround before Version 3.14.0
     *
     * @link https://github.com/jOOQ/jOOQ/issues/8893
     * @link https://github.com/jOOQ/jOOQ/issues/9055
     * @link https://github.com/jOOQ/jOOQ/issues/7258
     */
    @Bean
    @ConditionalOnProperty(name = FacelessJooqEnabledProp.Key$autoQualify, havingValue = "true")
    public VisitListenerProvider autoQualifyFieldListener() {
        logger.info("Wings conf autoQualifyFieldListener");
        return new DefaultVisitListenerProvider(new AutoQualifyFieldListener());
    }

    @Bean
    @ConditionalOnProperty(name = FacelessJooqEnabledProp.Key$journalDelete, havingValue = "true")
    public ExecuteListenerProvider journalDeleteListener() {
        logger.info("Wings conf journalDeleteListener");
        return new DefaultExecuteListenerProvider(new JournalDeleteListener());
    }


    @Bean
    @Order
    @ConditionalOnMissingBean(Settings.class)
    public Settings settings(FacelessJooqEnabledProp config) {
        WingsJooqEnv.daoBatchMysql = config.isBatchMysql();
        // ObjectProvider<Settings> settings
        return new Settings()
                .withRenderCatalog(false)
                .withRenderSchema(false)
                .withParseDialect(SQLDialect.MYSQL)
//                .withRenderTable(false)
                ;
    }

    @Autowired
    public void jooqObjectProviderProcessor(
            ObjectProvider<org.jooq.Configuration> config,
            ObjectProvider<ConverterProvider> providers,
            ObjectProvider<org.jooq.Converter<?, ?>> converters,
            FacelessJooqEnabledProp prop
    ) {
        if (!prop.isConverter()) {
            logger.info("Wings conf skip jooqObjectProviderProcessor by enabled = false");
            return;
        }

        final org.jooq.Configuration bean = config.getIfAvailable();
        if (bean == null) {
            logger.info("Wings conf skip jooqObjectProviderProcessor for null");
            return;
        }

        logger.info("Wings conf jooqConfiguration ConverterProvider");
        JooqConverterDelegate dcp = new JooqConverterDelegate();
        dcp.add(bean.converterProvider());

        providers.orderedStream().forEach(it -> {
            dcp.add(it);
            logger.info("   add jooqConverterProvider, class={}", it.getClass());
        });
        converters.orderedStream().forEach(it -> {
            dcp.add(it);
            logger.info("   add jooqConverter, class={}", it.getClass());
        });
        bean.set(dcp);
    }
}
