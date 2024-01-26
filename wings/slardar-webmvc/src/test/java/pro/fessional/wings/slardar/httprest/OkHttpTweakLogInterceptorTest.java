package pro.fessional.wings.slardar.httprest;

import io.qameta.allure.TmsLink;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Call;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.logging.LogLevel;
import org.springframework.boot.test.context.SpringBootTest;
import pro.fessional.wings.silencer.tweak.TweakLogger;
import pro.fessional.wings.slardar.httprest.okhttp.OkHttpClientHelper;

/**
 * @author trydofor
 * @since 2022-11-02
 */

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
class OkHttpTweakLogInterceptorTest {

    @Setter(onMethod_ = {@Autowired})
    private Call.Factory okHttpClient;

    @Test
    @TmsLink("C13051")
    void interceptLogger() {
        final String name = log.getName();
        log.warn(">>>> default");
        OkHttpClientHelper.getText(okHttpClient, "https://www.bing.com");
        final LogLevel lvl = TweakLogger.currentLevel(name);
        log.warn(">>>> default level={}", lvl);

        log.warn(">>>> DEBUG");
        TweakLogger.tweakThread(LogLevel.DEBUG);
        OkHttpClientHelper.getText(okHttpClient, "https://www.bing.com");
        log.warn(">>>> DEBUG level={}", TweakLogger.currentLevel(name));

        log.warn(">>>> INFO");
        TweakLogger.tweakThread(LogLevel.INFO);
        OkHttpClientHelper.getText(okHttpClient, "https://www.bing.com");
        log.warn(">>>> INFO level={}", TweakLogger.currentLevel(name));

        log.warn(">>>> WARN");
        TweakLogger.tweakThread(LogLevel.WARN);
        OkHttpClientHelper.getText(okHttpClient, "https://www.bing.com");
        log.warn(">>>> WARN level={}", TweakLogger.currentLevel(name));
    }
}
