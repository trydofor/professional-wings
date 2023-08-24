package pro.fessional.wings.slardar.concur;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import org.jetbrains.annotations.NotNull;
import pro.fessional.mirana.lock.GlobalLock;
import pro.fessional.wings.slardar.concur.impl.HazelcastMapLock;

import java.util.concurrent.locks.Lock;

import static pro.fessional.wings.slardar.constants.HazelcastConst.MapGlobalLock;

/**
 * <pre>
 * <a href="https://docs.hazelcast.com/hazelcast/5.1/data-structures/locking-maps">Pessimistic Locking IMap.lock/unlock</a>
 * The lock will automatically be collected by the garbage collector when the lock is released and no other waiting conditions exist on the lock.
 *
 * <a href="https://docs.hazelcast.com/hazelcast/5.1/data-structures/fencedlock">FencedLock</a> -
 * Locks are not automatically removed. If a lock is not used anymore,
 * Hazelcast does not automatically perform garbage collection in the lock.
 * This can lead to an OutOfMemoryError.
 * If you create locks on the fly, make sure they are destroyed.
 * </pre>
 *
 * @author trydofor
 * @see pro.fessional.wings.slardar.constants.HazelcastConst#MapGlobalLock
 * @since 2021-03-08
 */
public class HazelcastGlobalLock implements GlobalLock {

    private final IMap<Object, Object> hazelcastMap;

    public HazelcastGlobalLock(HazelcastInstance hazelcastInstance) {
        hazelcastMap = hazelcastInstance.getMap(MapGlobalLock);
    }

    @Override
    @NotNull
    public Lock getLock(@NotNull String name) {
        return new HazelcastMapLock(hazelcastMap, name);
    }
}
