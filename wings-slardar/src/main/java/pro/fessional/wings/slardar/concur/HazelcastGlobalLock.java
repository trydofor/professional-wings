package pro.fessional.wings.slardar.concur;

import com.hazelcast.core.HazelcastInstance;
import org.jetbrains.annotations.NotNull;
import pro.fessional.mirana.lock.GlobalLock;
import pro.fessional.wings.slardar.concur.impl.HazelcastIMapLock;

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
            return new HazelcastIMapLock(hazelcastInstance.getMap(IMapKey), name);
        }
    }
}
