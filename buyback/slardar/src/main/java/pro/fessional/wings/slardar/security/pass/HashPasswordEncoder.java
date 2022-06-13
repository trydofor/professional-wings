package pro.fessional.wings.slardar.security.pass;

import org.springframework.security.crypto.password.PasswordEncoder;
import pro.fessional.mirana.bits.MdHelp;

/**
 * 使用noop时，以密码的hash取代明文传递，进而后端对hash值加班noop比较。
 * {noop-md5}PLAIN_TEXT = {noop}md5(PLAIN_TEXT)
 *
 * @author trydofor
 * @since 2021-03-02
 */
public class HashPasswordEncoder implements PasswordEncoder {

    private final MdHelp helper;

    public HashPasswordEncoder(String algorithm) {
        this.helper = MdHelp.of(algorithm);
    }

    public static HashPasswordEncoder md5() {
        return new HashPasswordEncoder("MD5");
    }

    public static HashPasswordEncoder sha1() {
        return new HashPasswordEncoder("SHA-1");
    }

    public static HashPasswordEncoder sha256() {
        return new HashPasswordEncoder("SHA-256");
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
