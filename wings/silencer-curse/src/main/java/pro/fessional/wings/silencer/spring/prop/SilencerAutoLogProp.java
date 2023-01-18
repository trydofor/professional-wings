package pro.fessional.wings.silencer.spring.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Collections;
import java.util.Set;

/**
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
     * 自动设置日志的级别
     *
     * @see #Key$level
     */
    private String level = "WARN";
    public static final String Key$level = Key + ".level";

    /**
     * 被调整的appender名字，逗号分隔
     *
     * @see #Key$target
     */
    private Set<String> target = Collections.emptySet();
    public static final String Key$target = Key + ".target";

    /**
     * 当以下appender出现的时候
     *
     * @see #Key$exists
     */
    private Set<String> exists = Collections.emptySet();
    public static final String Key$exists = Key + ".exists";
}
