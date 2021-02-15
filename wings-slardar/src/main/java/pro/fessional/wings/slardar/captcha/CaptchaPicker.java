package pro.fessional.wings.slardar.captcha;

import javax.servlet.http.HttpServletRequest;
import java.util.Set;

/**
 * @author trydofor
 * @since 2021-02-14
 */
public interface CaptchaPicker {
    /**
     * 提取可以标识Session的Key
     *
     * @param request 请求
     * @param session session
     */
    void pickSession(HttpServletRequest request, Set<String> session);
}
