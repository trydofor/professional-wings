package pro.fessional.wings.slardar.event;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.topic.ITopic;
import com.hazelcast.topic.Message;
import com.hazelcast.topic.MessageListener;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationEventPublisher;

import java.util.UUID;

import static pro.fessional.wings.slardar.constants.HazelcastConst.TopicApplicationEvent;

/**
 * ApplicationEventPublisher is a helper. Generally used for non-transactional Event processing, with the following main functions:
 * (1) Asynchronous publishing.
 * (2) IDE prompt navigation.
 * (3) Hazelcast topic (#HazelcastTopic) in the SpringEvent pattern.
 *
 * @author trydofor
 * @see pro.fessional.wings.slardar.constants.HazelcastConst#TopicApplicationEvent
 * @since 2021-06-07
 */
@Slf4j
public class HazelcastSyncPublisher implements ApplicationEventPublisher, MessageListener<Object> {

    private final ApplicationEventPublisher publisher;
    private final ITopic<Object> topic;
    private final UUID uuid;

    public HazelcastSyncPublisher(@NotNull HazelcastInstance instance, @NotNull ApplicationEventPublisher publisher) {
        this.publisher = publisher;
        topic = instance.getTopic(TopicApplicationEvent);
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
        final Object event = message.getMessageObject();
        log.debug("publish event from hazelcast topic, event={}", event);
        publisher.publishEvent(event);
    }
}
