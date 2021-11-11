package pro.fessional.wings.warlock.controller.auth;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.session.web.http.HttpSessionIdResolver;
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
import pro.fessional.wings.slardar.servlet.resolver.WingsRemoteResolver;
import pro.fessional.wings.warlock.security.session.NonceTokenSessionHelper;
import pro.fessional.wings.warlock.spring.prop.WarlockEnabledProp;
import pro.fessional.wings.warlock.spring.prop.WarlockSecurityProp;
import pro.fessional.wings.warlock.spring.prop.WarlockUrlmapProp;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author trydofor
 * @since 2021-02-16
 */
@RestController
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = WarlockEnabledProp.Key$controllerAuth, havingValue = "true")
public class LoginPageController {

    private final WingsAuthPageHandler wingsAuthPageHandler;
    private final WingsAuthTypeParser wingsAuthTypeParser;
    private final WingsRemoteResolver wingsRemoteResolver;

    @Setter(onMethod_ = {@Autowired(required = false)})
    private HttpSessionIdResolver httpSessionIdResolver;

    @SuppressWarnings("MVCPathVariableInspection")
    @ApiOperation(value = "集成登录默认页，默认返回支持的type类表",
            notes = "①当鉴权失败时，重定向页面，status=401;"
                    + "②直接访问时返回status=200")
    @RequestMapping(value = "${" + WarlockUrlmapProp.Key$authLoginList + "}", method = {RequestMethod.POST, RequestMethod.GET})
    public ResponseEntity<?> loginList(@PathVariable("extName") String extName,
                                       HttpServletRequest request,
                                       HttpServletResponse response) {
        final MediaType mt = ContentTypeHelper.mediaTypeByUri(extName);
        log.info("default login-page media-type={}", mt);
        return wingsAuthPageHandler.response(Null.Enm, mt, request, response);
    }

    @SuppressWarnings("MVCPathVariableInspection")
    @ApiOperation(value = "具体验证登录默认页，根据content-type自动返回",
            notes = "一般用于定制访问，如github页面重定向。支持state参数，用于构造oauth2的有意义的state，如重定向(http或'/'开头的跳转)；回写非空内容"
                    + "①当鉴权失败时，重定向页面，status=401;"
                    + "②直接访问时返回status=200;")
    @RequestMapping(value = "${" + WarlockUrlmapProp.Key$authLoginPage + "}", method = {RequestMethod.POST, RequestMethod.GET})
    public ResponseEntity<?> LoginPage(@PathVariable("authType") String authType,
                                       @PathVariable("extName") String extName,
                                       @RequestParam(value = "state", required = false) String state,
                                       HttpServletRequest request,
                                       HttpServletResponse response) {
        final Enum<?> em = wingsAuthTypeParser.parse(authType);
        final MediaType mt = ContentTypeHelper.mediaTypeByUri(extName, MediaType.APPLICATION_JSON);
        log.info("{} login-page media-type={}, state={}", authType, mt, state);
        return wingsAuthPageHandler.response(em, mt, request, response);
    }

    @SuppressWarnings("UastIncorrectHttpHeaderInspection")
    @ApiOperation(value = "验证一次性token是否有效，oauth2使用state作为token，要求和发行client具有相同ip，agent等header信息",
            notes = "①status=401时，无|过期|失败"
                    + "②status=200&success=false时，进行中，message=authing"
                    + "③status=200&success=true时成功，data=sessionId"
                    + "④在header中，也可以有session和cookie")
    @PostMapping(value = "${" + WarlockUrlmapProp.Key$authNonceCheck + "}")
    public ResponseEntity<R<?>> nonceCheck(@RequestHeader("token") String token, HttpServletRequest request, HttpServletResponse response) {
        final String sid = NonceTokenSessionHelper.authNonce(token, wingsRemoteResolver.resolveRemoteKey(request));
        if (sid == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(R.ng());
        }
        else {
            final R<?> r;
            if (sid.isEmpty()) {
                r = R.ng("authing");
            }
            else {
                r = R.okData(sid);
                if (httpSessionIdResolver != null) {
                    httpSessionIdResolver.setSessionId(request, response, sid);
                }
            }

            return ResponseEntity.ok(r);
        }
    }

    @ApiOperation(value = "登出接口，有filter处理，仅做文档", notes = "默认失效Session，参考wings.warlock.security.logout-url")
    @PostMapping(value = "${" + WarlockSecurityProp.Key$logoutUrl + "}")
    public String logout() {
        return "handler by filter, never here";
    }


    @SuppressWarnings("MVCPathVariableInspection")
    @ApiOperation(value = "登录接口，有filter处理，仅做文档",
            notes = "根据类型自动处理，参考 wings.warlock.security.login-url")
    @PostMapping(value = "${" + WarlockSecurityProp.Key$loginUrl + "}")
    public String login(@PathVariable("authType") String authType,
                        @RequestParam(value = "username", defaultValue = "参考 wings.warlock.security.username-para") String username,
                        @RequestParam(value = "password", defaultValue = "参考 wings.warlock.security.password-para") String password) {
        log.info("authType={}, username={}, password={}", authType, username, password);
        return "handler by filter, never here";
    }
}
