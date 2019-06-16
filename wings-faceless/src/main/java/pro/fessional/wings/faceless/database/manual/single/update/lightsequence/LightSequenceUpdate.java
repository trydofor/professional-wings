package pro.fessional.wings.faceless.database.manual.single.update.lightsequence;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import pro.fessional.wings.faceless.database.manual.single.select.lightsequence.LightSequenceSelect;

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
public class LightSequenceUpdate {

    private final JdbcTemplate jdbcTemplate;

    private final String sqlUdp = "UPDATE SYS_LIGHT_SEQUENCE SET NEXT_VAL=? WHERE BLOCK_ID=? AND SEQ_NAME=? AND NEXT_VAL=?";

    public int updateNextVal(long newVal, int block, String name, long oldVal) {
        return jdbcTemplate.update(sqlUdp, newVal, block, name, oldVal);
    }

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
