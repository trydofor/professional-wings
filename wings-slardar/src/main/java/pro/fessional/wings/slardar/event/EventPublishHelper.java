package pro.fessional.wings.slardar.event;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.topic.Message;
import com.hazelcast.topic.MessageListener;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationEventPublisher;
import pro.fessional.wings.slardar.spring.bean.SlardarEventConfiguration;

import java.util.concurrent.Executor;

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

public class EventPublishHelper {

    /**
     * ApplicationEventPublisher 的封装，默认sync，建议不要修改
     */
    public static final ApplicationEventPublisher SyncSpring = new SyncPub();

    /**
     * 使用Executor(默认SLARDAR_EVENT_EXECUTOR)包装的异步无序ApplicationEventPublisher
     *
     * @see SlardarEventConfiguration#SLARDAR_EVENT_EXECUTOR
     */
    public static final ApplicationEventPublisher AsyncSpring = new AsyncPub();

    /**
     * 包装Hazelcast的(HazelcastTopic)topic转成SpringEvent。
     * 默认异步无序, fire and forget。若需要有序，自行设置globalOrderEnabled=true
     *
     * @see #HazelcastTopic
     */
    public static final ApplicationEventPublisher AsyncHazelcast = new HazelcastPub();

    public static final String HazelcastTopic = "SlardarApplicationEvent";

    //
    private static Executor executor;
    private static ApplicationEventPublisher publisher;
    private static HazelcastInstance hazelcast;

    protected EventPublishHelper(ApplicationEventPublisher pub, Executor exe, HazelcastInstance hzc) {
        if (hzc != null && hzc != hazelcast) {
            hzc.getTopic(HazelcastTopic).addMessageListener(new HazelcastRepublish());
        }
        hazelcast = hzc;
        publisher = pub;
        executor = exe;
    }

    private static class SyncPub implements ApplicationEventPublisher {

        @Override
        public void publishEvent(@NotNull Object event) {
            publisher.publishEvent(event);
        }
    }

    private static class AsyncPub implements ApplicationEventPublisher {

        @Override
        public void publishEvent(@NotNull Object event) {
            executor.execute(() -> publisher.publishEvent(event));
        }
    }

    private static class HazelcastPub implements ApplicationEventPublisher {

        @Override
        public void publishEvent(@NotNull Object event) {
            hazelcast.getTopic(HazelcastTopic).publish(event);
        }
    }

    private static class HazelcastRepublish implements MessageListener<Object> {
        @Override
        public void onMessage(Message<Object> message) {
            publisher.publishEvent(message.getMessageObject());
        }
    }
}
