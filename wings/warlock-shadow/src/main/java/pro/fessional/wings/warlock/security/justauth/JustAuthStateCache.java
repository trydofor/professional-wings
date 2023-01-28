package pro.fessional.wings.warlock.security.justauth;

import me.zhyd.oauth.cache.AuthStateCache;
import org.cache2k.Cache;
import pro.fessional.wings.slardar.cache.cache2k.WingsCache2k;


/**
 * @author trydofor
 * @since 2021-02-18
 */
public class JustAuthStateCache implements AuthStateCache {

    private final Cache<String, String> cache;

    public JustAuthStateCache(int max, int ttl) {
        this.cache = WingsCache2k.builder(JustAuthStateCache.class, "cache", max, ttl, -1, String.class, String.class)
                                 .build();
    }

    @Override
    public void cache(String key, String value) {
        cache.put(key, value);
    }

    @Override
    public void cache(String key, String value, long timeout) {
        cache.put(key, value);
    }

    @Override
    public String get(String key) {
        return cache.get(key);
    }

    @Override
    public boolean containsKey(String key) {
        return cache.get(key) != null;
    }
}
