package pro.fessional.wings.warlock.security.justauth;

import me.zhyd.oauth.cache.AuthStateCache;
import org.cache2k.Cache;
import org.cache2k.Cache2kBuilder;

import java.util.concurrent.TimeUnit;


/**
 * @author trydofor
 * @since 2021-02-18
 */
public class JustAuthStateCache implements AuthStateCache {

    private final Cache<String, String> caffeine;

    public JustAuthStateCache(int max, int ttl) {
        this.caffeine = Cache2kBuilder.of(String.class, String.class)
                                      .entryCapacity(max)
                                      .expireAfterWrite(ttl, TimeUnit.SECONDS)
                                      .build();
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
        return caffeine.get(key);
    }

    @Override
    public boolean containsKey(String key) {
        return caffeine.get(key) != null;
    }
}
