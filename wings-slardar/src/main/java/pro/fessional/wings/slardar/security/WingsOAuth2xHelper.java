package pro.fessional.wings.slardar.security;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.http.OAuth2ErrorHandler;
import org.springframework.security.oauth2.client.token.grant.password.ResourceOwnerPasswordResourceDetails;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import pro.fessional.wings.slardar.servlet.TypedRequestUtil;
import pro.fessional.wings.slardar.servlet.WingsRequestWrapper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static pro.fessional.wings.slardar.security.WingsOAuth2xContext.CLIENT_ID;
import static pro.fessional.wings.slardar.security.WingsOAuth2xContext.CLIENT_ID_ALIAS;
import static pro.fessional.wings.slardar.security.WingsOAuth2xContext.GRANT_TYPE;
import static pro.fessional.wings.slardar.security.WingsOAuth2xContext.GRANT_TYPE_ALIAS;
import static pro.fessional.wings.slardar.security.WingsOAuth2xContext.OAUTH_PASSWORD_ALIAS;
import static pro.fessional.wings.slardar.security.WingsOAuth2xContext.SCOPE;
import static pro.fessional.wings.slardar.security.WingsOAuth2xContext.STATE;

/**
 * 仅支持 oauth2x的password扩展模式登录，其他使用spring官方方式。
 *
 * @author trydofor
 * @since 2019-11-29
 */
@Setter
@Getter
public class WingsOAuth2xHelper {

    private String thirdTokenKey;
    private String renewTokenKey;
    private String tokenLiveKey;
    private HttpHeaders headers;


    public WingsRequestWrapper wrapRequest(HttpServletRequest request, Logout info) {
        final WingsRequestWrapper result;
        if (request instanceof WingsRequestWrapper) {
            result = (WingsRequestWrapper) request;
        } else {
            result = new WingsRequestWrapper(request);
        }

        result.putParameter("access_token", info.accessToken);
        result.setMethod("POST");
        return result;
    }

    public WingsRequestWrapper wrapRequest(HttpServletRequest request, Login info) {
        final WingsRequestWrapper result;
        if (request instanceof WingsRequestWrapper) {
            result = (WingsRequestWrapper) request;
        } else {
            result = new WingsRequestWrapper(request);
        }

        // see WingsOAuth2xFilter#wrapIfNeed
        result.putParameter(CLIENT_ID, info.clientId);
        result.putParameter(GRANT_TYPE, "password");

        result.putParameter(CLIENT_ID_ALIAS, info.clientIdAlias);
        result.putParameter(GRANT_TYPE_ALIAS, info.grantTypeAlias);
        result.putParameter(OAUTH_PASSWORD_ALIAS, info.oauthPasswordAlias);

        //
        result.putParameter("username", info.username);
        result.putParameter("password", info.password);
        result.putParameter(SCOPE, info.scope);
        result.putParameter(STATE, info.state);
        result.putParameter(tokenLiveKey, info.accessTokenLive);
        result.putParameter(thirdTokenKey, info.accessToken3rd);
        result.putParameter(renewTokenKey, info.renewToken ? "true" : null);

        result.putParameter(info.params);
        result.setMethod("POST");
        return result;
    }

    /**
     * 使用forward登录，适用于自身就是AuthServer,
     * controller public void forwardLogin(HttpServletRequest request, HttpServletResponse response);
     * 实际返回值是 OAuth2AccessToken对应的Json
     *
     * @param request  请求
     * @param response 响应
     * @param info     登录信息
     * @see org.springframework.security.oauth2.provider.endpoint.TokenEndpoint#getAccessToken
     */
    public void login(HttpServletRequest request, HttpServletResponse response, Login info) {
        String uri = info.loginUrl;
        try {
            WingsRequestWrapper req = wrapRequest(request, info);
            WingsOAuth2xContext.set(TypedRequestUtil.getParameter(req.getParameterMap()));
            Authentication auth = new UsernamePasswordAuthenticationToken(info.getClientId(), null, Collections.emptyList());
            SecurityContextHolder.getContext().setAuthentication(auth);
            req.getRequestDispatcher(uri).forward(req, response);
        } catch (Exception e) {
            throw new IllegalStateException("can not forward to " + uri, e);
        }
    }

