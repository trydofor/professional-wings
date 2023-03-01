package pro.fessional.wings.warlock.security.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import pro.fessional.wings.slardar.servlet.response.ResponseHelper;
import pro.fessional.wings.warlock.spring.prop.WarlockSecurityProp;

/**
 * invalid session
 *
 * @author trydofor
 * @since 2021-02-17
 */
@Slf4j
@RequiredArgsConstructor
public class LogoutOkHandler implements LogoutSuccessHandler {

    @Setter(onMethod_ = {@Autowired})
    protected WarlockSecurityProp warlockSecurityProp;

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        writeResponseBody(warlockSecurityProp.getLogoutSuccessBody(), request, response, authentication);
    }

    protected void writeResponseBody(@NotNull String body, HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        ResponseHelper.writeBodyUtf8(response, warlockSecurityProp.getLogoutSuccessBody());
    }
}
