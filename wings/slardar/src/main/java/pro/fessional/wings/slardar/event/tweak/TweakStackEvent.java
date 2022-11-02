package pro.fessional.wings.slardar.event.tweak;

import lombok.Data;

/**
 * @author trydofor
 * @since 2022-10-31
 */
@Data
public class TweakStackEvent {
    /**
     * userId为Long.MAX_VALUE时，为全部用户
     */
    private long userId;
    /**
     * null表示reset
     */
    private Boolean stack;
}
