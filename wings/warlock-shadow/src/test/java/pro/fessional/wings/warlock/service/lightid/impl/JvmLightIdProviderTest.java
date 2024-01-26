package pro.fessional.wings.warlock.service.lightid.impl;

import io.qameta.allure.TmsLink;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pro.fessional.mirana.id.LightIdBufferedProvider;
import pro.fessional.mirana.id.LightIdProvider;
import pro.fessional.wings.faceless.service.lightid.BlockIdProvider;
import pro.fessional.wings.faceless.service.lightid.LightIdService;
import pro.fessional.wings.faceless.service.lightid.impl.LightIdServiceImpl;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author trydofor
 * @since 2023-07-18
 */
@SpringBootTest(properties = {
        "wings.faceless.lightid.provider.monotonic=jvm",
        "wings.faceless.lightid.insert.step=10",
})
@Slf4j
public class JvmLightIdProviderTest {

    @Setter(onMethod_ = {@Autowired})
    protected LightIdProvider lightIdProvider;
    @Setter(onMethod_ = {@Autowired})
    protected BlockIdProvider blockIdProvider;
    @Setter(onMethod_ = {@Autowired})
    protected LightIdProvider.Loader lightIdLoader;

    @Test
    @TmsLink("C14063")
    public void jvmLightIdProvider() {
        Assertions.assertInstanceOf(LightIdBufferedProvider.class, lightIdProvider);
    }

    @Test
    @TmsLink("C14076")
    public void testMultiInstance() throws InterruptedException {
        final var threadCnt = 30;
        final var loopCount = 1000;
        final var idCache = new ConcurrentHashMap<Long, Long>();
        final var pools = Executors.newFixedThreadPool(threadCnt);

        final var seqName = "testMultiInstance";
        final var ids = new LightIdService[threadCnt];
        for (int i = 0; i < threadCnt; i++) {
            final var lip = new LightIdBufferedProvider(lightIdLoader);
            lip.setTimeout(10000);
            lip.setErrAlive(1000);
            lip.setMaxError(1);
            lip.setMaxCount(10);
            ids[i] = new LightIdServiceImpl(lip, blockIdProvider);
            ids[i].getId(seqName);
        }
        log.info("CountDownLatch {}", threadCnt);

        final var end = new CountDownLatch(threadCnt);
        for (int i = 0; i < threadCnt; i++) {
            final var idx = i;
            pools.submit(() -> {
                log.info("start {}", idx);
                for (int j = 0; j < loopCount; j++) {
                    var id = ids[idx].getId(seqName, 0);
                    idCache.put(id, id);
                }
                log.info("done {}", idx);
                end.countDown();
            });
        }
        end.await();
        log.info("all done");
        assertEquals(loopCount * threadCnt, idCache.size());
    }
}
