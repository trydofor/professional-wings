package pro.fessional.wings.slardar.spring.bean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pro.fessional.wings.slardar.servlet.stream.RequestResponseLogging;
import pro.fessional.wings.slardar.servlet.stream.WingsReuseStreamFilter;
import pro.fessional.wings.slardar.spring.prop.SlardarEnabledProp;

/**
 * @author trydofor
 * @since 2019-12-03
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(name = SlardarEnabledProp.Key$restream, havingValue = "true")
public class SlardarRestreamConfiguration {

    private static final Log log = LogFactory.getLog(SlardarRestreamConfiguration.class);

    @Bean
    @ConditionalOnMissingBean(WingsReuseStreamFilter.class)
    public WingsReuseStreamFilter wingsReuseStreamFilter(ObjectProvider<RequestResponseLogging> logging) {
        final WingsReuseStreamFilter filter = new WingsReuseStreamFilter();
        final RequestResponseLogging lg = logging.getIfAvailable();
        if (lg != null) {
            filter.setRequestResponseLogging(lg);
        }
        log.info("Wings conf wingsReuseStreamFilter, logging=" + (lg == null ? null : lg.getClass()));
        return filter;
    }
}
