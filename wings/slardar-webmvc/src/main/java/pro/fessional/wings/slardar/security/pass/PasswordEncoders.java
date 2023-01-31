package pro.fessional.wings.slardar.security.pass;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.security.crypto.scrypt.SCryptPasswordEncoder;

import java.util.HashMap;
import java.util.Map;

/**
 * @author trydofor
 * @since 2022-03-12
 * @see PasswordEncoderFactories
 */
public class PasswordEncoders {

    public static final String Noop = "noop";
    public static final String Never = "never";
    public static final String Basic = "basic";
    public static final String NoopMd5 = "noop-md5";
    public static final String NoopSha1 = "noop-sha1";
    public static final String NoopSha256 = "noop-sha256";
    public static final String Bcrypt = "bcrypt";
    public static final String Pbkdf2 = "pbkdf2";
    public static final String Scrypt = "scrypt";
    public static final String Argon2 = "argon2";
    public static final String Ldap = "ldap";
    public static final String Md4 = "MD4";
    public static final String Md5 = "MD5";
    public static final String Pbkdf2V58 = "pbkdf2@SpringSecurity_v5_8";
    public static final String ScryptV58 = "scrypt@SpringSecurity_v5_8";
    public static final String Sha1 = "SHA-1";
    public static final String Sha256 = "SHA-256";
    public static final String Argon2V58 = "argon2@SpringSecurity_v5_8";
    public static final String Sha256s = "sha256";

    private static final Map<String, PasswordEncoder> encoderMap = new HashMap<>();

    @NotNull
    public static Map<String, PasswordEncoder> getEncoders() {
        return encoderMap;
    }

    @Nullable
    public static PasswordEncoder getEncoder(String encoder) {
        return encoderMap.get(encoder);
    }

    /**
     * 使用encoder加密password
     *
     * @param encoder  算法Id
     * @param password 明文密码
     * @return 加密后字符串
     */
    @Nullable
    public static String encode(String encoder, CharSequence password) {
        final PasswordEncoder enc = encoderMap.get(encoder);
        return enc == null ? null : enc.encode(password);
    }

    /**
     * 以DelegatingPasswordEncoder格式加密，如 {noop-md5}password
     *
     * @param encoder  算法Id
     * @param password 明文密码
     * @return {算法名}加密后字符串
     */
    @Nullable
    public static String delegating(String encoder, CharSequence password) {
        if (encoder == null || password == null) return null;
        final PasswordEncoder enc = encoderMap.get(encoder);
        if (enc == null) return null;

        if (Never.equalsIgnoreCase(encoder)) {
            return enc.encode(password);
        }
        else {
            return "{" + encoder + "}" + enc.encode(password);
        }
    }

    /**
     * 检查密码是delegating格式，否则使用encoder加密，若失败在使用noop-md5算法
     *
     * @param password 密码
     * @param encoder  备选算法
     * @return delegating的密码
     */
    @Contract("!null, _ -> !null")
    public static String delegated(String password, String encoder) {
        if (password == null) return null;
        final int bc = password.indexOf("}", 2);
        if (password.startsWith("{") && bc > 0) return password;

        final String pw = delegating(encoder, password);
        return pw == null ? "{" + NoopMd5 + "}" + password : pw;
    }

    @NotNull
    @SuppressWarnings("deprecation")
    public static Map<String, PasswordEncoder> initEncoders(long basicMs) {
        encoderMap.put(Noop, org.springframework.security.crypto.password.NoOpPasswordEncoder.getInstance());
        encoderMap.put(Never, new NeverPasswordEncoder(Never));
        encoderMap.put(Basic, new BasicPasswordEncoder(basicMs));
        encoderMap.put(NoopMd5, HashPasswordEncoder.md5());
        encoderMap.put(NoopSha1, HashPasswordEncoder.sha1());
        encoderMap.put(NoopSha256, HashPasswordEncoder.sha256());
        encoderMap.put(Bcrypt, new BCryptPasswordEncoder());
        encoderMap.put(Pbkdf2, Pbkdf2PasswordEncoder.defaultsForSpringSecurity_v5_5());
        encoderMap.put(Scrypt, SCryptPasswordEncoder.defaultsForSpringSecurity_v4_1());
        encoderMap.put(Argon2, Argon2PasswordEncoder.defaultsForSpringSecurity_v5_2());
        encoderMap.put(Ldap, new org.springframework.security.crypto.password.LdapShaPasswordEncoder());
        encoderMap.put(Md4, new org.springframework.security.crypto.password.Md4PasswordEncoder());
        encoderMap.put(Md5, new org.springframework.security.crypto.password.MessageDigestPasswordEncoder("MD5"));
        encoderMap.put(Pbkdf2V58, Pbkdf2PasswordEncoder.defaultsForSpringSecurity_v5_8());
        encoderMap.put(ScryptV58, SCryptPasswordEncoder.defaultsForSpringSecurity_v5_8());
        encoderMap.put(Sha1, new org.springframework.security.crypto.password.MessageDigestPasswordEncoder("SHA-1"));
        encoderMap.put(Sha256, new org.springframework.security.crypto.password.MessageDigestPasswordEncoder("SHA-256"));
        encoderMap.put(Sha256s, new org.springframework.security.crypto.password.StandardPasswordEncoder());
        encoderMap.put(Argon2V58, Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8());
        return encoderMap;
    }
}
