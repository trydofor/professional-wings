package pro.fessional.wings.faceless.database.manual.single.select.lightsequence;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Optional;


/**
 * @author trydofor
 * @since 2019-06-03
 */
public interface LightSequenceSelect {

    @Data
    class NextStep {
        private long nextVal;
        private int stepVal;
        private long lastVal;

        public NextStep() {
        }

        public NextStep(long nextVal, int stepVal) {
            this(nextVal, stepVal, nextVal);
        }

        public NextStep(long nextVal, int stepVal, long lastVal) {
            this.nextVal = nextVal;
            this.stepVal = stepVal;
            this.lastVal = lastVal;
        }
    }

    Optional<NextStep> selectOneLock(int block, String name);

    @Data
    @EqualsAndHashCode(callSuper = true)
    @AllArgsConstructor
    @NoArgsConstructor
    class NameNextStep extends NextStep {
        private String seqName;
    }

    List<NameNextStep> selectAllLock(int block);
}
