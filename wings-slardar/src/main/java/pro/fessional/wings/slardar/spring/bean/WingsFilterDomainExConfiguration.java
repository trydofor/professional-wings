package pro.fessional.wings.slardar.spring.bean;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.HandlerMapping;
import pro.fessional.mirana.text.Wildcard;
import pro.fessional.wings.slardar.servlet.WingsServletConst;
import pro.fessional.wings.slardar.servlet.filter.WingsDomainExFilter;
import pro.fessional.wings.slardar.servlet.request.WingsRequestWrapper;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;

/**
 * @author trydofor
 * @since 2019-06-29
 */
@Configuration
@ConditionalOnExpression("${spring.wings.slardar.extend-controller.enabled:false} || ${spring.wings.slardar.extend-resource.enabled:false}")
public class WingsFilterDomainExConfiguration {

    private final Log logger = LogFactory.getLog(WingsFilterDomainExConfiguration.class);

    @Bean
    public WingsDomainExFilter wingsDomainFilter(WingsDomainExFilter.Config config, DispatcherServlet dispatcherServlet) {
        logger.info("Wings conf Domain filter");
        Map<String, List<String>> host = config.getHost();
        Map<String, List<String[]>> wildcards = new HashMap<>(host.size());
        for (Map.Entry<String, List<String>> entry : host.entrySet()) {
            List<String> vs = entry.getValue();
            List<String[]> ls = new ArrayList<>(vs.size());
            for (String v : vs) {
                ls.add(Wildcard.compile(v));
            }
            wildcards.put(entry.getKey(), ls);
        }

        String prefix = config.getPrefix();
        if (!prefix.endsWith("/")) {
            prefix = prefix + "/";
        }
        WingsDomainExFilter filter = new WingsDomainExFilter(
                prefix,
                wildcards,
                new DomainMatcher(dispatcherServlet, config.getUrlMapping()));
        filter.setOrder(WingsServletConst.ORDER_FILTER_DOMAINEX);
        return filter;
    }

    @Bean
    @ConfigurationProperties("wings.slardar.extend-domain")
    public WingsDomainExFilter.Config wingsDomainFilterConfig() {
        return new WingsDomainExFilter.Config();
    }


    private static class DomainMatcher implements BiFunction<HttpServletRequest, String, HttpServletRequest> {

        private volatile boolean initDone = false;
        private final List<HandlerMapping> mappingUrl = new ArrayList<>();
        private final AntPathMatcher antMatcher = new AntPathMatcher();
        private final Map<String, Boolean> cacheUrl = new ConcurrentHashMap<>();
        private final Set<String> matchedUrl = new HashSet<>();

        private final Cache<String, Boolean> notfoundUrl;
        private final DispatcherServlet dispatcherServlet;

        public DomainMatcher(DispatcherServlet dispatcher, Set<String> mapping) {
            dispatcherServlet = dispatcher;
            if (mapping != null) {
                matchedUrl.addAll(mapping);
            }
            notfoundUrl = Caffeine.newBuilder()
                                  .maximumSize(1024)
                                  .build();
        }

        @Override
        public HttpServletRequest apply(HttpServletRequest req, String domainUrl) {
            checkInitMapping();

            WingsRequestWrapper wrp = new WingsRequestWrapper(req).setRequestURI(domainUrl);
            Boolean b = cacheUrl.get(domainUrl);
            if (b != null && b) {
                return wrp;
            }

            for (String u : matchedUrl) {
                if (antMatcher.match(u, domainUrl)) {
                    cacheUrl.put(domainUrl, Boolean.TRUE);
                    return wrp;
                }
            }

            if (notfoundUrl.getIfPresent(domainUrl) != null) {
                return req;
            }

            //UrlPathHelper.getPathWithinServletMapping
            for (HandlerMapping hm : mappingUrl) {
                try {
                    if (hm.getHandler(wrp) != null) {
                        cacheUrl.put(domainUrl, Boolean.TRUE);
                        return wrp;
                    }
                } catch (Exception e) {
                    // ignore;
                }
            }

            notfoundUrl.put(domainUrl, Boolean.TRUE);
            return req;
        }

        private void checkInitMapping() {
            if (initDone) return;
            synchronized (mappingUrl) {
                if (initDone) return;
                List<HandlerMapping> mappings = dispatcherServlet.getHandlerMappings();
                if (mappings != null) {
                    mappingUrl.addAll(mappings);
                    initDone = true;
                }
            }
        }
    }
}
