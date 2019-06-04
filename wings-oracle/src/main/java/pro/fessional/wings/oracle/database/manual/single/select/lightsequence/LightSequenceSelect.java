package pro.fessional.wings.oracle.database.manual.single.select.lightsequence;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;
import pro.fessional.wings.oracle.database.autogen.tables.SysLightSequence;

import java.util.List;
import java.util.Optional;

import static pro.fessional.wings.oracle.database.autogen.tables.SysLightSequence.SYS_LIGHT_SEQUENCE;


/**
 * @author trydofor
 * @since 2019-06-03
 */
@Repository
@RequiredArgsConstructor
public class LightSequenceSelect {

    private final DSLContext dslContext;

    @Value
    public static class SelectOne {
        private long nextVal;
        private int stepVal;
    }

    public Optional<SelectOne> selectOneLock(int block, String name) {
        SysLightSequence t = SYS_LIGHT_SEQUENCE;
        return dslContext
                .select(t.NEXT_VAL, t.STEP_VAL)
                .from(t)
                .where(t.BLOCK_ID.eq(block).and(t.SEQ_NAME.eq(name)))
                .forUpdate()
                .fetchOptionalInto(SelectOne.class);
    }

    @Value
    public static class SelectAll {
        private String name;
        private long nextVal;
        private int stepVal;
    }

    public List<SelectAll> selectAllLock(int block) {
        SysLightSequence t = SYS_LIGHT_SEQUENCE;
        return dslContext
                .select(t.SEQ_NAME, t.NEXT_VAL, t.STEP_VAL)
                .from(t)
                .where(t.BLOCK_ID.eq(block))
                .forUpdate()
                .fetchInto(SelectAll.class);
    }

}
