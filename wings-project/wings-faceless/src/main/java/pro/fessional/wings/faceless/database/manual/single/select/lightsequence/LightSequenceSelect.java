package pro.fessional.wings.faceless.database.manual.single.select.lightsequence;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Optional;


/**
 * @author trydofor
 * @since 2019-06-03
 */
public interface LightSequenceSelect {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    class NextStep {
        private long nextVal;
        private int stepVal;
    }

    Optional<NextStep> selectOneLock(int block, String name);

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    class NameNextStep {
        private String seqName;
        private long nextVal;
        private int stepVal;
    }


    List<NameNextStep> selectAllLock(int block);
}
