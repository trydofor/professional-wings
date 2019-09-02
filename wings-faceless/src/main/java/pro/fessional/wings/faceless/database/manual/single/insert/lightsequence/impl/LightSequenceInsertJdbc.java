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
        String sql = "INSERT INTO sys_light_sequence (seq_name, block_id, next_val, step_val, comments) VALUES (?,?,?,?,?)";
        return jdbcTemplate.update(sql, po.getSeqName(), po.getBlockId(), po.getNextVal(), po.getStepVal(), po.getComments());
    }
}
