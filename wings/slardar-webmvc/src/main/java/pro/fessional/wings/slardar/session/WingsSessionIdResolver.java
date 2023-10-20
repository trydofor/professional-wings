package pro.fessional.wings.slardar.session;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.session.web.http.CookieHttpSessionIdResolver;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.HttpSessionIdResolver;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Combined HttpSessionIdResolver
 *
 * @author trydofor
 * @since 2021-02-03
 */
public class WingsSessionIdResolver implements HttpSessionIdResolver {

    private final List<HttpSessionIdResolver> httpSessionIdResolvers;

    @Setter(onMethod_ = {@Autowired(required = false)})
    private SessionTokenEncoder sessionTokenEncoder;

    public WingsSessionIdResolver(List<HttpSessionIdResolver> httpSessionIdResolvers) {
        this.httpSessionIdResolvers = httpSessionIdResolvers;
    }

    public WingsSessionIdResolver(HttpSessionIdResolver... resolvers) {
        Assert.notEmpty(resolvers, "need HttpSessionIdResolver");
        httpSessionIdResolvers = new ArrayList<>(resolvers.length);
        for (HttpSessionIdResolver resolver : resolvers) {
            if (resolver != null) httpSessionIdResolvers.add(resolver);
        }
    }

    @Override
    public List<String> resolveSessionIds(HttpServletRequest request) {
        for (HttpSessionIdResolver resolver : httpSessionIdResolvers) {
            final List<String> ids = resolver.resolveSessionIds(request);
            if (ids != null && !ids.isEmpty()) {
                return sessionTokenEncoder == null ? ids : sessionTokenEncoder.decode(ids, request);
            }
        }

        return Collections.emptyList();
    }

    @Override
    public void setSessionId(HttpServletRequest request, HttpServletResponse response, String sessionId) {
        if (sessionTokenEncoder != null) {
            sessionId = sessionTokenEncoder.encode(sessionId, request);
        }

        for (HttpSessionIdResolver resolver : httpSessionIdResolvers) {
            resolver.setSessionId(request, response, sessionId);
        }
    }

    @Override
    public void expireSession(HttpServletRequest request, HttpServletResponse response) {
        for (HttpSessionIdResolver resolver : httpSessionIdResolvers) {
            resolver.expireSession(request, response);
        }
    }

    public void setCookieSerializer(CookieSerializer cookieSerializer) {
        for (HttpSessionIdResolver resolver : httpSessionIdResolvers) {
            if (resolver instanceof CookieHttpSessionIdResolver) {
                ((CookieHttpSessionIdResolver) resolver).setCookieSerializer(cookieSerializer);
            }
        }
    }
}
