package pro.fessional.wings.faceless.service.journal;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.beans.Transient;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * <pre>
 * Perform a diff on the CUD of the table. There are 3 states,
 * `value1` is the old value and `value2` is the new value
 *
 * * insert - value1 == empty, value2 != empty
 * * update - value1 != empty, value2 != empty
 * * delete - value1 != empty, value2 == empty
 *
 * In addition, `column.size` x `count` == `value#.size`,
 * which can represent multiple records.
 *
 * Strong type when generated, but type is lost after JSON serialization,
 * when JSON deserialized, value only has String and Number type
 * </pre>
 *
 * @author trydofor
 * @since 2022-10-23
 */
@Getter @Setter
public class JournalDiff {

    private int count = 0;
    @NotNull
    private String table = "";
    @NotNull
    private List<String> column = Collections.emptyList();
    @NotNull
    private List<Object> value1 = Collections.emptyList();
    @NotNull
    private List<Object> value2 = Collections.emptyList();

    private transient boolean typed = false;

    /**
     * Whether value is typed. jooq-generated code has type
     */
    @Transient
    public boolean isTyped() {
        return typed;
    }

    /**
     * Whether it has record
     */
    @Transient
    public boolean hasRecord() {
        return count > 0 && !table.isEmpty();
    }

    /**
     * Whether valid (Insert,Update, or Delete)
     */
    @Transient
    public boolean isValid() {
        return isInsert() || isUpdate() || isDelete();
    }

    /**
     * Whether an insert
     */
    @Transient
    public boolean isInsert() {
        return hasRecord() &&
               value1.isEmpty() &&
               value2.size() == column.size() * count;
    }

    /**
     * Whether a delete
     */
    @Transient
    public boolean isDelete() {
        return hasRecord() &&
               value1.size() == column.size() * count &&
               value2.isEmpty();
    }

    /**
     * Whether an update
     */
    @Transient
    public boolean isUpdate() {
        final int cs = column.size() * count;
        return hasRecord() &&
               value1.size() == cs &&
               value2.size() == cs;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof JournalDiff diff)) return false;
        return count == diff.count && table.equals(diff.table) &&
               column.equals(diff.column) &&
               value1.equals(diff.value1) &&
               value2.equals(diff.value2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(count, table, column, value1, value2);
    }

    @Override public String toString() {
        return "JournalDiff{" +
               "count=" + count +
               ", table='" + table + '\'' +
               ", column=" + column +
               ", value1=" + value1 +
               ", value2=" + value2 +
               '}';
    }
}
