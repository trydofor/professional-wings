package pro.fessional.wings.warlock.service.flakeid.impl;

import io.qameta.allure.TmsLink;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pro.fessional.wings.faceless.service.flakeid.FlakeIdService;
import pro.fessional.wings.slardar.service.flakeid.FlakeIdHazelcastImpl;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

/**
 * @author trydofor
 * @since 2022-03-20
 */
@SpringBootTest
@Slf4j
class FlakeIdHazelcastImplTest {

    @Setter(onMethod_ = {@Autowired})
    private FlakeIdService flakeIdService;

    @Test
    @TmsLink("C14055")
    void hazelcastFlakeId() throws InterruptedException {
        assertInstanceOf(FlakeIdHazelcastImpl.class, flakeIdService);
        final var threadCnt = 100;
        final var loopCount = 5000;
        final var idCache = new ConcurrentHashMap<Long, Long>();
        final var service = Executors.newFixedThreadPool(threadCnt / 2);

        final var latch = new CountDownLatch(threadCnt);
        final String sn = "seqName";
        for (int i = 0; i < threadCnt; i++) {
            service.submit(() -> {
                for (int j = 0; j < loopCount; j++) {
                    final var id = flakeIdService.getId(sn);
                    final Long old = idCache.put(id, id);
                    if (old != null) {
                        log.info(sn + ":" + old);
                    }
                }
                latch.countDown();
            });
        }
        latch.await();
        assertEquals(loopCount * threadCnt, idCache.size());
    }
}
