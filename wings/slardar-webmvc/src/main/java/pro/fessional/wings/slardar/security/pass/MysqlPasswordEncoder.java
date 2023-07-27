package pro.fessional.wings.slardar.security.pass;

import org.springframework.security.crypto.password.PasswordEncoder;
import pro.fessional.mirana.bits.MdHelp;

import java.nio.charset.StandardCharsets;

/**
 * mysql5 `password` function compatible encoder
 *
 * @author trydofor
 * @since 2021-02-27
 */
public class MysqlPasswordEncoder implements PasswordEncoder {

    @Override
    public String encode(CharSequence rawPassword) {
        final String pass = rawPassword.toString();
        final byte[] bytes = pass.getBytes(StandardCharsets.UTF_8);
        final byte[] sha1 = MdHelp.sha1.digest(bytes);
        return MdHelp.sha1.sum(sha1, true);
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        int off = 0;
        if (encodedPassword.startsWith("*")) {
            off = 1;
        }
        final String code = encode(rawPassword);
        return encodedPassword.regionMatches(true, off, code, 0, code.length());
    }
}
