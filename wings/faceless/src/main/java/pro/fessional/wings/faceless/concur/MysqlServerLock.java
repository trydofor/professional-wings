package pro.fessional.wings.faceless.concur;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

import static pro.fessional.wings.faceless.database.helper.JdbcTemplateHelper.FirstIntegerOrNull;

/**
 * Locks based on the Mysql IS_FREE_LOCK and GET_LOCK at mysql instance level.
 *
 * @author trydofor
 * @since 2021-03-08
 */
@RequiredArgsConstructor
@Slf4j
public class MysqlServerLock implements Lock {

    private final JdbcTemplate jdbcTemplate;
    private final String lockName;

    /**
     * Tries to obtain a lock with a name given by the string str,
     * using a timeout of timeout seconds. A negative timeout value
     * means infinite timeout. The lock is exclusive.
     * While held by one session, other sessions
     * cannot obtain a lock of the same name.
     */
    @Override
    public void lock() {
        final Integer rc = jdbcTemplate.query(
                "SELECT GET_LOCK(?, -1) FROM DUAL",
                FirstIntegerOrNull,
                lockName);
        if (rc == null) {
            throw new IllegalStateException("can not get lock, name=" + lockName);
        }
    }

    /**
     * Returns 1 if the lock was obtained successfully,
     * 0 if the attempt timed out (for example, because another
     * client has previously locked the name),
     * or NULL if an error occurred (such as running out of memory
     * or the thread was killed with mysqladmin kill).
     */
    @Override
    public boolean tryLock() {
        final Integer rc = jdbcTemplate.query(
                "SELECT IF(IS_FREE_LOCK(?)=1, GET_LOCK(?,-1), -1) FROM DUAL",
                FirstIntegerOrNull,
                lockName, lockName);
        if (rc == null) {
            throw new IllegalStateException("can not get lock, name=" + lockName);
        }
        return rc == 1;
    }

    @Override
    public boolean tryLock(long time, @NotNull TimeUnit unit) {
        final int sec = (int) Math.max(1, unit.toSeconds(time));
        final Integer rc = jdbcTemplate.query(
                "SELECT IF(IS_FREE_LOCK(?)=1, GET_LOCK(?,?), -1) FROM DUAL",
                FirstIntegerOrNull,
                lockName, lockName, sec);
        if (rc == null) {
            throw new IllegalStateException("can not get lock, name=" + lockName);
        }
        return rc == 1;
    }

    /**
     * Returns 1 if the lock was released,
     * 0 if the lock was not established by this thread (in which case the lock is not released),
     * and NULL if the named lock did not exist.
     */
    @Override
    public void unlock() {
        final Integer rc = jdbcTemplate.query(
                "SELECT RELEASE_LOCK(?) FROM DUAL",
                FirstIntegerOrNull,
                lockName);
        if (rc == null) {
            log.warn("unlock not existed lock, name={}", lockName);
        }
        else if (rc == 0) {
            log.warn("unlock not owned lock, name={}", lockName);
        }
        else {
            log.debug("unlock lock, name={}", lockName);
        }
    }

    @Override
    public void lockInterruptibly() throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    @NotNull
    @Override
    public Condition newCondition() throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }
}
