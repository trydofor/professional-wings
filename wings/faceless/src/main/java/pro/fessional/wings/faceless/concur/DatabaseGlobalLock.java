package pro.fessional.wings.faceless.concur;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.jdbc.core.JdbcTemplate;
import pro.fessional.mirana.lock.GlobalLock;

import java.util.concurrent.locks.Lock;

/**
 * DataSource-based global lock for database-level locking.
 * When read/write separation or data sharding, pay attention to data source switching.
 *
 * @author trydofor
 * @since 2021-03-08
 */
@RequiredArgsConstructor
public class DatabaseGlobalLock implements GlobalLock {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public @NotNull Lock getLock(@NotNull String name) {
        return new MysqlServerLock(jdbcTemplate, name);
    }
}
