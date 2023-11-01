package pro.fessional.wings.slardar.event.tweak;

import lombok.Data;
import org.springframework.boot.logging.LogLevel;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author trydofor
 * @since 2022-10-31
 */
@Data
public class TweakLoggerEvent implements Serializable {

    @Serial
    private static final long serialVersionUID = 1559944973588991049L;
    /**
     * Long.MAX_VALUE means all user
     */
    private long userId;
    /**
     * OFF means reset setting, restores the original system settings.
     * FATAL is equivalent to ERROR (slf4j has no fatal level)
     */
    private LogLevel level;
}
