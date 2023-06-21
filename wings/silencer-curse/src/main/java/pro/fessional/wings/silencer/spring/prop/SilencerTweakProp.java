package pro.fessional.wings.silencer.spring.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Tweaking of the Application.
 *
 * @author trydofor
 * @see #Key
 * @since 2021-02-14
 */
@Data
@ConfigurationProperties(SilencerTweakProp.Key)
public class SilencerTweakProp {

    public static final String Key = "wings.silencer.tweak";

    /**
     * Whether the Global of CodeException has a stack. default false
     *
     * @see #Key$codeStack
     */
    private boolean codeStack = false;
    public static final String Key$codeStack = Key + ".code-stack";

    /**
     * Initial system clock with offset ms, default 0, means ignore
     *
     * @see #Key$clockOffset
     */
    private long clockOffset = 0;
    public static final String Key$clockOffset = Key + ".clock-offset";


    /**
     * Whether to configure WingsMdcThresholdFilter, default true
     *
     * @see #Key$mdcThreshold
     */
    private boolean mdcThreshold = true;
    public static final String Key$mdcThreshold = Key + ".mdc-threshold";
}
