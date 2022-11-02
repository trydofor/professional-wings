package pro.fessional.wings.slardar.event.tweak;

import lombok.Data;
import org.springframework.boot.logging.LogLevel;

/**
 * @author trydofor
 * @since 2022-10-31
 */
@Data
public class TweakLoggerEvent {
    /**
     * userId为Long.MAX_VALUE时，为全部用户
     */
    private long userId;
    /**
     * OFF为reset设定，恢复系统原设定
     * FATAL等同于ERROR（slf4j无fatal级别）
     */
    private LogLevel level;
}
