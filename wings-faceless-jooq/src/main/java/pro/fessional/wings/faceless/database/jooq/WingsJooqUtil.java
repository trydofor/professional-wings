package pro.fessional.wings.faceless.database.jooq;

import org.jetbrains.annotations.NotNull;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Operator;
import org.jooq.Param;
import org.jooq.QueryPart;
import org.jooq.Record;
import org.jooq.RowCountQuery;
import org.jooq.Table;
import org.jooq.TableRecord;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.jooq.impl.DSL.row;
import static org.jooq.impl.DSL.trueCondition;

/**
 * @author trydofor
 * @since 2020-06-01
 */
public class WingsJooqUtil {

    public static RowCountQuery replaceInto(TableRecord<?> record) {
        Table<?> table = record.getTable();
        Field<?>[] fields = table.fields();
        QueryPart[] qps = new QueryPart[fields.length * 2 + 1];
        qps[0] = table;
        System.arraycopy(fields, 0, qps, 1, fields.length);
        int off = fields.length;
        for (int i = 0; i < fields.length; i++) {
            qps[++off] = DSL.val(record.get(i));
        }

        StringBuilder sql = new StringBuilder();
        sql.append("replace into {0} (");
        int pos = buildHolder(sql, 0, fields.length);
        sql.append(") values (");
        buildHolder(sql, pos, fields.length);
        sql.append(")");

        return DSL.query(sql.toString(), qps);
    }

    private static final Field<?>[] EMPTY_FIELDS = new Field<?>[0];

    public static Field<?>[] primaryKeys(Table<?> table) {
        UniqueKey<?> key = table.getPrimaryKey();
        return key == null ? EMPTY_FIELDS : key.getFieldsArray();
    }

    public static void skipFields(TableRecord<?> record, Field<?>... fields) {
        for (Field<?> field : fields) {
            record.changed(field, false);
        }
    }

    public static void skipNullVals(TableRecord<?> record) {
        int size = record.size();
        for (int i = 0; i < size; i++) {
            if (record.get(i) == null) {
                record.changed(i, false);
            }
        }
    }

    public static RowCountQuery replaceInto(Table<?> table, Field<?>... fields) {
        if (fields == null || fields.length == 0) {
            fields = table.fields();
        }
        QueryPart[] qps = new QueryPart[fields.length * 2 + 1];
        qps[0] = table;
        System.arraycopy(fields, 0, qps, 1, fields.length);

        StringBuilder sql = new StringBuilder();
        sql.append("replace into {0} (");
        int pos = buildHolder(sql, 0, fields.length);
        sql.append(") values (");
        buildHolder(sql, pos, fields.length);
        sql.append(")");

        return DSL.query(sql.toString(), qps);
    }

    ///////////////// Condition /////////////////////

    /**
     * 构造一个between的条件
     *
     * @param field          字段
     * @param lowerInclusive 小值，包含
     * @param upperInclusive 大值，包含
     * @param <Z>            类型
     * @return 条件
     */
    @NotNull
    public static <Z> Condition condRange(Field<Z> field, Z lowerInclusive, Z upperInclusive) {
        if (lowerInclusive == null) {
            if (upperInclusive == null) {
                return trueCondition();
            } else {
                return field.le(upperInclusive);
            }
        } else {
            if (upperInclusive == null) {
                return field.ge(lowerInclusive);
            } else {
                return field.between(lowerInclusive, upperInclusive);
            }
        }
    }

    /**
     * 单key或复合key条件 (type,name) = (1,'dog')
     *
     * @param valN 复合值
     * @param rowN 复合Col
     * @return 条件
     */
    @NotNull
    @SuppressWarnings("unchecked")
    public static Condition condCombo(Object valN, Field<?>... rowN) {
        if (rowN.length == 1) {
            return ((Field<Object>) rowN[0]).eq(rowN[0].getDataType().convert(valN));
        }
        // [#2573] Composite key T types are of type Record[N]
        else {
            return row(rowN).eq((Record) valN);
        }
    }

    public static Condition condChain(TableRecord<?> record, Operator andOr) {
        return condChain(record, andOr, true);
    }

