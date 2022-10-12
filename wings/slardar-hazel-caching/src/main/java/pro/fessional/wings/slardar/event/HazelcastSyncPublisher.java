package pro.fessional.wings.slardar.event;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.topic.ITopic;
import com.hazelcast.topic.Message;
import com.hazelcast.topic.MessageListener;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationEventPublisher;

import java.util.UUID;

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

    private final ApplicationEventPublisher publisher;
    private final ITopic<Object> topic;
    private final UUID uuid;

    public HazelcastSyncPublisher(@NotNull HazelcastInstance instance, @NotNull ApplicationEventPublisher publisher) {
        this.publisher = publisher;
        topic = instance.getTopic(HazelcastTopic);
        uuid = topic.addMessageListener(this);
    }

    public UUID getMessageListenerUuid() {
        return uuid;
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
