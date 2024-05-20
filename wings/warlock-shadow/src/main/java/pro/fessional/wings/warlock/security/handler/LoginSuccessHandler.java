package pro.fessional.wings.warlock.security.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.util.StringUtils;
import pro.fessional.wings.slardar.servlet.response.ResponseHelper;
import pro.fessional.wings.slardar.spring.prop.SlardarSessionProp;
import pro.fessional.wings.warlock.security.SafeHttpHelper;
import pro.fessional.wings.warlock.spring.prop.WarlockJustAuthProp;
import pro.fessional.wings.warlock.spring.prop.WarlockSecurityProp;

import java.io.IOException;

/**
 * Return SessionId in cookie and header,
 * In Spring default, the sessionId in cookie is base64 encoded
 *
 * @author trydofor
 * @since 2021-02-17
 */
@Slf4j
public class LoginSuccessHandler extends NonceLoginSuccessHandler implements InitializingBean {

    @Setter(onMethod_ = {@Autowired})
    protected WarlockSecurityProp warlockSecurityProp;

    @Setter(onMethod_ = {@Autowired})
    protected WarlockJustAuthProp warlockJustAuthProp;

    @Setter(onMethod_ = {@Autowired})
    protected SlardarSessionProp slardarSessionProp;

    @Override
    protected void onResponse(@NotNull HttpServletRequest req, @NotNull HttpServletResponse res, @NotNull Authentication aun,
                              @NotNull State state) throws IOException, ServletException {

        String cts = state.getStateClient();
        if (cts != null && !cts.isEmpty()) {
            if (cts.startsWith("/") || isSafeRedirect(cts)) {
                log.debug("redirect to {}", cts);
                res.sendRedirect(cts);
            }
            else {
                writeResponseBody(req, res, aun, state, cts);
            }
        }
        else {
            if (warlockSecurityProp.isLoginSuccessRedirect()) {
                super.onResponse(req, res, aun, state);
            }
            else {
                writeResponseBody(req, res, aun, state, warlockSecurityProp.getLoginSuccessBody());
            }
        }
    }

    protected boolean isSafeRedirect(String state) {
        return SafeHttpHelper.isSafeRedirect(state, warlockJustAuthProp.getSafeHost());
    }

    protected void writeResponseBody(@NotNull HttpServletRequest req, @NotNull HttpServletResponse res, @NotNull Authentication aun,
                                     @NotNull State state, @NotNull String body) {
        ResponseHelper.writeBodyUtf8(res, body);
    }

    @Override
    public void afterPropertiesSet() {
        if (warlockSecurityProp != null && warlockSecurityProp.isLoginSuccessRedirect()) {
            final String ld = warlockSecurityProp.getLoginSuccessRedirectDefault();
            if (StringUtils.hasText(ld)) {
                setDefaultTargetUrl(ld);
            }
            final String lp = warlockSecurityProp.getLoginSuccessRedirectParam();
            if (StringUtils.hasText(ld)) {
                setTargetUrlParameter(lp);
            }
        }
    }
}
