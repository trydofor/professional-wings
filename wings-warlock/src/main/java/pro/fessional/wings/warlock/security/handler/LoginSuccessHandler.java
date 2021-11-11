package pro.fessional.wings.warlock.security.handler;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.security.core.Authentication;
import pro.fessional.wings.slardar.servlet.response.ResponseHelper;

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
@RequiredArgsConstructor
public class LoginSuccessHandler extends NonceLoginSuccessHandler {

    private final String body;
    private final String headerName;

    @SneakyThrows
    @Override
    protected void onResponse(@NotNull HttpServletRequest req, @NotNull HttpServletResponse res, @NotNull Authentication aun,
                              @Nullable String sid, long uid, @Nullable String state) {

        if (headerName != null) {
            res.setHeader(headerName, sid);
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
            writeResponseBody(body, req, res, aun, sid, uid, state);
        }
    }

    protected void writeResponseBody(@NotNull String body, @NotNull HttpServletRequest req, @NotNull HttpServletResponse res,
                                     @NotNull Authentication aun, @Nullable String sid, long uid, @Nullable String state) {
        ResponseHelper.writeBodyUtf8(res, body);
    }
}
