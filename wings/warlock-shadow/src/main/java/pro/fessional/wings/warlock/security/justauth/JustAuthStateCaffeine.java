package pro.fessional.wings.warlock.security.justauth;

import com.github.benmanes.caffeine.cache.Cache;
import me.zhyd.oauth.cache.AuthStateCache;
import pro.fessional.wings.slardar.cache.caffeine.WingsCaffeine;

/**
 * @author trydofor
 * @since 2021-02-18
 */
public class JustAuthStateCaffeine implements AuthStateCache {

    private final Cache<String, String> caffeine;

    public JustAuthStateCaffeine(int max, int ttl) {
        this.caffeine = WingsCaffeine.builder(max, ttl, 0).build();
    }

    @Override
    public void cache(String key, String value) {
        caffeine.put(key, value);
    }

    @Override
    public void cache(String key, String value, long timeout) {
        caffeine.put(key, value);
    }

    @Override
    public String get(String key) {
        return caffeine.getIfPresent(key);
    }

    @Override
    public boolean containsKey(String key) {
        return caffeine.getIfPresent(key) != null;
    }
}
