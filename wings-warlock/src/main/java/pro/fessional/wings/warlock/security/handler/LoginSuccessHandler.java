package pro.fessional.wings.warlock.security.handler;

import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import pro.fessional.wings.slardar.servlet.response.ResponseHelper;
import pro.fessional.wings.warlock.spring.prop.WarlockSecurityProp;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

    @SneakyThrows
    @Override
    protected void onResponse(@NotNull HttpServletRequest req, @NotNull HttpServletResponse res, @NotNull Authentication aun,
                              @Nullable String sid, long uid, @Nullable String state) {

        if (slardarSessionProp != null && slardarSessionProp.getHeaderName() != null) {
            res.setHeader(slardarSessionProp.getHeaderName(), sid);
        }

        if (state != null && !state.isEmpty()) {
            if (state.startsWith("http") || state.startsWith("/")) {
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

    protected void writeResponseBody(@NotNull String body, @NotNull HttpServletRequest req, @NotNull HttpServletResponse res,
                                     @NotNull Authentication aun, @Nullable String sid, long uid, @Nullable String state) {
        ResponseHelper.writeBodyUtf8(res, body);
    }
}
