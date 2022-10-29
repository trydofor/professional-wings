package pro.fessional.wings.silencer.spring.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author trydofor
 * @see #Key
 * @since 2021-02-14
 */
@Data
@ConfigurationProperties(SilencerDebugProp.Key)
public class SilencerDebugProp {

    public static final String Key = "wings.silencer.debug";

    /**
     * 初始CodeException的Global有栈或无栈
     *
     * @see #Key$codeStack
     */
    private boolean codeStack = false;
    public static final String Key$codeStack = Key + ".code-stack";

    /**
     * 初始系统时钟 offset ms
     *
     * @see #Key$clockOffset
     */
    private long clockOffset = 0;
    public static final String Key$clockOffset = Key + ".clock-offset";


    /**
     * 是否配置WingsMdcThresholdFilter
     *
     * @see #Key$mdcThreshold
     */
    private boolean mdcThreshold = true;
    public static final String Key$mdcThreshold = Key + ".mdc-threshold";
}
