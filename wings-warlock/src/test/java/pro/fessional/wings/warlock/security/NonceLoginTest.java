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

    @Setter(onMethod_ = {@Value("http://127.0.0.1:${local.server.port}")})
    private String host;

    @Setter(onMethod_ = {@Autowired})
    private OkHttpClient okHttpClient;

    @Test
    public void testNonceLogin() {

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
}
