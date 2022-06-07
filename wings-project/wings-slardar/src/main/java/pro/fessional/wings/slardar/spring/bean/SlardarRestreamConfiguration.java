package pro.fessional.wings.slardar.spring.bean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pro.fessional.wings.slardar.servlet.stream.WingsReuseStreamFilter;
import pro.fessional.wings.slardar.spring.prop.SlardarEnabledProp;

/**
 * @author trydofor
 * @since 2019-12-03
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(name = SlardarEnabledProp.Key$restream, havingValue = "true")
public class SlardarRestreamConfiguration {

    private static final Log logger = LogFactory.getLog(SlardarRestreamConfiguration.class);

    @Bean
    public WingsReuseStreamFilter wingsReuseStreamFilter() {
        logger.info("Wings conf wingsReuseStreamFilter");
        return new WingsReuseStreamFilter();
    }
}
