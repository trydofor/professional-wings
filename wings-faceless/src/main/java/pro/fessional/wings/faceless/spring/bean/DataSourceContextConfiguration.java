package pro.fessional.wings.faceless.spring.bean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pro.fessional.wings.faceless.database.DataSourceContext;

import javax.sql.DataSource;
import java.util.Map;

/**
 * @author trydofor
 * @since 2019-06-25
 */
@Configuration
public class DataSourceContextConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(DataSourceContextConfiguration.class);

    @Bean
    public DataSourceContext dataSourceContext(
            ObjectProvider<DataSource> dataSources,
            ObjectProvider<DataSourceContext.Modifier> modifiers) {

        final DataSourceContext ctx = new DataSourceContext();
        final boolean md = modifiers.orderedStream().anyMatch(it -> it.modify(ctx));

        if (ctx.getInuse() != null) {
            logger.info("init bean dataSourceContext, exclude-modifier={}", md);
        } else {
            final DataSource ds = dataSources.getIfAvailable();
            if (ds != null) {
                logger.info("init bean dataSourceContext by 1st data-source");
                ctx.setInuse(ds);
            } else {
                throw new IllegalStateException("can not find any data-source");
            }
        }

        if (logger.isInfoEnabled()) {
            for (Map.Entry<String, DataSource> e : ctx.getPlains().entrySet()) {
                logger.info("[Wings]🦄 database-" + e.getKey() + "-url=" + ctx.jdbcUrl(e.getValue()));
            }
            final DataSource shard = ctx.getShard();
            if (shard != null) {
                logger.info("[Wings]🦄 database-shard-url=" + ctx.jdbcUrl(shard));
            } else {
                logger.info("[Wings]🦄 database-shard-url=no-shard-plain-database");
            }
            logger.info("[Wings]🦄 database-inuse-url=" + ctx.jdbcUrl(ctx.getInuse()));
            logger.info("[Wings]🦄 database-split=" + ctx.isSplit());
        }

        return ctx;
    }
}
