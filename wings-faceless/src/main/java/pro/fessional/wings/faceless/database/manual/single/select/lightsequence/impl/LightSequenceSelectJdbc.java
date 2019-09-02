package pro.fessional.wings.faceless.database.manual.single.select.lightsequence.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import pro.fessional.wings.faceless.database.manual.single.select.lightsequence.LightSequenceSelect;

import java.util.List;
import java.util.Optional;


/**
 * @author trydofor
 * @since 2019-06-03
 */
@Repository
@RequiredArgsConstructor
public class LightSequenceSelectJdbc implements LightSequenceSelect {

    private final JdbcTemplate jdbcTemplate;

    private RowMapper<NextStep> mapperNextStep = (rs, rowNum) -> {
        NextStep one = new NextStep();
        one.setNextVal(rs.getLong("next_val"));
        one.setStepVal(rs.getInt("step_val"));
        return one;
    };

    @Override
    public Optional<NextStep> selectOneLock(int block, String name) {
        String sql = "SELECT next_val, step_val FROM sys_light_sequence WHERE block_id=? AND seq_name=? FOR UPDATE";
        List<NextStep> list = jdbcTemplate.query(sql, mapperNextStep, block, name);
        int size = list.size();
        if (size == 0) {
            return Optional.empty();
        } else if (size == 1) {
            return Optional.of(list.get(0));
        } else {
            throw new IllegalStateException("find " + size + " records, block=" + block + ", name=" + name);
        }
    }

    private RowMapper<NameNextStep> mapperNameNextStep = (rs, rowNum) -> {
        NameNextStep one = new NameNextStep();
        one.setSeqName(rs.getString("seq_name"));
        one.setStepVal(rs.getInt("step_val"));
        one.setStepVal(rs.getInt("step_val"));
        return one;
    };

    @Override
    public List<NameNextStep> selectAllLock(int block) {
        String sql = "SELECT seq_name, next_val, step_val FROM sys_light_sequence WHERE block_id=? FOR UPDATE";
        return jdbcTemplate.query(sql, mapperNameNextStep, block);
    }
}
