package pro.fessional.wings.faceless.spring.bean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pro.fessional.wings.faceless.database.DataSourceContext;

import javax.sql.DataSource;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author trydofor
 * @since 2019-06-25
 */
@Configuration(proxyBeanMethods = false)
public class FacelessDataSourceConfiguration {

    private static final Log log = LogFactory.getLog(FacelessDataSourceConfiguration.class);

    @Bean
    public DataSourceContext dataSourceContext(
            ObjectProvider<DataSource> dataSources,
            ObjectProvider<DataSourceContext.Customizer> modifiers) {

        final DataSourceContext ctx = new DataSourceContext();

        final AtomicBoolean skipOther = new AtomicBoolean(false);
        modifiers.orderedStream().forEach(it -> {
            if (skipOther.get()) {
                log.info("init bean dataSourceContext skip modifier, clz=" + it.getClass());
            }
            else {
                final boolean md = it.customize(ctx);
                log.info("init bean dataSourceContext by modifier skipOthers=" + md + ", clz=" + it.getClass());
                skipOther.set(md);
            }
        });

        if (ctx.getPrimary() != null) {
            log.info("init bean dataSourceContext's inuse, by modifier skipOthers=" + skipOther.get());
        }
        else {
            Optional<DataSource> ds = dataSources.orderedStream().findFirst();
            if (ds.isPresent()) {
                log.info("init bean dataSourceContext by 1st data-source");
                ctx.setPrimary(ds.get());
            }
            else {
                throw new IllegalStateException("can not find any data-source");
            }
        }

        final int ps = ctx.getPlains().size();
        if (ps > 0) {
            log.info("init bean dataSourceContext's plains, by modifier, count=" + ps);
        }
        else {
            AtomicInteger cnt = new AtomicInteger(0);
            dataSources.orderedStream().forEach(it -> ctx.addPlain("ds-" + cnt.incrementAndGet(), it));
            log.info("init bean dataSourceContext's plains, by all datasource, count=" + cnt.get());
        }

        if (log.isInfoEnabled()) {
            for (Map.Entry<String, DataSource> e : ctx.getPlains().entrySet()) {
                log.info("[Wings]🦄 database-" + e.getKey() + "-url=" + ctx.cacheJdbcUrl(e.getValue()));
            }
            final DataSource shard = ctx.getSharding();
            if (shard != null) {
                log.info("[Wings]🦄 database-sharding-url=" + ctx.cacheJdbcUrl(shard));
            }
            else {
                log.info("[Wings]🦄 database-sharding-url=no-shard-plain-database");
            }
            log.info("[Wings]🦄 database-primary-url=" + ctx.cacheJdbcUrl(ctx.getPrimary()));
            log.info("[Wings]🦄 database-separate=" + ctx.isSeparate());
        }

        return ctx;
    }
}
