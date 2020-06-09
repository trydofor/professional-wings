package pro.fessional.wings.faceless.database.common;

import org.jooq.Field;
import org.jooq.QueryPart;
import org.jooq.RowCountQuery;
import org.jooq.Table;
import org.jooq.TableRecord;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;

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
