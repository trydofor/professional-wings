package pro.fessional.wings.silencer.cache;

import com.github.benmanes.caffeine.cache.Caffeine;

import java.util.concurrent.TimeUnit;

/**
 * @author trydofor
 * @since 2020-08-10
 */
public class CaffeineUtil {

    public static Caffeine<Object, Object> build(int max, long ttl, long idle) {
        return Caffeine.newBuilder()
                       .maximumSize(max <= 0 ? Integer.MAX_VALUE : max)
                       .expireAfterWrite(ttl <= 0 ? Integer.MAX_VALUE : ttl, TimeUnit.SECONDS)
                       .expireAfterAccess(idle <= 0 ? Integer.MAX_VALUE : idle, TimeUnit.SECONDS);
    }

}
