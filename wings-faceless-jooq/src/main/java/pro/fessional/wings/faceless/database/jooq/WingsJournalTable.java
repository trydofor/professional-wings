package pro.fessional.wings.faceless.database.jooq;

import org.jetbrains.annotations.NotNull;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.True;
import org.jooq.impl.DSL;
import pro.fessional.wings.faceless.service.journal.JournalService;

import java.util.Collections;
import java.util.Map;

/**
 * @author trydofor
 * @since 2020-08-12
 */
public interface WingsJournalTable<T> extends WingsAliasTable<T> {

    @NotNull
    default Map<Field<?>, ?> markDelete(JournalService.Journal commit) {
        return Collections.emptyMap();
    }

    @NotNull
    default Condition getOnlyDied() {
        return DSL.falseCondition();
    }

    @NotNull
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
    default Condition onlyDied(Condition cond) {
        Condition died = getOnlyDied();
        if (died instanceof True) {
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
    @NotNull
    default Condition onlyLive(Condition cond) {
        Condition live = getOnlyLive();
        if (live instanceof True) {
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
