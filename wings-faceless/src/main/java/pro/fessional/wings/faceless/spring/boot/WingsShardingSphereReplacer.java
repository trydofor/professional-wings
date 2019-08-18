/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package pro.fessional.wings.faceless.spring.boot;

import com.google.common.base.Preconditions;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shardingsphere.core.exception.ShardingException;
import org.apache.shardingsphere.core.util.InlineExpressionParser;
import org.apache.shardingsphere.core.yaml.swapper.impl.EncryptRuleConfigurationYamlSwapper;
import org.apache.shardingsphere.core.yaml.swapper.impl.MasterSlaveRuleConfigurationYamlSwapper;
import org.apache.shardingsphere.core.yaml.swapper.impl.ShardingRuleConfigurationYamlSwapper;
import org.apache.shardingsphere.shardingjdbc.api.EncryptDataSourceFactory;
import org.apache.shardingsphere.shardingjdbc.api.MasterSlaveDataSourceFactory;
import org.apache.shardingsphere.shardingjdbc.api.ShardingDataSourceFactory;
import org.apache.shardingsphere.shardingjdbc.spring.boot.common.SpringBootPropertiesConfigurationProperties;
import org.apache.shardingsphere.shardingjdbc.spring.boot.encrypt.SpringBootEncryptRuleConfigurationProperties;
import org.apache.shardingsphere.shardingjdbc.spring.boot.masterslave.SpringBootMasterSlaveRuleConfigurationProperties;
import org.apache.shardingsphere.shardingjdbc.spring.boot.sharding.SpringBootShardingRuleConfigurationProperties;
import org.apache.shardingsphere.shardingjdbc.spring.boot.util.DataSourceUtil;
import org.apache.shardingsphere.shardingjdbc.spring.boot.util.PropertyUtil;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationPreparedEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationListener;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.StandardEnvironment;
import pro.fessional.mirana.cast.StringCastUtil;
import pro.fessional.wings.faceless.flywave.FlywaveDataSources;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * NOTE: copy most code from org.apache.shardingsphere.shardingjdbc.spring.boot.SpringBootConfiguration.
 * because I CAN'T override.
 * <p>
 * /////
 * 依照 flywave-jdbc-spring-boot-starter 配置，构造数据源，
 * 当只有一个数据源，且不存在分表时，直接使用原始数据源，而非sharding数据源。
 * 如果有多个数据源，使用sharding数据源，同时expose原始出来，可以独立使用。
 * <p/>
 * https://shardingsphere.apache.org/document/current/cn/manual/sharding-jdbc/configuration/config-java/ <br/>
 *
 * @author trydofor
 *
 * <p>
 * Spring boot flywave and master-slave configuration.
 * @author caohao
 * @author panjuan
 */

@Configuration
@AutoConfigureBefore(org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration.class)
@ConditionalOnProperty(prefix = "spring.wings.shardingsphere", name = "enabled", havingValue = "true")
@EnableConfigurationProperties({SpringBootShardingRuleConfigurationProperties.class, SpringBootMasterSlaveRuleConfigurationProperties.class, SpringBootEncryptRuleConfigurationProperties.class, SpringBootPropertiesConfigurationProperties.class})
public class WingsShardingSphereReplacer implements EnvironmentAware {

    private static final Log logger = LogFactory.getLog(WingsShardingSphereReplacer.class);

    private final SpringBootShardingRuleConfigurationProperties shardingProperties;

    private final SpringBootMasterSlaveRuleConfigurationProperties masterSlaveProperties;

    private final SpringBootEncryptRuleConfigurationProperties encryptProperties;

    private final SpringBootPropertiesConfigurationProperties propMapProperties;

    private final Map<String, DataSource> dataSourceMap = new LinkedHashMap<>();

    private final ShardingRuleConfigurationYamlSwapper shardingSwapper = new ShardingRuleConfigurationYamlSwapper();

    private final MasterSlaveRuleConfigurationYamlSwapper masterSlaveSwapper = new MasterSlaveRuleConfigurationYamlSwapper();

    private final EncryptRuleConfigurationYamlSwapper encryptSwapper = new EncryptRuleConfigurationYamlSwapper();

    public WingsShardingSphereReplacer(SpringBootShardingRuleConfigurationProperties shardingProperties, SpringBootMasterSlaveRuleConfigurationProperties masterSlaveProperties, SpringBootEncryptRuleConfigurationProperties encryptProperties, SpringBootPropertiesConfigurationProperties propMapProperties) {
        this.shardingProperties = shardingProperties;
        this.masterSlaveProperties = masterSlaveProperties;
        this.encryptProperties = encryptProperties;
        this.propMapProperties = propMapProperties;
    }

