package pro.fessional.wings.warlock.security.handler;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import pro.fessional.wings.slardar.context.SecurityContextUtil;
import pro.fessional.wings.warlock.security.justauth.AuthStateBuilder;
import pro.fessional.wings.warlock.security.session.NonceTokenSessionHelper;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * 实现了一次性token换session，需要自行覆盖onResponse
 *
 * @author trydofor
 * @see SavedRequestAwareAuthenticationSuccessHandler
 * @since 2021-02-17
 */
@Slf4j
public class NonceLoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    @Setter(onMethod_ = {@Autowired})
    protected AuthStateBuilder authStateBuilder;

    @Override
    public final void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
        final HttpSession session = request.getSession(false);

        String sid = null;
        final long uid = SecurityContextUtil.getUserId();
        final String state = request.getParameter(AuthStateBuilder.ParamState);

        if (session != null) {
            sid = session.getId();
            if (state != null) {
                NonceTokenSessionHelper.bindNonceSid(state, sid);
                log.info("parse client state={}, uid={}", state, uid);
            }
        }

        onResponse(request, response, authentication, sid, uid, authStateBuilder.parseState(request));
    }

    /**
     * response
     *
     * @param req   HttpServletRequest
     * @param res   HttpServletResponse
     * @param aun   Authentication
     * @param sid   session id, 无session登录时，可能为null
     * @param uid   user id
     * @param state oauth2 state中包含的客户端设置的state
     */
    protected void onResponse(@NotNull HttpServletRequest req, @NotNull HttpServletResponse res, @NotNull Authentication aun,
                              @Nullable String sid, long uid, @Nullable String state) throws ServletException, IOException {
        super.onAuthenticationSuccess(req, res, aun);
    }
}
