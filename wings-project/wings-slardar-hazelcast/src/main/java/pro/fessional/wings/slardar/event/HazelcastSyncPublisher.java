package pro.fessional.wings.slardar.event;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.topic.ITopic;
import com.hazelcast.topic.Message;
import com.hazelcast.topic.MessageListener;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ApplicationEventPublisher辅助类。一般用于非事务Event处理，主要功能：
 * ①异步发布。
 * ②IDE提示导航。
 * ③hazelcast的topic(#HazelcastTopic)按SpringEvent模式。
 *
 * @author trydofor
 * @see #HazelcastTopic
 * @since 2021-06-07
 */

public class HazelcastSyncPublisher implements ApplicationEventPublisher, MessageListener<Object> {

    public static final String HazelcastTopic = "SlardarApplicationEvent";
    private static final Map<UUID, Boolean> Listeners = new ConcurrentHashMap<>();

    private final ApplicationEventPublisher publisher;
    private final ITopic<Object> topic;

    public HazelcastSyncPublisher(@NotNull HazelcastInstance instance, @NotNull ApplicationEventPublisher publisher) {
        this.publisher = publisher;
        topic = instance.getTopic(HazelcastTopic);
        final UUID last = topic.addMessageListener(this);
        final Set<UUID> uuids = Listeners.keySet();
    }

    @Override
    public void publishEvent(@NotNull Object event) {
        topic.publish(event);
    }

    @Override
    public void onMessage(Message<Object> message) {
        publisher.publishEvent(message.getMessageObject());
    }
}
