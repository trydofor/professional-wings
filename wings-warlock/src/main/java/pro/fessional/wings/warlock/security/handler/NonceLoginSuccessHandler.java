package pro.fessional.wings.warlock.security.handler;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import pro.fessional.wings.slardar.context.SecurityContextUtil;
import pro.fessional.wings.warlock.security.session.NonceTokenSessionHelper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * 实现了一次性token换session，需要自行覆盖onResponse
 *
 * @author trydofor
 * @since 2021-02-17
 */
@Slf4j
public class NonceLoginSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public final void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        final HttpSession session = request.getSession(false);
        final long uid = SecurityContextUtil.getUserId();
        final String sid = session == null ? null : session.getId();

        if (sid == null) {
            log.error("login Success without session, uid={}", uid);
        }
        else {
            log.error("login Success and swap nonce, uid={}", uid);
            NonceTokenSessionHelper.swapNonceSid(uid, sid);
        }

        onResponse(request, response, authentication, sid, uid);
    }

    /**
     * response
     *
     * @param req HttpServletRequest
     * @param res HttpServletResponse
     * @param aun Authentication
     * @param sid session id, 无session登录时，可能为null
     * @param uid user id
     */
    protected void onResponse(@NotNull HttpServletRequest req, @NotNull HttpServletResponse res, @NotNull Authentication aun, @Nullable String sid, long uid) {

    }
}
