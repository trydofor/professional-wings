package pro.fessional.wings.slardar.spring.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * wings-passcoder-79.properties
 *
 * @author trydofor
 * @see #Key
 * @since 2021-02-14
 */
@Data
@ConfigurationProperties(SlardarPasscoderProp.Key)
public class SlardarPasscoderProp {

    public static final String Key = "wings.slardar.passcoder";

    /**
     * 默认加密算法  never|noop|bcrypt|pbkdf2|scrypt|argon2
     *
     * @see #Key$passEncoder
     */
    private String passEncoder = "argon2";
    public static final String Key$passEncoder = Key + ".pass-encoder";

    /**
     * 默认解密算法  never|noop|bcrypt|pbkdf2|scrypt|argon2
     *
     * @see #Key$passDecoder
     */
    private String passDecoder = "noop";
    public static final String Key$passDecoder = Key + ".pass-decoder";


    /**
     * 默认加盐算法 sha256|sha1|md5
     *
     * @see #Key$saltEncoder
     */
    private String saltEncoder = "sha256";
    public static final String Key$saltEncoder = Key + ".salt-encoder";

    /**
     * BasicPasswordEncoder 时间戳偏差秒数，正数
     *
     * @see #Key$timeDeviation
     */
    private int timeDeviation = 30;
    public static final String Key$timeDeviation = Key + ".time-deviation";

    public long getTimeDeviationMs() {
        return timeDeviation * 1000L;
    }
}
