package pro.fessional.wings.slardar.event;

import io.qameta.allure.TmsLink;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import pro.fessional.mirana.best.TypedReg;
import pro.fessional.mirana.time.Sleep;
import pro.fessional.wings.slardar.app.event.TestEvent;
import pro.fessional.wings.slardar.context.AttributeHolder;
import pro.fessional.wings.slardar.event.attr.AttributeRidEvent;

/**
 * @author trydofor
 * @since 2021-06-09
 */
@SpringBootTest(properties = "wings.enabled.slardar.hazelcast-standalone=false")
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

    private final TypedReg<Integer, String> Test1 = new TypedReg<>() {};

    @Test
    @TmsLink("C13118")
    public void testAttributeRidEvent() {
        AttributeHolder.putAttr(Test1, 1, "1");
        Assertions.assertEquals("1", AttributeHolder.getAttr(Test1, 1));
        AttributeRidEvent event = new AttributeRidEvent();
        event.rid(Test1, 1);
        EventPublishHelper.AsyncGlobal.publishEvent(event);
        Sleep.ignoreInterrupt(1000);
        Assertions.assertNull(AttributeHolder.getAttr(Test1, 1));

        //
        AttributeHolder.putAttr(Test1, 1, "1");
        Assertions.assertEquals("1", AttributeHolder.getAttr(Test1, 1));

        // unregister, no listener affect
        AttributeHolder.unregister(Test1);

        AttributeRidEvent event2 = new AttributeRidEvent();
        event2.rid(Test1, 1);
        EventPublishHelper.AsyncGlobal.publishEvent(event2);
        Sleep.ignoreInterrupt(1000);
        Assertions.assertEquals("1", AttributeHolder.getAttr(Test1, 1));
    }
}
