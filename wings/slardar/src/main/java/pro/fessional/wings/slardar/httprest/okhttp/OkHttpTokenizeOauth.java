package pro.fessional.wings.slardar.httprest.okhttp;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pro.fessional.mirana.code.RandCode;
import pro.fessional.mirana.time.ThreadNow;
import pro.fessional.wings.slardar.context.Now;
import pro.fessional.wings.slardar.jackson.JacksonHelper;

/**
 * <a href="https://developer.fedex.com/api/en-us/catalog/authorization/v1/docs.html">fedex authorization v1</a>
 *
 * @author trydofor
 * @since 2022-11-26
 */
@Setter @Getter
@Slf4j
public class OkHttpTokenizeOauth implements OkHttpTokenClient.Tokenize {

    public static final String AuthHeader = "Authorization";
    public static final String BearerPrefix = "Bearer ";
    public static final String AuthorizationCode = "authorization_code";
    public static final String ClientCredentials = "client_credentials";
    public static final String RefreshToken = "refresh_token";

    private String clientId;
    private String clientSecret;
    private String redirectUri;
    private String scopes;
    private String authorizeUrl;
    private String accessTokenUrl;
    private String headerAccept = "application/json";
    private String headerUserAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/105.0.0.0 Safari/537.36";

    /**
     * Only support AuthorizationCode ClientCredentials
     */
    private String keyGrantType = "grant_type";
    private String valRefreshToken = RefreshToken;
    private String valAccessToken = ClientCredentials;
    private String keyRefreshToken = "refresh_token";
    private String keyClientId = "client_id";
    private String keyRedirectUri = "redirect_uri";
    private String keyResponseType = "response_type";
    private String valResponseTypeCode = "code";
    private String keyScope = "scope";
    private String keyState = "state";
    private String keyAuthorizationCode = "authorization_code";
    private String keyCode = "code";
    private String keyClientSecret = "client_secret";

    private String keyAccessToken = "access_token";
    private String keyExpiresIn = "expires_in";

    /**
     * expiration (ms) of buffer, should avoid expiration
     */
    private int expireBuff = 30_000;
    private transient Token token;

    @Override
    public boolean needToken(@NotNull Request request) {
        final String ah = request.header(AuthHeader);
        return ah == null || !ah.startsWith(BearerPrefix);
    }

    @Override
    public boolean fillToken(Request.Builder builder) {
        final Token tkn = token;
        if (tkn == null) return false;

        final long now = ThreadNow.millis();
        if (now > tkn.expired) return false;

        builder.header(AuthHeader, tkn.access);
        return true;
    }

    @Override
    public boolean initToken(@NotNull Call.Factory callFactory) {
        final Token tkn = token;

        Token newTkn = null;
        if (tkn != null && tkn.refresh != null) {
            newTkn = fetchByRefresh(callFactory, tkn.refresh);
        }

        if (newTkn == null) {
            newTkn = fetchByGrantType(callFactory);
        }

        token = newTkn;
        return newTkn != null;
    }

    @SuppressWarnings("DuplicatedCode")
    protected Token fetchByRefresh(@NotNull Call.Factory callFactory, @NotNull String refresh) {
        // POST https://gitee.com/oauth/token?grant_type=refresh_token&refresh_token={refresh_token}
        final FormBody.Builder builder = buildRefresh(new FormBody.Builder())
                .add(keyGrantType, valRefreshToken)
                .add(keyRefreshToken, refresh);

        if (redirectUri != null) {
            builder.add(keyRedirectUri, redirectUri);
        }
        final Request request = buildCommonRequest(new Request.Builder())
                .url(accessTokenUrl)
                .post(builder.build())
                .build();

        final String body = OkHttpClientHelper.executeString(callFactory, request, false);
        return parseToken(body);
    }

    @Contract("_->param1")
    protected FormBody.Builder buildRefresh(@NotNull FormBody.Builder builder) {
        return builder;
    }

    protected Token fetchByGrantType(@NotNull Call.Factory callFactory) {
        final String code;
        final boolean notnull;
        if (ClientCredentials.equals(valAccessToken)) {
            code = null;
            notnull = false;
        }
        else if (AuthorizationCode.equals(valAccessToken)) {
            code = fetchAuthorizationCode(callFactory);
            notnull = true;
        }
        else {
            log.info("valAccessToken must is {} or {}", ClientCredentials, AuthorizationCode);
            return null;
        }

        if (notnull && code == null) {
            return null;
        }

        return fetchAccessToken(callFactory, code);
    }

