package pro.fessional.wings.testing.silencer;

import io.qameta.allure.TmsLink;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author trydofor
 * @since 2023-11-01
 */
@SpringBootTest(properties = {
        "logging.level.root=DEBUG", // AssertionLogger
})
@Slf4j
class TestingLoggerAssertTest {

    @Test
    @TmsLink("C11026")
    void install() {
        final TestingLoggerAssert al = TestingLoggerAssert.install();
        String str = "AssertionLoggerTest to assert in logger";
        al.rule("info", evn -> evn.getFormattedMessage().contains(str));
        al.start();

        //
        log.info(str);
        Assertions.assertTrue(al.getAssertCount("info") > 0, al::messageCount);

        //
        al.resetCount();
        al.stop();
        log.info(str);
        assertEquals(0, al.getAssertCount("info"), al::messageCount);

        al.resetCount();
        al.start();
        log.info(str);
        Assertions.assertTrue(al.getAssertCount("info") > 0, al::messageCount);

        /////
        al.resetCount();
        al.uninstall();
        al.rule("info", evn -> evn.getFormattedMessage().contains(str));
        al.start();

        log.info(str);
        assertEquals(0, al.getAssertCount("info"), al::messageCount);
    }
}