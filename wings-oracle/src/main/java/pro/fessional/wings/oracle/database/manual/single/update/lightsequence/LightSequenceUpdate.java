package pro.fessional.wings.oracle.database.manual.single.update.lightsequence;

import lombok.RequiredArgsConstructor;
import org.jooq.BatchBindStep;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;
import pro.fessional.wings.oracle.database.autogen.tables.SysLightSequence;
import pro.fessional.wings.oracle.database.manual.single.select.lightsequence.LightSequenceSelect;

import java.util.List;

import static pro.fessional.wings.oracle.database.autogen.tables.SysLightSequence.SYS_LIGHT_SEQUENCE;

/**
 * @author trydofor
 * @since 2019-06-03
 */
@Repository
@RequiredArgsConstructor
public class LightSequenceUpdate {

    private final DSLContext dslContext;

    public int updateNextVal(long newVal, int block, String name, long oldVal) {
        SysLightSequence t = SYS_LIGHT_SEQUENCE;
        return dslContext
                .update(t)
                .set(t.NEXT_VAL, newVal)
                .where(t.BLOCK_ID.eq(block))
                .and(t.SEQ_NAME.eq(name))
                .and(t.NEXT_VAL.eq(oldVal))
                .execute();
    }

    public int[] updateNextPlusStep(List<LightSequenceSelect.SelectAll> all, final int block) {
        SysLightSequence t = SYS_LIGHT_SEQUENCE;
        BatchBindStep update = dslContext.batch(
                dslContext.update(t)
                          .set(t.NEXT_VAL, (Long) null)
                          .where(t.BLOCK_ID.eq((Integer) null))
                          .and(t.SEQ_NAME.eq((String) null))
                          .and(t.NEXT_VAL.eq((Long) null))
        );

        for (LightSequenceSelect.SelectAll item : all) {
            update.bind(item.getNextVal() + item.getStepVal(), block, item.getName(), item.getNextVal());
        }

        return update.execute();
    }
}
