package pro.fessional.wings.faceless.database.jooq;

import org.jetbrains.annotations.NotNull;
import org.jooq.Condition;
import org.jooq.impl.DSL;

/**
 * @author trydofor
 * @since 2020-08-12
 */
public interface WingsAliasTable<T> {

    T getAliasTable();

    default Condition getOnlyDied() {
        return DSL.falseCondition();
    }

    default Condition getOnlyLive() {
        return DSL.trueCondition();
    }

    /**
     * 组合其他条件 and onlyDiedData
     *
     * @param cond 其他条件
     * @return Condition
     */
    @NotNull
    default Condition onlyDied(@NotNull Condition cond) {
        Condition died = getOnlyDied();
        return died == null ? cond : died.and(cond);
    }

    /**
     * 组合其他条件 and onlyLiveData
     *
     * @param cond 其他条件
     * @return Condition
     */
    @NotNull
    default Condition onlyLive(@NotNull Condition cond) {
        Condition live = getOnlyLive();
        return live == null ? cond : live.and(cond);
    }
}
