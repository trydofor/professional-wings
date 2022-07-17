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
import org.springframework.test.annotation.DirtiesContext;
import pro.fessional.wings.slardar.httprest.OkHttpClientHelper;

/**
 * https://docs.spring.io/spring-framework/docs/current/reference/html/testing.html#testcontext-parallel-test-execution
 *
 * @author trydofor
 * @since 2021-03-09
 */

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class NonceLoginTest {

    @Setter(onMethod_ = {@Value("${local.server.port}")})
    private int port;

    @Setter(onMethod_ = {@Autowired})
    private OkHttpClient okHttpClient;

    @Test
    public void testTestNyLogin() {
        final String host = "http://127.0.0.1:" + port;
//        final String host = "http://localhost:" + port;

        final Response r1 = OkHttpClientHelper.execute(okHttpClient, new Request.Builder().url(host + "/auth/console-nonce.json?username=test_ny"), false);
        String nonce = OkHttpClientHelper.extractString(r1, false);
        log.warn("get nonce for test_ny, nonce=" + nonce);
        Assertions.assertEquals(200, r1.code(), "如果失败，单独执行，排除Event干扰");

        final Response r2 = OkHttpClientHelper.execute(okHttpClient, new Request.Builder().url(host + "/auth/username/login.json?username=test_ny&password=" + nonce), false);
        String login = OkHttpClientHelper.extractString(r2, false);
        log.warn("get login res = " + login);
        Assertions.assertTrue(login.contains("true"), "如果失败，单独执行，排除Event干扰");

        final Response r4 = OkHttpClientHelper.execute(okHttpClient, new Request.Builder().url(host + "/auth/list-zoneid.json"), false);
        String zones = OkHttpClientHelper.extractString(r4, false);
        log.warn("get zones = " + zones);
        Assertions.assertTrue(zones.contains("America/New_York"));
        Assertions.assertTrue(zones.contains("2021-01-01 05:00:00"));
    }
}
