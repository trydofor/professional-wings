package pro.fessional.wings.slardar.event.tweak;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author trydofor
 * @since 2022-10-31
 */
@Data
public class TweakClockEvent implements Serializable {

    @Serial
    private static final long serialVersionUID = 7710834456093042523L;
    /**
     * Long.MAX_VALUE means all user
     */
    private long userId;
    /**
     * Condition, mills in the next 3650 days (315360000000), before 1980
     * (1) milliseconds difference from the system clock
     * (2) fixed time (from 1970-01-01, after 1980)
     * (3) 0 means reset setting, restores the original system settings.
     */
    private long mills;

}
