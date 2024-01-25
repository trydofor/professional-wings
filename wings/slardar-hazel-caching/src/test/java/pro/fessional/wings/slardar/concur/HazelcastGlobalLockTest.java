package pro.fessional.wings.slardar.concur;

import com.hazelcast.core.HazelcastInstance;
import io.qameta.allure.TmsLink;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author trydofor
 * @since 2021-03-08
 */
@SpringBootTest
@Slf4j
class HazelcastGlobalLockTest {

    @Setter(onMethod_ = {@Autowired})
    private HazelcastInstance hazelcastInstance;

    @Test
    @TmsLink("C13018")
    void hazelcastLock() throws InterruptedException {
        HazelcastGlobalLock globalLock = new HazelcastGlobalLock(hazelcastInstance);
        Lock lock = globalLock.getLock("test-lock");
        try {
            if (lock.tryLock(1, TimeUnit.MILLISECONDS)) {
                assertTrue(true, "get Lock");
                CountDownLatch latch = new CountDownLatch(1);
                lockFail(latch);
                latch.await();
            }
        } finally {
            lock.unlock();
        }
    }

    void lockFail(CountDownLatch latch) {
        final HazelcastGlobalLock globalLock = new HazelcastGlobalLock(hazelcastInstance);
        new Thread(() -> {
            Lock lock = globalLock.getLock("test-lock");
            final boolean b;
            try {
                b = lock.tryLock(1, TimeUnit.MILLISECONDS);
                assertFalse(b);
            } catch (InterruptedException e) {
                log.error("lock fail",e);
            }
            latch.countDown();
        }).start();
    }
}
