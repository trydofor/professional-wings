package pro.fessional.wings.faceless.spring.bean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shardingsphere.driver.jdbc.core.driver.ShardingSphereDriverURLManager;
import org.apache.shardingsphere.infra.util.yaml.YamlEngine;
import org.apache.shardingsphere.infra.yaml.config.pojo.YamlRootConfiguration;
import org.apache.shardingsphere.infra.yaml.config.swapper.resource.YamlDataSourceConfigurationSwapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pro.fessional.wings.faceless.database.DataSourceContext;
import pro.fessional.wings.faceless.database.sharding.WriteRouteOnlyAround;
import pro.fessional.wings.spring.consts.OrderedFacelessConst;

import javax.sql.DataSource;
import java.util.Map;


/**
 * Config sharding datasource to DataSourceContext
 *
 * @author trydofor
 */

@Configuration(proxyBeanMethods = false)
@AutoConfigureOrder(OrderedFacelessConst.ShardingsphereConfiguration)
public class FacelessShardingsphereConfiguration {
    private static final Log log = LogFactory.getLog(FacelessShardingsphereConfiguration.class);

    @Bean
    public WriteRouteOnlyAround writeRouteOnlyAround() {
        log.info("FacelessShard spring-bean writeRouteOnlyAround");
        return new WriteRouteOnlyAround();
    }

    @Bean
    @ConditionalOnClass(name = "pro.fessional.wings.faceless.database.DataSourceContext")
    public DataSourceContext.Customizer shardingSphereCustomizer(@Value("${spring.datasource.url}") String jdbcUrl) throws Exception {
        if (!jdbcUrl.startsWith("jdbc:shardingsphere:")) {
            log.info("FacelessShard skip shardingSphereCustomizer jdbcUrl=" + jdbcUrl);
            return ignored -> false;
        }

        final byte[] yamlBytes = ShardingSphereDriverURLManager.getContent(jdbcUrl);
        YamlRootConfiguration rootConfig = YamlEngine.unmarshal(yamlBytes, YamlRootConfiguration.class);
        final YamlDataSourceConfigurationSwapper configurationSwapper = new YamlDataSourceConfigurationSwapper();
        final Map<String, DataSource> dsMap = configurationSwapper.swapToDataSources(rootConfig.getDataSources());
        log.info("FacelessShard spring-bean shardingSphereCustomizer backends size=" + dsMap.size());

        return (ctx) -> {
            ctx.clearBackend().addBackend(dsMap);
            return true;
        };
    }
}
