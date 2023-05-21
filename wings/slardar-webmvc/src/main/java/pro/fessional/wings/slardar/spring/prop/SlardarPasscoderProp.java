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
     * default password encoder id.
     * support never|noop|bcrypt|pbkdf2|scrypt|argon2
     *
     * @see #Key$passEncoder
     */
    private String passEncoder = "argon2";
    public static final String Key$passEncoder = Key + ".pass-encoder";

    /**
     * default password decoder id.
     * support never|noop|bcrypt|pbkdf2|scrypt|argon2
     * setDefaultPasswordEncoderForMatches, If id does not match, use the default decoder.
     *
     * @see #Key$passDecoder
     */
    private String passDecoder = "never";
    public static final String Key$passDecoder = Key + ".pass-decoder";


    /**
     *  default salting algorithm. support sha256|sha1|md5
     *
     * @see #Key$saltEncoder
     */
    private String saltEncoder = "sha256";
    public static final String Key$saltEncoder = Key + ".salt-encoder";

    /**
     * The max seconds of timestamp deviation of BasicPasswordEncoder.
     *
     * @see #Key$timeDeviation
     */
    private int timeDeviation = 30;
    public static final String Key$timeDeviation = Key + ".time-deviation";

    public long getTimeDeviationMs() {
        return timeDeviation * 1000L;
    }
}
