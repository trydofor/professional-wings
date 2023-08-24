package pro.fessional.wings.slardar.event.tweak;

import lombok.Data;

/**
 * @author trydofor
 * @since 2022-10-31
 */
@Data
public class TweakStackEvent {
    /**
     * Long.MAX_VALUE means all user
     */
    private long userId;
    /**
     * `null` means reset setting, restores the original system settings.
     */
    private Boolean stack;
}
