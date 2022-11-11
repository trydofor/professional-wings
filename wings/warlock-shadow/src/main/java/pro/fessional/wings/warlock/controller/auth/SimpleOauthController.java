package pro.fessional.wings.warlock.controller.auth;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import io.swagger.v3.oas.annotations.Operation;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import pro.fessional.mirana.code.RandCode;
import pro.fessional.mirana.data.Null;
import pro.fessional.mirana.tk.Ticket;
import pro.fessional.mirana.tk.TicketHelp;
import pro.fessional.wings.slardar.context.Now;
import pro.fessional.wings.slardar.servlet.resolver.WingsLocaleResolver;
import pro.fessional.wings.slardar.servlet.response.ResponseHelper;
import pro.fessional.wings.warlock.service.auth.WarlockTicketService;
import pro.fessional.wings.warlock.service.auth.WarlockTicketService.Pass;
import pro.fessional.wings.warlock.service.auth.WarlockTicketService.Term;
import pro.fessional.wings.warlock.service.auth.impl.SimpleTicketServiceImpl;
import pro.fessional.wings.warlock.spring.prop.WarlockEnabledProp;
import pro.fessional.wings.warlock.spring.prop.WarlockTicketProp;
import pro.fessional.wings.warlock.spring.prop.WarlockUrlmapProp;

import java.time.Duration;
import java.util.HashMap;

/**
 * 简单的模仿Oauth验证，方便测试和API使用，
 * https://docs.github.com/en/developers/apps/building-oauth-apps/authorizing-oauth-apps
 * https://www.oauth.com/oauth2-servers/authorization/the-authorization-response/
 *
 * @author trydofor
 * @since 2022-11-04
 */
@Slf4j
@Controller
@ConditionalOnProperty(name = WarlockEnabledProp.Key$controllerOauth, havingValue = "true")
public class SimpleOauthController implements InitializingBean {

    public static final String KeyClientId = "client_id";
    public static final String KeyClientSecret = "client_secret";
    public static final String KeyRedirectUri = "redirect_uri";
    public static final String KeyScope = "scope";
    public static final String KeyCode = "code";
    public static final String KeyState = "state";
    public static final String KeyAccept = "Accept";
    public static final String KeyError = "error";
    public static final String KeyErrorDescription = "error_description";
    public static final String KeyExpireIn = "expires_in";
    public static final String KeyAccessToken = "access_token";
    public static final String KeyRefreshToken = "refresh_token";

    @Setter @Getter
    private Duration authCodeTtl = null;
    @Setter @Getter
    private Duration accessTokenTtl = null;

    @Setter(onMethod_ = {@Autowired(required = false)})
    private TicketHelp.Helper<String> helper = null;

    @Setter(onMethod_ = {@Autowired(required = false)})
    protected WarlockTicketService warlockTicketService = new SimpleTicketServiceImpl();

    @Setter(onMethod_ = {@Autowired})
    protected WingsLocaleResolver wingsLocaleResolver;

    @Setter(onMethod_ = {@Autowired(required = false)})
    protected WarlockTicketProp warlockTicketProp;


