package pro.fessional.wings.faceless.concur;

import lombok.Setter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author trydofor
 * @since 2021-03-09
 */
@SpringBootTest
class DatabaseGlobalLockTest {

    @Setter(onMethod_ = {@Autowired})
    private NamedParameterJdbcTemplate namedJdbcTemplate;
    // use NamedParameterJdbcTemplate for test
    // private JdbcTemplate jdbcTemplate;

    @Test
    void lock() throws InterruptedException {
        DatabaseGlobalLock globalLock = new DatabaseGlobalLock(namedJdbcTemplate.getJdbcTemplate());
        Lock lock = globalLock.getLock("test-lock");
        try {
            if (lock.tryLock(1, TimeUnit.MILLISECONDS)) {
                assertTrue(true, "get Lock");
                CountDownLatch latch = new CountDownLatch(1);
                lockFail(latch);
                latch.await();
            }
        }
        finally {
            lock.unlock();
        }
    }

    void lockFail(CountDownLatch latch) {
        final DatabaseGlobalLock globalLock = new DatabaseGlobalLock(namedJdbcTemplate.getJdbcTemplate());
        new Thread(() -> {
            Lock lock = globalLock.getLock("test-lock");
            final boolean b;
            try {
                b = lock.tryLock(1, TimeUnit.MILLISECONDS);
                assertFalse(b);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
            latch.countDown();
        }).start();
    }
}
