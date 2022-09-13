package pro.fessional.wings.slardar.servlet.cookie;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.util.function.Function;

/**
 * @author trydofor
 * @since 2019-11-29
 */
public class CookieRequestWrapper extends HttpServletRequestWrapper {

    private final Cookie[] cookies;

    public CookieRequestWrapper(HttpServletRequest request, Function<Cookie, Cookie> reader) {
        super(request);
        final Cookie[] cks = request.getCookies();
        if (cks == null || cks.length == 0) {
            cookies = new Cookie[0];
        }
        else {
            cookies = new Cookie[cks.length];
            for (int i = 0; i < cks.length; i++) {
                cookies[i] = reader.apply(cks[i]);
            }
        }
    }

    @Override
    public Cookie[] getCookies() {
        return cookies;
    }
}
