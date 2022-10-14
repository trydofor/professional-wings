package pro.fessional.wings.slardar.event;

import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.core.ResolvableType;
import pro.fessional.mirana.best.StateAssert;

import java.util.concurrent.Executor;

/**
 * <pre>
 * ApplicationEventPublisher辅助类。一般用于非事务Event处理，主要功能：
 * ①异步发布。
 * ②IDE提示导航。
 * ③hazelcast的topic(#HazelcastTopic)按SpringEvent模式。
 *
 * 注意，不要为ApplicationEventMulticaster变为异步，或处理异常，会破坏Spring默认的同步机制。
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
     * ApplicationEventPublisher 的封装，默认sync，建议不要修改
     */
    public static final ApplicationEventPublisher SyncSpring = new SyncPub();

    /**
     * 使用Executor(默认SLARDAR_EVENT_EXECUTOR)包装的异步无序ApplicationEventPublisher
     */
    public static final ApplicationEventPublisher AsyncSpring = new AsyncPub();

    /**
     * 包装Hazelcast的(HazelcastTopic)topic转成SpringEvent。
     * 默认异步无序, fire and forget。若需要有序，自行设置globalOrderEnabled=true
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
            StateAssert.notNull(globalPublisher, "no globalPublisher, use #hasAsyncGlobal to test");
            executor.execute(() -> globalPublisher.publishEvent(event));
        }
    }
}