    @SuppressWarnings("DuplicatedCode")
    @Nullable
    protected Token fetchAccessToken(@NotNull Call.Factory callFactory, String code) {
        // POST https://gitee.com/oauth/token
        // ?grant_type=authorization_code
        // &code={code}
        // &client_id={client_id}
        // &redirect_uri={redirect_uri}
        // &client_secret={client_secret}
        final FormBody.Builder builder = buildAccessToken(new FormBody.Builder())
                .add(keyGrantType, valAccessToken)
                .add(keyClientId, clientId)
                .add(keyClientSecret, clientSecret);

        if (redirectUri != null) {
            builder.add(keyRedirectUri, redirectUri);
        }

        if (code != null) {
            builder.add(keyCode, code);
        }

        final Request request = buildCommonRequest(new Request.Builder())
                .url(accessTokenUrl)
                .post(builder.build())
                .build();

        final String body = OkHttpClientHelper.executeString(callFactory, request, false);
        return parseToken(body);
    }

    @Contract("_->param1")
    protected FormBody.Builder buildAccessToken(@NotNull FormBody.Builder builder) {
        return builder;
    }

    @SneakyThrows
    @Nullable
    protected String fetchAuthorizationCode(@NotNull Call.Factory callFactory) {
        // GET https://gitee.com/oauth/authorize
        // ?client_id={client_id}
        // &redirect_uri={redirect_uri}
        // &response_type=code
        // &scope=user_info%20projects%20pull_requests
        final HttpUrl httpUrl = HttpUrl.parse(authorizeUrl);
        if (httpUrl == null) return null;

        final String state = RandCode.numlet(16);
        HttpUrl.Builder builder = buildAuthorizationCode(httpUrl.newBuilder())
                .addQueryParameter(keyClientId, clientId)
                .addQueryParameter(keyResponseType, valResponseTypeCode)
                .addQueryParameter(keyState, state);
        if (redirectUri != null) {
            builder.addQueryParameter(keyRedirectUri, redirectUri);
        }
        if (scopes != null) {
            builder.addQueryParameter(keyScope, scopes);
        }

        final Request request = buildCommonRequest(new Request.Builder())
                .url(builder.build())
                .get()
                .build();

        final Response response = OkHttpClientHelper.execute(callFactory, request, false);
        return parseAuthorizationCode(response, state);
    }

    @Contract("_->param1")
    protected HttpUrl.Builder buildAuthorizationCode(@NotNull HttpUrl.Builder builder) {
        return builder;
    }

    @SneakyThrows
    protected String parseAuthorizationCode(@NotNull Response response, @NotNull String state) {
        final HttpUrl acUrl;
        String loc = response.header("Location");
        if (loc != null) {
            acUrl = HttpUrl.parse(loc);
        }
        else {
            acUrl = response.request().url();
        }

        if (acUrl != null && state.equals(acUrl.queryParameter(keyState))) {
            final String cd = acUrl.queryParameter(keyCode);
            if (cd != null) return cd;
        }

        final ResponseBody body = response.body();
        if (body != null) {
            if (headerAccept.contains("json") || headerAccept.contains("xml")) {
                final JsonNode node = JacksonHelper.object(body.string());
                return JacksonHelper.getString(node, keyCode, null);
            }
        }
        return null;
    }

    @Contract("_->param1")
    protected Request.Builder buildCommonRequest(@NotNull Request.Builder builder) {
        return builder.header("Accept", headerAccept)
                      .header("User-Agent", headerUserAgent);
    }

    @SneakyThrows
    protected Token parseToken(String str) {
        if (str == null) return null;

        final JsonNode node;
        if (headerAccept.contains("json") || headerAccept.contains("xml")) {
            node = JacksonHelper.object(str);
        }
        else {
            return null;
        }

        final String act = JacksonHelper.getString(node, keyAccessToken, null);
        final int exp = JacksonHelper.getInt(node, keyExpiresIn, 0) * 1000;
        if (act == null || exp < expireBuff) return null;

        long ms = Now.millis() + exp - expireBuff;
        final String rft = JacksonHelper.getString(node, keyRefreshToken, null);

        return new Token(ms, BearerPrefix + act, rft);
    }

    @Data
    public static class Token {
        private final long expired;
        private final String access;
        private final String refresh;
    }
}
