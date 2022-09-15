package pro.fessional.wings.slardar.event;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author trydofor
 * @since 2021-06-09
 */
@SpringBootTest(properties = "spring.wings.slardar.enabled.mock-hazelcast=false")
@Slf4j
public class EventPublishHelperTest {

    @Test
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
    public void testAsyncSpring() {
        EventPublishHelper.AsyncSpring.publishEvent(new TestEvent("AsyncSpring"));
    }

    @Test
    public void testAsyncGlobal() {
        EventPublishHelper.AsyncGlobal.publishEvent(new TestEvent("AsyncHazelcast"));
    }
}
