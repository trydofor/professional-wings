package pro.fessional.wings.slardar.captcha;

import javax.servlet.http.HttpServletRequest;
import java.util.Set;

/**
 * @author trydofor
 * @since 2021-02-14
 */
public interface CaptchaTrigger {
    /**
     * 是否触发验证码，如果不触发返回null即可
     *
     * @param request  当前request
     * @param sessions 所有session信息
     * @return context
     */
    WingsCaptchaContext.Context trigger(HttpServletRequest request, Set<String> sessions);
}
