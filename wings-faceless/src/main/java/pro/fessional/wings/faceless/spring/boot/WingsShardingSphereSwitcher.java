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
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shardingsphere.shardingjdbc.spring.boot.common.SpringBootPropertiesConfigurationProperties;
import org.apache.shardingsphere.shardingjdbc.spring.boot.encrypt.SpringBootEncryptRuleConfigurationProperties;
import org.apache.shardingsphere.shardingjdbc.spring.boot.masterslave.SpringBootMasterSlaveRuleConfigurationProperties;
import org.apache.shardingsphere.shardingjdbc.spring.boot.sharding.SpringBootShardingRuleConfigurationProperties;
import org.apache.shardingsphere.spring.boot.util.DataSourceUtil;
import org.apache.shardingsphere.spring.boot.util.PropertyUtil;
import org.apache.shardingsphere.underlying.common.config.inline.InlineExpressionParser;
import org.apache.shardingsphere.underlying.common.exception.ShardingSphereException;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.event.ApplicationPreparedEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationListener;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.jdbc.support.MetaDataAccessException;
import org.springframework.jndi.JndiObjectFactoryBean;
import pro.fessional.mirana.cast.StringCastUtil;
import pro.fessional.wings.faceless.flywave.FlywaveDataSources;

import javax.naming.NamingException;
import javax.sql.DataSource;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

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
 * @author caohao
 * @author panjuan
 * @see org.apache.shardingsphere.shardingjdbc.spring.boot.SpringBootConfiguration
 */

@ConditionalOnProperty(name = "spring.wings.shardingsphere.enabled", havingValue = "true")
//////////////// >>>>>>> BGN ShardingSphere code ////////////////
@Configuration
@AutoConfigureBefore(DataSourceAutoConfiguration.class)
@EnableConfigurationProperties({
        SpringBootShardingRuleConfigurationProperties.class,
        SpringBootMasterSlaveRuleConfigurationProperties.class,
        SpringBootEncryptRuleConfigurationProperties.class,
        SpringBootPropertiesConfigurationProperties.class
})
@RequiredArgsConstructor
public class WingsShardingSphereSwitcher implements EnvironmentAware {
    private final LinkedHashMap<String, DataSource> dataSourceMap = new LinkedHashMap<>();
    private final String jndiName = "jndi-name";

    @Override
    public final void setEnvironment(final Environment environment) {
        String prefix = "spring.shardingsphere.datasource.";
        for (String each : getDataSourceNames(environment, prefix)) {
            try {
                dataSourceMap.put(each, getDataSource(environment, prefix, each));
            } catch (final ReflectiveOperationException ex) {
                throw new ShardingSphereException("Can't find datasource type!", ex);
            } catch (final NamingException namingEx) {
                throw new ShardingSphereException("Can't find JNDI datasource!", namingEx);
            }
        }
    }

    private List<String> getDataSourceNames(final Environment environment, final String prefix) {
        StandardEnvironment standardEnv = (StandardEnvironment) environment;
        standardEnv.setIgnoreUnresolvableNestedPlaceholders(true);
        return null == standardEnv.getProperty(prefix + "name")
                ? new InlineExpressionParser(standardEnv.getProperty(prefix + "names")).splitAndEvaluate() : Collections.singletonList(standardEnv.getProperty(prefix + "name"));
    }

    @SuppressWarnings("unchecked")
    private DataSource getDataSource(final Environment environment, final String prefix, final String dataSourceName) throws ReflectiveOperationException, NamingException {
        Map<String, Object> dataSourceProps = PropertyUtil.handle(environment, prefix + dataSourceName.trim(), Map.class);
        Preconditions.checkState(!dataSourceProps.isEmpty(), "Wrong datasource properties!");
        if (dataSourceProps.containsKey(jndiName)) {
            return getJndiDataSource(dataSourceProps.get(jndiName).toString());
        }
        return DataSourceUtil.getDataSource(dataSourceProps.get("type").toString(), dataSourceProps);
    }

    private DataSource getJndiDataSource(final String jndiName) throws NamingException {
        JndiObjectFactoryBean bean = new JndiObjectFactoryBean();
        bean.setResourceRef(true);
        bean.setJndiName(jndiName);
        bean.setProxyInterface(DataSource.class);
        bean.afterPropertiesSet();
        return (DataSource) bean.getObject();
    }
//////////////// <<<<<<< END ShardingSphere code ////////////////

    /**
     * 如果没有shardingProperties配置，则使用第一个真实datasource，免去SQL解析。
     *
     * @return data source bean
     */
    @Bean
    @Conditional(Switcher.class)
    public DataSource dataSource() {
        return defaultDataSource(true);
    }

