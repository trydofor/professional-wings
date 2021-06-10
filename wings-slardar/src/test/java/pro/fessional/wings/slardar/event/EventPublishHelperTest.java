package pro.fessional.wings.slardar.event;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author trydofor
 * @since 2021-06-09
 */
@SpringBootTest
public class EventPublishHelperTest {

    @Test
    public void test() {
        EventPublishHelper.SyncSpring.publishEvent(new TestEvent("SyncSpring"));
        EventPublishHelper.AsyncSpring.publishEvent(new TestEvent("AsyncSpring"));
        EventPublishHelper.AsyncHazelcast.publishEvent(new TestEvent("AsyncHazelcast"));
    }
}
