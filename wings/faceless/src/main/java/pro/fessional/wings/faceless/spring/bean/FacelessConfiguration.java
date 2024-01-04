package pro.fessional.wings.faceless.spring.bean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.core.JdbcTemplate;
import pro.fessional.wings.faceless.database.DataSourceContext;
import pro.fessional.wings.faceless.database.manual.single.modify.commitjournal.CommitJournalModify;
import pro.fessional.wings.faceless.database.manual.single.modify.commitjournal.impl.CommitJournalModifyJdbc;
import pro.fessional.wings.faceless.service.flakeid.impl.FlakeIdLightIdImpl;
import pro.fessional.wings.faceless.service.journal.impl.DefaultJournalService;
import pro.fessional.wings.faceless.service.lightid.BlockIdProvider;
import pro.fessional.wings.faceless.service.lightid.LightIdService;
import pro.fessional.wings.faceless.service.wini18n.impl.StandardI18nServiceJdbc;
import pro.fessional.wings.faceless.spring.prop.FacelessEnabledProp;
import pro.fessional.wings.silencer.spring.boot.ConditionalWingsEnabled;

import javax.sql.DataSource;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author trydofor
 * @since 2019-06-25
 */
@Configuration(proxyBeanMethods = false)
@ConditionalWingsEnabled
public class FacelessConfiguration {

    private static final Log log = LogFactory.getLog(FacelessConfiguration.class);

    @Bean
    @Lazy
    @ConditionalWingsEnabled
    public DataSourceContext dataSourceContext(
            DataSource dataSource,
            ObjectProvider<DataSourceContext.Customizer> modifiers) {

        final DataSourceContext ctx = new DataSourceContext();

        final AtomicBoolean skipOther = new AtomicBoolean(false);
        modifiers.orderedStream().forEach(it -> {
            if (skipOther.get()) {
                log.info("Faceless spring-bean dataSourceContext skip modifier, clz=" + it.getClass());
            }
            else {
                final boolean md = it.customize(ctx);
                log.info("Faceless spring-bean dataSourceContext by modifier skipOthers=" + md + ", clz=" + it.getClass());
                skipOther.set(md);
            }
        });

        if (ctx.getCurrent() != null) {
            log.info("Faceless spring-bean dataSourceContext's inuse, by modifier skipOthers=" + skipOther.get());
        }
        else {
            log.info("Faceless spring-bean dataSourceContext by 1st data-source");
            ctx.setCurrent(dataSource);
        }

        log.info("Faceless🦄 database-current-url=" + ctx.cacheJdbcUrl(ctx.getCurrent()));
        Map<String, DataSource> backends = ctx.getBackends();
        for (Map.Entry<String, DataSource> e : backends.entrySet()) {
            log.info("Faceless🦄 database-" + e.getKey() + "-url=" + ctx.cacheJdbcUrl(e.getValue()));
        }

        if (!backends.containsValue(dataSource)) {
            String name = "Current";
            for (int i = 1; backends.containsKey(name); i++) {
                name = "Current" + i;
            }
            ctx.addBackend(name, dataSource);
        }
        return ctx;
    }

    @Bean
    @ConditionalWingsEnabled
    public StandardI18nServiceJdbc standardI18nService(JdbcTemplate jdbcTemplate) {
        log.info("Faceless spring-bean standardI18nService");
        return new StandardI18nServiceJdbc(jdbcTemplate);
    }

    @Bean
    @ConditionalWingsEnabled(abs = FacelessEnabledProp.Key$simpleFlakeid)
    public FlakeIdLightIdImpl flakeIdService(LightIdService lightIdService) {
        log.info("Faceless spring-bean flakeIdService");
        return new FlakeIdLightIdImpl(lightIdService);
    }

    @Bean
    @ConditionalWingsEnabled
    public CommitJournalModifyJdbc commitJournalModify(JdbcTemplate jdbcTemplate) {
        log.info("Faceless spring-bean commitJournalModify");
        return new CommitJournalModifyJdbc(jdbcTemplate);
    }

    @Bean
    @ConditionalWingsEnabled(abs = FacelessEnabledProp.Key$simpleJournal)
    public DefaultJournalService journalService(LightIdService lightIdService, BlockIdProvider blockIdProvider, CommitJournalModify journalModify) {
        log.info("Faceless spring-bean journalService");
        return new DefaultJournalService(lightIdService, blockIdProvider, journalModify);
    }
}
