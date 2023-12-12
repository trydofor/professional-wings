package pro.fessional.wings.warlock.controller.auth;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
import pro.fessional.wings.silencer.spring.boot.ConditionalWingsEnabled;
import pro.fessional.wings.slardar.security.WingsAuthHelper;
import pro.fessional.wings.slardar.security.WingsAuthPageHandler;
import pro.fessional.wings.slardar.security.WingsAuthTypeParser;
import pro.fessional.wings.slardar.servlet.ContentTypeHelper;
import pro.fessional.wings.slardar.servlet.resolver.WingsRemoteResolver;
import pro.fessional.wings.warlock.security.justauth.AuthStateBuilder;
import pro.fessional.wings.warlock.security.session.NonceTokenSessionHelper;
import pro.fessional.wings.warlock.spring.prop.WarlockEnabledProp;
import pro.fessional.wings.warlock.spring.prop.WarlockUrlmapProp;

import java.util.List;

/**
 * @author trydofor
 * @since 2021-02-16
 */
@RestController
@ConditionalWingsEnabled(abs = WarlockEnabledProp.Key$mvcLogin)
@RequiredArgsConstructor
@Slf4j
public class LoginPageController {

    private final WingsAuthPageHandler wingsAuthPageHandler;
    private final WingsAuthTypeParser wingsAuthTypeParser;
    private final WingsRemoteResolver wingsRemoteResolver;

    @Setter(onMethod_ = {@Autowired(required = false)})
    private HttpSessionIdResolver httpSessionIdResolver;

    @SuppressWarnings("MVCPathVariableInspection")
    @Operation(summary = "Default integrated login page, return list of supported types", description = """
            # Usage
            Lists the supported logon type. The response content is determined by the MediaType
            inferred from extName and request.ContentType. e.g. for `html` and `json` extensions,
            the default implementation returns in json.
            ## Params
            * @param extName - PathVariable, extName (.html, .json)
            ## Returns
            * @return {401} auth failed and forward
            * @return {200} OK or redirect
            """)
    @RequestMapping(value = "${" + WarlockUrlmapProp.Key$authLoginList + "}", method = {RequestMethod.POST, RequestMethod.GET})
    public ResponseEntity<?> loginList(@PathVariable(WingsAuthHelper.ExtName) String extName,
                                       HttpServletRequest request,
                                       HttpServletResponse response) {
        final MediaType mt = ContentTypeHelper.mediaTypeByUri(extName);
        log.debug("default login-page media-type={}", mt);
        return wingsAuthPageHandler.response(Null.Enm, mt, request, response);
    }

    @SuppressWarnings("MVCPathVariableInspection")
    @Operation(summary = "The specific login page, according to content-type and extName", description = """
            # Usage
            Generally used to construct the login entry, such as the 3rd path and param of the Oauth2 login;
            Note, `state` is an array, is spring supported http param array, such as `a=1&a=2&a=3`
            ```bash
            curl -X POST 'http://localhost:8084/auth/login-page.json' \\
            --data 'authType=github&state=/order-list&state=http://localhost:8080&state=&host=localhost:8080'
            curl -X GET  "http://localhost:8084/auth/login-page.json\\
            ?authType=github&host=localhost:8080&state=/order-list&state=http://localhost%3A8080&state="
            ```
            ## Params
            * @param extName  - PathVariable extName (.html, .json)
            * @param authType - PathVariable auth type in the config (email, github)
            * @param authZone - help to grant perm
            * @param {string[]} state - Oauth2 state in MessageFormat: `state[0]` is Format's key, all `state` are Format's args;
            * @param host - Oauth2 redirect host to avoid CORS
            ## Returns
            * @return {401} auth failed and forward
            * @return {200} OK or redirect
            """)
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
        log.debug("login-page authType={}, authZone={}, mediaType={}, state={}, host={}", authType, authZone, mt, state, host);
        return wingsAuthPageHandler.response(em, mt, request, response);
    }

    @SuppressWarnings("MVCPathVariableInspection")
    @Operation(summary = "The specific login page, see " + WarlockUrlmapProp.Key$authLoginPage, description =
            "# Usage \n"
            + "change " + WingsAuthHelper.AuthType + "from PathVariable to RequestParam\n"
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
        log.debug("login-page authType={}, authZone={}, mediaType={}, state={}, host={}", authType, authZone, mt, state, host);
        return wingsAuthPageHandler.response(em, mt, request, response);
    }

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
                    .body(R.NG);
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
}
