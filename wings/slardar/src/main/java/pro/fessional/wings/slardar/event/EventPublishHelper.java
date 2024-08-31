package pro.fessional.wings.slardar.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.core.ResolvableType;

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

@Slf4j
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
     * <p>
     * throws IllegalStateException if no globalPublisher
     *
     * @see #isPreparedGlobal
     */
    public static final ApplicationEventPublisher AsyncGlobal = new GlobalPub(true);

    /**
     * try to publish widely, prefer AsyncGlobal, else AsyncSpring with warn
     *
     * @see #AsyncGlobal
     * @see #AsyncSpring
     */
    public static final ApplicationEventPublisher AsyncWidely = new GlobalPub(false);

    private static Executor asyncExecutor;
    private static ApplicationEventPublisher springPublisher;
    private static ApplicationEventPublisher globalPublisher;

    public static void prepareAsyncExecutor(@NotNull Executor async) {
        asyncExecutor = async;
    }

    public static void prepareSpringPublisher(@NotNull ApplicationEventPublisher spring) {
        springPublisher = spring;
    }

    public static void prepareGlobalPublisher(@NotNull ApplicationEventPublisher global) {
        globalPublisher = global;
    }

    /**
     * whether global is prepared
     */
    public static boolean isPreparedGlobal() {
        return globalPublisher != null && asyncExecutor != null;
    }

    /**
     * whether app async is prepared
     */
    public static boolean isPreparedAsync() {
        return springPublisher != null && asyncExecutor != null;
    }

    /**
     * whether app sync is prepared
     */
    public static boolean isPreparedSync() {
        return springPublisher != null;
    }

    /**
     * whether this helper is fully prepared
     */
    public static boolean isPrepared() {
        return springPublisher != null && globalPublisher != null && asyncExecutor != null;
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
            asyncExecutor.execute(() -> springPublisher.publishEvent(event));
        }
    }

    @RequiredArgsConstructor
    private static class GlobalPub implements ApplicationEventPublisher {

        private final boolean strict;

        @Override
        public void publishEvent(@NotNull Object event) {
            if (globalPublisher != null) {
                asyncExecutor.execute(() -> globalPublisher.publishEvent(event));
            }
            else {
                if (strict) {
                    throw new IllegalStateException("no globalPublisher, use #hasAsyncGlobal to test");
                }
                else {
                    log.warn("no globalPublisher, publish by spring async in no strict");
                }
                asyncExecutor.execute(() -> springPublisher.publishEvent(event));
            }
        }
    }
}
