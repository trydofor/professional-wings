package pro.fessional.wings.warlock.other;

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
import pro.fessional.mirana.time.StopWatch;
import pro.fessional.wings.silencer.watch.Watches;
import pro.fessional.wings.slardar.httprest.okhttp.OkHttpClientHelper;
import pro.fessional.wings.warlock.app.service.WatchingService;

/**
 * @author trydofor
 * @since 2022-11-22
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "spring.wings.warlock.enabled.watching=true",
                "wings.warlock.watching.jooq-threshold=0",
                "wings.warlock.watching.service-threshold=0",
                "wings.warlock.watching.controller-threshold=0",
        })
@Slf4j
public class WarlockWatchingTest {

    @Setter(onMethod_ = {@Value("http://localhost:${local.server.port}")})
    private String host;

    @Setter(onMethod_ = {@Autowired})
    private OkHttpClient okHttpClient;

    /**
     * Check the log
     */
    @Test
    @TmsLink("C14037")
    public void testWatching() {
        final StopWatch.Watch watch = Watches.acquire("testWatching");
        final Request.Builder body = new Request.Builder().url(host + "/test/watching.json");
        final Response r1 = OkHttpClientHelper.execute(okHttpClient, body, false);
        Assertions.assertEquals(200, r1.code());
        watch.close();
        final boolean del = Watches.release(false, "testWatching");
        Assertions.assertEquals(2, watch.owner.getWatches().size());
        Assertions.assertTrue(del);
        // async in async task pool
        Assertions.assertTrue(2 <= WatchingService.AsyncWatch.size());
        Assertions.assertTrue(WatchingService.WatchOwner.getWatches().isEmpty(),"Need init database via BootDatabaseTest");
    }
}
