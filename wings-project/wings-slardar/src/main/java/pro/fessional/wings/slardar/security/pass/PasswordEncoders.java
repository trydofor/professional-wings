package pro.fessional.wings.slardar.security.pass;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.security.crypto.scrypt.SCryptPasswordEncoder;

import java.util.HashMap;
import java.util.Map;

/**
 * @author trydofor
 * @since 2022-03-12
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
        encoderMap.put(Pbkdf2, new Pbkdf2PasswordEncoder());
        encoderMap.put(Scrypt, new SCryptPasswordEncoder());
        encoderMap.put(Argon2, new Argon2PasswordEncoder());
        return encoderMap;
    }
}
