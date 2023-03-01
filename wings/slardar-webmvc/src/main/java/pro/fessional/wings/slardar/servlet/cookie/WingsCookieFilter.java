package pro.fessional.wings.slardar.servlet.cookie;

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
import pro.fessional.wings.spring.consts.OrderedSlardarConst;

import java.io.IOException;

/**
 * @author trydofor
 * @since 2019-11-14
 */
@RequiredArgsConstructor
public class WingsCookieFilter extends OncePerRequestFilter implements Ordered {

    @Setter @Getter
    private int order = OrderedSlardarConst.WebFilterReCookie;

    private final WingsCookieInterceptor interceptor;

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest req, @NotNull HttpServletResponse res, @NotNull FilterChain chain)
            throws ServletException, IOException {

        if (interceptor.notIntercept()) {
            chain.doFilter(req, res);
            return;
        }

        // read
        CookieRequestWrapper request = new CookieRequestWrapper(req, interceptor::read);
        // write
        CookieResponseWrapper response = new CookieResponseWrapper(res, interceptor::write);
        chain.doFilter(request, response);
    }
}
