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
import java.util.TreeSet;

import static pro.fessional.wings.slardar.cache.WingsCache.State;

/**
 * <a href="https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-2.1-Release-Notes#endpoint-id-names">Endpoint ID names</a>
 * <p>
 * If you have developed your own actuator @Endpoint beans you should ensure that
 * they following the tighter naming rules introduced in Spring Boot 2.1.
 * Specifically, IDs should be alpha-numeric only and must start with a letter
 * (see the EndpointId class documentation for full details).
 * If you use - or . characters Spring Boot 2.1 will log a warning and
 * ask you to migrate to the correct format.
 *
 * @author trydofor
 * @see EndpointId
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


    /**
     * name + `:` + count
     */
    @ReadOperation
    public Set<String> listCacheSize(@Selector String manager) {
        final State cm = (State) cacheManagers.get(manager);
        if (cm != null) {
            final Map<String, Integer> set = cm.statsCacheSize();
            Set<String> rs = new TreeSet<>();
            for (Map.Entry<String, Integer> en : set.entrySet()) {
                rs.add(en.getKey() + ":" + en.getValue());
            }
            return rs;
        }
        return Collections.emptySet();
    }

    /**
     * id + `:` + key
     */
    @ReadOperation
    public Set<String> listCacheKeys(@Selector String manager, @Selector String cache) {
        final State cm = (State) cacheManagers.get(manager);
        if (cm != null) {
            final Set<Object> set = cm.statsCacheKeys(cache);
            Set<String> rs = new TreeSet<>();
            for (Object k : set) {
                rs.add(System.identityHashCode(k) + ":" + k.toString());
            }
            return rs;
        }

        return Collections.emptySet();
    }

    @DeleteOperation
    public String evictCacheKey(@Selector String manager, @Selector String cache, String id) {
        final CacheManager cm = cacheManagers.get(manager);
        if (cm == null) return "manager not found";
        final Cache cc = cm.getCache(cache);
        if (cc == null) return "cache not found";

        final State st = (State) cm;
        final Set<Object> set = st.statsCacheKeys(cache);
        final int ki = Integer.parseInt(id);
        for (Object k : set) {
            if (System.identityHashCode(k) == ki) {
                cc.evict(k);
                return "key evict";
            }
        }
        return "key not found";
    }
}
