package pro.fessional.wings.slardar.security.impl;

import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * always not match
 *
 * @author trydofor
 * @since 2021-02-27
 */
public class NeverPasswordEncoder implements PasswordEncoder {

    public static final String Key = "never";
    private static final String Tkn = "{" + Key + "}";

    @Override
    public String encode(CharSequence rawPassword) {
        final String pass = rawPassword.toString();
        final int pos = pass.lastIndexOf(Tkn);
        return pos < 0 ? Tkn + pass : pass.substring(pos + Tkn.length());
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return false;
    }
}
