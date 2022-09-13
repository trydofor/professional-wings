package pro.fessional.wings.faceless.database.jooq;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Operator;
import org.jooq.Param;
import org.jooq.QueryPart;
import org.jooq.RowCountQuery;
import org.jooq.Table;
import org.jooq.TableRecord;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.TableImpl;
import pro.fessional.mirana.cast.BoxedCastUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author trydofor
 * @since 2020-06-01
 */
public class WingsJooqUtil extends DSL {

    private static final Field<?>[] EMPTY_FIELDS = new Field<?>[0];

    @NotNull
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

    ///////////////// replace into /////////////////////

    /**
     * 不受CUD listener管理
     */
    public static RowCountQuery replaceInto(TableRecord<?> record) {
        Table<?> table = record.getTable();
        Field<?>[] fields = table.fields();
        return replaceInto(table, fields);
    }

    /**
     * 不受CUD listener管理
     */
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
        sql.append(')');

        return query(sql.toString(), qps);
    }

    ///////////////// Condition /////////////////////

    /**
     * 若 value.isEmpty，则返回NoCondition，否则返回eq NotNull
     *
     * @param filed 字段
     * @param value 数据
     * @param <Z>   类型
     * @return 条件
     */
    @NotNull
    public static <Z> Condition condEqSkip(Field<Z> filed, Collection<Z> value) {
        return condEqSkip(filed, value, Objects::nonNull);
    }

    /**
     * 若 value.isEmpty，则返回NoCondition，否则返回eq NotNull
     *
     * @param filed  字段
     * @param value  数据
     * @param filter 过滤
     * @param <Z>    类型
     * @return 条件
     */
    @NotNull
    public static <Z> Condition condEqSkip(Field<Z> filed, Collection<Z> value, Predicate<Z> filter) {
        if (value == null || value.isEmpty()) return noCondition();

        for (Z v : value) {
            if (filter.test(v)) return filed.eq(v);
        }

        return noCondition();
    }

    /**
     * 若 value.isEmpty，则返回NoCondition，否则返回in
     *
     * @param filed 字段
     * @param value 数据
     * @param <Z>   类型
     * @return 条件
     */
    @NotNull
    public static <Z> Condition condInSkip(Field<Z> filed, Collection<Z> value) {
        return condInSkip(filed, value, Objects::nonNull);
    }

    /**
     * 若 value.isEmpty，则返回NoCondition，否则返回in
     *
     * @param filed  字段
     * @param value  数据
     * @param filter 过滤
     * @param <Z>    类型
     * @return 条件
     */
    @NotNull
    public static <Z> Condition condInSkip(Field<Z> filed, Collection<Z> value, Predicate<Z> filter) {
        return value == null || value.isEmpty() ?
               noCondition() :
               filed.in(value.stream().filter(filter).collect(Collectors.toList()));
    }

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
                return noCondition();
            }
            else {
                return field.le(upperInclusive);
            }
        }
        else {
            if (upperInclusive == null) {
                return field.ge(lowerInclusive);
            }
            else {
                return field.between(lowerInclusive, upperInclusive);
            }
        }
    }

    /**
     * @see #condChain(Operator, TableRecord, boolean)
     */
    @NotNull
    public static Condition condChain(TableRecord<?> record) {
        return condChain(Operator.AND, record, true);
    }

    /**
     * @see #condChain(Operator, TableRecord, boolean)
     */
    @NotNull
    public static Condition condChain(TableRecord<?> record, boolean ignoreNull) {
        return condChain(Operator.AND, record, ignoreNull);
    }

    /**
     * @see #condChain(Operator, TableRecord, boolean)
     */
    @NotNull
    public static Condition condChain(Operator andOr, TableRecord<?> record) {
        return condChain(andOr, record, true);
    }

    /**
     * 构造一个 and 级联的条件， type=1 and name='dog'
     *
     * @param record     条件
     * @param ignoreNull 是否忽略null
     * @return 条件
     */
    @NotNull
    public static Condition condChain(Operator andOr, TableRecord<?> record, boolean ignoreNull) {
        List<Condition> conds = condField(record, ignoreNull);
        return conds.isEmpty() ? noCondition() : condition(andOr, conds);
    }

    /**
     * @see #condChain(Operator, Map, boolean, TableImpl)
     */
    @NotNull
    public static Condition condChain(Map<String, Object> fieldValue) {
        return condChain(Operator.AND, fieldValue, true, null);
    }

    /**
     * @see #condChain(Operator, Map, boolean, TableImpl)
     */
    @NotNull
    public static Condition condChain(Map<String, Object> fieldValue, boolean ignoreNull) {
        return condChain(Operator.AND, fieldValue, ignoreNull, null);
    }

    /**
     * @see #condChain(Operator, Map, boolean, TableImpl)
     */
    @NotNull
    public static Condition condChain(Map<String, Object> fieldValue, boolean ignoreNull, TableImpl<?> alias) {
        return condChain(Operator.AND, fieldValue, ignoreNull, null);
    }

    /**
     * @see #condChain(Operator, Map, boolean, TableImpl)
     */
    @NotNull
    public static Condition condChain(Operator andOr, Map<String, Object> fieldValue, boolean ignoreNull) {
        return condChain(andOr, fieldValue, ignoreNull, null);
    }

    /**
     * 根据 map中的值，生成and条件，比如统一的用户数据隔离条件。
     * value是collection时翻译为f.in(v)，否则为 f.eq(v)
     *
     * @param andOr      链接操作
     * @param fieldValue 字段名和值
     * @param ignoreNull 是否忽略null
     * @param alias      表名或别名
     * @return 条件
     */
    @NotNull
    public static Condition condChain(Operator andOr, Map<String, Object> fieldValue, boolean ignoreNull, TableImpl<?> alias) {
        Map<Field<?>, Object> fvs = new LinkedHashMap<>(fieldValue.size());
        if (alias == null) {
            for (Map.Entry<String, Object> en : fieldValue.entrySet()) {
                Field<?> f = field(en.getKey());
                fvs.put(f, en.getValue());
            }
        }
        else {
            Field<?>[] fields = alias.fields();
            for (Map.Entry<String, Object> en : fieldValue.entrySet()) {
                for (Field<?> f : fields) {
                    if (en.getKey().equalsIgnoreCase(f.getName())) {
                        fvs.put(f, en.getValue());
                        break;
                    }
                }
            }
        }

        if (fvs.isEmpty()) return noCondition();

        List<Condition> cds = new ArrayList<>(fvs.size());
        for (Map.Entry<Field<?>, Object> en : fvs.entrySet()) {
            Object v = en.getValue();
            Field<?> f = en.getKey();
            Condition c = condField(f, ignoreNull, v);
            if (c != null) {
                cds.add(c);
            }
        }

        return condition(andOr, cds);
    }

    /**
     * <pre>
     * filed is null -> throw
     * value is Array | Coolection -> f.in(v)
     * null && !ignore -> f.isNull()
     * _ -> f.eq(v)
     * </pre>
     *
     * @param field      filed
     * @param ignoreNull filed
     * @param value      值
     * @return 条件
     */
    @Nullable
    public static Condition condField(Field<?> field, boolean ignoreNull, Object value) {
        List<?> vs;
        if (value == null) {
            return ignoreNull ? null : field.isNull();
        }
        else if (value instanceof Collection) {
            vs = new ArrayList<>(((Collection<?>) value));
        }
        else if (value.getClass().isArray()) {
            if (value instanceof boolean[]) {
                vs = BoxedCastUtil.list((boolean[]) value);
            }
            else if (value instanceof byte[]) {
                vs = BoxedCastUtil.list((byte[]) value);
            }
            else if (value instanceof char[]) {
                vs = BoxedCastUtil.list((char[]) value);
            }
            else if (value instanceof int[]) {
                vs = BoxedCastUtil.list((int[]) value);
            }
            else if (value instanceof long[]) {
                vs = BoxedCastUtil.list((long[]) value);
            }
            else if (value instanceof float[]) {
                vs = BoxedCastUtil.list((float[]) value);
            }
            else if (value instanceof double[]) {
                vs = BoxedCastUtil.list((double[]) value);
            }
            else {
                vs = Arrays.asList((Object[]) value);
            }
        }
        else {
            @SuppressWarnings("unchecked")
            Field<Object> f = (Field<Object>) field;
            return f.eq(f.getDataType().convert(value));
        }

        if (vs.isEmpty()) {
            return null;
        }
        else {
            vs.removeIf(Objects::isNull);
            return field.in(field.getDataType().convert(vs));
        }
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
            Condition cond = condField(field, ignoreNull, record.getValue(i));
            if (cond != null) {
                conds.add(cond);
            }
        }

        return conds;
    }

    /**
     * 判断友好的链式条件builder
     *
     * @return builder
     */
    public static CondBuilder condBuilder() {
        return new CondBuilder();
    }

    /**
     * 判断友好的链式条件builder
     *
     * @return builder
     */
    public static CondBuilder condBuilder(Condition cond) {
        return new CondBuilder().and(cond);
    }

    /**
     * <pre>
     * (1=1) and ((2=2 or 3=3) or (4=4 and 5=5))
     * 可以通过以下 grp-end，构造括号条件
     * (1=1).and()
     * .grp()
     *    .grp(2=2).or(3=3).end()
     *        .and()
     *    .grp(4=4).or(5=5).end()
     * .end()
     * </pre>
     * 判断友好的链式条件builder
     */
    public static class CondBuilder {

        private static final String BGN = "(";
        private final ArrayList<Object> calcStack = new ArrayList<>(16);

        /**
         * @see #and(Condition, boolean)
         */
        @NotNull
        public CondBuilder and() {
            return cond(Operator.AND, null, true);
        }

        /**
         * @see #and(Condition, boolean)
         */
        @NotNull
        public CondBuilder and(Condition cond) {
            return cond(Operator.AND, cond, cond != null);
        }

        /**
         * @see #and(Condition, boolean)
         */
        @NotNull
        public CondBuilder andNotNull(Condition cond, Object... value) {
            final boolean vd = cond != null && pro.fessional.mirana.data.Z.notNull(value) != null;
            return cond(Operator.AND, cond, vd);
        }

        /**
         * @see #and(Condition, boolean)
         */
        @NotNull
        public CondBuilder andNotEmpty(Condition cond, Collection<?> value) {
            final boolean vd = cond != null && value != null && !value.isEmpty();
            return cond(Operator.AND, cond, vd);
        }

        /**
         * 当 valid且cond != null时，and cond
         *
         * @param cond  目标
         * @param valid 判定
         * @return builder
         */
        @NotNull
        public CondBuilder and(Condition cond, boolean valid) {
            return cond(Operator.AND, cond, valid);
        }

        /**
         * @see #or(Condition, boolean)
         */
        @NotNull
        public CondBuilder or() {
            return cond(Operator.OR, null, true);
        }

        /**
         * @see #or(Condition, boolean)
         */
        @NotNull
        public CondBuilder or(Condition cond) {
            return cond(Operator.OR, cond, cond != null);
        }

        /**
         * @see #and(Condition, boolean)
         */
        @NotNull
        public CondBuilder orNotNull(Condition cond, Object... value) {
            final boolean vd = cond != null && pro.fessional.mirana.data.Z.notNull(value) != null;
            return cond(Operator.OR, cond, vd);
        }

        /**
         * @see #and(Condition, boolean)
         */
        @NotNull
        public CondBuilder orNotEmpty(Condition cond, Collection<?> value) {
            final boolean vd = cond != null && value != null && !value.isEmpty();
            return cond(Operator.OR, cond, vd);
        }

        /**
         * 当 valid且cond != null时，or cond
         *
         * @param cond  目标
         * @param valid 判定
         * @return builder
         */
        @NotNull
        public CondBuilder or(Condition cond, boolean valid) {
            return cond(Operator.OR, cond, valid);
        }

        /**
         * @see #grp(Condition, boolean)
         */
        @NotNull
        public CondBuilder grp() {
            return grp(null, true);
        }

        /**
         * @see #grp(Condition, boolean)
         */
        @NotNull
        public CondBuilder grp(Condition cond) {
            return grp(cond, true);
        }

        /**
         * 开启一个括号条件组 (....)
         *
         * @return builder
         */
        @NotNull
        public CondBuilder grp(Condition cond, boolean valid) {
            calcStack.add(BGN);
            if (valid && cond != null) calcStack.add(cond);
            return this;
        }

        /**
         * 当 valid且cond != null时，and/or cond
         *
         * @param opr   操作
         * @param cond  目标
         * @param valid 判定
         * @return builder
         */
        @NotNull
        public CondBuilder cond(Operator opr, Condition cond, boolean valid) {
            if (!valid || opr == null) return this;

            if (calcStack.isEmpty()) {
                if (cond != null) calcStack.add(cond);
            }
            else {
                for (int i = calcStack.size() - 1; i >= 0; i--) {
                    Object obj = calcStack.get(i);
                    if (obj instanceof Condition) { // Condition -> 计算
                        if (cond == null) { // only opr (group)
                            calcStack.add(opr);
                        }
                        else { // 如果错误，抛出异常
                            calcStack.set(i, eval((Condition) obj, opr, cond));
                        }
                        break;
                    }
                    else if (obj instanceof Operator) {
                        if (cond == null) {
                            break; // 忽略当前操作符
                        }
                        else {
                            // Operator -> 移除，找上一个
                            calcStack.remove(i);
                        }
                    }
                    else { // "(" -> append Condition
                        if (cond != null) calcStack.add(cond);
                        break;
                    }
                }
            }

            return this;
        }

        /**
         * 结束上一个括号条件组，并求值。
         *
         * @return 条件
         */
        @NotNull
        public CondBuilder end() {
            final int size = calcStack.size();
            if (size <= 1) return this;

            Condition rt = null;
            Operator op = null;
            int grp = -1;
            int cur = size - 1;
            for (; cur >= 0; cur--) {
                Object obj = calcStack.get(cur);
                if (obj instanceof Condition) {
                    if (rt == null) {
                        rt = (Condition) obj;
                    }
                    else {
                        rt = eval((Condition) obj, op, rt);
                    }
                }
                else if (obj instanceof Operator) {
                    op = (Operator) obj;
                }
                else { // 括号
                    if (grp < 0) { // 结束当前，继续求值
                        grp = cur;
                    }
                    else {
                        break;
                    }
                }
            }

            final int idx = cur + 1;
            if (idx < size - 1) {
                calcStack.set(idx, rt);
                calcStack.subList(idx + 1, size).clear();
            }
            return this;
        }

        /**
         * null友好的条件求值
         *
         * @param h1 条件1
         * @param op 操作
         * @param h2 条件2
         * @return 条件
         */
        @NotNull
        public Condition eval(Condition h1, Operator op, Condition h2) {
            if (h1 == null) throw new IllegalStateException("bad expression: no left-hand Condition");
            if (op == null && h2 == null) return h1;
            if (op == null || h2 == null) throw new IllegalStateException("bad expression: no Condition or Operator");
            return condition(op, h1, h2);
        }

        public Condition build() {
            for (int i = calcStack.size(); i > 1 && calcStack.size() > 1; i--) {
                end();
            }

            return (Condition) calcStack.get(0);
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            for (Object o : calcStack) {
                sb.append(o.toString());
            }
            return sb.toString();
        }
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
            result.add(val(value));
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
            result.add(param(field.getName(), value));
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
            result.add(param(entry.getKey(), value));
        }

        return result.isEmpty() ? emptyParams : result.toArray(emptyParams);
    }

    //
    private static int buildHolder(StringBuilder sql, int pos, int len) {
        if (len == 0) return pos;

        for (int i = 0; i < len; i++) {
            sql.append('{');
            sql.append(++pos);
            sql.append("},");
        }

        sql.deleteCharAt(sql.length() - 1);
        return pos;
    }
}
