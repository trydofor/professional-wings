package pro.fessional.wings.warlock.security;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MemLoginTest {

    @Setter(onMethod_ = {@Value("http://localhost:${local.server.port}")})
    private String host;

    @Setter(onMethod_ = {@Autowired})
    private OkHttpClient okHttpClient;

    @Test
    @Order(1)
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
        final Set<String> st3 = JSON.parseObject(au3, setRef, FastJsonHelper.DefaultReader());
        final Set<String> st4 = JSON.parseObject(au4, setRef, FastJsonHelper.DefaultReader());
        Assertions.assertEquals(st3, st4);

        List<String> exp = Arrays.asList("ROLE_SYSTEM", "ROLE_ADMIN", "user-perm");
        Assertions.assertTrue(st3.containsAll(exp));

        //
        final Response r5 = OkHttpClientHelper.execute(okHttpClient, new Request.Builder()
                .url(host + "/auth/current-principal.json"), false);
        String au5 = OkHttpClientHelper.extractString(r5, false);
        log.info("current-principal={}", au5);
    }

    @Test
    @Order(2)
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

        final Set<?> st3 = JSON.parseObject(au3, new TypeReference<Set<String>>() {}, FastJsonHelper.DefaultReader());
        final Set<?> st4 = JSON.parseObject(au4, new TypeReference<Set<String>>() {}, FastJsonHelper.DefaultReader());
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
    public void testListSession() {
        final String r4 = OkHttpClientHelper.postJson(okHttpClient, host + "/user/list-session.json", "");
        log.info("list-session auth4={}", r4);
        Assertions.assertTrue(r4.contains("\"username\":\"trydofor@qq.com\""));
        Assertions.assertTrue(r4.contains("\"username\":\"trydofor\""));
    }
}