    @Operation(summary = "简单模拟Oauth2的AuthorizationCode授权", description =
            "# Usage \n"
            + "参考 https://docs.github.com/en/developers/apps/building-oauth-apps/authorizing-oauth-apps\n"
            + "参考 https://www.oauth.com/oauth2-servers/authorization/the-authorization-response\n"
            + "* 默认为标准的302重定向\n"
            + "* 当Accept: application/json返回json\n"
            + "* 当Accept: application/xml返回xml\n"
            + "* 错误时，以error,error_description为key返回\n"
            + "## Params \n"
            + "* @param client_id  - Required. The client ID \n"
            + "* @param redirect_uri - 重定向或直接返回json格式\n"
            + "* @param scope - 空格分隔的字符串\n"
            + "* @param state - 防SCRF攻击，原值返回数据\n"
            + "* @header Accept  - application/json返回json，application/xml返回xml \n"
            + "## Returns \n"
            + "* @return {302} redirect to redirect_uri\n"
            + "* @return {200} json/xml\n"
            + "")
    @RequestMapping(value = "${" + WarlockUrlmapProp.Key$oauthAuthorize + "}", method = {RequestMethod.POST, RequestMethod.GET})
    public ResponseEntity<String> authorize(@RequestParam(KeyClientId) String clientId,
                                            @RequestParam(value = KeyRedirectUri, required = false) String redirectUri,
                                            @RequestParam(value = KeyScope, required = false) String scope,
                                            @RequestParam(value = KeyState, required = false) String state,
                                            @RequestHeader(value = KeyAccept, required = false) String accept) {
        final OAuth data = new OAuth();
        final Pass pass = warlockTicketService.findPass(clientId);
        if (pass == null) {
            data.put(KeyError, "unauthorized_client");
            data.put(KeyErrorDescription, "the client is not allowed to request an authorization code");
        }
        else if (!warlockTicketService.checkScope(pass.getUserId(), scope)) {
            data.put(KeyError, "invalid_scope");
            data.put(KeyErrorDescription, "the requested scope is invalid or unknown");
        }
        else {
            final int seq = warlockTicketService.nextSeq(pass.getUserId(), Term.TypeAuthorizeCode);
            final long due = warlockTicketService.calcDue(authCodeTtl);
            Term term = new Term();
            term.setType(Term.TypeAuthorizeCode);
            term.setUserId(pass.getUserId());
            term.setScopes(scope);
            final Ticket tk = helper.encode(seq, due, Term.encode(term));

            data.put(KeyCode, tk.serialize());
            data.put(KeyExpireIn, authCodeTtl.toSeconds());
            data.put(KeyState, state);
            log.info("authorize for term={}", term);
        }

        return ResponseHelper.flatResponse(data, accept, redirectUri);
    }

    @Operation(summary = "简单模拟Oauth2，仅支持授权码模式", description =
            "# Usage \n"
            + "参考 https://docs.github.com/en/developers/apps/building-oauth-apps/authorizing-oauth-apps\n"
            + "参考 https://www.oauth.com/oauth2-servers/access-tokens/access-token-response\n"
            + "* 默认为标准的302重定向\n"
            + "* 当Accept: application/json返回json\n"
            + "* 当Accept: application/xml返回xml\n"
            + "* 错误时，以error,error_description为key返回\n"
            + "* 有效期内（默认1小时），可以使用access_token作为code再次刷新code\n"
            + "## Params \n"
            + "* @param client_id  - Required. The client ID \n"
            + "* @param client_secret  - Required. The client secret \n"
            + "* @param code - Required. AuthorizationCode \n"
            + "* @param redirect_uri - 重定向或直接返回数据 \n"
            + "* @header Accept  - application/json返回json，application/xml返回xml \n"
            + "## Returns \n"
            + "* @return {302} redirect to redirect_uri\n"
            + "* @return {200} json/xml\n"
            + "")
    @RequestMapping(value = "${" + WarlockUrlmapProp.Key$oauthAccessToken + "}", method = {RequestMethod.POST})
    public ResponseEntity<?> accessToken(@RequestParam(KeyClientId) String clientId,
                                         @RequestParam(KeyClientSecret) String clientSecret,
                                         @RequestParam(KeyCode) String code,
                                         @RequestParam(value = KeyRedirectUri, required = false) String redirectUri,
                                         @RequestHeader(value = KeyAccept, required = false) String accept) {
        final OAuth data = new OAuth();
        final Term term = parse(data, code);
        if (!data.isEmpty() || term == null) {
            return ResponseHelper.flatResponse(data, accept, redirectUri);
        }

        final Pass pass = warlockTicketService.findPass(clientId);
        if (pass == null || term.getUserId() != pass.getUserId() || !clientSecret.equals(pass.getSecret())) {
            data.put(KeyError, "invalid_client");
            data.put(KeyErrorDescription, "Client authentication failed");
            return ResponseHelper.flatResponse(data, accept, redirectUri);
        }

        final int seq = warlockTicketService.nextSeq(pass.getUserId(), Term.TypeAccessToken);
        final long due = warlockTicketService.calcDue(accessTokenTtl);
        term.setType(Term.TypeAccessToken);
        final Ticket tk1 = helper.encode(seq, due, Term.encode(term));

        final String token = tk1.serialize();
        data.put(KeyAccessToken, token);
        data.put(KeyRefreshToken, token);
        data.put(KeyExpireIn, accessTokenTtl.toSeconds());
        data.put(KeyScope, term.getScopes());

        log.info("accessToken for term={}", term);
        return ResponseHelper.flatResponse(data, accept, redirectUri);
    }

