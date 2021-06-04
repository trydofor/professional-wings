package pro.fessional.wings.slardar.actuator.cache;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.endpoint.annotation.DeleteOperation;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.Selector;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static pro.fessional.wings.slardar.cache.WingsCache.State;

/**
 * @author trydofor
 * @since 2021-06-02
 */
@Endpoint(id = "wings-cache")
@Slf4j
public class SlardarCacheEndpoint {

    private final Map<String, CacheManager> cacheManagers = new HashMap<>();

    public SlardarCacheEndpoint(Map<String, CacheManager> cm) {
        for (Map.Entry<String, CacheManager> en : cm.entrySet()) {
            final CacheManager v = en.getValue();
            final String k = en.getKey();
            if (v instanceof State) {
                cacheManagers.put(k, v);
                log.info("init CacheManager with State, bean=" + k);
            }
            else {
                log.info("skip CacheManager without State, bean=" + k);
            }
        }
    }

    @ReadOperation
    public Set<String> listManager() {
        return cacheManagers.keySet();
    }


    @ReadOperation
    public Map<String, Integer> listCacheSize(@Selector String manager) {
        final State cm = (State) cacheManagers.get(manager);
        return cm != null ? cm.statsCacheSize() : Collections.emptyMap();
    }

    @ReadOperation
    public Set<Object> listCacheKeys(@Selector String manager, @Selector String cache) {
        final State cm = (State) cacheManagers.get(manager);
        return cm != null ? cm.statsCacheKeys(cache) : Collections.emptySet();
    }

    @DeleteOperation
    public boolean evictCacheKey(@Selector String manager, @Selector String cache, String key) {
        final CacheManager cm = cacheManagers.get(manager);
        if (cm == null) return false;
        final Cache cc = cm.getCache(cache);
        if (cc == null) return false;
        cc.evict(key);
        return true;
    }
}
