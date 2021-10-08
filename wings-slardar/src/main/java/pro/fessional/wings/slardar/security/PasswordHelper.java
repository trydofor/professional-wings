package pro.fessional.wings.slardar.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @author trydofor
 * @since 2021-09-22
 */
@RequiredArgsConstructor
public class PasswordHelper {
    private final PasswordEncoder passwordEncoder;
    private final PasssaltEncoder passsaltEncoder;

    public String salt(String pass, String salt) {
        return passsaltEncoder.salt(pass, salt);
    }

    public String hash(String pass, String salt) {
        final String s = passsaltEncoder.salt(pass, salt);
        return passwordEncoder.encode(s);
    }
}
