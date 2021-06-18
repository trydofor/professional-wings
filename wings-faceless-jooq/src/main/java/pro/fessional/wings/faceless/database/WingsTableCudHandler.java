package pro.fessional.wings.faceless.database;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * 数据表CUD处理，可以是CUD或者Other
 *
 * @author trydofor
 * @since 2021-06-12
 */
public interface WingsTableCudHandler {

    enum Cud {
        Create,
        Update,
        Delete,
        Unsure
    }

    /**
     * 处理表变更，建议轻任务或异步
     *
     * @param cud   类型
     * @param table 表名
     * @param field 关联字段和值(或值集合)
     */
    void handle(@NotNull Cud cud, @NotNull String table, @NotNull Map<String, Set<Object>> field);

    /**
     * 处理表变更，建议轻任务或异步，关联键为empty
     *
     * @param cud   类型
     * @param table 表名
     */
    default void handle(@NotNull Cud cud, @NotNull String table) {
        handle(cud, table, Collections.emptyMap());
    }

    /**
     * 处理表变更，建议轻任务或异步，类型为Unsure
     *
     * @param table 表名
     */
    default void handle(@NotNull String table) {
        handle(Cud.Unsure, table, Collections.emptyMap());
    }
}
