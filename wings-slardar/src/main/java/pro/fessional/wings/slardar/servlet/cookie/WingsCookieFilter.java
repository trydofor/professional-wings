package pro.fessional.wings.slardar.servlet.cookie;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.filter.OrderedFilter;
import pro.fessional.wings.slardar.servlet.WingsServletConst;
import pro.fessional.wings.slardar.servlet.request.WingsRequestWrapper;
import pro.fessional.wings.slardar.servlet.response.WingsResponseWrapper;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

/**
 * @author trydofor
 * @since 2019-11-14
 */
@RequiredArgsConstructor
public class WingsCookieFilter implements OrderedFilter {

    private final WingsCookieInterceptor interceptor;

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        if (interceptor.notIntercept()) {
            chain.doFilter(req, res);
            return;
        }

        WingsRequestWrapper request = WingsRequestWrapper.infer(req);
        if (request == null) {
            request = new WingsRequestWrapper((HttpServletRequest) req);
        }


        WingsResponseWrapper response = WingsResponseWrapper.infer(res);
        if (response == null) {
            response = new WingsResponseWrapper((HttpServletResponse) res);
        }


        final Cookie[] ckOld = request.getCookies();
        if (ckOld != null && ckOld.length > 0) {
            final Cookie[] ckNew = Arrays
                    .stream(ckOld)
                    .map(interceptor::read)
                    .filter(Objects::nonNull)
                    .toArray(Cookie[]::new);
            request.setCookies(ckNew);
        }

        response.setCookieInterceptor(interceptor::write);

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
