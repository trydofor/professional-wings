package pro.fessional.wings.silencer.spring.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Collections;
import java.util.Set;

/**
 * Automatically switch log levels for appender.
 * wings-autolog-79.properties
 *
 * @author trydofor
 * @see #Key
 * @since 2021-02-13
 */
@Data
@ConfigurationProperties(SilencerAutoLogProp.Key)
public class SilencerAutoLogProp {
    public static final String Key = "wings.silencer.autolog";

    /**
     * Slf4j format, default WARN.
     * Automatically set the log level, such as ALL, TRACE, DEBUG, INFO, WARN, ERROR, OFF
     *
     * @see #Key$level
     */
    private String level = "WARN";
    public static final String Key$level = Key + ".level";

    /**
     * The names of the appender to adjust, commas separated, default CONSOLE,STDOUT
     *
     * @see #Key$target
     */
    private Set<String> target = Collections.emptySet();
    public static final String Key$target = Key + ".target";

    /**
     * If the following appenders exist, the above log level is automatically adjusted
     *
     * @see #Key$exists
     */
    private Set<String> exists = Collections.emptySet();
    public static final String Key$exists = Key + ".exists";
}
