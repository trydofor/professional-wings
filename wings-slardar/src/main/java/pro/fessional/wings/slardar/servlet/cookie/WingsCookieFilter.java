package pro.fessional.wings.slardar.servlet.cookie;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.Ordered;
import org.springframework.web.filter.OncePerRequestFilter;
import pro.fessional.wings.slardar.servlet.WingsServletConst;
import pro.fessional.wings.slardar.servlet.request.WingsRequestWrapper;
import pro.fessional.wings.slardar.servlet.response.WingsResponseWrapper;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Function;

/**
 * @author trydofor
 * @since 2019-11-14
 */
@RequiredArgsConstructor
public class WingsCookieFilter extends OncePerRequestFilter implements Ordered {

    private final WingsCookieInterceptor interceptor;

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest req, @NotNull HttpServletResponse res, @NotNull FilterChain chain)
            throws ServletException, IOException {

        if (interceptor.notIntercept()) {
            chain.doFilter(req, res);
            return;
        }

        WingsRequestWrapper request = WingsRequestWrapper.infer(req);
        if (request == null) {
            request = new WingsRequestWrapper(req);
        }


        WingsResponseWrapper response = WingsResponseWrapper.infer(res);
        if (response == null) {
            response = new WingsResponseWrapper(res);
        }

        // read
        final Cookie[] ckOld = request.getCookies();
        if (ckOld != null && ckOld.length > 0) {
            final Cookie[] ckNew = Arrays
                    .stream(ckOld)
                    .map(interceptor::read)
                    .filter(Objects::nonNull)
                    .toArray(Cookie[]::new);
            request.setCookies(ckNew);
        }

        // write
        final Function<Cookie, Cookie> ciOld = response.getCookieInterceptor();
        if (ciOld == null) {
            response.setCookieInterceptor(interceptor::write);
        }
        else {
            response.setCookieInterceptor(it -> interceptor.write(ciOld.apply(it)));
        }

        chain.doFilter(request, response);
    }

    //
    private int order = WingsServletConst.ORDER_FILTER_COOKIES;

    @Override
    public int getOrder() {
        return this.order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}
