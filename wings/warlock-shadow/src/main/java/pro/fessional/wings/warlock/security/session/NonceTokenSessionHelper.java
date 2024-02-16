package pro.fessional.wings.warlock.security.session;


import org.cache2k.Cache;
import org.jetbrains.annotations.Nullable;
import pro.fessional.mirana.data.R;
import pro.fessional.wings.slardar.cache.cache2k.WingsCache2k;


/**
 * <pre>
 * Provides a one-time token that valid for 5 minutes to authn.
 * (1) initNonce: Init one-time token, result = R.NG
 * (2) bindNonceResult: after successful login, bind result.
 * </pre>
 *
 * @author trydofor
 * @since 2021-07-01
 */
public class NonceTokenSessionHelper {

    private static final Cache<String, Sf> cache = WingsCache2k
            .builder(NonceTokenSessionHelper.class, "nonce", 100_000, 300, 0, String.class, Sf.class)
            .build();

    private static class Sf {
        private String ip = null;
        private R<?> result = null;
    }

    /**
     * Init one-time token
     */
    public static void initNonce(String token, String ip) {
        if (token == null) return;
        final Sf s = new Sf();
        s.ip = ip;
        cache.put(token, s);
    }

    /**
     * bind token to sessionId
     */
    public static void bindNonceSession(String token, String sid) {
        final SidData data = () -> sid;
        final R<?> result = R.okData(data);
        bindNonceResult(token, result);
    }

    /**
     * bind token to result
     */
    public static void bindNonceResult(String token, R<?> result) {
        final Sf s = cache.get(token);
        if (s != null) {
            s.result = result;
        }
    }

    /**
     * invalid the token
     */
    public static void invalidNonce(String token) {
        cache.remove(token);
    }

    /**
     * <pre>
     * null - authn not exist
     * empty - authn in action
     * sid - authn success, (auto remove and return only once)
     * </pre>
     *
     * @param token one-time token
     * @return null|empty|sid
     */
    @Nullable
    public static R<?> authNonce(String token, String ip) {
        if (token == null || token.isEmpty()) return null;

        final Sf s = cache.get(token);
        if (s == null) return null;
        if (s.result == null) return R.NG;

        invalidNonce(token);
        return s.ip.equals(ip) ? s.result : null;
    }

    public interface SidData {
        String getSid();
    }
}
