package pro.fessional.wings.slardar.servlet.cookie;

import javax.servlet.http.Cookie;

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
     * 是否对cookie有Intercept，true时短路处理，可关闭拦截功能
     *
     * @return 是否
     */
    boolean notIntercept();

    /**
     * 读取时转换一个cookie，并转换成新的，返回null表示丢弃
     *
     * @param cookie 原始cookie
     * @return 新
     */
    Cookie read(Cookie cookie);

    /**
     * 写入时一个cookie，并转换成新的，返回null表示丢弃
     *
     * @param cookie 原始cookie
     * @return 新
     */
    Cookie write(Cookie cookie);
}
