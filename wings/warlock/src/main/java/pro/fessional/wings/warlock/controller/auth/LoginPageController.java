package pro.fessional.wings.warlock.controller.auth;

import io.swagger.v3.oas.annotations.Operation;
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
import pro.fessional.wings.slardar.security.WingsAuthHelper;
import pro.fessional.wings.slardar.security.WingsAuthPageHandler;
import pro.fessional.wings.slardar.security.WingsAuthTypeParser;
import pro.fessional.wings.slardar.servlet.ContentTypeHelper;
import pro.fessional.wings.slardar.servlet.resolver.WingsRemoteResolver;
import pro.fessional.wings.warlock.security.justauth.AuthStateBuilder;
import pro.fessional.wings.warlock.security.session.NonceTokenSessionHelper;
import pro.fessional.wings.warlock.spring.prop.WarlockEnabledProp;
import pro.fessional.wings.warlock.spring.prop.WarlockUrlmapProp;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

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
    @Operation(summary = "集成登录默认页，默认返回支持的type类表", description =
            "# Usage \n"
            + "列出支持的登录方式。具体恢复内容，以根据extName和request.ContentType推测的MediaType确定\n"
            + "比如`html`和`json`扩展名，默认实现中，结果都以json形式返回\n"
            + "## Params \n"
            + "* @param extName - PathVariable，扩展名，如html,json\n"
            + "## Returns \n"
            + "* @return {401} 当鉴权失败，有系统forward时 \n"
            + "* @return {200} 直接访问或redirect时 \n"
            + "")
    @RequestMapping(value = "${" + WarlockUrlmapProp.Key$authLoginList + "}", method = {RequestMethod.POST, RequestMethod.GET})
    public ResponseEntity<?> loginList(@PathVariable(WingsAuthHelper.ExtName) String extName,
                                       HttpServletRequest request,
                                       HttpServletResponse response) {
        final MediaType mt = ContentTypeHelper.mediaTypeByUri(extName);
        log.info("default login-page media-type={}", mt);
        return wingsAuthPageHandler.response(Null.Enm, mt, request, response);
    }

    @SuppressWarnings("MVCPathVariableInspection")
    @Operation(summary = "具体验证登录默认页，根据content-type及extName规则做相应的处理", description =
            "# Usage \n"
            + "一般用于构造访问入口，如Oauth2登录的第三方路径和参数；获取反扒登录的验证码\n"
            + "需要注意state是数组，是spring支持的http协议的参数数组，如`a=1&a=2&a=3`\n"
            + "```bash \n"
            + "curl -X POST 'http://localhost:8084/auth/login-page.json' \\\n"
            + "--data 'authType=github&state=/order-list&state=http://localhost:8080&state=&host=localhost:8080'\n"
            + "curl -X GET  \"http://localhost:8084/auth/login-page.json\\\n"
            + "?authType=github&host=localhost:8080&state=/order-list&state=http://localhost%3A8080&state=\"\n"
            + "```\n"
            + "## Params \n"
            + "* @param extName  - PathVariable 辅助构造返回数据 \n"
            + "* @param authType - PathVariable 验证类型，系统配置项，可由【集成登录】查看，比如email,github \n"
            + "* @param authZone - 辅助验证参数，可关联权限等 \n"
            + "* @param {string[]} state - 构造Oauth2的state，MessageFormat格式，state[0]作为Format的key,state整体是Format的参数; \n"
            + "* @param host - 构造Oauth2的重定向host，以减少跨域 \n"
            + "## Returns \n"
            + "* @return {401} 当鉴权失败，有系统forward时 \n"
            + "* @return {200} 直接访问或redirect时 \n"
            + "")
    @RequestMapping(value = "${" + WarlockUrlmapProp.Key$authLoginPage + "}", method = {RequestMethod.POST, RequestMethod.GET})
    public ResponseEntity<?> LoginPage(@PathVariable(WingsAuthHelper.ExtName) String extName,
                                       @PathVariable(WingsAuthHelper.AuthType) String authType,
                                       @RequestParam(value = WingsAuthHelper.AuthZone, required = false) String authZone,
                                       @RequestParam(value = AuthStateBuilder.ParamState, required = false) List<String> state,
                                       @RequestParam(value = "host", required = false) String host,
                                       HttpServletRequest request,
                                       HttpServletResponse response) {
        final Enum<?> em = wingsAuthTypeParser.parse(authType);
        final MediaType mt = ContentTypeHelper.mediaTypeByUri(extName, MediaType.APPLICATION_JSON);
        log.info("login-page authType={}, authZone={}, mediaType={}, state={}, host={}", authType, authZone, mt, state, host);
        return wingsAuthPageHandler.response(em, mt, request, response);
    }

    @SuppressWarnings("MVCPathVariableInspection")
    @Operation(summary = "具体验证登录默认页，参考" + WarlockUrlmapProp.Key$authLoginPage, description =
            "# Usage \n"
            + "把" + WingsAuthHelper.AuthType + "参数从PathVariable变为RequestParam\n"
            + "")
    @RequestMapping(value = "${" + WarlockUrlmapProp.Key$authLoginPage2 + "}", method = {RequestMethod.POST, RequestMethod.GET})
    public ResponseEntity<?> LoginPage2(@PathVariable(WingsAuthHelper.ExtName) String extName,
                                        @RequestParam(value = WingsAuthHelper.AuthType, required = false) String authType,
                                        @RequestParam(value = WingsAuthHelper.AuthZone, required = false) String authZone,
                                        @RequestParam(value = AuthStateBuilder.ParamState, required = false) List<String> state,
                                        @RequestParam(value = "host", required = false) String host,
                                        HttpServletRequest request,
                                        HttpServletResponse response) {
        final Enum<?> em = wingsAuthTypeParser.parse(authType);
        final MediaType mt = ContentTypeHelper.mediaTypeByUri(extName, MediaType.APPLICATION_JSON);
        log.info("login-page authType={}, authZone={}, mediaType={}, state={}, host={}", authType, authZone, mt, state, host);
        return wingsAuthPageHandler.response(em, mt, request, response);
    }

    @SuppressWarnings("UastIncorrectHttpHeaderInspection")
    @Operation(summary = "验证一次性token是否有效", description =
            "# Usage \n"
            + "Oauth2使用state作为token，要求和发行client具有相同ip，agent等header信息\n"
            + "验证成功后，在header中，可同样获取login时的session和cookie\n"
            + "## Params \n"
            + "* @param token - RequestHeader Oauth2使用state作为token\n"
            + "## Returns \n"
            + "* @return {401} 无|过期|失败 \n"
            + "* @return {200 | Result(false, message='authing')} 验证进行中 \n"
            + "* @return {200 | Result(true, data=sessionId)} 验证成功 \n"
            + "")
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
}
