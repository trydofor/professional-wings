package pro.fessional.wings.slardar.async;

import io.qameta.allure.TmsLink;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pro.fessional.mirana.time.Sleep;
import pro.fessional.wings.slardar.app.service.TestAsyncService;
import pro.fessional.wings.slardar.app.service.TestAsyncService.AsyncType;
import pro.fessional.wings.slardar.app.service.TestTtlDecorator;
import pro.fessional.wings.slardar.context.TerminalContext;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author trydofor
 * @since 2022-12-03
 */
@Slf4j
@SpringBootTest
public class AsyncHelperTest {

    @Setter(onMethod_ = {@Autowired})
    protected TestAsyncService testAsyncService;

    @Setter(onMethod_ = {@Autowired})
    protected TestTtlDecorator testTtlDecorator;

    @Test
    @TmsLink("C13122")
    void testAsync() throws Exception {
        final TerminalContext.Builder builder = new TerminalContext.Builder();
        final long userId = 1L;
        builder.user(userId);
        TerminalContext.login(builder.build());

        CompletableFuture<Long> uid = testAsyncService.userIdAsync(AsyncType.Return);
        Assertions.assertEquals(userId, uid.get());

        final AtomicInteger eqs1 = new AtomicInteger(0);
        Sleep.ignoreInterrupt(500);
        // If a non-TtlThreadPoolTaskScheduler is set up, but a ttlExecutor is used.
        // then only one thread will succeed in TTL, others will fail
        final var task1 = AsyncHelper.Async(() -> delayUid("TaskSchedulerTest Default", userId, eqs1));
        task1.join();
        Assertions.assertEquals(1, eqs1.get(), "userid not equals, see log");
        eqs1.set(0);

        // exception
        failedFuture(AsyncHelper.Async(()->testAsyncService.userId(AsyncType.FailedFuture)), TestAsyncService.UserIdFailedFuture);
        failedFuture(AsyncHelper.Async(()->testAsyncService.userId(AsyncType.UncaughtException)), TestAsyncService.UserIdUncaughtException);

        Assertions.assertNotNull(testTtlDecorator);
        Assertions.assertTrue(TestTtlDecorator.Count.get() > 0);
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

    private void delayUid(String caller, long userId, AtomicInteger eqs) {
        final String name = Thread.currentThread().getName();
        if (name.contains("exec-")) {
            final long ud = TerminalContext.currentLoginUser();
            log.info("{} , uid={}", caller, ud);
            if (ud == userId) {
                eqs.incrementAndGet();
            }
        }
        else {
            Assertions.fail("bad thread prefix, should start with 'exec-'");
        }
    }
}
