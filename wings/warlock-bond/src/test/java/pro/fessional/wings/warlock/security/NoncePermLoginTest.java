package pro.fessional.wings.warlock.security;

import io.qameta.allure.TmsLink;
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
import pro.fessional.mirana.time.Sleep;
import pro.fessional.wings.slardar.httprest.okhttp.OkHttpClientHelper;

import java.util.Locale;

/**
 * <a href="https://docs.spring.io/spring-framework/docs/current/reference/html/testing.html#testcontext-parallel-test-execution">Parallel Test Execution</a>
 *
 * @author trydofor
 * @since 2021-03-09
 */

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
        "wings.warlock.urlmap.admin-authn-danger=" + NoncePermLoginTest.DangerUrl
})
@Slf4j
class NoncePermLoginTest {

    public static final String DangerUrl = "/test/authn/danger.json";

    @Setter(onMethod_ = {@Value("http://localhost:${local.server.port}")})
    private String host;

    @Setter(onMethod_ = {@Autowired})
    private OkHttpClient okHttpClient;

    @Test
    @TmsLink("C14014")
    public void testRootLogin() {
        OkHttpClientHelper.postJson(okHttpClient, host + DangerUrl, "{\"userId\":1,\"danger\":false}");

        final Response r1 = OkHttpClientHelper.execute(okHttpClient, new Request.Builder().url(host + "/auth/console-nonce.json?username=root"), false);
        String nonce = OkHttpClientHelper.extractString(r1, false);
        log.warn("get nonce for root, nonce=" + nonce);
        Assertions.assertEquals(200, r1.code());
        Sleep.ignoreInterrupt(1000); // wait for event sync

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
    @TmsLink("C14015")
    public void testDangerLocked() {
        log.warn("current locale = {}", Locale.getDefault());
        OkHttpClientHelper.postJson(okHttpClient, host + DangerUrl, "{\"userId\":1,\"danger\":true}");
        {
            final Response r2 = OkHttpClientHelper.execute(okHttpClient, new Request.Builder().url(host + "/auth/username/login.json?username=root&password=BAD&locale=zh"), false);
            String login = OkHttpClientHelper.extractString(r2, false);
            log.warn("testDanger-a get login res = " + login);
            Assertions.assertTrue(login.contains("error.authn.locked"));
            Assertions.assertTrue(login.contains("账号已锁定"), "check i18n config");
        }

        OkHttpClientHelper.postJson(okHttpClient, host + DangerUrl, "{\"userId\":1,\"danger\":false}");
        {
            final Response r1 = OkHttpClientHelper.execute(okHttpClient, new Request.Builder().url(host + "/auth/console-nonce.json?username=root"), false);
            String nonce = OkHttpClientHelper.extractString(r1, false);
            log.warn("testDanger-b get nonce for root, nonce=" + nonce);
            Assertions.assertEquals(200, r1.code());
            Sleep.ignoreInterrupt(1000); // wait for event sync

            final Response r2 = OkHttpClientHelper.execute(okHttpClient, new Request.Builder().url(host + "/auth/username/login.json?username=root&password=" + nonce), false);
            String login = OkHttpClientHelper.extractString(r2, false);
            log.warn("testDanger-b get login res = " + login);
            Assertions.assertTrue(login.contains("true"));
        }

        for (int i = 0; i < 6; i++) {
            final Response r2 = OkHttpClientHelper.execute(okHttpClient, new Request.Builder().url(host + "/auth/username/login.json?username=root&password=BAD&locale=en"), false);
            String login = OkHttpClientHelper.extractString(r2, false);
            log.warn("testDanger-c " + i + " get login res = " + login);
            Assertions.assertTrue(login.contains("error.authn."));
            if (i > 2) {
                Assertions.assertTrue(login.contains("error.authn.failureWaiting"));
                Assertions.assertTrue(login.contains("retry after"), "check i18n config");
            }
        }

        OkHttpClientHelper.postJson(okHttpClient, host + DangerUrl, "{\"userId\":1,\"danger\":false}");
        {
            final Response r1 = OkHttpClientHelper.execute(okHttpClient, new Request.Builder().url(host + "/auth/console-nonce.json?username=root"), false);
            String nonce = OkHttpClientHelper.extractString(r1, false);
            log.warn("testDanger-d get nonce for root, nonce=" + nonce);
            Assertions.assertEquals(200, r1.code());
            Sleep.ignoreInterrupt(1000); // wait for event sync

            final Response r2 = OkHttpClientHelper.execute(okHttpClient, new Request.Builder().url(host + "/auth/username/login.json?username=root&password=" + nonce), false);
            String login = OkHttpClientHelper.extractString(r2, false);
            log.warn("testDanger-d get login res = " + login);
            Assertions.assertTrue(login.contains("true"));
        }
    }
}
