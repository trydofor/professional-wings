package pro.fessional.wings.slardar.spring.bean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import pro.fessional.wings.slardar.spring.prop.SlardarEnabledProp;

/**
 * wings-async-79.properties
 *
 * https://docs.spring.io/spring-boot/docs/2.4.2/reference/html/spring-boot-features.html#boot-features-task-execution-scheduling
 *
 * @author trydofor
 * @since 2019-12-03
 */
@Configuration
@EnableAsync
@ConditionalOnProperty(name = SlardarEnabledProp.Key$async, havingValue = "true")
public class SlardarAsyncConfiguration {

    private static final Log logger = LogFactory.getLog(SlardarAsyncConfiguration.class);

}
