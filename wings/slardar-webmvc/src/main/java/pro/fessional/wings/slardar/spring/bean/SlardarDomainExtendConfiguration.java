package pro.fessional.wings.slardar.spring.bean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.web.servlet.HandlerMapping;
import pro.fessional.mirana.text.Wildcard;
import pro.fessional.wings.slardar.domainx.DefaultDomainRequestMatcher;
import pro.fessional.wings.slardar.domainx.WingsDomainExtendFilter;
import pro.fessional.wings.slardar.spring.prop.DomainExtendProp;
import pro.fessional.wings.slardar.spring.prop.SlardarEnabledProp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

/**
 * @author trydofor
 * @since 2019-06-29
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(name = SlardarEnabledProp.Key$domainExtend, havingValue = "true")
public class SlardarDomainExtendConfiguration {

    private final static Log log = LogFactory.getLog(SlardarDomainExtendConfiguration.class);

    @Bean
    public WingsDomainExtendFilter wingsDomainFilter(DomainExtendProp config, ApplicationContext context) {
        log.info("SlardarWebmvc spring-bean wingsDomainFilter");
        Map<String, List<String[]>> hostMatcher = new HashMap<>();
        for (Map.Entry<String, Set<String>> entry : config.getHost().entrySet()) {
            Set<String> vs = entry.getValue();
            List<String[]> ls = new ArrayList<>(vs.size());
            for (String v : vs) {
                ls.add(Wildcard.compile(v));
            }
            String key = entry.getKey();
            // spring official log is common log
            log.info(" - conf Domain filter - " + key + ":" + String.join(",", vs));
            hostMatcher.put(key, ls);
        }

        String prefix = config.getPrefix();
        if (!prefix.endsWith("/")) {
            prefix = prefix + "/";
        }

        final Supplier<List<HandlerMapping>> supplier = () -> {
            Map<String, HandlerMapping> matchingBeans = BeanFactoryUtils.beansOfTypeIncludingAncestors(context, HandlerMapping.class, true, false);
            ArrayList<HandlerMapping> handlerMappings = new ArrayList<>(matchingBeans.values());
            // We keep HandlerMappings in sorted order.
            AnnotationAwareOrderComparator.sort(handlerMappings);
            return handlerMappings;
        };

        DefaultDomainRequestMatcher requestMatcher = new DefaultDomainRequestMatcher(prefix,
                config.getOtherUrl(), config.getCacheSize(), supplier);

        return new WingsDomainExtendFilter(
                hostMatcher,
                requestMatcher);
    }
}
