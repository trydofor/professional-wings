package pro.fessional.wings.faceless.database.manual.single.update.lightsequence;

import pro.fessional.wings.faceless.database.manual.single.select.lightsequence.LightSequenceSelect;

import java.util.List;

/**
 * @author trydofor
 * @since 2019-06-03
 */
public interface LightSequenceUpdate {

    int updateNextVal(long newVal, int block, String name, long oldVal);

    int[] updateNextPlusStep(List<LightSequenceSelect.NameNextStep> all, final int block);
}
