package pro.fessional.wings.slardar.event;

import io.qameta.allure.TmsLink;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import pro.fessional.wings.slardar.app.event.TestEvent;

/**
 * @author trydofor
 * @since 2021-06-09
 */
@SpringBootTest(properties = "spring.wings.slardar.enabled.mock-hazelcast=false")
@Slf4j
public class EventPublishHelperTest {

    @Test
    @TmsLink("C13019")
    public void testSyncSpring() {
        try {
            EventPublishHelper.SyncSpring.publishEvent(new TestEvent("SyncSpring"));
            Assertions.fail();
        }
        catch (Exception e) {
            log.warn("caught", e);
        }
    }

    @Test
    @TmsLink("C13020")
    public void testAsyncSpring() {
        EventPublishHelper.AsyncSpring.publishEvent(new TestEvent("AsyncSpring"));
    }

    @Test
    @TmsLink("C13021")
    public void testAsyncGlobal() {
        EventPublishHelper.AsyncGlobal.publishEvent(new TestEvent("AsyncHazelcast"));
    }
}
