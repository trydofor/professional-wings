package pro.fessional.wings.warlock.controller.api;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import pro.fessional.wings.slardar.httprest.okhttp.OkHttpClientHelper;
import pro.fessional.wings.slardar.httprest.okhttp.OkHttpTokenClient;
import pro.fessional.wings.slardar.httprest.okhttp.OkHttpTokenizeLogin;
import pro.fessional.wings.slardar.httprest.okhttp.OkHttpTokenizeOauth;
import pro.fessional.wings.slardar.spring.prop.SlardarSessionProp;
import pro.fessional.wings.warlock.spring.prop.WarlockUrlmapProp;

/**
 * @author trydofor
 * @since 2022-11-16
 */
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "wings.warlock.apiauth.must-signature=false",
        })
@Slf4j
class OkHttpTokenizeTest {

    @Setter(onMethod_ = {@Autowired})
    private WarlockUrlmapProp urlmapProp;

    @Setter(onMethod_ = {@Autowired})
    private SlardarSessionProp slardarSessionProp;

    @Setter(onMethod_ = {@Value("http://localhost:${local.server.port}")})
    private String host;

    @Setter(onMethod_ = {@Autowired})
    private OkHttpClient okHttpClient;

    private final String client = "wings-trydofor";
    private final String secret = "wings-trydofor-secret";

    @Test
    public void testOauthAuthorizationCode() {
        OkHttpTokenizeOauth tokenize = new OkHttpTokenizeOauth();
        tokenize.setClientId(client);
        tokenize.setClientSecret(secret);
        tokenize.setValAccessToken(OkHttpTokenizeOauth.AuthorizationCode);
        tokenize.setScopes("api1 api2");
        tokenize.setRedirectUri(host);
        tokenize.setAuthorizeUrl(host + urlmapProp.getOauthAuthorize());
        tokenize.setAccessTokenUrl(host + urlmapProp.getOauthAccessToken());

        OkHttpTokenClient oauthClient = new OkHttpTokenClient(okHttpClient, tokenize);
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(host + "/api/oauth.json")
                .post(OkHttpClientHelper.EMPTY)
                .build();

        final String str = OkHttpClientHelper.executeString(oauthClient, request, false);
        Assertions.assertNotNull(str);
        Assertions.assertNotEquals("failed", str);
    }

    @Test
    public void testOauthClientCredentials() {
        OkHttpTokenizeOauth tokenize = new OkHttpTokenizeOauth();
        tokenize.setClientId(client);
        tokenize.setClientSecret(secret);
        tokenize.setAuthorizeUrl(host + urlmapProp.getOauthAuthorize());
        tokenize.setAccessTokenUrl(host + urlmapProp.getOauthAccessToken());

        OkHttpTokenClient oauthClient = new OkHttpTokenClient(okHttpClient, tokenize);
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(host + "/api/oauth.json")
                .post(OkHttpClientHelper.EMPTY)
                .build();

        final String str = OkHttpClientHelper.executeString(oauthClient, request, false);
        Assertions.assertNotNull(str);
        Assertions.assertNotEquals("failed", str);
    }

    @Test
    public void testFormLogin() {
        OkHttpTokenizeLogin tokenize = new OkHttpTokenizeLogin();
        tokenize.setLoginUrl(host + "/auth/username/login.json");
        tokenize.setKeyUsername("username");
        tokenize.setKeyPassword("password");
        tokenize.setUsername("trydofor");
        tokenize.setPassword("moMxVKXxA8Pe9XX9");
        tokenize.setHeaderAuth(slardarSessionProp.getHeaderName());

        OkHttpTokenClient oauthClient = new OkHttpTokenClient(okHttpClient, tokenize);
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(host + "/api/login.json")
                .post(OkHttpClientHelper.EMPTY)
                .build();

        final String str = OkHttpClientHelper.executeString(oauthClient, request, false);
        Assertions.assertNotNull(str);
        Assertions.assertNotEquals("failed", str);
    }
}
