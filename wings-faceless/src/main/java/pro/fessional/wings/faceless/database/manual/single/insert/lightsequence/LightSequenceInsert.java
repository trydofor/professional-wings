package pro.fessional.wings.faceless.database.manual.single.insert.lightsequence;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * @author trydofor
 * @since 2019-06-04
 */
public interface LightSequenceInsert {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    class SysLightSequence {
        private static final long serialVersionUID = 299_792_458L; //m/s
        private String seqName;
        private Integer blockId;
        private Long nextVal;
        private Integer stepVal;
        private String comments;
    }

    int insert(SysLightSequence po);
}
