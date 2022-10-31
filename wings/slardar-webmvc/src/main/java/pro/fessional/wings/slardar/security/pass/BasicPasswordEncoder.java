package pro.fessional.wings.slardar.security.pass;

import org.springframework.security.crypto.password.PasswordEncoder;
import pro.fessional.mirana.bits.Md5;
import pro.fessional.mirana.time.ThreadNow;

/**
 * 折中BasicAuthentication与DigestAuthentication，仅避免明文传递密码。
 * <pre>
 * 使用密码对时间戳Md5，以时间戳和md5值代替密码传送。要求时间戳与服务器相差在正负3分钟内。
 * timestamp - 1970 ms
 * password - user password
 * md5_hash = md5($timestamp + "#" + $password)
 * token =  $timestamp + "#" + $md5_hash
 * Authorization:"Basic base64_url_safe($username + ":" + $token)"
 * </pre>
 *
 * @author trydofor
 * @since 2021-02-27
 */
public class BasicPasswordEncoder implements PasswordEncoder {

    private static final String Splitter = "#";
    private final long deviation;

    public BasicPasswordEncoder(long deviation) {
        this.deviation = Math.abs(deviation);
    }

    @Override
    public String encode(CharSequence rawPassword) {
        final String pass = rawPassword.toString();
        final long time = ThreadNow.millis();
        final String hash = Md5.sum(time + Splitter + pass, false);
        return time + Splitter + hash;
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        final String raw = rawPassword.toString();
        if(rawPassword.equals(encodedPassword)){
            return true;
        }

        final int pos = raw.indexOf(Splitter);
        if (pos <= 0 || pos >= raw.length() - 1) {
            return false;
        }

        final long time = Long.parseLong(raw.substring(0, pos));
        if (Math.abs(ThreadNow.millis() - time) > deviation) return false;

        final String hash1 = raw.substring(pos + 1);
        final String hash2 = Md5.sum(time + Splitter + encodedPassword, false);

        return hash1.equalsIgnoreCase(hash2);
    }
}
