package pro.fessional.wings.faceless.spring.bean;

import org.jooq.Converter;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import pro.fessional.wings.faceless.database.jooq.WingsJooqEnv;
import pro.fessional.wings.faceless.database.jooq.converter.ConsEnumConverter;
import pro.fessional.wings.faceless.database.jooq.converter.DelegatingConverterProvider;
import pro.fessional.wings.faceless.database.jooq.converter.WingsEnumConverters;
import pro.fessional.wings.faceless.database.jooq.listener.AutoQualifyFieldListener;
import pro.fessional.wings.faceless.database.jooq.listener.JournalDeleteListener;
import pro.fessional.wings.faceless.enums.auto.StandardLanguage;
import pro.fessional.wings.faceless.enums.auto.StandardTimezone;

/**
 * @author trydofor
 * @since 2019-08-12
 */
@Configuration
@ConditionalOnProperty(name = "spring.wings.faceless.jooq.enabled", havingValue = "true")
@ConditionalOnClass(name = "org.jooq.conf.Settings")
public class WingsJooqConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(WingsJooqConfiguration.class);

    /**
     * workaround before Version 3.14.0
     *
     * @link https://github.com/jOOQ/jOOQ/issues/8893
     * @link https://github.com/jOOQ/jOOQ/issues/9055
     * @link https://github.com/jOOQ/jOOQ/issues/7258
     */
    @Bean
    @ConditionalOnProperty(name = "spring.wings.faceless.jooq.auto-qualify.enabled", havingValue = "true")
    public VisitListenerProvider autoQualifyFieldListener() {
        logger.info("Wings config autoQualifyFieldListener");
        return new DefaultVisitListenerProvider(new AutoQualifyFieldListener());
    }

    @Bean
    @ConditionalOnProperty(name = "spring.wings.faceless.trigger.journal-delete.enabled", havingValue = "true")
    public ExecuteListenerProvider journalDeleteListener() {
        logger.info("Wings config journalDeleteListener");
        return new DefaultExecuteListenerProvider(new JournalDeleteListener());
    }


    @Bean
    @Order
    @ConditionalOnMissingBean(Settings.class)
    public Settings settings(@Value("${spring.wings.faceless.jooq.dao.batch-mysql.enabled}") boolean daoBatchMysql) {
        WingsJooqEnv.daoBatchMysql = daoBatchMysql;
        // ObjectProvider<Settings> settings
        return new Settings()
                .withRenderCatalog(false)
                .withRenderSchema(false)
                .withParseDialect(SQLDialect.MYSQL)
//                .withRenderTable(false)
                ;
    }

    @Bean
    @ConditionalOnProperty(name = "spring.wings.faceless.jooq.converter.enabled", havingValue = "true")
    public ConsEnumConverter<StandardLanguage> languageIdConverter() {
        logger.info("Wings config StandardLanguageConverter");
        return WingsEnumConverters.LanguageIdConverter;
    }

    @Bean
    @ConditionalOnProperty(name = "spring.wings.faceless.jooq.converter.enabled", havingValue = "true")
    public ConsEnumConverter<StandardTimezone> timezoneIdConverter() {
        logger.info("Wings config StandardTimezoneConverter");
        return WingsEnumConverters.TimezoneIdConverter;
    }

    @Autowired
    public void jooqConfigurationPostProcessor(
            ObjectProvider<org.jooq.Configuration> config,
            ObjectProvider<ConverterProvider> providers,
            ObjectProvider<Converter<?,?>> converters

    ) {
        final org.jooq.Configuration bean = config.getIfAvailable();
        if (bean == null) {
            logger.info("Wings config skip jooqConfiguration for config is null");
            return;
        }

        logger.info("Wings config jooqConfiguration ConverterProvider");
        DelegatingConverterProvider dcp = new DelegatingConverterProvider();
        dcp.add(bean.converterProvider());

        providers.orderedStream().forEach(it -> {
            dcp.add(it);
            logger.info("   add converterProvider, class={}", it.getClass());
        });
        converters.orderedStream().forEach(it -> {
            dcp.add(it);
            logger.info("   add Converter, class={}", it.getClass());
        });
        bean.set(dcp);
    }
}
