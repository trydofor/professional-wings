package pro.fessional.wings.slardar.servlet.filter;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.filter.OrderedFilter;
import pro.fessional.wings.silencer.context.WingsI18nContext;
import pro.fessional.wings.slardar.security.WingsTerminalContext;
import pro.fessional.wings.slardar.servlet.WingsServletConst;
import pro.fessional.wings.slardar.servlet.resolver.WingsLocaleResolver;
import pro.fessional.wings.slardar.servlet.resolver.WingsRemoteResolver;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @author trydofor
 * @since 2019-11-16
 */
@RequiredArgsConstructor
public class WingsTerminalFilter implements OrderedFilter {

    private final WingsLocaleResolver localeResolver;
    private final WingsRemoteResolver remoteResolver;

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        WingsI18nContext i18nContext = localeResolver.resolveI18nContext(request);
        String remoteIp = remoteResolver.resolveRemoteIp(request);
        String agentInfo = remoteResolver.resolveAgentInfo(request);

        WingsTerminalContext.set(i18nContext, remoteIp, agentInfo);
        try {
            chain.doFilter(req, res);
        } finally {
            WingsTerminalContext.clear();
        }
    }

    //
    private int order = WingsServletConst.ORDER_FILTER_TERMINAL;

    @Override
    public int getOrder() {
        return this.order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

}