    @Operation(summary = "吊销AuthorizationCode或AccessToken授权，应付Token外泄情况", description =
            "# Usage \n"
            + "* 默认为标准的302重定向\n"
            + "* 当Accept: application/json返回json\n"
            + "* 当Accept: application/xml返回xml\n"
            + "* 错误时，以error,error_description为key返回\n"
            + "## Params \n"
            + "* @param code - Required. 有效的AuthorizationCode或AccessToken \n"
            + "* @param redirect_uri - 重定向或直接返回json格式\n"
            + "* @header Accept  - application/json返回json，application/xml返回xml \n"
            + "## Returns \n"
            + "* @return {302} redirect to redirect_uri\n"
            + "* @return {200} json/xml\n"
            + "")
    @RequestMapping(value = "${" + WarlockUrlmapProp.Key$oauthRevokeToken + "}", method = {RequestMethod.POST})
    public ResponseEntity<String> revokeToken(@RequestParam(KeyCode) String code,
                                              @RequestParam(value = KeyRedirectUri, required = false) String redirectUri,
                                              @RequestHeader(value = KeyAccept, required = false) String accept) {
        final OAuth data = new OAuth();
        final Term term = parse(data, code);
        if (data.isEmpty() && term != null) {
            warlockTicketService.revokeAll(term.getUserId());
            log.info("revoke all token, term={}", term);

            data.put(KeyAccessToken, Null.Str);
            data.put(KeyRefreshToken, Null.Str);
            data.put(KeyExpireIn, 0);
            data.put(KeyScope, term.getScopes());
        }

        return ResponseHelper.flatResponse(data, accept, redirectUri);
    }

    private Term parse(OAuth data, String code) {
        final Ticket tk = TicketHelp.parse(code, helper::accept);
        if (tk == null || tk.getPubDue() * 1000 < Now.millis()) {
            data.put(KeyError, "invalid_request");
            data.put(KeyErrorDescription, "invalid ticket");
            return null;
        }

        final Term term = Term.decode(helper.decode(tk));
        if (term == null) {
            data.put(KeyError, "invalid_request");
            data.put(KeyErrorDescription, "invalid ticket");
            return null;
        }

        if (!warlockTicketService.checkSeq(term.getUserId(), term.getType(), tk.getPubSeq())) {
            data.put(KeyError, "invalid_request");
            data.put(KeyErrorDescription, "invalid ticket");
        }
        return term;
    }

    @Override
    public void afterPropertiesSet() {
        if (helper == null) {
            String key = warlockTicketProp.getAesKey();
            if (key == null || key.isBlank()) {
                log.info("TicketHelp use random aes-key, may fail api-call in cluster");
                key = RandCode.strong(32);
            }
            helper = new TicketHelp.Ah1Help(warlockTicketProp.getPubMod(), key);
        }

        if (authCodeTtl == null) {
            authCodeTtl = warlockTicketProp.getCodeTtl();
        }
        if (accessTokenTtl == null) {
            accessTokenTtl = warlockTicketProp.getTokenTtl();
        }
    }

    @JacksonXmlRootElement(localName = "OAuth")
    public static class OAuth extends HashMap<String, Object> {
    }
}
