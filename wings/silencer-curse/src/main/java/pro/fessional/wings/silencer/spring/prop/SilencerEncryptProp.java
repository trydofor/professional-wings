package pro.fessional.wings.silencer.spring.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Collections;
import java.util.Map;

/**
 * wings-encrypt-79.properties
 *
 * @author trydofor
 * @see #Key
 * @since 2021-02-13
 */
@Data
@ConfigurationProperties(SilencerEncryptProp.Key)
public class SilencerEncryptProp {
    public static final String Key = "wings.silencer.encrypt";

    /**
     * LeapCode seed，安全有关，需要修改
     *
     * @see #Key$leapCode
     */
    private String leapCode = null;
    public static final String Key$leapCode = Key + ".leap-code";

    /**
     * Crc8Long seed，安全有关，需要修改
     *
     * @see #Key$crc8Long
     */
    private int[] crc8Long = null;
    public static final String Key$crc8Long = Key + ".crc8-long";

    /**
     * 全局AesKey，安全有关，需要修改
     *
     * @see #Key$aesKey
     */
    private Map<String, String> aesKey = Collections.emptyMap();
    public static final String Key$aesKey = Key + ".aes-key";

    public String getAesKey(String name) {
        return aesKey.get(name);
    }
}
