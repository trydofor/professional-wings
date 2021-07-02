package pro.fessional.wings.warlock.security.session;

import com.github.benmanes.caffeine.cache.Cache;
import pro.fessional.mirana.data.Null;
import pro.fessional.wings.slardar.cache.WingsCaffeine;

/**
 * 提供5分钟内有效的一次性token关联验证。
 * ①initNonce:发型一次性token
 * ②bindNonceAuth:三方验证后，绑定token和AuthDetails，bindDetail
 * ③swapNonceUid:获取用户后，通过AuthDetails，绑定token和uid
 * ④swapNonceSid:登录成功后，通过uid，绑定token和sessionId
 *
 * @author trydofor
 * @since 2021-07-01
 */
public class NonceTokenSessionHelper {

    private static final Cache<Object, Object> caffeine = WingsCaffeine
            .builder(100_000, 300, 0).build();

    /**
     * 初始化一次性token
     */
    public static void initNonce(String token) {
        if (token == null) return;
        caffeine.put(token, Null.Str);
    }

    /**
     * 绑定token和AuthDetails
     */
    public static void bindNonceAuth(String token, Object auth) {
        if (token == null || auth == null) return;
        if (caffeine.getIfPresent(token) != null) {
            caffeine.put(auth, token);
        }
    }

    /**
     * 通过AuthDetails交换，绑定token和uid
     */
    public static void swapNonceUid(long uid, Object auth) {
        if (auth == null) return;
        final Object tkn = caffeine.getIfPresent(auth);
        if (tkn == null) return;

        caffeine.invalidate(auth);
        if (tkn instanceof String) {
            bindNonceUid((String) tkn, uid);
        }
    }

    /**
     * 通过uid交换，绑定token和sid
     */
    public static void swapNonceSid(long uid, String sid) {
        if (sid == null) return;
        final Object tkn = caffeine.getIfPresent(uid);
        if (tkn == null) return;

        caffeine.invalidate(uid);
        if (tkn instanceof String) {
            bindNonceSid((String) tkn, sid);
        }
    }


    /**
     * 绑定token和uid
     */
    public static void bindNonceUid(String token, long uid) {
        caffeine.put(uid, token);
    }

    /**
     * 绑定token和sid
     */
    public static void bindNonceSid(String token, String sid) {
        caffeine.put(token, sid);
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
    public static String authNonce(String token) {
        if (token == null || token.isEmpty()) return null;

        final String sid = (String) caffeine.getIfPresent(token);
        if (sid != null && sid.length() > 0) {
            invalidNonce(token);
        }
        return sid;
    }
}
