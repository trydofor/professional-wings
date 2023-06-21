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
     * the number of block bytes, in the range [3,23], empty by default. LightId is 9 by default, so 2^9=512 zones.
     *
     * @see #Key$blockBits
     */
    private Integer blockBits = null;
    public static final String Key$blockBits = Key + ".block-bits";

    /**
     * sequence layout, whether Block precedes Sequence, empty by default. LightId is true by default
     *
     * @see #Key$blockFirst
     */
    private Boolean blockFirst = null;
    public static final String Key$blockFirst = Key + ".block-first";
}
