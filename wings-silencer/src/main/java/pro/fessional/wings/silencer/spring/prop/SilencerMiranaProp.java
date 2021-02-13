package pro.fessional.wings.silencer.spring.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * wings-mirana-79.properties
 *
 * @author trydofor
 * @since 2021-02-13
 */
@Data
@ConfigurationProperties("wings.silencer.mirana.code")
public class SilencerMiranaProp {

    /**
     * LeapCode seed
     */
    private String leapCode = "安全有关，需要修改";

    /**
     * Crc8Long seed
     */
    private int[] crc8Long;
}
