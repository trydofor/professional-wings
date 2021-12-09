package pro.fessional.wings.warlock.security.handler;

import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import pro.fessional.wings.slardar.servlet.response.ResponseHelper;
import pro.fessional.wings.warlock.spring.prop.WarlockJustAuthProp;
import pro.fessional.wings.warlock.spring.prop.WarlockSecurityProp;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Set;

/**
 * cookie和header返回sid,
 * 默认情况下 cookie是sid的base64字符串
 *
 * @author trydofor
 * @since 2021-02-17
 */
@Slf4j
public class LoginSuccessHandler extends NonceLoginSuccessHandler {

    @Setter(onMethod_ = {@Autowired})
    protected WarlockSecurityProp warlockSecurityProp;

    @Setter(onMethod_ = {@Autowired})
    protected WarlockJustAuthProp warlockJustAuthProp;

    @SneakyThrows
    @Override
    protected void onResponse(@NotNull HttpServletRequest req, @NotNull HttpServletResponse res, @NotNull Authentication aun,
                              @Nullable String sid, long uid, @Nullable String state) {

        if (slardarSessionProp != null && slardarSessionProp.getHeaderName() != null) {
            res.setHeader(slardarSessionProp.getHeaderName(), sid);
        }

        if (state != null && !state.isEmpty()) {
            if (state.startsWith("/") || isSafeRedirect(state)) {
                log.info("redirect to {}", state);
                res.sendRedirect(state);
            }
            else {
                writeResponseBody(state, req, res, aun, sid, uid, state);
            }
        }
        else {
            writeResponseBody(warlockSecurityProp.getLoginSuccessBody(), req, res, aun, sid, uid, state);
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
}
