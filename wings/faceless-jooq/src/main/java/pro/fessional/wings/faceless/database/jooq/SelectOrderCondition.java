package pro.fessional.wings.faceless.database.jooq;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.QueryPart;
import org.jooq.SQL;
import org.jooq.Select;
import org.jooq.impl.DSL;

import static org.jooq.impl.DSL.condition;
import static org.jooq.impl.DSL.exists;
import static org.jooq.impl.DSL.notExists;

/**
 * 仅用在 lambda(table):Condition场景，以保证
 * Condition和QueryPart的table无语法歧义。
 *
 * @author trydofor
 * @since 2022-06-14
 */

public class SelectOrderCondition implements Condition {

    public static final QueryPart[] EmptyQueryPart = new QueryPart[0];
    @NotNull
    @Getter
    public final Condition condition;

    @NotNull
    @Getter
    public final QueryPart[] selectsOrders;

    public SelectOrderCondition(Condition condition, QueryPart[] selectsOrders) {
        this.condition = condition == null ? DSL.noCondition() : condition;
        this.selectsOrders = selectsOrders == null ? EmptyQueryPart : selectsOrders;
    }

    public static SelectOrderCondition of(QueryPart... selectsOrders) {
        return new SelectOrderCondition(null, selectsOrders);
    }

    public static SelectOrderCondition of(Condition cond, QueryPart... selectsOrders) {
        return new SelectOrderCondition(cond, selectsOrders);
    }

    @NotNull
    public static QueryPart[] getSelectsOrders(Condition condition) {
        return condition instanceof SelectOrderCondition ? ((SelectOrderCondition) condition).getSelectsOrders() : EmptyQueryPart;
    }

    @NotNull
    public static Condition getCondition(Condition condition) {
        return condition instanceof SelectOrderCondition ? ((SelectOrderCondition) condition).getCondition() : condition;
    }

    @Override
    @NotNull
    public Condition not() {
        return DSL.not(condition);
    }

    @Override
    @NotNull
    public final Condition and(Condition other) {
        return DSL.and(condition, other);
    }

    @Override
    @NotNull
    public final Condition or(Condition other) {
        return DSL.or(condition, other);
    }

    @Override
    @NotNull
    public Condition and(Field<Boolean> other) {
        return and(condition(other));
    }

    @Override
    @NotNull
    public final Condition or(Field<Boolean> other) {
        return or(condition(other));
    }

    @Override
    @NotNull
    public final Condition and(SQL sql) {
        return and(condition(sql));
    }

    @Override
    @NotNull
    public final Condition and(String sql) {
        return and(condition(sql));
    }

    @Override
    @NotNull
    public final Condition and(String sql, Object... bindings) {
        return and(condition(sql, bindings));
    }

    @Override
    @NotNull
    public final Condition and(String sql, QueryPart... parts) {
        return and(condition(sql, parts));
    }

    @Override
    @NotNull
    public final Condition or(SQL sql) {
        return or(condition(sql));
    }

    @Override
    @NotNull
    public final Condition or(String sql) {
        return or(condition(sql));
    }

    @Override
    @NotNull
    public final Condition or(String sql, Object... bindings) {
        return or(condition(sql, bindings));
    }

    @Override
    @NotNull
    public final Condition or(String sql, QueryPart... parts) {
        return or(condition(sql, parts));
    }

    @Override
    @NotNull
    public final Condition andNot(Condition other) {
        return and(other.not());
    }

    @Override
    @NotNull
    public final Condition andNot(Field<Boolean> other) {
        return andNot(condition(other));
    }

    @Override
    @NotNull
    public final Condition orNot(Condition other) {
        return or(other.not());
    }

    @Override
    @NotNull
    public final Condition orNot(Field<Boolean> other) {
        return orNot(condition(other));
    }

    @Override
    @NotNull
    public final Condition andExists(Select<?> select) {
        return and(exists(select));
    }

    @Override
    @NotNull
    public final Condition andNotExists(Select<?> select) {
        return and(notExists(select));
    }

    @Override
    @NotNull
    public final Condition orExists(Select<?> select) {
        return or(exists(select));
    }

    @Override
    @NotNull
    public final Condition orNotExists(Select<?> select) {
        return or(notExists(select));
    }

    @Override
    public @NotNull Condition and(Boolean other) {
        throw new IllegalStateException("removed in 3.16");
    }

    @Override
    public @NotNull Condition andNot(Boolean other) {
        throw new IllegalStateException("removed in 3.16");
    }

    @Override
    public @NotNull Condition or(Boolean other) {
        throw new IllegalStateException("removed in 3.16");
    }

    @Override
    public @NotNull Condition orNot(Boolean other) {
        throw new IllegalStateException("removed in 3.16");
    }
}
