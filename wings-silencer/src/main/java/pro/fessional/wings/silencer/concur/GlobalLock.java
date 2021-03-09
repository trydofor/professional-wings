package pro.fessional.wings.silencer.concur;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.locks.Lock;

/**
 * 全局锁，可基于JVM，数据库，分布式等实现。
 * 因为锁的实现不同，所以不建议保持Lock引用，
 * 应该每次使用，每次getLock，符合以下格式。
 *
 * <p>A typical usage idiom for this method would be:
 *  <pre> {@code
 * final Lock lock = xxx.getLock("...");
 * if (lock.tryLock()) {
 *   try {
 *     // manipulate protected state
 *   } finally {
 *     lock.unlock();
 *   }
 * } else {
 *   // perform alternative actions
 * }}</pre>
 *
 * @author trydofor
 * @since 2021-03-08
 */
public interface GlobalLock {

    /**
     * 创建以锁实例，锁实例中必须实现 tryLock和unlock方法
     *
     * @param name 锁名
     * @return 锁
     */
    @NotNull
    Lock getLock(@NotNull String name);
}
