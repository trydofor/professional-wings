package pro.fessional.wings.slardar.concur.impl;

import com.google.errorprone.annotations.DoNotCall;
import com.hazelcast.map.IMap;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * @author trydofor
 * @since 2021-03-09
 */
@RequiredArgsConstructor
public class HazelcastMapLock implements Lock {
    private final IMap<Object, Object> imap;
    private final String name;

    @Override
    public void lock() {
        imap.lock(name);
    }

    @Override
    @DoNotCall
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
    @DoNotCall
    public Condition newCondition() {
        throw new UnsupportedOperationException();
    }
}
