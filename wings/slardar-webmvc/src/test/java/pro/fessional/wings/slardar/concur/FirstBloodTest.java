package pro.fessional.wings.slardar.concur;

import io.qameta.allure.TmsLink;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import pro.fessional.wings.slardar.httprest.okhttp.OkHttpClientHelper;

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
                "wings.enabled.pro.fessional.wings.slardar.app.conf.TestFirstBloodTestConfiguration=true",
                "wings.enabled.slardar.first-blood=true",
                "wings.enabled.slardar.first-blood-image=false",
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
    private Call.Factory okHttpClient;

    @Test
    @TmsLink("C13040")
    public void testFirstBlood0() {
        checkFirstBlood(firstBloodUrl0);
    }

    @Test
    @TmsLink("C13041")
    public void testFirstBlood30() throws InterruptedException {
        new Thread(() -> {
            try (Response r1 = OkHttpClientHelper.execute(okHttpClient, new Request.Builder().url(firstBloodUrl30), false)) {
                assertEquals(HttpStatus.OK.value(), r1.code());
            }
        }).start();
        Thread.sleep(1000);
        checkFirstBlood(firstBloodUrl30);
    }

    public void checkFirstBlood(String url) {
        try (Response r2 = OkHttpClientHelper.execute(okHttpClient, new Request.Builder().url(url), false)) {
            assertEquals(202, r2.code());
            final String ct = r2.header("Content-Type");
            final String tk = r2.header("Client-Ticket");
            final String ck = r2.header("Set-Cookie");
            assertNotNull(ct);
            assertNotNull(tk);
            assertNotNull(ck);
            assertTrue(ct.contains("text/plain"));
            assertEquals("first-blood", OkHttpClientHelper.extractString(r2, false));
            log.warn("get client-ticket = " + tk);
            assertTrue(ck.contains(tk));
        }

        final String code;
        try (Response r3 = OkHttpClientHelper.execute(okHttpClient, new Request.Builder().url(url + "?quest-captcha-image=1234567890"), false)) {
            code = OkHttpClientHelper.extractString(r3, false);
            log.warn("get captcha code = " + code);
            assertEquals(200, r3.code());
        }

        try (Response r4 = OkHttpClientHelper.execute(okHttpClient, new Request.Builder().url(url + "?check-captcha-image=" + code), false)) {
            assertEquals(200, r4.code());
        }

        try (Response r5 = OkHttpClientHelper.execute(okHttpClient, new Request.Builder().url(url + "?check-captcha-image=" + code), false)) {
            assertEquals(202, r5.code());
        }
    }
}
