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
        one.setNextVal(rs.getLong("NEXT_VAL"));
        one.setStepVal(rs.getInt("STEP_VAL"));
        return one;
    };

    @Override
    public Optional<NextStep> selectOneLock(int block, String name) {
        String sql = "SELECT NEXT_VAL, STEP_VAL FROM SYS_LIGHT_SEQUENCE WHERE BLOCK_ID=? AND SEQ_NAME=? FOR UPDATE";
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
        one.setSeqName(rs.getString("SEQ_NAME"));
        one.setStepVal(rs.getInt("STEP_VAL"));
        one.setStepVal(rs.getInt("STEP_VAL"));
        return one;
    };

    @Override
    public List<NameNextStep> selectAllLock(int block) {
        String sql = "SELECT SEQ_NAME, NEXT_VAL, STEP_VAL FROM SYS_LIGHT_SEQUENCE WHERE BLOCK_ID=? FOR UPDATE";
        return jdbcTemplate.query(sql, mapperNameNextStep, block);
    }
}
