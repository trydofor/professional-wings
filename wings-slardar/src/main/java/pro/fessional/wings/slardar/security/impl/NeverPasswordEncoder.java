package pro.fessional.wings.slardar.security.impl;

import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * always not match
 *
 * @author trydofor
 * @since 2021-02-27
 */
public class NeverPasswordEncoder implements PasswordEncoder {
    @Override
    public String encode(CharSequence rawPassword) {
        return "{never}" + rawPassword;
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return false;
    }
}
