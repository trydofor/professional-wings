package pro.fessional.wings.tiny.task.other;

import io.qameta.allure.TmsLink;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import pro.fessional.wings.silencer.testing.AssertionLogger;
import pro.fessional.wings.slardar.async.TaskSchedulerHelper;

import java.time.Instant;
import java.util.concurrent.ScheduledFuture;

/**
 * @author trydofor
 * @since 2022-12-13
 */
@SpringBootTest
@Slf4j
@Disabled("Simulate batch, check manually")
class ExecutorServiceTest {

    @Test
    @TmsLink("C15012")
    void schedule() throws InterruptedException {
        final AssertionLogger al = AssertionLogger.install();
        al.rule("-1 run", event -> event.getFormattedMessage().contains("-1 run="));
        al.rule("-1 cancel", event -> event.getFormattedMessage().contains("-1 cancel=false"));
        al.rule("=0 run", event -> event.getFormattedMessage().contains("=0 run="));
        al.rule("=0 cancel", event -> event.getFormattedMessage().contains("=0 cancel=false"));
        al.rule("+1 cancel", event -> event.getFormattedMessage().contains("+1 cancel=true"));
        al.rule("== run", event -> event.getFormattedMessage().contains("== run="));
        al.rule("== cancel", event -> event.getFormattedMessage().contains("== cancel=true"));
        al.rule(".. run", event -> event.getFormattedMessage().contains(".. run="));
        al.rule(".. cancel", event -> event.getFormattedMessage().contains(".. cancel=true"));
        al.start();

        final ThreadPoolTaskScheduler scheduler = TaskSchedulerHelper.referScheduler(false);
        final ScheduledFuture<?> f1 = scheduler.schedule(() -> log.info("-1 run={}", System.currentTimeMillis()),
                Instant.ofEpochMilli(System.currentTimeMillis() - 1000));
        Thread.sleep(500);
        log.info("-1 cancel={}", f1.cancel(false));

        final ScheduledFuture<?> f2 = scheduler.schedule(() -> log.info("=0 run={}", System.currentTimeMillis()),
                Instant.ofEpochMilli(System.currentTimeMillis()));
        Thread.sleep(500);
        log.info("=0 cancel={}", f2.cancel(false));


        final ScheduledFuture<?> f3 = scheduler.schedule(() -> log.info("+1 run={}", System.currentTimeMillis()),
                Instant.ofEpochMilli(System.currentTimeMillis() + 1000));
        Thread.sleep(500);
        log.info("+1 cancel={}", f3.cancel(false));


        final ScheduledFuture<?> f4 = scheduler.schedule(() -> {
                    for (int i = 0; i < 10; i++) {
                        log.info("== run={}", i);
                        try {
                            Thread.sleep(100);
                        }
                        catch (InterruptedException e) {
                            break;
                        }
                    }
                },
                Instant.ofEpochMilli(System.currentTimeMillis()));
        Thread.sleep(500);
        log.info("== cancel={}", f4.cancel(false));

        final ScheduledFuture<?> f5 = scheduler.schedule(() -> {
                    for (int i = 0; i < 10; i++) {
                        log.info(".. run={}", i);
                        try {
                            Thread.sleep(100);
                        }
                        catch (InterruptedException e) {
                            break;
                        }
                    }
                },
                Instant.ofEpochMilli(System.currentTimeMillis()));
        Thread.sleep(500);
        log.info(".. cancel={}", f5.cancel(true));

        Thread.sleep(2000);
        log.info("== done=");

        al.stop();
        Assertions.assertTrue(al.assertAllTrue());
        al.uninstall();
    }
}
