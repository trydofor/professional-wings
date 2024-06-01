package pro.fessional.wings.warlock.security.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.Data;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
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

    @Setter(onMethod_ = { @Autowired })
    protected AuthStateBuilder authStateBuilder;

    @Override
    public final void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
        final State state = new State();

        final long uid = SecurityContextUtil.getUserId();
        state.setUserId(uid);

        final String sts = request.getParameter(AuthStateBuilder.ParamState);
        state.setStateOauth(sts);

        final HttpSession session = request.getSession(false);
        if (session != null) {
            String sid = session.getId();
            state.setSessionId(sid);

            if (sts != null) {
                NonceTokenSessionHelper.bindNonceSession(sts, sid);
                log.debug("parse oauth state={}, uid={}", sts, uid);
            }
        }
        state.setStateClient(authStateBuilder.parseState(request));
        onResponse(request, response, authentication, state);
    }

    @Data
    public static class State {
        /**
         * SecurityContextUtil.getUserId()
         */
        private long userId;

        /**
         * session id, null if no-login
         */
        private String sessionId;

        /**
         * the state via oauth builder
         */
        private String stateOauth;

        /**
         * the safe-state send by client
         */
        private String stateClient;
    }

    /**
     * response
     *
     * @param req   HttpServletRequest
     * @param res   HttpServletResponse
     * @param aun   Authentication
     * @param state login state
     */
    protected void onResponse(@NotNull HttpServletRequest req, @NotNull HttpServletResponse res, @NotNull Authentication aun,
                              @NotNull State state) throws ServletException, IOException {
        super.onAuthenticationSuccess(req, res, aun);
    }
}
