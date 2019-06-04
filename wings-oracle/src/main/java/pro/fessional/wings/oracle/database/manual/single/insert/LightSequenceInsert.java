package pro.fessional.wings.oracle.database.manual.single.insert;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;
import pro.fessional.wings.oracle.database.autogen.tables.pojos.SysLightSequence;
import pro.fessional.wings.oracle.database.autogen.tables.records.SysLightSequenceRecord;

import static pro.fessional.wings.oracle.database.autogen.tables.SysLightSequence.SYS_LIGHT_SEQUENCE;

/**
 * @author trydofor
 * @since 2019-06-04
 */
@Repository
@RequiredArgsConstructor
public class LightSequenceInsert {

    private final DSLContext dslContext;

    public int insert(SysLightSequence po) {
        SysLightSequenceRecord record = dslContext.newRecord(SYS_LIGHT_SEQUENCE);
        record.from(po);
        return record.insert();
    }
}
