package pro.fessional.wings.faceless.spring.bean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shardingsphere.driver.jdbc.core.datasource.ShardingSphereDataSource;
import org.apache.shardingsphere.spring.boot.datasource.DataSourceMapSetter;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import pro.fessional.wings.faceless.database.DataSourceContext;
import pro.fessional.wings.faceless.database.sharding.WriteRouteOnlyAround;

import javax.sql.DataSource;
import java.util.Map;

import static org.apache.shardingsphere.spring.boot.util.PropertyUtil.containPropertyPrefix;

/**
 * 依照 shardingsphere-jdbc-core-spring-boot-starter 配置，构造数据源，
 * 如果有多个数据源，使用sharding数据源，同时expose原始出来，可以独立使用。
 * <p/>
 * https://shardingsphere.apache.org/document/5.1.0/en/user-manual/shardingsphere-jdbc/configuration/spring-boot-starter/ <br/>
 *
 * @author trydofor
 * @see org.apache.shardingsphere.spring.boot.ShardingSphereAutoConfiguration
 */

@ConditionalOnProperty(name = "spring.shardingsphere.enabled", havingValue = "true", matchIfMissing = true)
@Configuration(proxyBeanMethods = false)
public class FacelessShardingsphereConfiguration {
    private static final Log log = LogFactory.getLog(FacelessShardingsphereConfiguration.class);

    @Bean
    public WriteRouteOnlyAround writeRouteOnlyAround() {
        log.info("FacelessShard spring-bean writeRouteOnlyAround");
        return new WriteRouteOnlyAround();
    }

    @Bean
    @ConditionalOnClass(name = "pro.fessional.wings.faceless.database.DataSourceContext")
    public DataSourceContext.Customizer shardingSphereCustomizer(ObjectProvider<DataSource> opDataSource,
                                                                 Environment environment) {

        final DataSource shard;
        DataSource inuse = opDataSource.getIfAvailable();
        if (inuse instanceof ShardingSphereDataSource) {
            shard = inuse;
        }
        else {
            shard = null;
        }

        final boolean separate = containPropertyPrefix(environment, "spring.shardingsphere.opRules.readwrite-splitting");
        final Map<String, DataSource> dsMap = DataSourceMapSetter.getDataSourceMap(environment);

        log.info("FacelessShard spring-bean shardingSphereCustomizer shard=" + (shard != null) + ", separate=" + separate);

        return (ctx) -> {
            ctx.cleanPlain()
               .addPlain(dsMap)
               .setSharding(shard)
               .setSeparate(separate);
            return true;
        };
    }
}
