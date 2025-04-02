package pro.fessional.wings.warlock.security.session;


import org.cache2k.Cache;
import org.jetbrains.annotations.NotNull;
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

    public static final String CodeAuthing = "authing";
    public static final String CodeSession = "session";

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
        if (token == null || token.isEmpty()) return;
        final Sf s = new Sf();
        s.ip = ip;
        cache.put(token, s);
    }

    /**
     * bind token to sessionId
     */
    public static void bindNonceSession(String token, String sid) {
        if (token == null || token.isEmpty()) return;
        final R<?> result = R.ok(sid, CodeSession);
        bindNonceResult(token, result);
    }

    /**
     * bind token to result, for user specified binding
     */
    public static void bindNonceResult(String token, R<?> result) {
        if (token == null || token.isEmpty()) return;
        final Sf s = cache.get(token);
        if (s != null) {
            s.result = result;
        }
    }

    /**
     * invalid the token
     */
    public static void invalidNonce(String token) {
        if (token == null || token.isEmpty()) return;
        cache.remove(token);
    }

    /**
     * <pre>
     * check auth result by token in nonce
     *  * Result(false) - authn not exist
     *  * Result(false, code='authing', message='authing')} - authing
     *  * Result(true, code='session', data=sessionId)} - bind session
     *  * Result(true, code='xxx', data=object)} other code/object
     * </pre>
     */
    @NotNull
    public static R<?> authNonce(String token, String ip) {
        if (token == null || token.isEmpty()) return R.NG();

        final Sf s = cache.get(token);
        if (s == null) return R.NG();
        if (s.result == null) return R.ng(null, CodeAuthing, CodeAuthing);

        invalidNonce(token);
        return s.ip.equals(ip) ? s.result : R.NG();
    }
}
