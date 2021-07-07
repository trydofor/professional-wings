package pro.fessional.wings.warlock.security;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import pro.fessional.wings.slardar.httprest.OkHttpClientHelper;

/**
 * @author trydofor
 * @since 2021-03-09
 */

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
class NonceLoginTest {

    @Setter(onMethod_ = {@Value("${local.server.port}")})
    private int port;

    @Setter(onMethod_ = {@Autowired})
    private OkHttpClient okHttpClient;

    @Test
    public void testRootLogin() {

        final String host = "http://127.0.0.1:" + port;
        final Response r1 = OkHttpClientHelper.execute(okHttpClient, new Request.Builder().url(host + "/auth/console-nonce.json?username=root"), false);
        String nonce = OkHttpClientHelper.extractString(r1, false);
        log.warn("get nonce for root, nonce=" + nonce);
        Assertions.assertEquals(200, r1.code());

        final Response r2 = OkHttpClientHelper.execute(okHttpClient, new Request.Builder().url(host + "/auth/username/login.json?username=root&password=" + nonce), false);
        String login = OkHttpClientHelper.extractString(r2, false);
        log.warn("get login res = " + login);
        Assertions.assertTrue(login.contains("true"));

        final Response r3 = OkHttpClientHelper.execute(okHttpClient, new Request.Builder().url(host + "/auth/list-auth.json"), false);
        String auths = OkHttpClientHelper.extractString(r3, false);
        log.warn("get auths = " + auths);
        Assertions.assertTrue(auths.contains("ROLE_ROOT"));
    }

    @Test
    public void testTestNyLogin() {
        final String host = "http://127.0.0.1:" + port;
//        final String host = "http://localhost:" + port;

        final Response r1 = OkHttpClientHelper.execute(okHttpClient, new Request.Builder().url(host + "/auth/console-nonce.json?username=test_ny"), false);
        String nonce = OkHttpClientHelper.extractString(r1, false);
        log.warn("get nonce for test_ny, nonce=" + nonce);
        Assertions.assertEquals(200, r1.code());

        final Response r2 = OkHttpClientHelper.execute(okHttpClient, new Request.Builder().url(host + "/auth/username/login.json?username=test_ny&password=" + nonce), false);
        String login = OkHttpClientHelper.extractString(r2, false);
        log.warn("get login res = " + login);
        Assertions.assertTrue(login.contains("true"));

        final Response r4 = OkHttpClientHelper.execute(okHttpClient, new Request.Builder().url(host + "/auth/list-zoneid.json"), false);
        String zones = OkHttpClientHelper.extractString(r4, false);
        log.warn("get zones = " + zones);
        Assertions.assertTrue(zones.contains("America/New_York"));
        Assertions.assertTrue(zones.contains("2021-01-01 00:00:00"));
    }
}
