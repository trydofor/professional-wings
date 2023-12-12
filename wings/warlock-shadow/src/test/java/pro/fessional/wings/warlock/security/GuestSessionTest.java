package pro.fessional.wings.warlock.security;

import io.qameta.allure.TmsLink;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import pro.fessional.wings.slardar.httprest.okhttp.OkHttpClientHelper;

/**
 * <a href="https://github.com/trydofor/pro.fessional.wings/issues/106">Principal required</a>
 *
 * @author trydofor
 * @since 2021-03-09
 */

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
class GuestSessionTest {

    @Setter(onMethod_ = {@Value("http://localhost:${local.server.port}")})
    private String host;

    @Setter(onMethod_ = {@Autowired})
    private OkHttpClient okHttpClient;

    @Test
    @TmsLink("C14045")
    public void guestSession() {
        final Response r1 = OkHttpClientHelper.execute(okHttpClient, new Request.Builder().url(host + "/test/guest-session.json"), false);
        String s1 = OkHttpClientHelper.extractString(r1, false);

        final Response r2 = OkHttpClientHelper.execute(okHttpClient, new Request.Builder().url(host + "/test/guest-session.json"), false);
        String s2 = OkHttpClientHelper.extractString(r2, false);
        Assertions.assertEquals(s1, s2);
    }

    @Test
    @TmsLink("C14046")
    public void guest401() {
        OkHttpClientHelper.clearCookie(okHttpClient, HttpUrl.get(host));
        try (final Response r1 = OkHttpClientHelper.execute(okHttpClient, new Request.Builder().url(host + "/user/guest-401.json"), false)) {
            Assertions.assertEquals(401, r1.code(), "retry standalone to avoid cookies");
        }
    }
}
