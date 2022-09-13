package pro.fessional.wings.slardar.security.pass;

import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * always not match
 *
 * @author trydofor
 * @since 2021-02-27
 */
public class NeverPasswordEncoder implements PasswordEncoder {

    private final String token;

    public NeverPasswordEncoder(String name) {
        this.token = "{" + name + "}";
    }

    @Override
    public String encode(CharSequence rawPassword) {
        final String pass = rawPassword.toString();
        final int pos = pass.lastIndexOf(token);
        return pos < 0 ? token + pass : pass.substring(pos + token.length());
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return false;
    }
}
