package pro.fessional.wings.slardar.async;

import com.alibaba.ttl.TtlRunnable;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import pro.fessional.wings.slardar.context.TerminalContext;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author trydofor
 * @since 2022-12-03
 */
@Slf4j
@SpringBootTest(properties = {"debug = true"})
public class TaskSchedulerTest {

    @Setter(onMethod_ = {@Autowired})
    protected ThreadPoolTaskScheduler threadPoolTaskScheduler;

    @Setter(onMethod_ = {@Autowired})
    protected AsyncService asyncService;

    @Test
    void testTask() throws Exception {
        final TerminalContext.Builder builder = new TerminalContext.Builder();
        final long userId = 1L;
        builder.user(userId);
        TerminalContext.login(builder.build());

        CompletableFuture<Long> uid = asyncService.asyncUserId();
        Assertions.assertEquals(userId, uid.get());

        final AtomicInteger cnt1 = new AtomicInteger(0);
        final AtomicInteger eqs1 = new AtomicInteger(0);
        Thread.sleep(500);
        // 若非TtlThreadPoolTaskScheduler设置，却用了ttlExecutor，
        // 则仅一个线程能会TTL成功，其会失败，
        final ScheduledFuture<?> task1 = threadPoolTaskScheduler.scheduleWithFixedDelay(() -> delayUid("TaskSchedulerTest Default", userId, cnt1, eqs1), 1_000);
        Thread.sleep(5_000);
        task1.cancel(false);
        Assertions.assertEquals(cnt1.get(), eqs1.get(), "userid not equals, see log");
        cnt1.set(0);
        eqs1.set(0);
        Thread.sleep(500);
        final ScheduledFuture<?> task2 = threadPoolTaskScheduler.scheduleWithFixedDelay(TtlRunnable.get(() -> delayUid("TaskSchedulerTest TtlRun", userId, cnt1, eqs1), false, true), 1_000);
        Thread.sleep(5_000);
        task2.cancel(false);
        Assertions.assertEquals(cnt1.get(), eqs1.get(), "userid not equals, see log");
    }

    private void delayUid(String caller, long userId, AtomicInteger cnt, AtomicInteger eqs) {
        final long ud = TerminalContext.get().getUserId();
        log.info("{} delay {}, uid={}", caller, cnt.incrementAndGet(), ud);
        if (ud == userId) {
            eqs.incrementAndGet();
        }
    }
}
