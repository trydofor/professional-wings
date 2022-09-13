package pro.fessional.wings.slardar.domainx;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.Ordered;
import org.springframework.web.filter.OncePerRequestFilter;
import pro.fessional.mirana.text.Wildcard;
import pro.fessional.wings.slardar.constants.SlardarOrderConst;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static pro.fessional.wings.slardar.constants.SlardarServletConst.AttrDomainExtend;

/**
 * @author trydofor
 * @since 2019-11-16
 */
@RequiredArgsConstructor
public class WingsDomainExtendFilter extends OncePerRequestFilter implements Ordered {

    @Setter @Getter
    private int order = SlardarOrderConst.OrderFilterDomainEx;

    private final Map<String, List<String[]>> hostWildcard;
    private final DomainRequestMatcher domainRequestMatcher;

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse res, @NotNull FilterChain chain)
            throws ServletException, IOException {

        String host = request.getServerName();
        String domain = null;

        out:
        for (Map.Entry<String, List<String[]>> entry : hostWildcard.entrySet()) {
            for (String[] ptn : entry.getValue()) {
                if (Wildcard.match(true, host, ptn)) {
                    domain = entry.getKey();
                    break out;
                }
            }
        }

        HttpServletRequest wrap = request;
        if (domain != null) {
            request.setAttribute(AttrDomainExtend, domain);
            wrap = domainRequestMatcher.match(request, domain);
        }

        chain.doFilter(wrap, res);
    }
}
