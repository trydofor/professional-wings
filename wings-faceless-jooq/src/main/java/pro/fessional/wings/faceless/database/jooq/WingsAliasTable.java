package pro.fessional.wings.faceless.database.jooq;

import org.jooq.Condition;
import org.jooq.True;
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
    default Condition onlyDied(Condition cond) {
        Condition died = getOnlyDied();
        if (died == null || died instanceof True) {
            return cond;
        } else {
            if (cond == null) {
                return died;
            } else {
                return cond.and(died);
            }
        }
    }

    /**
     * 组合其他条件 and onlyLiveData
     *
     * @param cond 其他条件
     * @return Condition
     */
    default Condition onlyLive(Condition cond) {
        Condition live = getOnlyLive();
        if (live == null || live instanceof True) {
            return cond;
        } else {
            if (cond == null) {
                return live;
            } else {
                return cond.and(live);
            }
        }
    }
}
