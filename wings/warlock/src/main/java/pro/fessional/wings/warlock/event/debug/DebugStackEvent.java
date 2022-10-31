package pro.fessional.wings.warlock.event.debug;

import lombok.Data;
import pro.fessional.wings.warlock.event.WarlockMetadataEvent;

/**
 * @author trydofor
 * @since 2022-10-31
 */
@Data
public class DebugStackEvent implements WarlockMetadataEvent {
    /**
     * userId为Long.MAX_VALUE时，为全部用户
     */
    private long userId;
    /**
     * null表示reset
     */
    private Boolean stack;
}
