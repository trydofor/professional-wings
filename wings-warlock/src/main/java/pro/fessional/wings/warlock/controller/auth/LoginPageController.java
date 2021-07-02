package pro.fessional.wings.warlock.controller.auth;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pro.fessional.mirana.data.Null;
import pro.fessional.mirana.data.R;
import pro.fessional.wings.slardar.security.WingsAuthPageHandler;
import pro.fessional.wings.slardar.security.WingsAuthTypeParser;
import pro.fessional.wings.slardar.servlet.ContentTypeHelper;
import pro.fessional.wings.warlock.security.session.NonceTokenSessionHelper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author trydofor
 * @since 2021-02-16
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class LoginPageController {

    private final WingsAuthPageHandler wingsAuthPageHandler;
    private final WingsAuthTypeParser wingsAuthTypeParser;

    @ApiOperation(value = "集成登录默认页，默认返回支持的type类表", notes = "当鉴权失败时，重定向页面")
    @RequestMapping(value = "/auth/login-page.{extName}", method = {RequestMethod.POST, RequestMethod.GET})
    public ResponseEntity<?> loginPageDefault(@PathVariable("extName") String extName,
                                              HttpServletRequest request,
                                              HttpServletResponse response) {
        final MediaType mt = ContentTypeHelper.mediaTypeByUri(extName);
        log.info("default login-page media-type={}", mt);
        return wingsAuthPageHandler.response(Null.Enm, mt, request, response);
    }

    @ApiOperation(value = "具体验证登录默认页，根据content-type自动返回", notes = "一般用于定制访问，如github页面重定向")
    @RequestMapping(value = "/auth/{authType}/login-page.{extName}", method = {RequestMethod.POST, RequestMethod.GET})
    public ResponseEntity<?> LoginPageAuto(@PathVariable("authType") String authType,
                                           @PathVariable("extName") String extName,
                                           HttpServletRequest request,
                                           HttpServletResponse response) {
        final Enum<?> em = wingsAuthTypeParser.parse(authType);
        final MediaType mt = ContentTypeHelper.mediaTypeByUri(extName, MediaType.APPLICATION_JSON);
        log.info("{} login-page media-type={}", authType, mt);
        return wingsAuthPageHandler.response(em, mt, request, response);
    }

    @ApiOperation(value = "验证一次性token是否有效，oauth2使用state作为token", notes = "message='authing'为进行中")
    @PostMapping(value = "/auth/nonce/check.json")
    public R<String> tokenNonce(@RequestHeader("token") String token) {
        final String sid = NonceTokenSessionHelper.authNonce(token);
        if (sid == null) {
            return R.ng();
        }
        else if (sid.isEmpty()) {
            return R.ng("authing");
        }
        else {
            return R.okData(sid);
        }
    }

    @ApiOperation(value = "登出接口，有filter处理，仅做文档", notes = "默认失效Session，参考wings.warlock.security.logout-url")
    @RequestMapping(value = "${wings.warlock.security.logout-url}", method = {RequestMethod.POST, RequestMethod.GET})
    public String logout() {
        return "handler by filter, never here";
    }


    @SuppressWarnings("MVCPathVariableInspection")
    @ApiOperation(value = "登录验证接口，有filter处理，仅做文档", notes = "根据类型自动处理，参考 wings.warlock.security.login-url")
    @RequestMapping(value = "${wings.warlock.security.login-url}", method = {RequestMethod.POST, RequestMethod.GET})
    public String login(@PathVariable("authType") String authType,
                        @RequestParam(value = "username", defaultValue = "参考 wings.warlock.security.username-para") String username,
                        @RequestParam(value = "password", defaultValue = "参考 wings.warlock.security.password-para") String password) {
        log.info("authType={}, username={}, password={}", authType, username, password);
        return "handler by filter, never here";
    }
}
