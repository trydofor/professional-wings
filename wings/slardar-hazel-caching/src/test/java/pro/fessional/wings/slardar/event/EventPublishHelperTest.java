package pro.fessional.wings.slardar.event;

import io.qameta.allure.TmsLink;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pro.fessional.mirana.best.TypedReg;
import pro.fessional.mirana.time.Sleep;
import pro.fessional.wings.slardar.app.event.TestEvent;
import pro.fessional.wings.slardar.app.service.TestEventListener;
import pro.fessional.wings.slardar.context.AttributeHolder;
import pro.fessional.wings.slardar.event.attr.AttributeRidEvent;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author trydofor
 * @since 2021-06-09
 */
@SpringBootTest
@Slf4j
public class EventPublishHelperTest {

    @Setter(onMethod_ = {@Autowired})
    protected TestEventListener testEventListener;

    @Test
    @TmsLink("C13019")
    public void testSyncSpring() {
        String msg = "SyncSpring";
        EventPublishHelper.SyncSpring.publishEvent(new TestEvent(msg));
        TestEvent ent = testEventListener.getEvents().get(msg);
        assertEquals(msg, ent.getMessage());
    }

    @Test
    @TmsLink("C13020")
    public void testAsyncSpring() {
        String msg = "AsyncSpring";
        EventPublishHelper.AsyncSpring.publishEvent(new TestEvent(msg));
        Sleep.ignoreInterrupt(1000);
        TestEvent ent = testEventListener.getEvents().get(msg);
        assertEquals(msg, ent.getMessage());
    }

    @Test
    @TmsLink("C13021")
    public void testAsyncGlobal() {
        String msg = "AsyncHazelcast";
        EventPublishHelper.AsyncGlobal.publishEvent(new TestEvent(msg));
        Sleep.ignoreInterrupt(1000);
        TestEvent ent = testEventListener.getEvents().get(msg);
        assertEquals(msg, ent.getMessage());
    }

    private final TypedReg<Integer, String> Test1 = new TypedReg<>() {};

    @Test
    @TmsLink("C13118")
    public void testAttributeRidEvent() {
        AttributeHolder.putAttr(Test1, 1, "1");
        assertEquals("1", AttributeHolder.getAttr(Test1, 1));
        AttributeRidEvent event = new AttributeRidEvent();
        event.rid(Test1, 1);
        EventPublishHelper.AsyncGlobal.publishEvent(event);
        Sleep.ignoreInterrupt(1000);
        Assertions.assertNull(AttributeHolder.getAttr(Test1, 1));

        //
        AttributeHolder.putAttr(Test1, 1, "1");
        assertEquals("1", AttributeHolder.getAttr(Test1, 1));

        // unregister, no listener affect
        AttributeHolder.unregister(Test1);

        AttributeRidEvent event2 = new AttributeRidEvent();
        event2.rid(Test1, 1);
        EventPublishHelper.AsyncGlobal.publishEvent(event2);
        Sleep.ignoreInterrupt(1000);
        assertEquals("1", AttributeHolder.getAttr(Test1, 1));
    }
}
