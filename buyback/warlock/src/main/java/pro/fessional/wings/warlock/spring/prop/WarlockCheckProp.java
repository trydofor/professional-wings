package pro.fessional.wings.warlock.spring.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * wings-warlock-check-77.properties
 *
 * @author trydofor
 * @see #Key
 * @since 2021-02-17
 */
@Data
@ConfigurationProperties(WarlockCheckProp.Key)
public class WarlockCheckProp {

    public static final String Key = "wings.warlock.check";

    /**
     * @see #Key$tzOffset
     */
    private int tzOffset = 5;
    public static final String Key$tzOffset = Key + ".tz-offset";

    /**
     * @see #Key$tzFail
     */
    private boolean tzFail = true;
    public static final String Key$tzFail = Key + ".tz-fail";
}
