package pro.fessional.wings.faceless.database.manual.single.insert.lightsequence;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;


/**
 * @author trydofor
 * @since 2019-06-04
 */
@Repository
@RequiredArgsConstructor
public class LightSequenceInsert {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SysLightSequence {
        private static final long serialVersionUID = 299_792_458L; //m/s
        private String seqName;
        private Integer blockId;
        private Long nextVal;
        private Integer stepVal;
        private String comments;
    }

    private final JdbcTemplate jdbcTemplate;

    public int insert(SysLightSequence po) {
        String sql = "INSERT INTO SYS_LIGHT_SEQUENCE (SEQ_NAME, BLOCK_ID, NEXT_VAL, STEP_VAL, COMMENTS) VALUES (?,?,?,?,?)";
        return jdbcTemplate.update(sql, po.getSeqName(), po.getBlockId(), po.getNextVal(), po.getStepVal(), po.getComments());
    }
}
