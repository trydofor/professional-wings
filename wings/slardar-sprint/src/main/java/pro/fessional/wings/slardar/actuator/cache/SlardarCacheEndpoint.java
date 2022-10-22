package pro.fessional.wings.slardar.actuator.cache;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.endpoint.EndpointId;
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
 * https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-2.1-Release-Notes#endpoint-id-names
 *
 * @see EndpointId
 * @author trydofor
 * @since 2021-06-02
 */
// WARN 41663 --- [restartedMain] o.s.boot.actuate.endpoint.EndpointId  TODO
// Endpoint ID 'wings-cache' contains invalid characters, please migrate to a valid format
@Endpoint(id = "wingscache")
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
