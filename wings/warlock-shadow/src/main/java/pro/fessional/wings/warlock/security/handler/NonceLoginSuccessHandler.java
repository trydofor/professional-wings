package pro.fessional.wings.warlock.security.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
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

import java.io.IOException;

/**
 * UUse the one-time token to obtain the session, `onResponse` need to be Override
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
                NonceTokenSessionHelper.bindNonceSession(state, sid);
                log.debug("parse client state={}, uid={}", state, uid);
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
     * @param sid   session id, null if no-login
     * @param uid   user id
     * @param state The state set by the client contained in the oauth2 state
     */
    protected void onResponse(@NotNull HttpServletRequest req, @NotNull HttpServletResponse res, @NotNull Authentication aun,
                              @Nullable String sid, long uid, @Nullable String state) throws ServletException, IOException {
        super.onAuthenticationSuccess(req, res, aun);
    }
}
