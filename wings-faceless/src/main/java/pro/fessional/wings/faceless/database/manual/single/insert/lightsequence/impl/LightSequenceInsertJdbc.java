package pro.fessional.wings.faceless.database.manual.single.insert.lightsequence.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import pro.fessional.wings.faceless.database.manual.single.insert.lightsequence.LightSequenceInsert;


/**
 * @author trydofor
 * @since 2019-06-04
 */
@Repository
@RequiredArgsConstructor
public class LightSequenceInsertJdbc implements LightSequenceInsert {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public int insert(SysLightSequence po) {
        String sql = "INSERT INTO SYS_LIGHT_SEQUENCE (SEQ_NAME, BLOCK_ID, NEXT_VAL, STEP_VAL, COMMENTS) VALUES (?,?,?,?,?)";
        return jdbcTemplate.update(sql, po.getSeqName(), po.getBlockId(), po.getNextVal(), po.getStepVal(), po.getComments());
    }
}
