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
import pro.fessional.wings.slardar.httprest.okhttp.OkHttpClientHelper;

import java.util.Locale;

import static pro.fessional.wings.warlock.security.NoncePermLoginTest.DangerUrl;

/**
 * <a href="https://docs.spring.io/spring-framework/docs/current/reference/html/testing.html#testcontext-parallel-test-execution">Parallel Test Execution</a>
 *
 * @author trydofor
 * @since 2021-03-09
 */

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
        "wings.warlock.urlmap.admin-authn-danger=" + DangerUrl
})
@Slf4j
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class NoncePermLoginTest {

    public static final String DangerUrl = "/test/authn/danger.json";

    @Setter(onMethod_ = {@Value("http://localhost:${local.server.port}")})
    private String host;

    @Setter(onMethod_ = {@Autowired})
    private OkHttpClient okHttpClient;

    @Test
    public void testRootLogin() {
        OkHttpClientHelper.postJson(okHttpClient, host + DangerUrl, "{\"userId\":1,\"danger\":false}");

        final Response r1 = OkHttpClientHelper.execute(okHttpClient, new Request.Builder().url(host + "/auth/console-nonce.json?username=root"), false);
        String nonce = OkHttpClientHelper.extractString(r1, false);
        log.warn("get nonce for root, nonce=" + nonce);
        Assertions.assertEquals(200, r1.code(), "Need init database via BootDatabaseTest");

        final Response r2 = OkHttpClientHelper.execute(okHttpClient, new Request.Builder().url(host + "/auth/username/login.json?username=root&password=" + nonce), false);
        String login = OkHttpClientHelper.extractString(r2, false);
        log.warn("get login res = " + login);
        Assertions.assertTrue(login.contains("true"), "Need init database via BootDatabaseTest");

        final Response r3 = OkHttpClientHelper.execute(okHttpClient, new Request.Builder().url(host + "/auth/list-auth.json"), false);
        String auths = OkHttpClientHelper.extractString(r3, false);
        log.warn("get auths = " + auths);
        Assertions.assertTrue(auths.contains("ROLE_ROOT"));
    }

    @Test
    public void testDanger() {
        log.warn("current locale = {}", Locale.getDefault());
        OkHttpClientHelper.postJson(okHttpClient, host + DangerUrl, "{\"userId\":1,\"danger\":true}");
        {
            final Response r2 = OkHttpClientHelper.execute(okHttpClient, new Request.Builder().url(host + "/auth/username/login.json?username=root&password=BAD&locale=zh"), false);
            String login = OkHttpClientHelper.extractString(r2, false);
            log.warn("testDanger-a get login res = " + login);
            Assertions.assertTrue(login.contains("error.authn.locked"), "Need init database via BootDatabaseTest");
            Assertions.assertTrue(login.contains("账号已锁定"), "check i18n config");
        }

        OkHttpClientHelper.postJson(okHttpClient, host + DangerUrl, "{\"userId\":1,\"danger\":false}");
        {
            final Response r1 = OkHttpClientHelper.execute(okHttpClient, new Request.Builder().url(host + "/auth/console-nonce.json?username=root"), false);
            String nonce = OkHttpClientHelper.extractString(r1, false);
            log.warn("testDanger-b get nonce for root, nonce=" + nonce);
            Assertions.assertEquals(200, r1.code(), "Need init database via BootDatabaseTest");

            final Response r2 = OkHttpClientHelper.execute(okHttpClient, new Request.Builder().url(host + "/auth/username/login.json?username=root&password=" + nonce), false);
            String login = OkHttpClientHelper.extractString(r2, false);
            log.warn("testDanger-b get login res = " + login);
            Assertions.assertTrue(login.contains("true"), "Need init database via BootDatabaseTest");
        }

        for (int i = 0; i < 6; i++) {
            final Response r2 = OkHttpClientHelper.execute(okHttpClient, new Request.Builder().url(host + "/auth/username/login.json?username=root&password=BAD&locale=en"), false);
            String login = OkHttpClientHelper.extractString(r2, false);
            log.warn("testDanger-c " + i + " get login res = " + login);
            Assertions.assertTrue(login.contains("error.authn."), "Need init database via BootDatabaseTest");
            if (i > 2) {
                Assertions.assertTrue(login.contains("error.authn.failureWaiting"), "Need init database via BootDatabaseTest");
                Assertions.assertTrue(login.contains("retry after"), "check i18n config");
            }
        }

        OkHttpClientHelper.postJson(okHttpClient, host + DangerUrl, "{\"userId\":1,\"danger\":false}");
        {
            final Response r1 = OkHttpClientHelper.execute(okHttpClient, new Request.Builder().url(host + "/auth/console-nonce.json?username=root"), false);
            String nonce = OkHttpClientHelper.extractString(r1, false);
            log.warn("testDanger-d get nonce for root, nonce=" + nonce);
            Assertions.assertEquals(200, r1.code(), "Need init database via BootDatabaseTest");

            final Response r2 = OkHttpClientHelper.execute(okHttpClient, new Request.Builder().url(host + "/auth/username/login.json?username=root&password=" + nonce), false);
            String login = OkHttpClientHelper.extractString(r2, false);
            log.warn("testDanger-d get login res = " + login);
            Assertions.assertTrue(login.contains("true"), "Need init database via BootDatabaseTest");
        }
    }
}
