package pro.fessional.wings.faceless.database.jooq;

import pro.fessional.mirana.code.Excel26Az;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 全局的静态的wings jooq控制变量
 *
 * @author trydofor
 * @since 2020-06-01
 */
public class WingsJooqEnv {
    /**
     * 控制dao中是否只支持mysql的高效 insert ignore和replace into
     * spring.wings.faceless.jooq.enabled.batch-mysql=true
     */
    public static volatile boolean daoBatchMysql = true;

    private static final AtomicInteger seq = new AtomicInteger(0);

    public static String uniqueAlias() {
        return Excel26Az.index(seq.getAndIncrement()).toLowerCase();
    }
}