    @Override
    public final void setEnvironment(final Environment environment) {
        String prefix = "spring.shardingsphere.datasource.";
        for (String each : getDataSourceNames(environment, prefix)) {
            try {
                dataSourceMap.put(each, getDataSource(environment, prefix, each));
            } catch (final ReflectiveOperationException ex) {
                throw new ShardingException("Can't find datasource type!", ex);
            }
        }
    }

    protected List<String> getDataSourceNames(final Environment environment, final String prefix) {
        StandardEnvironment standardEnv = (StandardEnvironment) environment;
        standardEnv.setIgnoreUnresolvableNestedPlaceholders(true);
        return null == standardEnv.getProperty(prefix + "name") ? new InlineExpressionParser(standardEnv.getProperty(prefix + "names")).splitAndEvaluate() : Collections.singletonList(standardEnv.getProperty(prefix + "name"));
    }

    @SuppressWarnings("unchecked")
    protected DataSource getDataSource(final Environment environment, final String prefix, final String dataSourceName) throws ReflectiveOperationException {
        Map<String, Object> dataSourceProps = PropertyUtil.handle(environment, prefix + dataSourceName.trim(), Map.class);
        Preconditions.checkState(!dataSourceProps.isEmpty(), "Wrong datasource properties!");
        return DataSourceUtil.getDataSource(dataSourceProps.get("type").toString(), dataSourceProps);
    }

    // /////////////////////////////////////
    // trydofor start

    /**
     * 如果没有shardingProperties配置，则使用第一个真实datasource，免去SQL解析。
     * <p>
     * Get data source bean.
     *
     * @return data source bean
     * @throws SQLException SQL exception
     */
    @Bean
    public DataSource dataSource() throws SQLException {
        if (null != masterSlaveProperties.getMasterDataSourceName()) {
            return MasterSlaveDataSourceFactory.createDataSource(dataSourceMap, masterSlaveSwapper.swap(masterSlaveProperties), propMapProperties.getProps());
        }
        if (!encryptProperties.getEncryptors().isEmpty()) {
            return EncryptDataSourceFactory.createDataSource(dataSourceMap.values().iterator().next(), encryptSwapper.swap(encryptProperties));
        }

        // trydofor
        DataSource ds;
        if (needSharding()) {
            logger.info("Wings use sharding datasource, ShardingDataSourceFactory");
            ds = ShardingDataSourceFactory.createDataSource(dataSourceMap, shardingSwapper.swap(shardingProperties), propMapProperties.getProps());
        } else {
            logger.info("Wings use the first datasource, because NOT sharding config");
            ds = dataSourceMap.values().iterator().next();
        }

        return ds;
    }

    @Bean
    public FlywaveDataSources wingsDataSources(DataSource inuse) {
        DataSource shard = dataSourceMap.values().iterator().next() == inuse ? null : inuse;
        boolean slave = null != masterSlaveProperties.getMasterDataSourceName() || !shardingProperties.getMasterSlaveRules().isEmpty();
        return new FlywaveDataSources(dataSourceMap, inuse, shard, slave);
    }

    public static class DisableDefault implements ApplicationListener<ApplicationPreparedEvent> {

        @Override
        public void onApplicationEvent(ApplicationPreparedEvent event) {
            ConfigurableEnvironment environment = event.getApplicationContext().getEnvironment();
            String enable = environment.getProperty("spring.wings.shardingsphere.enabled");
            if (StringCastUtil.asTrue(enable)) {
                environment.getPropertySources().addFirst(new MapPropertySource("wings-shardingsphere-disable", Collections.singletonMap("spring.shardingsphere.enabled", "false")));
                logger.info("Wings shardingsphere replace default config, by adding first 'spring.shardingsphere.enabled=false'");
            } else {
                logger.info("Wings shardingsphere config is disabled, skip it.");
            }
        }
    }

    private boolean needSharding() {
        return !shardingProperties.getTables().isEmpty()
                || !shardingProperties.getMasterSlaveRules().isEmpty()
                || !shardingProperties.getBroadcastTables().isEmpty()
                || shardingProperties.getEncryptRule() != null;
    }
    // trydofor end
    // /////////////////////////////////////
}
