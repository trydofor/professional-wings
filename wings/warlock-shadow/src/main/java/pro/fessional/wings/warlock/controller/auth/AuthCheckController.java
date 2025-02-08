package pro.fessional.wings.warlock.controller.auth;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.session.web.http.HttpSessionIdResolver;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import pro.fessional.mirana.data.R;
import pro.fessional.wings.silencer.spring.boot.ConditionalWingsEnabled;
import pro.fessional.wings.slardar.context.SecurityContextUtil;
import pro.fessional.wings.slardar.security.WingsUserDetails;
import pro.fessional.wings.slardar.servlet.resolver.WingsRemoteResolver;
import pro.fessional.wings.warlock.security.session.NonceTokenSessionHelper;
import pro.fessional.wings.warlock.spring.prop.WarlockEnabledProp;
import pro.fessional.wings.warlock.spring.prop.WarlockUrlmapProp;

import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * @author trydofor
 * @since 2024-12-31
 */
@RestController
@ConditionalWingsEnabled(abs = WarlockEnabledProp.Key$mvcProc)
@RequiredArgsConstructor
@Slf4j
public class AuthCheckController {

    private final WingsRemoteResolver wingsRemoteResolver;

    @Setter(onMethod_ = { @Autowired(required = false) })
    private HttpSessionIdResolver httpSessionIdResolver;

    @SuppressWarnings("UastIncorrectHttpHeaderInspection")
    @Operation(summary = "Verify that the one-time token is valid", description = """
        # Usage
        Use Oauth2 state as the token and require the same ip, agent and other header as the original client.
        After successful verification, the session and cookie are in the header as a normal login
        ## Params
        * @param token - RequestHeader Oauth2 state as token
        ## Returns
        * @return {401} token is not-found, expired, or failed
        * @return {200 | Result(false, message='authing')} in authing
        * @return {200 | Result(true, data=sessionId)} success
        * @return {200 | Result(true, code='xxx', data=object)} other code/object
        """)
    @PostMapping(value = "${" + WarlockUrlmapProp.Key$authNonceCheck + "}")
    public ResponseEntity<R<?>> nonceCheck(@RequestHeader("token") String token, HttpServletRequest request, HttpServletResponse response) {
        final R<?> result = NonceTokenSessionHelper.authNonce(token, wingsRemoteResolver.resolveRemoteKey(request));
        if (result == null) {
            return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(R.NG());
        }

        R<?> body = result;
        if (result.isSuccess()) {
            if (httpSessionIdResolver != null && body.getData() instanceof NonceTokenSessionHelper.SidData sd) {
                httpSessionIdResolver.setSessionId(request, response, sd.getSid());
            }
            return ResponseEntity.ok(body);
        }
        else {
            body = R.ng("authing");
        }
        return ResponseEntity.ok(body);
    }

    @Schema(description = "Basic info of login user")
    @Data
    public static class Dto {
        @Schema(description = "nickname", example = "trydofor")
        private String nickname;
        @Schema(description = "language, see java.util.Locale", example = "zh-CN")
        private String locale;
        @Schema(description = "timezone, see java.time.ZoneId", example = "Asia/Shanghai")
        private String zoneid;
        @Schema(description = "time offset in second to UTD", example = "28800")
        private int offset;
        @Schema(description = "auth type of current session", example = "EMAIL")
        private String authtype;
    }

    @Operation(summary = "check current session, return basic info if valid", description = """
        # Usage
        unlike user-authed-user, this always returns 200, without session and username (for secure)
        ## Returns
        * @return {200 | Result(Dto)} logined user and basis info
        * @return {200 | Result(false)} not logined""")
    @PostMapping(value = "${" + WarlockUrlmapProp.Key$authSessionCheck + "}")
    public R<Dto> sessionCheck() {
        final WingsUserDetails wd = SecurityContextUtil.getUserDetails(false);
        if (wd == null) return R.NG();

        Dto dto = new Dto();
        dto.setNickname(wd.getNickname());
        dto.setLocale(wd.getLocale().toLanguageTag());
        final ZoneId zid = wd.getZoneId();
        dto.setZoneid(zid.getId());
        dto.setOffset(ZonedDateTime.now(zid).getOffset().getTotalSeconds());
        final Enum<?> at = wd.getAuthType();
        if (at != null) dto.setAuthtype(at.name());

        return R.ok(dto);
    }
}
