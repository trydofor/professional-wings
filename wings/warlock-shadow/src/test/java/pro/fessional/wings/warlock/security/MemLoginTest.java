package pro.fessional.wings.warlock.security;

import com.alibaba.fastjson2.TypeReference;
import io.qameta.allure.TmsLink;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import pro.fessional.wings.slardar.fastjson.FastJsonHelper;
import pro.fessional.wings.slardar.httprest.okhttp.OkHttpClientHelper;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * @author trydofor
 * @since 2021-03-09
 */

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {"wings.slardar.terminal.locale-request=true"})
@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MemLoginTest {

    @Setter(onMethod_ = {@Value("http://localhost:${local.server.port}")})
    private String host;

    @Setter(onMethod_ = {@Autowired})
    private OkHttpClient okHttpClient;

    @Test
    @Order(1)
    @TmsLink("C14047")
    public void testUsernameLogin() {
        OkHttpClientHelper.clearCookie(okHttpClient, HttpUrl.get(host));
        final Response r2 = OkHttpClientHelper.execute(okHttpClient, new Request.Builder()
                .url(host + "/auth/username/login.json?username=trydofor&password=moMxVKXxA8Pe9XX9"), false);
        String login = OkHttpClientHelper.extractString(r2, false);
        log.info("get login res = " + login);
        Assertions.assertTrue(login.contains("true"));

        final Response r3 = OkHttpClientHelper.execute(okHttpClient, new Request.Builder()
                .url(host + "/auth/list-auth.json"), false);
        String au3 = OkHttpClientHelper.extractString(r3, false);
        log.info("UsernameLogin auth3={}", au3);

        final Response r4 = OkHttpClientHelper.execute(okHttpClient, new Request.Builder()
                .url(host + "/auth/list-hold.json"), false);
        String au4 = OkHttpClientHelper.extractString(r4, false);
        log.info("UsernameLogin auth4={}", au4);

        final TypeReference<Set<String>> setRef = new TypeReference<>() {};
        final Set<String> st3 = FastJsonHelper.object(au3, setRef);
        final Set<String> st4 = FastJsonHelper.object(au4, setRef);
        Assertions.assertEquals(st3, st4);

        List<String> exp = Arrays.asList("ROLE_SYSTEM", "ROLE_ADMIN", "user-perm");
        Assertions.assertTrue(st3.containsAll(exp));

        //
        final Response r5 = OkHttpClientHelper.execute(okHttpClient, new Request.Builder()
                .url(host + "/auth/current-principal.json"), false);
        String au5 = OkHttpClientHelper.extractString(r5, false);
        log.info("current-principal={}", au5);

        // CodeException i18n
        final Response r6 = OkHttpClientHelper.execute(okHttpClient, new Request.Builder()
                .url(host + "/test/code-exception.json")
                .header("Accept-Language", "zh_CN"), false);
        String er6 = OkHttpClientHelper.extractString(r6, false);
        Assertions.assertTrue(er6.contains("test不能为空"));

        final Response r7 = OkHttpClientHelper.execute(okHttpClient, new Request.Builder()
                .url(host + "/test/code-exception.json")
                .header("Accept-Language", "en_US"), false);
        String er7 = OkHttpClientHelper.extractString(r7, false);
        Assertions.assertTrue(er7.contains("test should not empty"));
    }

    @Test
    @Order(2)
    @TmsLink("C14048")
    public void testEmailLogin() {
        OkHttpClientHelper.clearCookie(okHttpClient, HttpUrl.get(host));
        final Response r2 = OkHttpClientHelper.execute(okHttpClient, new Request.Builder()
                .url(host + "/auth/email/login.json?username=trydofor@qq.com&password=3bvlPy7oQbds28c1"), false);
        String login = OkHttpClientHelper.extractString(r2, false);
        log.info("get login res = " + login);
        Assertions.assertTrue(login.contains("true"));

        final Response r3 = OkHttpClientHelper.execute(okHttpClient, new Request.Builder()
                .url(host + "/auth/list-auth.json"), false);
        String au3 = OkHttpClientHelper.extractString(r3, false);
        log.info("EmailLogin auth3={}", au3);

        final Response r4 = OkHttpClientHelper.execute(okHttpClient, new Request.Builder()
                .url(host + "/auth/list-hold.json"), false);
        String au4 = OkHttpClientHelper.extractString(r4, false);
        log.info("EmailLogin auth4={}", au4);

        final Set<?> st3 = FastJsonHelper.object(au3, new TypeReference<Set<String>>() {});
        final Set<?> st4 = FastJsonHelper.object(au4, new TypeReference<Set<String>>() {});
        Assertions.assertEquals(st3, st4);

        Assertions.assertTrue(st3.contains("email-perm"));

        //
        final Response r5 = OkHttpClientHelper.execute(okHttpClient, new Request.Builder()
                .url(host + "/auth/current-principal.json"), false);
        String au5 = OkHttpClientHelper.extractString(r5, false);
        log.info("current-principal={}", au5);
    }

    @Test
    @Order(3)
    @TmsLink("C14049")
    public void testListSession() {
        final String r6 = OkHttpClientHelper.postJson(okHttpClient, host + "/user/list-session.json", "");
        log.info("list-session auth4={}", r6);
        Assertions.assertTrue(r6.contains("\"username\":\"trydofor@qq.com\""));
        Assertions.assertTrue(r6.contains("\"username\":\"trydofor\""));
        OkHttpClientHelper.clearCookie(okHttpClient, HttpUrl.get(host));
    }
}
