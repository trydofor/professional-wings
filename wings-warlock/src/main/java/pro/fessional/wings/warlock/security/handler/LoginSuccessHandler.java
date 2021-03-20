package pro.fessional.wings.warlock.security.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import pro.fessional.wings.slardar.servlet.response.ResponseHelper;

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
    private final String cookieName;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        final HttpSession session = request.getSession(false);
        if (session == null) {
            log.error("login Success, but no session");
        } else {
            final String sid = session.getId();
            response.setHeader(cookieName, sid);
            log.info("login Success, session-id={}", sid);
        }
        ResponseHelper.writeBodyUtf8(response, body);
    }
}
