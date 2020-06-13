package pro.fessional.wings.faceless.database.manual.single.modify.lightsequence.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import pro.fessional.wings.faceless.database.manual.single.modify.lightsequence.LightSequenceModify;
import pro.fessional.wings.faceless.database.manual.single.select.lightsequence.LightSequenceSelect;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


/**
 * @author trydofor
 * @since 2019-06-04
 */
@RequiredArgsConstructor
public class LightSequenceModifyJdbc implements LightSequenceModify {

    private final JdbcTemplate jdbcTemplate;

    /////////// insert ///////////
    private static final String INS_SQL = "INSERT INTO sys_light_sequence (seq_name, block_id, next_val, step_val, comments) VALUES (?,?,?,?,?)";

    @Override
    public int insert(SysLightSequence po) {
        return jdbcTemplate.update(INS_SQL, po.getSeqName(), po.getBlockId(), po.getNextVal(), po.getStepVal(), po.getComments());
    }

    /////////// update ///////////

    private static final String UDP_SQL = "UPDATE sys_light_sequence SET next_val=? WHERE block_id=? AND seq_name=? AND next_val=?";

    @Override
    public int updateNextVal(long newVal, int block, String name, long oldVal) {
        return jdbcTemplate.update(UDP_SQL, newVal, block, name, oldVal);
    }

    @Override
    public int[] updateNextPlusStep(List<LightSequenceSelect.NameNextStep> all, final int block) {
        return jdbcTemplate.batchUpdate(UDP_SQL, new BatchPreparedStatementSetter() {
            private final ArrayList<LightSequenceSelect.NameNextStep> objs = new ArrayList<>(all);

            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                LightSequenceSelect.NameNextStep obj = objs.get(i);
                ps.setLong(1, obj.getNextVal() + obj.getStepVal());
                ps.setInt(2, block);
                ps.setString(3, obj.getSeqName());
                ps.setLong(4, obj.getNextVal());
            }

            @Override
            public int getBatchSize() {
                return objs.size();
            }
        });
    }

    /////////// delete ///////////

}
