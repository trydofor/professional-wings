package pro.fessional.wings.silencer.tweak;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import io.qameta.allure.TmsLink;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.boot.logging.LogLevel;
import org.springframework.boot.test.context.SpringBootTest;
import pro.fessional.mirana.pain.MessageException;
import pro.fessional.mirana.time.ThreadNow;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * @author trydofor
 * @since 2022-10-29
 */
@SpringBootTest(properties = {
        "wings.silencer.tweak.code-stack=true",
        "wings.silencer.tweak.mdc-threshold=true"
})
@Slf4j
public class SilenceDebugTest {

    @Test
    @TmsLink("C11020")
    public void tweakClock() {
        final LocalDateTime n0 = ThreadNow.localDateTime();
        TweakClock.tweakThread(Duration.ofSeconds(60));
        final LocalDateTime n1 = ThreadNow.localDateTime();
        TweakClock.resetThread();
        final LocalDateTime n2 = ThreadNow.localDateTime();
        Assertions.assertTrue(Duration.between(n0, n2).getSeconds() <= 1);
        Assertions.assertTrue(Duration.between(n0, n1).getSeconds() >= 59);
    }

    @Test
    @TmsLink("C11021")
    public void tweakStack() {
        TweakStack.tweakGlobal(true);
        final MessageException me0 = new MessageException("test message");
        final StackTraceElement[] st0 = me0.getStackTrace();
        Assertions.assertTrue(st0.length > 0);

        TweakStack.resetGlobal();
        final MessageException me1 = new MessageException("test message");
        final StackTraceElement[] st1 = me1.getStackTrace();
        Assertions.assertTrue(st1.length > 0);

        TweakStack.tweakGlobal(false);
        final MessageException me2 = new MessageException("test message");
        final StackTraceElement[] st2 = me2.getStackTrace();
        Assertions.assertFalse(st2.length > 0);
        TweakStack.resetGlobal();
    }

    @Test
    @TmsLink("C11022")
    public void tweakLogger() {
        final Map<Integer, Boolean> map = new HashMap<>();
        final Logger root = (Logger) LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
        final AppenderBase<ILoggingEvent> debug = new AppenderBase<>() {
            @Override
            protected void append(ILoggingEvent eventObject) {
                final String format = eventObject.getFormattedMessage();
                if (format.contains("===1===")) {
                    map.put(1, Boolean.TRUE);
                }
                if (format.contains("===2===")) {
                    map.put(2, Boolean.TRUE);
                }
                if (format.contains("===3===")) {
                    map.put(3, Boolean.TRUE);
                }
            }
        };
        debug.setContext(root.getLoggerContext());
        debug.start();
        root.addAppender(debug);

        log.trace("===1=== before debug, hide");
        TweakLogger.tweakThread(LogLevel.TRACE);
        log.trace("===2=== after debug1, show");
        TweakLogger.resetThread();
        log.trace("===3=== after debug2, hide");

        Assertions.assertNull(map.get(1));
        Assertions.assertTrue(map.get(2));
        Assertions.assertNull(map.get(3));
        map.clear();

        log.trace("===1=== before debug, hide");
        TweakLogger.tweakGlobal(LogLevel.TRACE);
        log.trace("===2=== after debug1, show");
        TweakLogger.resetGlobal();
        log.trace("===3=== after debug2, hide");

        Assertions.assertNull(map.get(1));
        Assertions.assertTrue(map.get(2));
        Assertions.assertNull(map.get(3));
        map.clear();
    }
}
