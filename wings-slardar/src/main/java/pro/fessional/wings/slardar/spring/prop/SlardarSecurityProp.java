package pro.fessional.wings.slardar.spring.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author trydofor
 * @see #Key
 * @since 2021-02-14
 */
@Data
@ConfigurationProperties(SlardarSecurityProp.Key)
public class SlardarSecurityProp {

    public static final String Key = "wings.slardar.security";

    /**
     * 在 2019 年，我建议你以后不要使用 PBKDF2 或 BCrypt，并强烈建议将 Argon2（最好是 Argon2id）用于最新系统。
     * BScrypt 是当 Argon2 不可用时的不二选择，但要记住，它在侧信道泄露方面也存在相同的问题。
     *
     * @see #Key$passwordEncoder
     */
    private String passwordEncoder = "argon2";
    public static final String Key$passwordEncoder = Key + ".password-encoder";
}
