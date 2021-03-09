package pro.fessional.wings.slardar.concur;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import pro.fessional.wings.silencer.concur.GlobalLock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * 默认使用IMap.lock实现，可配置使用CPSubsystem实现锁。
 * 当CpSubsystem可用时(CPMemberCount>0)，可选用Raft的lock锁
 *
 * @author trydofor
 * @since 2021-03-08
 */
public class HazelcastGlobalLock implements GlobalLock {

    private static final String IMapKey = "wings:global:lock";
    private final HazelcastInstance hazelcastInstance;
    private final boolean useCpIfSafe;

    /**
     * 默认使用Imap实现
     *
     * @param hazelcastInstance 实例
     */
    public HazelcastGlobalLock(HazelcastInstance hazelcastInstance) {
        this.hazelcastInstance = hazelcastInstance;
        this.useCpIfSafe = false;
    }

    /**
     * @param hazelcastInstance 实例
     * @param useCpIfSafe       当CPMemberCount>0时，使用CP
     */
    public HazelcastGlobalLock(HazelcastInstance hazelcastInstance, boolean useCpIfSafe) {
        this.hazelcastInstance = hazelcastInstance;
        this.useCpIfSafe = useCpIfSafe && hazelcastInstance.getConfig().getCPSubsystemConfig().getCPMemberCount() > 0;
    }

    @Override
    public @NotNull Lock getLock(@NotNull String name) {
        if (useCpIfSafe) {
            return hazelcastInstance.getCPSubsystem().getLock(name);
        } else {
            return new Hd(hazelcastInstance.getMap(IMapKey), name);
        }
    }

    @RequiredArgsConstructor
    public static class Hd implements Lock {

        private final IMap<Object, Object> imap;
        private final String name;

        @Override
        public void lock() {
            imap.lock(name);
        }

        @Override
        public void lockInterruptibly() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean tryLock() {
            return imap.tryLock(name);
        }

        @Override
        public boolean tryLock(long time, @NotNull TimeUnit unit) throws InterruptedException {
            return imap.tryLock(name, time, unit);
        }

        @Override
        public void unlock() {
            imap.unlock(name);
        }

        @NotNull
        @Override
        public Condition newCondition() {
            throw new UnsupportedOperationException();
        }
    }
}
