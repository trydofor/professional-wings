package pro.fessional.wings.faceless.database.jooq;

import org.jetbrains.annotations.NotNull;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.impl.DSL;
import pro.fessional.wings.faceless.service.journal.JournalService;

import java.util.Collections;
import java.util.Map;

import static org.jooq.impl.DSL.falseCondition;
import static org.jooq.impl.DSL.noCondition;

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
        return falseCondition();
    }

    @NotNull
    default Condition getOnlyLive() {
        return noCondition();
    }

    /**
     * Combine other condition with `and onlyDiedData`
     *
     * @param cond other condition
     * @return Condition
     */
    @NotNull
    default Condition onlyDied(Condition cond) {
        return DSL.and(cond, getOnlyDied());
    }

    /**
     * Combine other condition with `and onlyLiveData`
     *
     * @param cond other condition
     * @return Condition
     */
    @NotNull
    default Condition onlyLive(Condition cond) {
        return DSL.and(cond, getOnlyLive());
    }
}
