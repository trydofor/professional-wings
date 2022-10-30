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
 * 对数据表的CUD进行diff。存在以下3种状态，value1表示前值，value2表示后值
 * - insert - value1 == empty, value2 != empty
 * - update - value1 != empty, value2 != empty
 * - delete - value1 != empty, value2 == empty
 *
 * 此外，column.size * count == value#.size，可表示多条记录。
 * 生成时携带类型，但JSON序列化后会类型丢失，JSON反序列化时，value仅有String和Number类型
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
     * values是否存在类型，jooq生成存在类型
     */
    @Transient
    public boolean isTyped() {
        return typed;
    }

    /**
     * 是否有记录
     */
    @Transient
    public boolean hasRecord() {
        return count > 0 && !table.isEmpty();
    }

    /**
     * 是否有效，Insert,Update,Delete
     */
    @Transient
    public boolean isValid() {
        return isInsert() || isUpdate() || isDelete();
    }

    /**
     * 是否为插入
     */
    @Transient
    public boolean isInsert() {
        return hasRecord() &&
               value1.isEmpty() &&
               value2.size() == column.size() * count;
    }

    /**
     * 是否为删除
     */
    @Transient
    public boolean isDelete() {
        return hasRecord() &&
               value1.size() == column.size() * count &&
               value2.isEmpty();
    }

    /**
     * 是否为更新
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
        if (!(o instanceof JournalDiff)) return false;
        JournalDiff diff = (JournalDiff) o;
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
