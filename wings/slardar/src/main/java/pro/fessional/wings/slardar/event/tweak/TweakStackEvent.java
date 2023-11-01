package pro.fessional.wings.slardar.event.tweak;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author trydofor
 * @since 2022-10-31
 */
@Data
public class TweakStackEvent implements Serializable {

    @Serial
    private static final long serialVersionUID = -3524639212951770945L;
    /**
     * Long.MAX_VALUE means all user
     */
    private long userId;
    /**
     * `null` means reset setting, restores the original system settings.
     */
    private Boolean stack;
}
