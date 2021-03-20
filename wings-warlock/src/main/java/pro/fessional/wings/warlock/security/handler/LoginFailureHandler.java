package pro.fessional.wings.warlock.security.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import pro.fessional.wings.slardar.servlet.response.ResponseHelper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author trydofor
 * @since 2021-02-17
 */
@Slf4j
@RequiredArgsConstructor
public class LoginFailureHandler implements AuthenticationFailureHandler {

    private final String body;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) {
        ResponseHelper.writeBodyUtf8(response, body);
        log.info("login failure", exception);
    }
}
