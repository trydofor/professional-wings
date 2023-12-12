package pro.fessional.wings.slardar.servlet.cookie;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Function;

/**
 * @author trydofor
 * @since 2019-11-29
 */
@Slf4j
public class CookieRequestWrapper extends HttpServletRequestWrapper {

    private final Function<Cookie, Cookie> reader;
    private volatile Cookie[] cookies;

    public CookieRequestWrapper(HttpServletRequest request, Function<Cookie, Cookie> reader) {
        super(request);
        this.reader = reader;
    }

    @Override
    public Cookie[] getCookies() {
        if (cookies != null) return cookies;

        final Cookie[] cks = ((HttpServletRequest) super.getRequest()).getCookies();
        if (cks == null || cks.length == 0) {
            cookies = new Cookie[0];
        }
        else {
            cookies = new Cookie[cks.length];
            for (int i = 0; i < cks.length; i++) {
                Cookie ck = cks[i];
                try {
                    cookies[i] = reader.apply(ck);
                }
                catch (Exception e) {
                    log.info("failed to wrap cookie=" + ck, e);
                }

                if (cookies[i] == null) cookies[i] = ck;
            }
        }

        return cookies;
    }
}
