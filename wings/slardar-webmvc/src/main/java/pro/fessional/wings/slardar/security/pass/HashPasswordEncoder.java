package pro.fessional.wings.slardar.security.pass;

import org.springframework.security.crypto.password.PasswordEncoder;
import pro.fessional.mirana.bits.MdHelp;

/**
 * <pre>
 * When using `noop`, the hash of the password is passed instead of plaintext,
 * and the hash value is compared in `noop` way
 *
 * {noop-md5}PLAIN_TEXT = {noop}md5(PLAIN_TEXT)
 * </pre>
 *
 * @author trydofor
 * @since 2021-03-02
 */
public class HashPasswordEncoder implements PasswordEncoder {

    private final MdHelp helper;

    public HashPasswordEncoder(String algorithm, int hexLen) {
        this.helper = MdHelp.of(algorithm, hexLen);
    }

    public static HashPasswordEncoder md5() {
        return new HashPasswordEncoder(MdHelp.MD_MD5, MdHelp.LEN_MD5_HEX);
    }

    public static HashPasswordEncoder sha1() {
        return new HashPasswordEncoder(MdHelp.MD_SHA1, MdHelp.LEN_SHA1_HEX);
    }

    public static HashPasswordEncoder sha256() {
        return new HashPasswordEncoder(MdHelp.MD_SHA256, MdHelp.LEN_SHA256_HEX);
    }

    @Override
    public String encode(CharSequence rawPassword) {
        return rawPassword.toString();
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        if (rawPassword.equals(encodedPassword)) {
            return true;
        }
        final String hash = hash(encodedPassword);
        return hash.equalsIgnoreCase(rawPassword.toString());
    }

    public String hash(String plain) {
        return helper.sum(plain, false);
    }
}
