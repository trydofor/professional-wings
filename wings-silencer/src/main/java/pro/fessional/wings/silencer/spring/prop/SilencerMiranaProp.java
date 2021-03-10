package pro.fessional.wings.silencer.spring.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * wings-mirana-79.properties
 *
 * @author trydofor
 * @see #Key
 * @since 2021-02-13
 */
@Data
@ConfigurationProperties(SilencerMiranaProp.Key)
public class SilencerMiranaProp {
    public static final String Key = "wings.silencer.mirana.code";

    /**
     * LeapCode seed
     *
     * @see #Key$leapCode
     */
    private String leapCode = "安全有关，需要修改";
    public static final String Key$leapCode = Key + ".leap-code";

    /**
     * Crc8Long seed
     *
     * @see #Key$crc8Long
     */
    private int[] crc8Long;
    public static final String Key$crc8Long = Key + ".crc8-long";
}
