package pro.fessional.wings.slardar.spring.bean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pro.fessional.wings.silencer.spring.boot.ConditionalWingsEnabled;
import pro.fessional.wings.slardar.servlet.stream.RequestResponseLogging;
import pro.fessional.wings.slardar.servlet.stream.WingsReuseStreamFilter;
import pro.fessional.wings.slardar.spring.prop.SlardarEnabledProp;

/**
 * @author trydofor
 * @since 2019-12-03
 */
@Configuration(proxyBeanMethods = false)
@ConditionalWingsEnabled(abs = SlardarEnabledProp.Key$restream)
public class SlardarReuseStreamConfiguration {

    private static final Log log = LogFactory.getLog(SlardarReuseStreamConfiguration.class);

    @Bean
    @ConditionalWingsEnabled
    public WingsReuseStreamFilter wingsReuseStreamFilter(ObjectProvider<RequestResponseLogging> logging) {
        final WingsReuseStreamFilter filter = new WingsReuseStreamFilter();
        final RequestResponseLogging lg = logging.getIfAvailable();
        if (lg != null) {
            filter.setRequestResponseLogging(lg);
        }
        log.info("SlardarWebmvc spring-bean wingsReuseStreamFilter, logging=" + (lg == null ? null : lg.getClass()));
        return filter;
    }
}
