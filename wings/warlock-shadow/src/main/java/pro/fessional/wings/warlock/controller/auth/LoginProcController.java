package pro.fessional.wings.warlock.controller.auth;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pro.fessional.mirana.data.R;
import pro.fessional.wings.silencer.spring.boot.ConditionalWingsEnabled;
import pro.fessional.wings.slardar.security.WingsAuthHelper;
import pro.fessional.wings.warlock.spring.prop.WarlockEnabledProp;
import pro.fessional.wings.warlock.spring.prop.WarlockSecurityProp;

/**
 * @author trydofor
 * @since 2021-02-16
 */
@RestController
@ConditionalWingsEnabled(abs = WarlockEnabledProp.Key$mvcProc)
@RequiredArgsConstructor
@Slf4j
public class LoginProcController {

    @Operation(summary = "Logout entry, handled by filter, used for document only", description = """
            # Usage
            Invalid all Session, see wings.warlock.security.logout-url
            ## Returns
            * @return {200} always
            """)
    @GetMapping(value = "${" + WarlockSecurityProp.Key$logoutUrl + "}")
    public R<Void> logout() {
        return R.ng("handler by filter, never here");
    }


    @SuppressWarnings("MVCPathVariableInspection")
    @Operation(summary = "Login entry, handled by filter, used for document only", description = """
            # Usage
            Auto handle by authType, see wings.warlock.security.login-proc-url
            username and password can be changed, see wings.warlock.security.username-para
            After successfully login, the token and session can get in header
            ## Params
            * @param authType - PathVariable auth type in the config (email, github)
            * @param authZone - help to grant perm, support in `path` and `param`
            * @param username - Oauth2 use state as token
            * @param password - Oauth2 use state as token
            """)
    @PostMapping(value = "${" + WarlockSecurityProp.Key$loginProcUrl + "}")
    public R<Void> login(@PathVariable(WingsAuthHelper.AuthType) String authType,
                         @RequestParam(value = WingsAuthHelper.AuthZone, required = false) String authZone,
                         @RequestParam("username") String username,
                         @RequestParam("password") String password) {
        log.debug("authType={}, authZone={}, username={}, password={}", authType, authZone, username, password);
        return R.ng("handler by filter, never here");
    }
}
