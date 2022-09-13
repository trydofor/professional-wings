package pro.fessional.wings.slardar.concur;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import pro.fessional.wings.slardar.httprest.OkHttpClientHelper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author trydofor
 * @since 2021-03-09
 */

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "debug = true",
                "spring.wings.slardar.enabled.first-blood-image=false",
                "spring.wings.slardar.enabled.first-blood-image-test=true",
                "wings.slardar.first-blood.http-status=202",
                "wings.slardar.first-blood.content-type=text/plain",
                "wings.slardar.first-blood.response-body=first-blood",
        })
@Slf4j
class FirstBloodTest {

    @Setter(onMethod_ = {@Value("http://localhost:${local.server.port}/test/captcha.json")})
    private String firstBloodUrl0;

    @Setter(onMethod_ = {@Value("http://localhost:${local.server.port}/test/captcha-30.json")})
    private String firstBloodUrl30;

    @Setter(onMethod_ = {@Autowired})
    private OkHttpClient okHttpClient;

    @Test
    public void testFirstBlood0() {
        checkFirstBlood(firstBloodUrl0);
    }

    @Test
    public void testFirstBlood30() throws InterruptedException {
        new Thread(() -> {
            final Response r1 = OkHttpClientHelper.execute(okHttpClient, new Request.Builder().url(firstBloodUrl30), false);
            assertEquals(HttpStatus.OK.value(), r1.code());
        }).start();
        Thread.sleep(1000);
        checkFirstBlood(firstBloodUrl30);
    }

    public void checkFirstBlood(String url) {
        final Response r2 = OkHttpClientHelper.execute(okHttpClient, new Request.Builder().url(url), false);
        assertEquals(202, r2.code());
        final String ct = r2.header("Content-Type");
        final String tk = r2.header("Client-Ticket");
        final String ck = r2.header("Set-Cookie");
        assertNotNull(ct);
        assertNotNull(tk);
        assertNotNull(ck);
        assertTrue(ct.contains("text/plain"));
        assertEquals("first-blood", OkHttpClientHelper.extractString(r2, false));
        assertTrue(ck.contains(tk));
        log.warn("get client-ticket = " + tk);

        final Response r3 = OkHttpClientHelper.execute(okHttpClient, new Request.Builder().url(url + "?quest-captcha-image=1234567890"), false);
        String code = OkHttpClientHelper.extractString(r3, false);
        log.warn("get captcha code = " + code);
        assertEquals(200, r3.code());

        final Response r4 = OkHttpClientHelper.execute(okHttpClient, new Request.Builder().url(url + "?check-captcha-image=" + code), false);
        assertEquals(200, r4.code());

        final Response r5 = OkHttpClientHelper.execute(okHttpClient, new Request.Builder().url(url + "?check-captcha-image=" + code), false);
        assertEquals(202, r5.code());
    }
}
