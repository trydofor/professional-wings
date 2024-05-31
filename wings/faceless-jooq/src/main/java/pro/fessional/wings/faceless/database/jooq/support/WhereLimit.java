package pro.fessional.wings.faceless.database.jooq.support;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jooq.Condition;
import org.jooq.impl.DSL;

/**
 * @author trydofor
 * @since 2024-05-31
 */
public class WhereLimit {

    protected Condition where = null;
    protected int offset = -1;
    protected int limit = -1;

    /**
     * t.Id.gt(1L).and(t.CommitId.lt(200L))
     *
     * @param cond condition
     * @return this
     */
    @Contract("_->this")
    public WhereLimit where(Condition cond) {
        where = cond;
        return this;
    }

    @NotNull
    public Condition where() {
        return where == null ? DSL.noCondition() : where;
    }

    @Contract("_,_->this")
    public WhereLimit limit(int offset, int limit) {
        this.offset = offset;
        this.limit = limit;
        return this;
    }

    @Contract("_->this")
    public WhereLimit limit(int limit) {
        if (this.offset < 0) this.offset = 0;
        this.limit = limit;
        return this;
    }

    public int offset() {
        return offset;
    }

    public int limit() {
        return limit;
    }
}