    @Bean
    public FlywaveDataSources flywaveDataSources(@NotNull DataSource inuse, Environment environment) {
        DataSource shard = defaultDataSource(false) == inuse ? null : inuse;
        Map<DataSource, String> dsJdbc = new HashMap<>();
        for (Map.Entry<String, DataSource> e : dataSourceMap.entrySet()) {
            String jdbc = dsJdbc.computeIfAbsent(e.getValue(), this::jdbcUrl);
            logger.info("[Wings]🦄 database-" + e.getKey() + "-url=" + jdbc);
        }

        if (shard != null) {
            logger.info("[Wings]🦄 database-shard-url=" + dsJdbc.get(shard));
        } else {
            logger.info("[Wings]🦄 database-shard-url=no-shard-plain-database");
        }
        logger.info("[Wings]🦄 database-inuse-url=" + dsJdbc.get(inuse));
        return new FlywaveDataSources(dataSourceMap, inuse, shard, hasSlave(environment));
    }

    //
    private static final Log logger = LogFactory.getLog(WingsShardingSphereSwitcher.class);

    private String jdbcUrl(DataSource ds) {
        if (ds == null) return "";
        try {
            return JdbcUtils.extractDatabaseMetaData(ds, "getURL");
        } catch (MetaDataAccessException e) {
            return "unknown";
        }
    }

    private DataSource defaultDataSource(boolean log) {
        Map.Entry<String, DataSource> first = dataSourceMap.entrySet().iterator().next();
        if (log) {
            logger.info("[Wings]🦄 datasource use the first as default = " + first.getKey() + ", for no sharding config");
        }
        return first.getValue();
    }

    private boolean hasSlave(Environment environment) {
        boolean hasMasterSlaveName = environment.containsProperty("spring.shardingsphere.masterslave.name");
        boolean hasShardingMasterSlave = PropertyUtil.containPropertyPrefix(environment, "spring.shardingsphere.sharding.master-slave-rules");

        return hasMasterSlaveName || hasShardingMasterSlave;
    }

    public static class Switcher extends SpringBootCondition implements ApplicationListener<ApplicationPreparedEvent> {

        @Override
        public void onApplicationEvent(ApplicationPreparedEvent event) {
            // run first
            ConfigurableEnvironment environment = event.getApplicationContext().getEnvironment();
            if (cachedShardStatus(environment)) {
                logger.info("[Wings]🦄 switch on  shardingsphere datasource.");
            } else {
                environment.getPropertySources().addFirst(new MapPropertySource("wings-shardingsphere-disable", Collections.singletonMap("spring.shardingsphere.enabled", "false")));
                logger.info("[Wings]🦄 switch off shardingsphere datasource, by adding first 'spring.shardingsphere.enabled=false'");
            }
        }

        @Override
        public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {
            return cachedShardStatus(context.getEnvironment()) ? ConditionOutcome.noMatch("has sharding config") : ConditionOutcome.match();
        }

        private static final AtomicInteger sharding = new AtomicInteger(0);

        private boolean cachedShardStatus(Environment environment) {
            int status = sharding.get();
            if (status != 0) return status > 0;
            boolean result = needShard(environment);
            sharding.set(result ? 1 : -1);
            return result;
        }

        private boolean needShard(Environment environment) {
            String enable = environment.getProperty("spring.wings.shardingsphere.enabled");
            if (StringCastUtil.asFalse(enable)) {
                logger.info("[Wings]🦄 shardingsphere is disabled");
                return false;
            }

            boolean hasMasterSlaveName = containsProperty(environment, "spring.shardingsphere.masterslave.name", false);
            // 数据脱敏
            boolean hasEncryptEncryptors = containsProperty(environment, "spring.shardingsphere.encrypt.encryptors", true);
            boolean hasEncryptTables = containsProperty(environment, "spring.shardingsphere.encrypt.tables", true);
            // 数据分片
            boolean hasShardingTables = containsProperty(environment, "spring.shardingsphere.sharding.tables", true);
            boolean hasShardingMasterSlave = containsProperty(environment, "spring.shardingsphere.sharding.master-slave-rules", true);
            boolean hasShardingBroadcast = containsProperty(environment, "spring.shardingsphere.sharding.broadcast-tables", true);
            boolean hasShardingEncrypt = containsProperty(environment, "spring.shardingsphere.sharding.encrypt-rule", true);

            return hasMasterSlaveName
                    || hasEncryptEncryptors
                    || hasEncryptTables
                    || hasShardingTables
                    || hasShardingMasterSlave
                    || hasShardingBroadcast
                    || hasShardingEncrypt
                    ;
        }

        private boolean containsProperty(Environment environment, String key, boolean prefix) {
            boolean has;
            if (prefix) {
                has = PropertyUtil.containPropertyPrefix(environment, key);
            } else {
                has = environment.containsProperty(key);
            }
            if (has) {
                logger.info("[Wings]🦄 " + key + " = " + has + " 🦄");
            } else {
                logger.info("[Wings]🦄 " + key + " = " + has);
            }
            return has;
        }
    }
}
