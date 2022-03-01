package pro.fessional.wings.warlock.security.handler;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.util.StringUtils;
import pro.fessional.wings.slardar.servlet.response.ResponseHelper;
import pro.fessional.wings.slardar.spring.prop.SlardarSessionProp;
import pro.fessional.wings.warlock.spring.prop.WarlockJustAuthProp;
import pro.fessional.wings.warlock.spring.prop.WarlockSecurityProp;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;

/**
 * cookie和header返回sid,
 * 默认情况下 cookie是sid的base64字符串
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
                              @Nullable String sid, long uid, @Nullable String state) throws IOException, ServletException {

        if (state != null && !state.isEmpty()) {
            writeSidHeader(res, sid);
            if (state.startsWith("/") || isSafeRedirect(state)) {
                log.info("redirect to {}", state);
                res.sendRedirect(state);
            }
            else {
                writeResponseBody(state, req, res, aun, sid, uid, state);
            }
        }
        else {
            if (warlockSecurityProp.isLoginSuccessRedirect()) {
                super.onResponse(req, res, aun, sid, uid, state);
            }
            else {
                writeSidHeader(res, sid);
                writeResponseBody(warlockSecurityProp.getLoginSuccessBody(), req, res, aun, sid, uid, state);
            }
        }
    }

    private void writeSidHeader(@NotNull HttpServletResponse res, @Nullable String sid) {
        if (slardarSessionProp != null) {
            final String hn = slardarSessionProp.getHeaderName();
            if (hn != null) {
                res.setHeader(hn, sid);
            }
        }
    }

    protected boolean isSafeRedirect(String state) {
        if (!state.startsWith("http")) return false;
        final Set<String> safe = warlockJustAuthProp.getSafeHost();
        if (safe == null || safe.isEmpty()) return false;

        final String tkn = "://";
        int p0 = state.indexOf(tkn);
        if (p0 < 0) return false;

        final int p1 = p0 + tkn.length();
        int p2 = state.indexOf("/", p1);
        if (p2 > p1) {
            final String host = state.substring(p1, p2);
            return safe.contains(host);
        }
        return false;
    }

    protected void writeResponseBody(@NotNull String body, @NotNull HttpServletRequest req, @NotNull HttpServletResponse res,
                                     @NotNull Authentication aun, @Nullable String sid, long uid, @Nullable String state) {
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
