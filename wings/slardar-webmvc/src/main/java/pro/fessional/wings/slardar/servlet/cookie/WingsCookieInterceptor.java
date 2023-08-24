package pro.fessional.wings.slardar.servlet.cookie;

import jakarta.servlet.http.Cookie;

/**
 * @author trydofor
 * @since 2021-10-08
 */
public interface WingsCookieInterceptor {

    enum Coder {
        Nop,
        B64,
        Aes
    }

    /**
     * Whether no Intercept for cookies, true when short-circuit processing, can turn off the intercept function
     */
    boolean notIntercept();

    /**
     * Convert to new cookie on read, return `null` means discard it
     *
     * @param cookie original cookie
     * @return new / original / null
     */
    Cookie read(Cookie cookie);

    /**
     * Convert to new cookie on write, return `null` means discard it
     *
     * @param cookie original cookie
     * @return new / original / null
     */
    Cookie write(Cookie cookie);
}
