package pro.fessional.wings.warlock.other;

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
import pro.fessional.wings.warlock.service.watching.WatchingService;

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

    @Setter(onMethod_ = {@Value("${local.server.port}")})
    private int port;

    @Setter(onMethod_ = {@Autowired})
    private OkHttpClient okHttpClient;


    /**
     * 查看日志输出
     */
    @Test
    public void testWatching() {
        final StopWatch.Watch watch = Watches.acquire().start("testWatching");
        final String host = "http://localhost:" + port;
        final Request.Builder body = new Request.Builder().url(host + "/test/watching.json");
        final Response r1 = OkHttpClientHelper.execute(okHttpClient, body, false);
        Assertions.assertEquals(200, r1.code());
        watch.close();
        final boolean del = Watches.release(false, "testWatching");
        Assertions.assertEquals(1, watch.owner.getWatches().size());
        Assertions.assertTrue(del);

        Assertions.assertTrue(2 <= WatchingService.AsyncWatch.size());
        Assertions.assertTrue(WatchingService.WatchOwner.getWatches().isEmpty());
    }
}
