package pro.fessional.wings.faceless.database.jooq;

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
}
