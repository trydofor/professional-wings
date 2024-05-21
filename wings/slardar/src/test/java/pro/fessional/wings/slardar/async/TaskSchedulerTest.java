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
import pro.fessional.mirana.time.Sleep;
import pro.fessional.wings.slardar.app.service.TestAsyncService;
import pro.fessional.wings.slardar.app.service.TestAsyncService.AsyncType;
import pro.fessional.wings.slardar.context.TerminalContext;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
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
    protected TestAsyncService testAsyncService;

    @Test
    @TmsLink("C13001")
    void testTask() throws Exception {
        final TerminalContext.Builder builder = new TerminalContext.Builder();
        final long userId = 1L;
        builder.user(userId);
        TerminalContext.login(builder.build());

        CompletableFuture<Long> uid = testAsyncService.userIdAsync(AsyncType.Return);
        Assertions.assertEquals(userId, uid.get());

        final AtomicInteger cnt1 = new AtomicInteger(0);
        final AtomicInteger eqs1 = new AtomicInteger(0);
        Sleep.ignoreInterrupt(500);
        // If a non-TtlThreadPoolTaskScheduler is set up, but a ttlExecutor is used.
        // then only one thread will succeed in TTL, others will fail
        final ScheduledFuture<?> task1 = threadPoolTaskScheduler.scheduleWithFixedDelay(() -> delayUid("TaskSchedulerTest Default", userId, cnt1, eqs1), Duration.ofMillis(1_000));
        Sleep.ignoreInterrupt(5_000);
        task1.cancel(false);
        Assertions.assertTrue(eqs1.get() > 0, "userid not equals, see log");
        Assertions.assertEquals(cnt1.get(), eqs1.get(), "userid not equals, see log");
        cnt1.set(0);
        eqs1.set(0);
        Sleep.ignoreInterrupt(500);
        final ScheduledFuture<?> task2 = threadPoolTaskScheduler.scheduleWithFixedDelay(
                TtlRunnable.get(() -> delayUid("TaskSchedulerTest TtlRun", userId, cnt1, eqs1),
                        false, true), Duration.ofMillis(1_000));
        Sleep.ignoreInterrupt(5_000);
        task2.cancel(false);
        Assertions.assertTrue(eqs1.get() > 0, "userid not equals, see log");
        Assertions.assertEquals(cnt1.get(), eqs1.get(), "userid not equals, see log");

        // exception
        failedFuture(testAsyncService.userIdAsync(AsyncType.FailedFuture), TestAsyncService.UserIdFailedFuture);
        failedFuture(testAsyncService.userIdAsync(AsyncType.UncaughtException), TestAsyncService.UserIdUncaughtException);

        /*
         * == by default ==
         * SimpleAsyncUncaughtExceptionHandler : Unexpected exception occurred invoking async method:
         * public void pro.fessional.wings.slardar.app.service.TestAsyncService.asyncVoid(pro.fessional.wings.slardar.app.service.TestAsyncService$AsyncType)
         * java.lang.RuntimeException: asyncVoid UncaughtException
         */
        testAsyncService.voidAsync(AsyncType.UncaughtException);
    }

    private void failedFuture(CompletableFuture<?> future, String msg) {
        try {
            future.get();
            Assertions.fail();
        }
        catch (Exception e) {
            boolean got = false;
            if (e instanceof ExecutionException ee) {
                if (ee.getCause() instanceof RuntimeException re) {
                    got = msg.equals(re.getMessage());
                }
            }
            Assertions.assertTrue(got);
        }
    }

    private void delayUid(String caller, long userId, AtomicInteger cnt, AtomicInteger eqs) {
        final String name = Thread.currentThread().getName();
        if (name.contains("task-")) {
            final long ud = TerminalContext.currentLoginUser();
            log.info("{} delay {}, uid={}", caller, cnt.incrementAndGet(), ud);
            if (ud == userId) {
                eqs.incrementAndGet();
            }
        }
        else {
            Assertions.fail("bad thread prefix, should start with 'task-'");
        }
    }
}
