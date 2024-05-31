package pro.fessional.wings.faceless.database.jooq.support;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jooq.OrderField;
import org.jooq.QueryPart;
import org.jooq.SelectFieldOrAsterisk;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author trydofor
 * @since 2024-05-31
 */
public class SelectOrder {

    protected final List<QueryPart> queries = new ArrayList<>(16);

    /**
     * t.Id, t.CommitId, t.Id.desc()
     *
     * @param part fields to select and order by
     * @return this
     */
    @Contract("_ -> this")
    public SelectOrder query(QueryPart... part) {
        Collections.addAll(queries, part);
        return this;
    }

    /**
     * t.Id, t.CommitId, t.Id.desc()
     *
     * @param part fields to select and order by
     * @return this
     */
    @Contract("_ -> this")
    public SelectOrder query(Collection<? extends QueryPart> part) {
        queries.addAll(part);
        return this;
    }

    @NotNull
    public List<QueryPart> queries() {
        return queries;
    }

    @NotNull
    public Parts parts() {
        return new Parts(queries);
    }

    public static class Parts {
        public final Collection<SelectFieldOrAsterisk> selects;
        public final Collection<OrderField<?>> orders;

        public Parts(QueryPart... selectsOrders) {
            this(List.of(selectsOrders));
        }

        public Parts(Collection<? extends QueryPart> selectsOrders) {
            if (selectsOrders == null || selectsOrders.isEmpty()) {
                this.selects = Collections.emptyList();
                this.orders = Collections.emptyList();
            }
            else {
                int size = selectsOrders.size();
                final ArrayList<SelectFieldOrAsterisk> fields = new ArrayList<>(size);
                final ArrayList<OrderField<?>> orders = new ArrayList<>(size);

                for (QueryPart qp : selectsOrders) {
                    if (qp instanceof SelectFieldOrAsterisk) {
                        fields.add((SelectFieldOrAsterisk) qp);
                    }
                    else if (qp instanceof OrderField) {
                        orders.add((OrderField<?>) qp);
                    }
                    else {
                        throw new IllegalArgumentException("unsupported querypart=" + qp);
                    }
                }

                this.selects = fields;
                this.orders = orders;
            }
        }
    }
}
