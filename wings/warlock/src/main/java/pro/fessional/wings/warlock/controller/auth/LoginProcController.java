package pro.fessional.wings.warlock.controller.auth;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pro.fessional.mirana.data.R;
import pro.fessional.wings.slardar.security.WingsAuthHelper;
import pro.fessional.wings.warlock.spring.prop.WarlockEnabledProp;
import pro.fessional.wings.warlock.spring.prop.WarlockSecurityProp;

/**
 * @author trydofor
 * @since 2021-02-16
 */
@RestController
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = WarlockEnabledProp.Key$controllerProc, havingValue = "true")
public class LoginProcController {

    @Operation(summary = "登出接口，有filter处理，仅做文档", description =
            "# Usage \n"
            + "默认失效Session，参考wings.warlock.security.logout-url\n"
            + "## Params \n"
            + "* @param token - Oauth2使用state作为token\n"
            + "## Returns \n"
            + "* @return {200} 任何时候 \n"
            + "")
    @GetMapping(value = "${" + WarlockSecurityProp.Key$logoutUrl + "}")
    public R<Void> logout() {
        return R.ng("handler by filter, never here");
    }


    @SuppressWarnings("MVCPathVariableInspection")
    @Operation(summary = "登录接口，有filter处理，仅做文档", description =
            "# Usage \n"
            + "根据类型自动处理，参考 wings.warlock.security.login-proc-url\n"
            + "username和password可变，参考 参考 wings.warlock.security.username-para\n"
            + "登录成功后，可在header中获得token和session\n"
            + "## Params \n"
            + "* @param authType - PathVariable 验证类型，系统配置项，可由【集成登录】查看，比如email,github \n"
            + "* @param authZone - 辅助验证参数，可关联权限等，支持path和param传参 \n"
            + "* @param username - Oauth2使用state作为token\n"
            + "* @param password - Oauth2使用state作为token\n"
            + "## Returns \n"
            + "* @return {200} 登录成功 \n"
            + "")
    @PostMapping(value = "${" + WarlockSecurityProp.Key$loginProcUrl + "}")
    public R<Void> login(@PathVariable(WingsAuthHelper.AuthType) String authType,
                         @RequestParam(value = WingsAuthHelper.AuthZone, required = false) String authZone,
                         @RequestParam("username") String username,
                         @RequestParam("password") String password) {
        log.info("authType={}, authZone={}, username={}, password={}", authType, authZone, username, password);
        return R.ng("handler by filter, never here");
    }
}
