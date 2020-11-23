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
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;
import pro.fessional.mirana.text.Wildcard;
import pro.fessional.wings.slardar.servlet.WingsServletConst;
import pro.fessional.wings.slardar.servlet.filter.WingsDomainExFilter;
import pro.fessional.wings.slardar.servlet.request.WingsRequestWrapper;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;

/**
 * @author trydofor
 * @since 2019-06-29
 */
@Configuration
@ConditionalOnExpression("${spring.wings.slardar.extend-controller.enabled:false} || ${spring.wings.slardar.extend-resource.enabled:false}")
public class WingsFilterDomainExConfiguration {

    private final static Log logger = LogFactory.getLog(WingsFilterDomainExConfiguration.class);

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
            String key = entry.getKey();
            // spring official log is common log
            logger.info(" - conf Domain filter - " + key + ":" + String.join(",", vs));
            wildcards.put(key, ls);
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
                    HandlerExecutionChain hdc = hm.getHandler(wrp);
                    if (hdc != null) {
                        Object hd = hdc.getHandler();
                        if (hd instanceof ResourceHttpRequestHandler) {
                            if (ResourceChecker.exist((ResourceHttpRequestHandler) hd, wrp)) {
                                cacheUrl.put(domainUrl, Boolean.TRUE);
                                return wrp;
                            }
                        } else {
                            cacheUrl.put(domainUrl, Boolean.TRUE);
                            return wrp;
                        }
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

    private static class ResourceChecker {
        private static final AtomicReference<Method> resourceHttpRequestHandler = new AtomicReference<>();

        public static boolean exist(ResourceHttpRequestHandler rh, HttpServletRequest rq) {
            if (resourceHttpRequestHandler.get() == null) {
                synchronized (resourceHttpRequestHandler) {
                    if (resourceHttpRequestHandler.get() == null) {
                        // getResource(HttpServletRequest request)
                        try {
                            Method md = ResourceHttpRequestHandler.class.getDeclaredMethod("getResource", HttpServletRequest.class);
                            md.setAccessible(true);
                            resourceHttpRequestHandler.set(md);
                        } catch (Exception e) {
                            logger.warn("failed to check resource=" + rq.getRequestURI(), e);
                            throw new IllegalStateException(e);
                        }
                    }
                }
            }
            try {
                //
                rq.setAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE,rq.getRequestURI());
                Object obj = resourceHttpRequestHandler.get().invoke(rh, rq);
                return obj != null;
            } catch (Exception e) {
                return false;
            }
        }
    }
}
