package pro.fessional.wings.faceless.database.manual.single.modify.lightsequence;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pro.fessional.wings.faceless.database.manual.single.select.lightsequence.LightSequenceSelect;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;


/**
 * @author trydofor
 * @since 2019-06-04
 */
public interface LightSequenceModify {

    /////////// insert ///////////
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    class SysLightSequence implements Serializable {
        @Serial private static final long serialVersionUID = 299_792_458L; // m/s
        private String seqName;
        private Integer blockId;
        private Long nextVal;
        private Integer stepVal;
        private String comments;
    }

    int insert(SysLightSequence po);

    /////////// update ///////////

    int updateNextVal(long newVal, int block, String name, long oldVal);

    int[] updateNextPlusStep(List<LightSequenceSelect.NameNextStep> all, final int block);

    /////////// delete ///////////

}
