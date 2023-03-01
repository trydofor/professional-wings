package pro.fessional.wings.warlock.service.flakeid.impl;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pro.fessional.wings.faceless.service.flakeid.FlakeIdService;
import pro.fessional.wings.slardar.service.flakeid.FlakeIdHazelcastImpl;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
    void getId() throws InterruptedException {
        assertTrue(flakeIdService instanceof FlakeIdHazelcastImpl);
        val threadCnt = 100;
        val loopCount = 5000;
        val idCache = new ConcurrentHashMap<Long, Long>();
        val service = Executors.newFixedThreadPool(threadCnt / 2);

        val latch = new CountDownLatch(threadCnt);
        final String sn = "seqName";
        for (int i = 0; i < threadCnt; i++) {
            service.submit(() -> {
                for (int j = 0; j < loopCount; j++) {
                    val id = flakeIdService.getId(sn);
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
