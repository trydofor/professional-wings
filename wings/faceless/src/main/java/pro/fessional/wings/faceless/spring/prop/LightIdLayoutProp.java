package pro.fessional.wings.faceless.spring.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author trydofor
 * @see #Key
 * @since 2019-05-30
 */
@Data
@ConfigurationProperties(LightIdLayoutProp.Key)
public class LightIdLayoutProp {

    public static final String Key = "wings.faceless.lightid.layout";

    /**
     * 设置block字节数，范围[3,23]，配置项默认空。LightId默认为9，2^9=512个区
     *
     * @see #Key$blockBits
     */
    private Integer blockBits = null;
    public static final String Key$blockBits = Key + ".block-bits";

    /**
     * 序列布局，是否Block先于Sequence，配置项默认空。LightId默认为true
     *
     * @see #Key$blockFirst
     */
    private Boolean blockFirst = null;
    public static final String Key$blockFirst = Key + ".block-first";
}
