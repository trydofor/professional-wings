package pro.fessional.wings.faceless.database.manual.single.select.lightsequence;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;


/**
 * @author trydofor
 * @since 2019-06-03
 */
public interface LightSequenceSelect {

    @Data
    class NextStep {
        /**
         * current next value
         */
        private long nextVal;
        /**
         * suggest step
         */
        private int stepVal;
        /**
         * next value for update
         */
        private long oldNext;

        public NextStep() {
        }

        public NextStep(long nextVal, int stepVal) {
            this(nextVal, stepVal, nextVal);
        }

        public NextStep(long nextVal, int stepVal, long oldNext) {
            this.nextVal = nextVal;
            this.stepVal = stepVal;
            this.oldNext = oldNext;
        }
    }

    @Nullable
    NextStep selectOneLock(int block, @NotNull String name);

    @Data
    @EqualsAndHashCode(callSuper = true)
    @AllArgsConstructor
    @NoArgsConstructor
    class NameNextStep extends NextStep {
        private String seqName;
    }

    List<NameNextStep> selectAllLock(int block);
}