    /**
     * 构造一个 and 级联的条件， type=1 and name='dog'
     *
     * @param record     条件
     * @param ignoreNull 是否忽略null
     * @return 条件
     */
    @NotNull
    public static Condition condChain(TableRecord<?> record, Operator andOr, boolean ignoreNull) {
        List<Condition> conds = condField(record, ignoreNull);
        return conds.isEmpty() ? DSL.trueCondition() : DSL.condition(andOr, conds);
    }

    public static List<Condition> condField(TableRecord<?> record, Field<?>... includes) {
        return condField(record, true, includes);
    }

    /**
     * 构造一个 and 级联的条件， type=1 and name='dog'
     *
     * @param record     条件
     * @param ignoreNull 是否忽略null
     * @param includes   包含的字段，默认全包含
     * @return 条件
     */
    @NotNull
    @SuppressWarnings("unchecked")
    public static List<Condition> condField(TableRecord<?> record, boolean ignoreNull, Field<?>... includes) {
        Field<?>[] fields = record.fields();
        // 按include赋值，其他为null
        if (includes != null && includes.length > 0) {
            Field<?>[] temp = new Field<?>[fields.length];
            for (Field<?> fld : includes) {
                for (int i = 0; i < fields.length; i++) {
                    if (fields[i].equals(fld)) {
                        temp[i] = fields[i];
                        break;
                    }
                }
            }
            fields = temp;
        }

        List<Condition> conds = new ArrayList<>(fields.length);
        for (int i = 0; i < fields.length; i++) {
            Field<Object> field = (Field<Object>) fields[i];
            if (field == null) continue;

            Object value = record.getValue(i);
            if (value == null) {
                if (ignoreNull) {
                    // ignore
                } else {
                    conds.add(field.isNull());
                }
            } else {
                conds.add(field.eq(field.getDataType().convert(value)));
            }
        }

        return conds;
    }

    ///////////////// binding /////////////////////

    public static Param<?>[] bindValue(TableRecord<?> record) {
        return bindValue(record, true);
    }

    public static Param<?>[] bindValue(TableRecord<?> record, boolean ignoreNull) {
        Field<?>[] fields = record.fields();
        List<Param<?>> result = new ArrayList<>(fields.length);
        for (int i = 0; i < fields.length; i++) {
            Object value = record.getValue(i);
            if (value == null && ignoreNull) continue;
            result.add(DSL.val(value));
        }

        return result.isEmpty() ? emptyParams : result.toArray(emptyParams);
    }

    public static Param<?>[] bindNamed(TableRecord<?> record) {
        return bindNamed(record, true);
    }

    public static Param<?>[] bindNamed(TableRecord<?> record, boolean ignoreNull) {
        Field<?>[] fields = record.fields();
        List<Param<?>> result = new ArrayList<>(fields.length);
        for (int i = 0; i < fields.length; i++) {
            Field<?> field = fields[i];
            Object value = record.getValue(i);
            if (value == null && ignoreNull) continue;
            result.add(DSL.param(field.getName(), value));
        }

        return result.isEmpty() ? emptyParams : result.toArray(emptyParams);
    }

    private static final Param<?>[] emptyParams = new Param<?>[0];

    public static Param<?>[] bindNamed(Map<String, Object> bindings) {
        return bindNamed(bindings, true);
    }

    public static Param<?>[] bindNamed(Map<String, Object> bindings, boolean ignoreNull) {
        List<Param<?>> result = new ArrayList<>(bindings.size());
        for (Map.Entry<String, Object> entry : bindings.entrySet()) {
            Object value = entry.getValue();
            if (value == null && ignoreNull) continue;
            result.add(DSL.param(entry.getKey(), value));
        }

        return result.isEmpty() ? emptyParams : result.toArray(emptyParams);
    }

    //
    private static int buildHolder(StringBuilder sql, int pos, int len) {
        if (len == 0) return pos;

        for (int i = 0; i < len; i++) {
            sql.append("{");
            sql.append(++pos);
            sql.append("},");
        }

        sql.deleteCharAt(sql.length() - 1);
        return pos;
    }
}
