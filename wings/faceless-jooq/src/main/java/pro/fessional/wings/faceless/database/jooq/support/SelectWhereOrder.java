package pro.fessional.wings.faceless.database.jooq.support;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jooq.Condition;
import org.jooq.OrderField;
import org.jooq.QueryPart;
import org.jooq.SelectFieldOrAsterisk;

import java.util.Collection;
import java.util.List;

/**
 * query = select and order
 *
 * @author trydofor
 * @since 2024-05-31
 */ /////
public class SelectWhereOrder extends WhereLimit {

    public final SelectOrder selectOrder = new SelectOrder();

    /**
     * t.Id, t.CommitId, t.Id.desc()
     *
     * @param part fields to select and order by
     * @return this
     */
    @Contract("_ -> this")
    public SelectWhereOrder select(SelectFieldOrAsterisk... part) {
        selectOrder.query(part);
        return this;
    }

    /**
     * t.Id, t.CommitId, t.Id.desc()
     *
     * @param part fields to select and order by
     * @return this
     */
    @Contract("_ -> this")
    public SelectWhereOrder select(Collection<? extends SelectFieldOrAsterisk> part) {
        selectOrder.query(part);
        return this;
    }

    /**
     * t.Id.desc()
     *
     * @param part fields to order by
     * @return this
     */
    @Contract("_ -> this")
    public SelectWhereOrder order(OrderField<?>... part) {
        selectOrder.query(part);
        return this;
    }

    /**
     * t.Id.desc()
     *
     * @param part fields to order by
     * @return this
     */
    @Contract("_ -> this")
    public SelectWhereOrder order(Collection<? extends OrderField<?>> part) {
        selectOrder.query(part);
        return this;
    }

    @Override
    @Contract("_->this")
    public SelectWhereOrder where(Condition cond) {
        super.where(cond);
        return this;
    }

    @Override
    @Contract("_,_->this")
    public SelectWhereOrder limit(int offset, int limit) {
        super.limit(offset, limit);
        return this;
    }

    @Override
    @Contract("_->this")
    public SelectWhereOrder limit(int limit) {
        super.limit(limit);
        return this;
    }

    @NotNull
    public List<QueryPart> queries() {
        return selectOrder.queries();
    }
}
