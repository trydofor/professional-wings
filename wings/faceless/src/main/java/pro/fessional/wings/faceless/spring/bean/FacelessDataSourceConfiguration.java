package pro.fessional.wings.faceless.spring.bean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pro.fessional.wings.faceless.database.DataSourceContext;
import pro.fessional.wings.spring.consts.OrderedFacelessConst;

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
@AutoConfigureOrder(OrderedFacelessConst.DataSourceConfiguration)
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
            Optional<DataSource> ds = dataSources.orderedStream().findFirst();
            if (ds.isPresent()) {
                log.info("Faceless spring-bean dataSourceContext by 1st data-source");
                ctx.setCurrent(ds.get());
            }
            else {
                throw new IllegalStateException("can not find any data-source");
            }
        }

        final int ps = ctx.getBackends().size();
        if (ps > 0) {
            log.info("Faceless spring-bean dataSourceContext's plains, by modifier, count=" + ps);
        }
        else {
            AtomicInteger cnt = new AtomicInteger(0);
            dataSources.orderedStream().forEach(it -> ctx.addBackend("ds-" + cnt.incrementAndGet(), it));
            log.info("Faceless spring-bean dataSourceContext's plains, by all datasource, count=" + cnt.get());
        }

        for (Map.Entry<String, DataSource> e : ctx.getBackends().entrySet()) {
            log.info("Faceless🦄 database-" + e.getKey() + "-url=" + ctx.cacheJdbcUrl(e.getValue()));
        }
        log.info("Faceless🦄 database-current-url=" + ctx.cacheJdbcUrl(ctx.getCurrent()));

        return ctx;
    }
}
