package pro.fessional.wings.slardar.event;

import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.core.ResolvableType;
import pro.fessional.mirana.best.AssertState;

import java.util.concurrent.Executor;

/**
 * <pre>
 * ApplicationEventPublisher helper. Generally used for non-transactional Event, the main function:
 * (1) async publish event
 * (2) IDE hit and navigation
 * (3) wrap hazelcast topic(#HazelcastTopic) to SpringEvent
 *
 * Note, do NOT use ApplicationEventMulticaster in async, or handle exception,
 * that would break Spring's default synchronization mechanism.
 * </pre>
 *
 * @author trydofor
 * @see ApplicationEventPublisher
 * @see ApplicationEventMulticaster
 * @see AbstractApplicationContext#initApplicationEventMulticaster()
 * @see AbstractApplicationContext#publishEvent(Object, ResolvableType)
 * @since 2021-06-07
 */

public class EventPublishHelper {

    /**
     * Wrapper of ApplicationEventPublisher, default sync, Recommended not to modify.
     */
    public static final ApplicationEventPublisher SyncSpring = new SyncPub();

    /**
     * Async and unordered ApplicationEventPublisher wrapped with Executor (default SLARDAR_EVENT_EXECUTOR)
     */
    public static final ApplicationEventPublisher AsyncSpring = new AsyncPub();

    /**
     * Wrap Hazelcast (HazelcastTopic)topic to SpringEvent.
     * fire and forget, async and unordered by default.
     * If the event needs to be ordered, set globalOrderEnabled=true.
     *
     * @see #hasAsyncGlobal
     */
    public static final ApplicationEventPublisher AsyncGlobal = new GlobalPub();

    private static Executor executor;
    private static ApplicationEventPublisher springPublisher;
    private static ApplicationEventPublisher globalPublisher;

    public static void setGlobalPublisher(ApplicationEventPublisher globalPublisher) {
        EventPublishHelper.globalPublisher = globalPublisher;
    }

    public static void setExecutor(Executor executor) {
        EventPublishHelper.executor = executor;
    }

    public static void setSpringPublisher(ApplicationEventPublisher springPublisher) {
        EventPublishHelper.springPublisher = springPublisher;
    }

    public static boolean hasAsyncGlobal() {
        return globalPublisher != null;
    }

    private static class SyncPub implements ApplicationEventPublisher {

        @Override
        public void publishEvent(@NotNull Object event) {
            springPublisher.publishEvent(event);
        }
    }

    private static class AsyncPub implements ApplicationEventPublisher {

        @Override
        public void publishEvent(@NotNull Object event) {
            executor.execute(() -> springPublisher.publishEvent(event));
        }
    }

    private static class GlobalPub implements ApplicationEventPublisher {

        @Override
        public void publishEvent(@NotNull Object event) {
            AssertState.notNull(globalPublisher, "no globalPublisher, use #hasAsyncGlobal to test");
            executor.execute(() -> globalPublisher.publishEvent(event));
        }
    }
}
