package pro.fessional.wings.slardar.async;

import com.alibaba.ttl.TtlRunnable;
import io.qameta.allure.TmsLink;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import pro.fessional.wings.slardar.context.TerminalContext;
import pro.fessional.wings.slardar.app.service.AsyncService;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicInteger;

import static org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor.DEFAULT_TASK_SCHEDULER_BEAN_NAME;

/**
 * @author trydofor
 * @since 2022-12-03
 */
@Slf4j
@SpringBootTest
public class TaskSchedulerTest {

    @Setter(onMethod_ = {@Autowired, @Qualifier(DEFAULT_TASK_SCHEDULER_BEAN_NAME)})
    protected ThreadPoolTaskScheduler threadPoolTaskScheduler;

    @Setter(onMethod_ = {@Autowired})
    protected AsyncService asyncService;

    @Test
    @TmsLink("C13001")
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
        // If a non-TtlThreadPoolTaskScheduler is set up, but a ttlExecutor is used.
        // then only one thread will succeed in TTL, others will fail
        final ScheduledFuture<?> task1 = threadPoolTaskScheduler.scheduleWithFixedDelay(() -> delayUid("TaskSchedulerTest Default", userId, cnt1, eqs1), Duration.ofMillis(1_000));
        Thread.sleep(5_000);
        task1.cancel(false);
        Assertions.assertEquals(cnt1.get(), eqs1.get(), "userid not equals, see log");
        cnt1.set(0);
        eqs1.set(0);
        Thread.sleep(500);
        final ScheduledFuture<?> task2 = threadPoolTaskScheduler.scheduleWithFixedDelay(TtlRunnable.get(() -> delayUid("TaskSchedulerTest TtlRun", userId, cnt1, eqs1), false, true), Duration.ofMillis(1_000));
        Thread.sleep(5_000);
        task2.cancel(false);
        Assertions.assertEquals(cnt1.get(), eqs1.get(), "userid not equals, see log");
    }

    private void delayUid(String caller, long userId, AtomicInteger cnt, AtomicInteger eqs) {
        final String name = Thread.currentThread().getName();
        if (name.startsWith("win-task-")) {
            final long ud = TerminalContext.get().getUserId();
            log.info("{} delay {}, uid={}", caller, cnt.incrementAndGet(), ud);
            if (ud == userId) {
                eqs.incrementAndGet();
            }
        }
        else {
            log.error("bad thread prefix, should start with 'win-task-'");
        }
    }
}
