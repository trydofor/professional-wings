package pro.fessional.wings.warlock.service.event;

import org.jetbrains.annotations.NotNull;
import org.jooq.Record;
import org.jooq.Table;
import org.jooq.TableField;
import pro.fessional.mirana.best.AssertArgs;
import pro.fessional.wings.warlock.event.cache.TableChangeEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.singletonMap;
import static pro.fessional.wings.warlock.event.cache.TableChangeEvent.DELETE;
import static pro.fessional.wings.warlock.event.cache.TableChangeEvent.INSERT;
import static pro.fessional.wings.warlock.event.cache.TableChangeEvent.UPDATE;

/**
 * Publish table record change (insert, update, delete) events (default and recommended asynchronous)
 *
 * @author trydofor
 * @since 2021-06-10
 */
public interface TableChangePublisher {

    default void publishInsert(@NotNull Class<?> source, @NotNull String table, @NotNull Map<String, List<?>> field) {
        publish(INSERT, source, table, field);
    }

    default <R extends Record, F> void publishInsert(@NotNull Class<?> source, @NotNull TableField<R, F> field, @NotNull List<F> value) {
        publish(INSERT, source, field, value);
    }

    default <R extends Record> void publishInsert(@NotNull Class<?> source, @NotNull Table<R> table, @NotNull Map<TableField<R, ?>, List<?>> field) {
        publish(INSERT, source, table, field);
    }

    //
    default void publishUpdate(@NotNull Class<?> source, @NotNull String table, @NotNull Map<String, List<?>> field) {
        publish(UPDATE, source, table, field);
    }

    default <R extends Record, F> void publishUpdate(@NotNull Class<?> source, @NotNull TableField<R, F> field, @NotNull List<F> value) {
        publish(UPDATE, source, field, value);
    }

    default <R extends Record> void publishUpdate(@NotNull Class<?> source, @NotNull Table<R> table, @NotNull Map<TableField<R, ?>, List<?>> field) {
        publish(UPDATE, source, table, field);
    }

    //
    default void publishDelete(@NotNull Class<?> source, @NotNull String table, @NotNull Map<String, List<?>> field) {
        publish(DELETE, source, table, field);
    }

    default <R extends Record, F> void publishDelete(@NotNull Class<?> source, @NotNull TableField<R, F> field, @NotNull List<F> value) {
        publish(DELETE, source, field, value);
    }

    default <R extends Record> void publishDelete(@NotNull Class<?> source, @NotNull Table<R> table, @NotNull Map<TableField<R, ?>, List<?>> field) {
        publish(DELETE, source, table, field);
    }

    //
    default void publishAllCud(@NotNull Class<?> source, @NotNull String table, @NotNull Map<String, List<?>> field) {
        publish(INSERT | UPDATE | DELETE, source, table, field);
    }

    default <R extends Record, F> void publishAllCud(@NotNull Class<?> source, @NotNull TableField<R, F> field, @NotNull List<F> value) {
        publish(INSERT | UPDATE | DELETE, source, field, value);
    }

    default <R extends Record> void publishAllCud(@NotNull Class<?> source, @NotNull Table<R> table, @NotNull Map<TableField<R, ?>, List<?>> field) {
        publish(INSERT | UPDATE | DELETE, source, table, field);
    }

    //
    default <R extends Record, F> void publish(int cud, @NotNull Class<?> source, @NotNull TableField<R, F> field, @NotNull List<F> value) {
        final Table<R> table = field.getTable();
        AssertArgs.notNull(table, "field's table is null. field=" + field);
        publish(cud, source, table.getName(), singletonMap(field.getName(), value));
    }

    default <R extends Record> void publish(int cud, @NotNull Class<?> source, @NotNull Table<R> table, @NotNull Map<TableField<R, ?>, List<?>> field) {
        Map<String, List<?>> map = new HashMap<>();
        for (Map.Entry<TableField<R, ?>, List<?>> en : field.entrySet()) {
            map.put(en.getKey().getName(), en.getValue());
        }
        publish(cud, source, table.getName(), map);
    }

    default void publish(int cud, @NotNull Class<?> source, @NotNull String table, @NotNull Map<String, List<?>> field) {
        TableChangeEvent event = new TableChangeEvent();
        event.setChange(cud);
        event.addSource(source.getName());
        event.setTable(table);
        event.setField(field);
        publish(event);
    }

    void publish(@NotNull TableChangeEvent event);
}
