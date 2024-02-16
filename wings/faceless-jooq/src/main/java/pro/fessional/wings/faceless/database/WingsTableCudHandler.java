package pro.fessional.wings.faceless.database;

import org.jetbrains.annotations.NotNull;
import org.jooq.Table;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * <pre
 * Handle table changes. include field and its values.
 * - create/insert - values are new values
 * - update - values are current values and where (eq/le/ge/in)
 * - delete - values are where (eq/le/ge/in)
 * </pre>
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
     * Auto Handler
     */
    interface Auto {
        boolean accept(@NotNull Class<?> source, @NotNull Cud cud, @NotNull String table);
    }

    /**
     * Handle table changes, light task or async are recommended,
     *
     * @param source event source
     * @param cud    type
     * @param table  case-sensitive table name
     * @param field  field and its changes
     */
    void handle(@NotNull Class<?> source, @NotNull Cud cud, @NotNull String table, @NotNull Supplier<Map<String, List<?>>> field);

    /**
     * @see #handle(Class, Cud, String, Supplier)
     */
    default void handle(@NotNull Class<?> source, @NotNull Cud cud, @NotNull Table<?> table, @NotNull Supplier<Map<String, List<?>>> field) {
        handle(source, cud, table.getName(), field);
    }

    default void handle(@NotNull Class<?> source, @NotNull Cud cud, @NotNull Table<?> table, @NotNull Consumer<Map<String, List<?>>> field) {
        handle(source, cud, table.getName(), field);
    }

    default void handle(@NotNull Class<?> source, @NotNull Cud cud, @NotNull String table, @NotNull Consumer<Map<String, List<?>>> field) {
        handle(source, cud, table, () -> {
            Map<String, List<?>> map = new HashMap<>();
            field.accept(map);
            return map;
        });
    }

    default void handle(@NotNull Class<?> source, @NotNull Cud cud, @NotNull String table, @NotNull Map<String, List<?>> field) {
        handle(source, cud, table, () -> field);
    }

    /**
     * @see #handle(Class, Cud, Table, Map)
     */
    default void handle(@NotNull Class<?> source, @NotNull Cud cud, @NotNull Table<?> table, @NotNull Map<String, List<?>> field) {
        handle(source, cud, table.getName(), () -> field);
    }

    /**
     * Handle table changes, light task or async are recommended. empty field as default.
     *
     * @param source event source
     * @param cud    type
     * @param table  case-sensitive table name
     */
    default void handle(@NotNull Class<?> source, @NotNull Cud cud, @NotNull String table) {
        handle(source, cud, table, Collections::emptyMap);
    }

    /**
     * @see #handle(Class, Cud, String)
     */
    default void handle(@NotNull Class<?> source, @NotNull Cud cud, @NotNull Table<?> table) {
        handle(source, cud, table.getName(), Collections::emptyMap);
    }

    /**
     * Register an auto handler to handle table changes instead of doing it manually.
     *
     * @param auto the handler to register
     */
    default void register(@NotNull Auto auto) {
    }
}
