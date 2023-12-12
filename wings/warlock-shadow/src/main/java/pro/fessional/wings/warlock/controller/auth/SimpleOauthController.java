package pro.fessional.wings.warlock.controller.auth;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import pro.fessional.wings.silencer.spring.boot.ConditionalWingsEnabled;
import pro.fessional.wings.slardar.servlet.response.ResponseHelper;
import pro.fessional.wings.warlock.service.auth.WarlockOauthService;
import pro.fessional.wings.warlock.service.auth.WarlockOauthService.OAuth;
import pro.fessional.wings.warlock.spring.prop.WarlockEnabledProp;
import pro.fessional.wings.warlock.spring.prop.WarlockUrlmapProp;

/**
 * Simple Oauth validation for testing and API use.
 * <a href="https://docs.github.com/en/developers/apps/building-oauth-apps/authorizing-oauth-apps">github authorizing-oauth-apps</a>
 * <a href="https://www.oauth.com/oauth2-servers/authorization/the-authorization-response/">authorization/the-authorization-response</a>
 *
 * @author trydofor
 * @since 2022-11-04
 */
@Controller
@ConditionalWingsEnabled(abs = WarlockEnabledProp.Key$mvcOauth)
@Slf4j
public class SimpleOauthController {

    @Setter(onMethod_ = {@Autowired})
    protected WarlockOauthService warlockOauthService;

    @Operation(summary = "Simple simulation of Oauth2 AuthorizationCode", description = """
            # Usage
            see https://docs.github.com/en/developers/apps/building-oauth-apps/authorizing-oauth-apps
            see https://www.oauth.com/oauth2-servers/authorization/the-authorization-response
            * Default standard 302 redirect
            * return json if Accept: application/json
            * return xml if Accept: application/xml
            * return error, error_description if error
            ## Params
            * @param client_id  - Required. The client ID
            * @param redirect_uri - redirect_uri if 302, or in json
            * @param scope - scope seperated by space
            * @param state - anti SCRF, return the raw value
            * @header Accept  - help to content type
            ## Returns
            * @return {302} redirect to redirect_uri
            * @return {200} json/xml
            """)
    @RequestMapping(value = "${" + WarlockUrlmapProp.Key$oauthAuthorize + "}", method = {RequestMethod.POST, RequestMethod.GET})
    public ResponseEntity<String> authorize(@RequestParam(WarlockOauthService.ClientId) String clientId,
                                            @RequestParam(value = WarlockOauthService.RedirectUri, required = false) String redirectUri,
                                            @RequestParam(value = WarlockOauthService.Scope, required = false) String scope,
                                            @RequestParam(value = WarlockOauthService.State, required = false) String state,
                                            @RequestHeader(value = "Accept", required = false) String accept,
                                            HttpServletRequest request) {
        final HttpSession session = request.getSession(false);
        final String sid = session != null ? session.getId() : null;
        final OAuth data = warlockOauthService.authorizeCode(clientId, scope, redirectUri, sid);
        data.put(WarlockOauthService.State, state);
        return ResponseHelper.flatResponse(data, accept, redirectUri);
    }

    @Operation(summary = "Simple simulation of Oauth2 authorization-code and client-credentials", description = """
            # Usage
            see https://docs.github.com/en/developers/apps/building-oauth-apps/authorizing-oauth-apps
            see https://www.oauth.com/oauth2-servers/access-tokens/authorization-code-request/
            see https://www.oauth.com/oauth2-servers/access-tokens/client-credentials/
            * Default standard 302 redirect
            * return json if Accept: application/json
            * return xml if Accept: application/xml
            * return error, error_description if error
            * during the validity period (defaut 1H), can refresh the code by passing access_token as the code.
            ## Params
            * @param client_id  - Required. The client ID
            * @param client_secret  - Required. The client secret
            * @param code - authorization_code if not empty, otherwise client_credentials
            * @param redirect_uri - redirect or in json
            * @header Accept  - help to content type
            ## Returns
            * @return {302} redirect to redirect_uri
            * @return {200} json/xml
            """)
    @RequestMapping(value = "${" + WarlockUrlmapProp.Key$oauthAccessToken + "}", method = {RequestMethod.POST})
    public ResponseEntity<?> accessToken(@RequestParam(WarlockOauthService.ClientId) String clientId,
                                         @RequestParam(WarlockOauthService.ClientSecret) String clientSecret,
                                         @RequestParam(value = WarlockOauthService.Code, required = false) String code,
                                         @RequestParam(value = WarlockOauthService.RedirectUri, required = false) String redirectUri,
                                         @RequestHeader(value = "Accept", required = false) String accept) {

        final OAuth data = warlockOauthService.accessToken(clientId, clientSecret, code);
        return ResponseHelper.flatResponse(data, accept, redirectUri);
    }

    @Operation(summary = "Revoke AuthorizationCode or AccessToken in case of Token leakage", description = """
            # Usage
            * Default standard 302 redirect
            * return json if Accept: application/json
            * return xml if Accept: application/xml
            * return error, error_description if error
            ## Params
            * @param code - Required. valid AuthorizationCode or AccessToken
            * @param redirect_uri - redirect or in json
            * @header Accept  - help to content type
            ## Returns
            * @return {302} redirect to redirect_uri
            * @return {200} json/xml
            """)
    @RequestMapping(value = "${" + WarlockUrlmapProp.Key$oauthRevokeToken + "}", method = {RequestMethod.POST})
    public ResponseEntity<String> revokeToken(@RequestParam(WarlockOauthService.ClientId) String clientId,
                                              @RequestParam(WarlockOauthService.Code) String code,
                                              @RequestParam(value = WarlockOauthService.RedirectUri, required = false) String redirectUri,
                                              @RequestHeader(value = "Accept", required = false) String accept) {
        final OAuth data = warlockOauthService.revokeToken(clientId, code);
        return ResponseHelper.flatResponse(data, accept, redirectUri);
    }
}