    /**
     * 使用 RestTemplate 远程请求登录。
     * <pre>
     * @ExceptionHandler(OAuth2Exception.class)
     * public ResponseEntity<OAuth2Exception> handleException(OAuth2Exception e) throws Exception {
     *    return new DefaultWebResponseExceptionTranslator().translate(e);
     * }
     * </pre>
     *
     * @param tmpl Rest模板
     * @param info 登录信息
     * @return 远程返回的token
     * @throws OAuth2Exception
     */
    public OAuth2AccessToken login(RestTemplate tmpl, Login info) throws OAuth2Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

        put(params, info.clientId, info.clientIdAlias == null ? CLIENT_ID : info.clientIdAlias);
        put(params, info.oauthPasswordAlias, info.grantTypeAlias == null ? GRANT_TYPE : info.grantTypeAlias);

        put(params, info.username, "username");
        put(params, info.password, "password");
        put(params, info.scope, SCOPE);
        put(params, info.state, STATE);
        put(params, info.accessTokenLive, tokenLiveKey);
        put(params, info.accessToken3rd, thirdTokenKey);
        put(params, info.renewToken ? "true" : null, renewTokenKey);

        params.setAll(info.params);

        WingsTerminalContext.Context ctx = SecurityContextUtil.getTerminalContext();
        HttpHeaders head = headers;
        if (ctx.getRemoteIp() != null) {
            head = new HttpHeaders(headers);
            head.add("X-Real-IP", ctx.getRemoteIp());
            head.add("User-Agent", ctx.getAgentInfo());
        }
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(params, head);
        ResourceOwnerPasswordResourceDetails resource = new ResourceOwnerPasswordResourceDetails();
        resource.setUsername(info.username);
        resource.setPassword(info.password);
        tmpl.setErrorHandler(new OAuth2ErrorHandler(resource));

        return tmpl.postForObject(info.loginUrl, entity, OAuth2AccessToken.class);
    }

    private void put(MultiValueMap<String, String> params, String value, String key) {
        if (value == null || key == null) return;
        params.add(key, value);
    }


    /**
     * 使用forward登出，适用于自身就是AuthServer
     *
     * @param request  请求
     * @param response 响应
     * @param info     登录信息
     */
    public void logout(HttpServletRequest request, HttpServletResponse response, Logout info) {
        String uri = info.logoutUrl;
        try {
            WingsRequestWrapper req = wrapRequest(request, info);
            req.getRequestDispatcher(uri).forward(req, response);
        } catch (Exception e) {
            throw new IllegalStateException("can not forward to " + uri, e);
        }
    }

    /**
     * 使用 RestTemplate 远程请求登出。
     *
     * @param tmpl Rest模板
     * @param info 登录信息
     */
    public String logout(RestTemplate tmpl, Logout info) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("access_token", info.accessToken);
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(params, headers);
        return tmpl.postForObject(info.logoutUrl, entity, String.class);
    }

    /**
     * 通过tokenstore登出
     *
     * @param store       store
     * @param accessToken 信息
     */
    public void logout(WingsTokenStore store, String accessToken) {
        if (store == null || accessToken == null) return;

        OAuth2AccessToken at = store.readAccessToken(accessToken);
        if (at == null) return;
        store.removeAccessToken(at);

        OAuth2RefreshToken rt = at.getRefreshToken();
        if (rt == null) return;
        store.removeRefreshToken(rt);
    }

    @Data
    public static class Logout {
        private String logoutUrl;
        private String accessToken;
    }

    @Data
    public static class Login {
        private String loginUrl = "/oauth/token";

        private String clientId;

        private String clientIdAlias = "client_id";
        private String grantTypeAlias = "grant_type"; // grant-type-alias=gtp
        private String oauthPasswordAlias = "passwordx";

        private String username;
        private String password;
        private String scope;
        private String state;
        private String accessTokenLive;
        private String accessToken3rd;
        private boolean renewToken;

        private Map<String, String> params = new HashMap<>();
    }
}
