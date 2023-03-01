package pro.fessional.wings.slardar.cache.spring;

import org.jetbrains.annotations.NotNull;
import org.springframework.cache.Cache;
import org.springframework.cache.interceptor.CacheInterceptor;

/**
 * @author trydofor
 * @since 2022-04-19
 */
public class WingsCacheInterceptor extends CacheInterceptor {

    @Override
    protected void doEvict(@NotNull Cache cache, @NotNull Object key, boolean immediate) {
        if (key instanceof CacheEvictMultiKeys r) {
            if (r.isEvictAll()) {
                super.doClear(cache, immediate);
            }
            else {
                for (Object k : r.getEvictKey()) {
                    super.doEvict(cache, k, immediate);
                }
            }
        }
        else {
            super.doEvict(cache, key, immediate);
        }
    }
}
