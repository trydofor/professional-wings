package pro.fessional.wings.slardar.spring.bean;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.config.ClientConfigRecognizer;
import com.hazelcast.client.config.XmlClientConfigBuilder;
import com.hazelcast.client.config.YamlClientConfigBuilder;
import com.hazelcast.config.Config;
import com.hazelcast.config.ConfigStream;
import com.hazelcast.config.NetworkConfig;
import com.hazelcast.config.XmlConfigBuilder;
import com.hazelcast.config.YamlConfigBuilder;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.hazelcast.HazelcastAutoConfiguration;
import org.springframework.boot.autoconfigure.hazelcast.HazelcastConfigResourceCondition;
import org.springframework.boot.autoconfigure.hazelcast.HazelcastProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.devtools.autoconfigure.DevToolsProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;
import pro.fessional.wings.slardar.spring.prop.SlardarEnabledProp;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * @author trydofor
 * @since 2019-12-03
 */
@ConditionalOnProperty(name = SlardarEnabledProp.Key$devtool, havingValue = "true")
@ConditionalOnClass(DevToolsProperties.class)
public class SlardarDevtoolConfiguration {

    private static final Log logger = LogFactory.getLog(SlardarDevtoolConfiguration.class);

    /**
     * 为devtool设置classloader，避免开发环境下，hazelcast的自定义序列化(kyro)无法加载类
     *
     * @see org.springframework.boot.autoconfigure.hazelcast.HazelcastAutoConfiguration
     */
    @Configuration(proxyBeanMethods = false)
    @EnableConfigurationProperties(HazelcastProperties.class)
    @ConditionalOnMissingBean(value = {HazelcastInstance.class, Config.class, ClientConfig.class})
    @Conditional(HazelcastSpringConfigAvailableCondition.class)
    @AutoConfigureBefore(HazelcastAutoConfiguration.class)
    public static class HazelcastDevtoolConfiguration {

        @Bean
        public HazelcastInstance hazelcastInstance(HazelcastProperties properties, SlardarEnabledProp enabled) throws IOException {

            if (enabled.isMockHazelcast()) {
                logger.info("Wings conf Standalone hazelcastInstance for mock");
                Config config = new Config();
                config.setClusterName("slardar-standalone");
                config.setProperty("hazelcast.shutdownhook.enabled", "false");
                NetworkConfig network = config.getNetworkConfig();
                network.getJoin().getTcpIpConfig().setEnabled(false);
                network.getJoin().getMulticastConfig().setEnabled(false);
                return StringUtils.hasText(config.getInstanceName()) ? Hazelcast.getOrCreateHazelcastInstance(config) : Hazelcast.newHazelcastInstance(config);
            }

            Resource resource = properties.resolveConfigLocation();
            final URL url = resource.getURL();
            final String path = url.getPath();

            if (isClientConfig(resource)) {
                logger.info("Wings conf HazelcastClient Config=" + path);
                final ClientConfig config = path.endsWith(".yaml") ?
                                            new YamlClientConfigBuilder(url).build() :
                                            new XmlClientConfigBuilder(url).build();

                config.setClassLoader(Thread.currentThread().getContextClassLoader());
                return StringUtils.hasText(config.getInstanceName()) ? HazelcastClient.getOrCreateHazelcastClient(config) : HazelcastClient.newHazelcastClient(config);
            }
            else {
                logger.info("Wings conf HazelcastServer Config=" + path);
                final Config config = path.endsWith(".yaml") ?
                                      new YamlConfigBuilder(url).build() :
                                      new XmlConfigBuilder(url).build();
                if (ResourceUtils.isFileURL(url)) {
                    config.setConfigurationFile(resource.getFile());
                }
                else {
                    config.setConfigurationUrl(url);
                }
                //
                config.setClassLoader(Thread.currentThread().getContextClassLoader());
                return StringUtils.hasText(config.getInstanceName()) ? Hazelcast.getOrCreateHazelcastInstance(config) : Hazelcast.newHazelcastInstance(config);
            }
        }

        private boolean isClientConfig(Resource resource) {
            try (InputStream in = resource.getInputStream()) {
                return new ClientConfigRecognizer().isRecognized(new ConfigStream(in));
            }
            catch (Throwable ex) { // Hazelcast 4 specific API
                return false;
            }
        }
    }

    public static class HazelcastSpringConfigAvailableCondition extends HazelcastConfigResourceCondition {

        public HazelcastSpringConfigAvailableCondition() {
            super(HAZELCAST_CONFIG_PROPERTY);
        }
    }
}
