package pro.fessional.wings.faceless.database.jooq;

import pro.fessional.mirana.code.Excel26Az;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Global static variable of wings jooq setting.
 *
 * @author trydofor
 * @since 2020-06-01
 */
public class WingsJooqEnv {
    /**
     * Whether mysql's efficient `insert ignore` and `replace into` are supported in Dao.
     * spring.wings.faceless.jooq.enabled.batch-mysql=true
     */
    public static volatile boolean daoBatchMysql = true;

    private static final AtomicInteger seq = new AtomicInteger(0);

    public static String uniqueAlias() {
        return Excel26Az.index(seq.getAndIncrement()).toLowerCase();
    }
}
