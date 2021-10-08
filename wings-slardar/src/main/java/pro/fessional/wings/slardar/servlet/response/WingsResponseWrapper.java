package pro.fessional.wings.slardar.servlet.response;

import lombok.Getter;
import lombok.Setter;

import javax.servlet.ServletResponse;
import javax.servlet.ServletResponseWrapper;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.util.function.Function;

/**
 * @author trydofor
 * @since 2021-10-08
 */
public class WingsResponseWrapper extends HttpServletResponseWrapper {

    /**
     * 检测是否Wrapper过，返回最新的WingsRequestWrapper或null
     *
     * @param response response
     * @return 最新的一个WingsRequestWrapper
     * @see #isWrapperFor(ServletResponse)
     */
    public static WingsResponseWrapper infer(ServletResponse response) {
        if (response instanceof WingsResponseWrapper) {
            return (WingsResponseWrapper) response;
        }
        if (response instanceof ServletResponseWrapper) {
            final ServletResponse res = ((ServletResponseWrapper) response).getResponse();
            return infer(res);
        }
        return null;
    }

    public WingsResponseWrapper(HttpServletResponse response) {
        super(response);
    }

    @Setter @Getter
    private Function<Cookie, Cookie> cookieInterceptor = null;

    @Override
    public void addCookie(Cookie cookie) {
        if (cookieInterceptor != null) {
            cookie = cookieInterceptor.apply(cookie);
        }
        if (cookie != null) {
            super.addCookie(cookie);
        }
    }
}
