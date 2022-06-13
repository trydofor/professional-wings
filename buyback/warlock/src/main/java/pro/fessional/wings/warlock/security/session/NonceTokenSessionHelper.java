package pro.fessional.wings.warlock.security.session;

import com.github.benmanes.caffeine.cache.Cache;
import pro.fessional.mirana.data.Null;
import pro.fessional.wings.slardar.cache.caffeine.WingsCaffeine;

/**
 * 提供5分钟内有效的一次性token关联验证。
 * ①initNonce:发型一次性token
 * ②bindNonceSid:登录成功后，通过uid，绑定token和sessionId
 *
 * @author trydofor
 * @since 2021-07-01
 */
public class NonceTokenSessionHelper {

    private static final Cache<String, Sf> caffeine = WingsCaffeine
            .builder(100_000, 300, 0).build();

    private static class Sf {
        private String ip = null;
        private String sid = null;
    }

    /**
     * 初始化一次性token
     */
    public static void initNonce(String token, String ip) {
        if (token == null) return;
        final Sf s = new Sf();
        s.ip = ip;
        caffeine.put(token, s);
    }

    /**
     * 绑定token和sid
     */
    public static void bindNonceSid(String token, String sid) {
        final Sf s = caffeine.getIfPresent(token);
        if (s != null) {
            s.sid = sid;
        }
    }

    /**
     * 无效掉token
     */
    public static void invalidNonce(String token) {
        caffeine.invalidate(token);
    }

    /**
     * null-为不存在验证
     * empty-验证进行中
     * sid-验证成功（如果成功，则自动移除，仅返回一次）
     *
     * @param token 一次性token
     * @return null|empty|sid
     */
    public static String authNonce(String token, String ip) {
        if (token == null || token.isEmpty()) return null;

        final Sf s = caffeine.getIfPresent(token);
        if (s == null) return null;
        if (s.sid == null) return Null.Str;

        invalidNonce(token);
        return s.ip.equals(ip) ? s.sid : null;
    }
}
