package pro.fessional.wings.slardar.event;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author trydofor
 * @since 2021-06-09
 */
@SpringBootTest(properties = "spring.wings.slardar.enabled.mock-hazelcast=false")
public class EventPublishHelperTest {

    @Test
    public void test() {
        EventPublishHelper.SyncSpring.publishEvent(new TestEvent("SyncSpring"));
        EventPublishHelper.AsyncSpring.publishEvent(new TestEvent("AsyncSpring"));
        EventPublishHelper.AsyncGlobal.publishEvent(new TestEvent("AsyncHazelcast"));
    }
}
