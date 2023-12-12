package pro.fessional.wings.faceless.spring.bean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jooq.ConverterProvider;
import org.jooq.VisitListenerProvider;
import org.jooq.conf.Settings;
import org.jooq.impl.DefaultVisitListenerProvider;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.jooq.DefaultConfigurationCustomizer;
import org.springframework.boot.autoconfigure.jooq.JooqAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pro.fessional.wings.faceless.database.jooq.WingsJooqEnv;
import pro.fessional.wings.faceless.database.jooq.converter.JooqConverterDelegate;
import pro.fessional.wings.faceless.database.jooq.listener.AutoQualifyFieldListener;
import pro.fessional.wings.faceless.spring.prop.FacelessJooqConfProp;
import pro.fessional.wings.silencer.spring.boot.ConditionalWingsEnabled;

/**
 * @author trydofor
 * @see JooqAutoConfiguration
 * @since 2019-08-12
 */
@Configuration(proxyBeanMethods = false)
@ConditionalWingsEnabled
@ConditionalOnClass(Settings.class)
public class FacelessJooqConfiguration {

    private static final Log log = LogFactory.getLog(FacelessJooqConfiguration.class);

    /**
     * enable jooq auto qualify.
     * <p>
     * workaround before Version 3.14.0
     * still opening, maybe 3.18.0 checked on 2023-01-18
     *
     * @link <a href="https://github.com/jOOQ/jOOQ/issues/8893">Add Settings.renderTable</a>
     * @link <a href="https://github.com/jOOQ/jOOQ/issues/9055">should NO table qualify if NO table alias</a>
     * @link <a href="https://github.com/jOOQ/jOOQ/issues/7258">Deprecate org.jooq.Clause</a>
     * @link <a href="https://github.com/jOOQ/jOOQ/issues/12092">group_concat_max_len</a>
     */
    @Bean
    @ConditionalWingsEnabled(abs = FacelessJooqConfProp.Key$autoQualify)
    public VisitListenerProvider jooqAutoQualifyFieldListener() {
        log.info("FacelessJooq spring-bean jooqAutoQualifyFieldListener");
        return new DefaultVisitListenerProvider(new AutoQualifyFieldListener());
    }
    
    @Bean
    @ConditionalWingsEnabled
    public DefaultConfigurationCustomizer jooqWingsConfigCustomizer(
            FacelessJooqConfProp config,
            ObjectProvider<ConverterProvider> providers,
            ObjectProvider<org.jooq.Converter<?, ?>> converters,
            ObjectProvider<VisitListenerProvider> visitListenerProviders
    ) {
        log.info("FacelessJooq spring-bean jooqWingsConfigCustomizer");
        return configuration -> {

            final VisitListenerProvider[] visitArr = visitListenerProviders.orderedStream().toArray(VisitListenerProvider[]::new);
            log.info("FacelessJooq conf visitListener, size=" + visitArr.length);
            configuration.set(visitArr); // boot 3.0 remove visit autoconfig

            final Settings settings = configuration.settings();
            WingsJooqEnv.daoBatchMysql = config.isBatchMysql();
            settings.withRenderCatalog(config.isRenderCatalog())
                    .withRenderSchema(config.isRenderSchema())
                    .withRenderGroupConcatMaxLenSessionVariable(config.isRenderGroupConcat())
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
}
