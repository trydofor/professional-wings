package pro.fessional.wings.faceless.spring.boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.boot.logging.DeferredLog;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import pro.fessional.wings.silencer.spring.boot.WingsDeferredLogFactory;

import java.util.Collections;

/**
 * 依照 flywave-jdbc-spring-boot-starter 配置，构造数据源，
 * 当只有一个数据源，且不存在分表时，直接使用原始数据源，而非sharding数据源。
 * 如果有多个数据源，使用sharding数据源，同时expose原始出来，可以独立使用。
 * <p/>
 * https://shardingsphere.apache.org/document/current/cn/manual/sharding-jdbc/configuration/config-java/ <br/>
 *
 * @author trydofor
 * @since 2019-05-21
 */
public class WingsShardingSphereProcessor implements EnvironmentPostProcessor {

    private static final DeferredLog logger = WingsDeferredLogFactory.getLog(WingsShardingSphereProcessor.class);

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        logger.info("Wings disable shardingsphere default config, add first 'spring.shardingsphere.enabled=false'");
        environment.getPropertySources().addFirst(new MapPropertySource("wings-shardingsphere-disable", Collections.singletonMap("spring.shardingsphere.enabled", "false")));
    }
}
