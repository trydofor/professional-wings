package pro.fessional.wings.slardar.domainx;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.cache2k.Cache;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;
import org.springframework.web.util.ServletRequestPathUtils;
import pro.fessional.wings.slardar.cache.cache2k.WingsCache2k;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.function.Supplier;

import static pro.fessional.wings.slardar.servlet.request.ResourceHttpRequestUtil.existResource;

/**
 * @author trydofor
 * @since 2021-02-15
 */
@Slf4j
public class DefaultDomainRequestMatcher implements DomainRequestMatcher {

    private final String pathPrefix;
    private final List<HandlerMapping> mappingUrl = new ArrayList<>();
    private final AntPathMatcher antMatcher = new AntPathMatcher();
    private final LinkedHashSet<String> otherUrl = new LinkedHashSet<>();
    private final Cache<String, Boolean> matchedUrl;
    private final Cache<String, Boolean> notfoundUrl;
    private final Supplier<List<HandlerMapping>> handlerMappingSupplier;

    public DefaultDomainRequestMatcher(String pathPrefix, Collection<String> otherUrl, int cacheSize, Supplier<List<HandlerMapping>> supplier) {
        this.pathPrefix = pathPrefix;
        this.otherUrl.addAll(otherUrl);
        this.matchedUrl = WingsCache2k.builder(DefaultDomainRequestMatcher.class, "matchedUrl", cacheSize, -1, -1, String.class, Boolean.class).build();
        this.notfoundUrl = WingsCache2k.builder(DefaultDomainRequestMatcher.class, "notfoundUrl", cacheSize, -1, -1, String.class, Boolean.class).build();
        this.handlerMappingSupplier = supplier;
    }

    @Override
    public HttpServletRequest match(HttpServletRequest request, String domain) {
        checkInitMapping();

        String domainUrl = pathPrefix + domain + request.getRequestURI();
        DomainRequestWrapper wrapper = new DomainRequestWrapper(request);
        wrapper.setRequestURI(domainUrl);

        Boolean b = matchedUrl.get(domainUrl);
        if (b != null && b) {
            return wrapper;
        }

        for (String u : otherUrl) {
            if (antMatcher.match(u, domainUrl)) {
                matchedUrl.put(domainUrl, Boolean.TRUE);
                return wrapper;
            }
        }

        if (notfoundUrl.get(domainUrl) != null) {
            return request;
        }

        //UrlPathHelper.getPathWithinServletMapping
        ServletRequestPathUtils.parseAndCache(wrapper);
        for (HandlerMapping hm : mappingUrl) {
            try {
                HandlerExecutionChain hdc = hm.getHandler(wrapper);
                if (hdc != null) {
                    log.debug("find handler={}, in {}", hdc.getClass(), hm.getClass());
                    Object hd = hdc.getHandler();
                    if (hd instanceof ResourceHttpRequestHandler) {
                        if (existResource((ResourceHttpRequestHandler) hd, wrapper)) {
                            matchedUrl.put(domainUrl, Boolean.TRUE);
                            return wrapper;
                        }
                    }
                    else {
                        matchedUrl.put(domainUrl, Boolean.TRUE);
                        return wrapper;
                    }
                }
            }
            catch (Exception e) {
                log.warn("failed to getHandler in HandlerMapping=" + hm.getClass(), e);
            }
        }

        notfoundUrl.put(domainUrl, Boolean.TRUE);
        return request;
    }

    private volatile boolean initMapping = false;

    private void checkInitMapping() {
        if (initMapping) return;

        synchronized (mappingUrl) {
            if (initMapping) return;
            List<HandlerMapping> mappings = handlerMappingSupplier.get();
            if (mappings != null) {
                for (HandlerMapping mapping : mappings) {
                    log.info("add HandlerMapping={}", mapping.getClass());
                    mappingUrl.add(mapping);
                }
                initMapping = true;
            }
        }
    }
}
