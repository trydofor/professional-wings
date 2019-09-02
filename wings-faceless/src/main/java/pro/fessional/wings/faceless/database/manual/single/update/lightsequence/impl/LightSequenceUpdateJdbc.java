package pro.fessional.wings.faceless.database.manual.single.update.lightsequence.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import pro.fessional.wings.faceless.database.manual.single.select.lightsequence.LightSequenceSelect;
import pro.fessional.wings.faceless.database.manual.single.update.lightsequence.LightSequenceUpdate;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author trydofor
 * @since 2019-06-03
 */
@Repository
@RequiredArgsConstructor
public class LightSequenceUpdateJdbc implements LightSequenceUpdate {

    private final JdbcTemplate jdbcTemplate;

    private final String sqlUdp = "UPDATE sys_light_sequence SET next_val=? WHERE block_id=? AND seq_name=? AND next_val=?";

    @Override
    public int updateNextVal(long newVal, int block, String name, long oldVal) {
        return jdbcTemplate.update(sqlUdp, newVal, block, name, oldVal);
    }

    @Override
    public int[] updateNextPlusStep(List<LightSequenceSelect.NameNextStep> all, final int block) {
        return jdbcTemplate.batchUpdate(sqlUdp, new BatchPreparedStatementSetter() {
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
}
