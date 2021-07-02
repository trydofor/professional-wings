package pro.fessional.wings.warlock.security.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import pro.fessional.wings.slardar.security.WingsUidPrincipalToken;
import pro.fessional.wings.slardar.servlet.response.ResponseHelper;
import pro.fessional.wings.warlock.security.session.NonceTokenSessionHelper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * cookie和header返回sid
 *
 * @author trydofor
 * @since 2021-02-17
 */
@Slf4j
@RequiredArgsConstructor
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final String body;
    private final String headerName;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        String sid = null;
        if (headerName != null) {
            sid = trySid(sid, request);
            response.setHeader(headerName, sid);
            log.info("login Success, session-id={}", sid);
        }

        if (authentication instanceof WingsUidPrincipalToken) {
            long uid = ((WingsUidPrincipalToken) authentication).getUserId();
            NonceTokenSessionHelper.swapNonceSid(uid, trySid(sid, request));
        }
        ResponseHelper.writeBodyUtf8(response, body);
    }

    private String trySid(String sid, HttpServletRequest request) {
        if (sid == null) {
            final HttpSession s = request.getSession(false);
            if (s != null) {
                sid = s.getId();
            }
        }
        return sid;
    }
}
