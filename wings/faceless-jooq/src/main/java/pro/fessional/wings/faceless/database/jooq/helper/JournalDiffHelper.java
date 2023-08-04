package pro.fessional.wings.faceless.database.jooq.helper;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.ResultQuery;
import org.jooq.Table;
import pro.fessional.wings.faceless.service.journal.JournalDiff;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @author trydofor
 * @since 2022-10-24
 */
public class JournalDiffHelper {

    public static final String Default = "default";
    @Getter
    private static final Map<String, Set<String>> DefaultIgnore = new HashMap<>();

    public static void putDefaultIgnore(String... field) {
        final Set<String> set = DefaultIgnore.computeIfAbsent(Default, k -> new HashSet<>());
        for (String f : field) {
            if (f != null) {
                set.add(f);
            }
        }
    }

    public static void putDefaultIgnore(Map<String, Set<String>> map) {
        DefaultIgnore.putAll(map);
    }

    public static JournalDiff diffInsert(Table<?> table, ResultQuery<?> query, Runnable exec) {
        exec.run();
        final Result<?> rs2 = query.fetch();

        final JournalDiff diff = new JournalDiff();
        diff.setTable(table.getName());
        diff.setTyped(true);

        helpInsert(diff, rs2);
        return diff;
    }

    public static JournalDiff diffUpdate(Table<?> table, ResultQuery<?> query, Runnable exec) {
        final Result<?> rs1 = query.fetch();
        exec.run();
        final Result<?> rs2 = query.fetch();

        final JournalDiff diff = new JournalDiff();
        diff.setTable(table.getName());
        diff.setTyped(true);

        helpUpdate(diff, rs1, rs2);
        return diff;
    }

    public static JournalDiff diffDelete(Table<?> table, ResultQuery<?> query, Runnable exec) {
        final Result<?> rs1 = query.fetch();
        exec.run();

        final JournalDiff diff = new JournalDiff();
        diff.setTable(table.getName());
        diff.setTyped(true);

        helpDelete(diff, rs1);
        return diff;
    }

    // ////

    /**
     * Refine the diff results by removing duplicate data and removing ignored fields
     */
    public static void tidy(@NotNull JournalDiff diff, Field<?>... ignore) {
        tidy(true, diff, ignore);
    }

    /**
     * Refine the diff results by removing duplicate data and removing ignored fields
     */
    public static void tidy(boolean withDefault, @NotNull JournalDiff diff, Field<?>... ignore) {
        if (!diff.isValid()) return;

        boolean del = false;
        final ArrayList<String> col = arrayList(diff.getColumn());
        final ArrayList<Object> vs1 = arrayList(diff.getValue1());
        final ArrayList<Object> vs2 = arrayList(diff.getValue2());

        final Object delFlag = new Object();
        // remove same value, if update
        if (diff.isUpdate()) {
            for (int i = 0, len = col.size(); i < len; i++) {
                boolean sm = true;
                for (int j = 0, cnt = diff.getCount(); j < cnt; j += len) {
                    final int d = j + i;
                    if (notEquals(vs1.get(d), vs2.get(d))) {
                        sm = false;
                        break;
                    }
                }

                if (sm) {
                    col.set(i, null);
                    markDelete(vs1, i, len, delFlag);
                    markDelete(vs2, i, len, delFlag);
                    del = true;
                }
            }
        }

        // ignore fields
        final Set<String> fds = new HashSet<>();
        if (withDefault) {
            fds.addAll(DefaultIgnore.getOrDefault(Default, Collections.emptySet()));
            fds.addAll(DefaultIgnore.getOrDefault(diff.getTable(), Collections.emptySet()));
        }
        for (Field<?> f : ignore) {
            fds.add(f.getName());
        }
        if (!fds.isEmpty()) {
            for (int i = 0, len = col.size(); i < len; i++) {
                final String c = col.get(i);
                for (String f : fds) {
                    if (c != null && c.equalsIgnoreCase(f)) {
                        col.set(i, null);
                        markDelete(vs1, i, len, delFlag);
                        markDelete(vs2, i, len, delFlag);
                        del = true;
                        break;
                    }
                }
            }
        }

        // delete
        if (del) {
            col.removeIf(Objects::isNull);
            vs1.removeIf(it -> it == delFlag);
            vs2.removeIf(it -> it == delFlag);

            diff.setColumn(col);
            diff.setValue1(vs1);
            diff.setValue2(vs2);
        }
    }

    // ////
    /**
     * Generate information about record count, field name, and data before and after the update.
     */
    public static void help(@NotNull JournalDiff diff, Result<? extends Record> before, Result<? extends Record> after) {
        final ArrayList<String> column;
        final ArrayList<Object> value1;
        final ArrayList<Object> value2;
        final int count;
        if (before == null) {
            if (after == null) {
                throw new IllegalArgumentException("before and after both null");
            }
            // insert
            else {
                final @NotNull Field<?>[] fds = after.fields();
                column = fieldName(fds);
                count = after.size();
                value1 = new ArrayList<>(0);
                value2 = flatResult(after);
            }
        }
        else {
            // delete
            if (after == null) {
                column = fieldName(before.fields());
                count = before.size();
                value1 = flatResult(before);
                value2 = new ArrayList<>(0);
            }
            // update
            else {
                column = fieldName(before.fields());
                count = before.size();
                if (!column.equals(fieldName(after.fields()))) {
                    throw new IllegalArgumentException("before and after column mismatch");
                }
                if (count != after.size()) {
                    throw new IllegalArgumentException("before and after record mismatch");
                }
                value1 = flatResult(before);
                value2 = flatResult(after);
            }
        }

        diff.setCount(count);
        diff.setColumn(column);
        diff.setValue1(value1);
        diff.setValue2(value2);
    }

    public static void helpInsert(@NotNull JournalDiff diff, @NotNull Result<? extends Record> after) {
        help(diff, null, after);
    }

    public static void helpDelete(@NotNull JournalDiff diff, @NotNull Result<? extends Record> before) {
        help(diff, before, null);
    }

    public static void helpUpdate(@NotNull JournalDiff diff, @NotNull Result<? extends Record> before, @NotNull Result<? extends Record> after) {
        help(diff, before, after);
    }

    // ////
    public static ArrayList<String> fieldName(Field<?>... fields) {
        final ArrayList<String> cols = new ArrayList<>(fields.length);
        for (Field<?> fd : fields) {
            cols.add(fd.getName());
        }
        return cols;
    }

    public static ArrayList<Object> flatResult(Result<? extends Record> result) {
        ArrayList<Object> vs2 = new ArrayList<>();
        for (Record r : result) {
            vs2.addAll(Arrays.asList(r.intoArray()));
        }
        return vs2;
    }

    public static <T> ArrayList<T> arrayList(List<T> os) {
        return os instanceof ArrayList ? (ArrayList<T>) os : new ArrayList<>(os);
    }

    private static void markDelete(ArrayList<Object> arr, int idx, int st, Object flg) {
        final int len = arr.size();
        for (int i = idx; i < len; i += st) {
            arr.set(i, flg);
        }
    }

    private static boolean notEquals(Object v1, Object v2) {
        if (v1 == null && v2 == null) return false;
        if (v1 != null && v2 != null) return !v1.equals(v2);
        return true;
    }
}
