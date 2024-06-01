package pro.fessional.wings.slardar.domainx;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.Ordered;
import org.springframework.web.filter.OncePerRequestFilter;
import pro.fessional.mirana.text.Wildcard;
import pro.fessional.wings.silencer.spring.WingsOrdered;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static pro.fessional.wings.slardar.constants.SlardarServletConst.AttrDomainExtend;

/**
 * @author trydofor
 * @since 2019-11-16
 */
@RequiredArgsConstructor
@Setter @Getter
public class WingsDomainExtendFilter extends OncePerRequestFilter implements Ordered {

    public static final int ORDER = WingsOrdered.Lv4Application + 3_000;

    private int order = ORDER;

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
