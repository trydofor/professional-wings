package pro.fessional.wings.slardar.cache.spring;

import org.jetbrains.annotations.NotNull;
import org.springframework.cache.Cache;
import org.springframework.cache.interceptor.CacheInterceptor;

import java.util.List;

/**
 * @author trydofor
 * @since 2022-04-19
 */
public class WingsCacheInterceptor extends CacheInterceptor {

    @Override
    protected void doEvict(@NotNull Cache cache, @NotNull Object key, boolean immediate) {
        if (key instanceof CacheEvictKey r) {
            List<Object> keys = r.getKeys();
            if (keys.isEmpty()) {
                super.doClear(cache, immediate);
            }
            else {
                for (Object k : keys) {
                    super.doEvict(cache, k, immediate);
                }
            }
        }
        else {
            super.doEvict(cache, key, immediate);
        }
    }
}
