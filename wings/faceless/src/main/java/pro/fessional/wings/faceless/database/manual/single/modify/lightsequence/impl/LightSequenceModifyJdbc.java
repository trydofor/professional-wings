package pro.fessional.wings.faceless.database.manual.single.modify.lightsequence.impl;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import pro.fessional.wings.faceless.database.manual.single.modify.lightsequence.LightSequenceModify;
import pro.fessional.wings.faceless.database.manual.single.select.lightsequence.LightSequenceSelect.NameNextStep;

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
    private final String insertSql;
    private final String updateSql;

    /////////// insert ///////////

    @Override
    public int insert(SysLightSequence po) {
        return jdbcTemplate.update(insertSql, po.getSeqName(), po.getBlockId(), po.getNextVal(), po.getStepVal(), po.getComments());
    }

    /////////// update ///////////

    @Override
    public int updateNextVal(long newVal, int block, String name, long oldVal) {
        return jdbcTemplate.update(updateSql, newVal, block, name, oldVal);
    }

    @Override
    public int[] updateNextPlusStep(List<NameNextStep> all, final int block) {
        return jdbcTemplate.batchUpdate(updateSql, new BatchPreparedStatementSetter() {
            private final ArrayList<NameNextStep> objs = new ArrayList<>(all);

            @Override
            public void setValues(@NotNull PreparedStatement ps, int i) throws SQLException {
                NameNextStep obj = objs.get(i);
                ps.setLong(1, obj.getNextVal() + obj.getStepVal());
                ps.setInt(2, block);
                ps.setString(3, obj.getSeqName());
                ps.setLong(4, obj.getOldNext());
            }

            @Override
            public int getBatchSize() {
                return objs.size();
            }
        });
    }

    /////////// delete ///////////

}
