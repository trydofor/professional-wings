package pro.fessional.wings.faceless.concur;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.jdbc.core.JdbcTemplate;
import pro.fessional.wings.silencer.concur.GlobalLock;

import java.util.concurrent.locks.Lock;

/**
 * 基于DataSource的全局锁，用于数据库级锁。
 * 当读写分离或分库时，注意数据源切换。
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
