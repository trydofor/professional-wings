package pro.fessional.wings.slardar.spring.bean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.DispatcherServlet;
import pro.fessional.mirana.text.Wildcard;
import pro.fessional.wings.slardar.domainx.DefaultDomainRequestMatcher;
import pro.fessional.wings.slardar.domainx.WingsDomainExtendFilter;
import pro.fessional.wings.slardar.servlet.WingsServletConst;
import pro.fessional.wings.slardar.spring.prop.DomainExtendProp;
import pro.fessional.wings.slardar.spring.prop.SlardarEnabledProp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author trydofor
 * @since 2019-06-29
 */
@Configuration
@ConditionalOnProperty(name = SlardarEnabledProp.Key$domainExtend, havingValue = "true")
public class SlardarDomainExtendConfiguration {

    private final static Log logger = LogFactory.getLog(SlardarDomainExtendConfiguration.class);

    @Bean
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public WingsDomainExtendFilter wingsDomainFilter(DomainExtendProp config, DispatcherServlet dispatcher) {
        logger.info("Wings conf Domain filter");
        Map<String, List<String[]>> hostMatcher = new HashMap<>();
        for (Map.Entry<String, List<String>> entry : config.getHost().entrySet()) {
            List<String> vs = entry.getValue();
            List<String[]> ls = new ArrayList<>(vs.size());
            for (String v : vs) {
                ls.add(Wildcard.compile(v));
            }
            String key = entry.getKey();
            // spring official log is common log
            logger.info(" - conf Domain filter - " + key + ":" + String.join(",", vs));
            hostMatcher.put(key, ls);
        }

        String prefix = config.getPrefix();
        if (!prefix.endsWith("/")) {
            prefix = prefix + "/";
        }

        DefaultDomainRequestMatcher requestMatcher = new DefaultDomainRequestMatcher(dispatcher, prefix,
                config.getOtherUrl(), config.getCacheSize());
        WingsDomainExtendFilter filter = new WingsDomainExtendFilter(
                hostMatcher,
                requestMatcher);
        filter.setOrder(WingsServletConst.ORDER_FILTER_DOMAINEX);
        return filter;
    }
}
