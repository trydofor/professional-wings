package pro.fessional.wings.slardar.cache.hazelcast;

import com.hazelcast.core.DistributedObject;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import com.hazelcast.spring.cache.HazelcastCacheManager;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import pro.fessional.wings.slardar.cache.WingsCache;
import pro.fessional.wings.slardar.cache.spring.NullsCache;
import pro.fessional.wings.slardar.spring.prop.SlardarCacheProp;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <a href="https://docs.hazelcast.org/docs/4.0.3/manual/html-single/index.html#dynamically-adding-data-structure-configuration-on-a-cluster">v4.0.3: Dynamically Adding Data Structure Configuration on a Cluster</a>
 * <a href="https://docs.hazelcast.org/docs/4.0.3/manual/html-single/index.html#configuration-pattern-matcher">v4.0.3: Configuration Pattern Matcher</a>
 * <a href="https://docs.hazelcast.com/hazelcast/5.1/configuration/dynamic-config-programmatic-api">v5.1 Dynamic Configuration with Programmatic APIs (Java)</a>
 * <a href="https://docs.hazelcast.com/hazelcast/5.1/configuration/using-wildcards">v5.1 Using Wildcards</a>
 *
 * @author trydofor
 * @since 2023-07-18
 */
@Slf4j
public class WingsHazelcastCacheManager extends HazelcastCacheManager implements WingsCache.State {
    private final SlardarCacheProp slardarCacheProp;
    private final ConcurrentHashMap<String, NullsCache> nullsCache = new ConcurrentHashMap<>();

    public WingsHazelcastCacheManager(SlardarCacheProp cacheProp, HazelcastInstance hazelcastInstance) {
        super(hazelcastInstance);
        this.slardarCacheProp = cacheProp;
    }

    @Override
    public void setHazelcastInstance(HazelcastInstance hazelcastInstance) {
        super.setHazelcastInstance(hazelcastInstance);
    }

    @Override
    public org.springframework.cache.Cache getCache(@NotNull String name) {
        final int size = slardarCacheProp.getNullSize();
        if (size < 0) {
            return super.getCache(name);
        }
        else {
            return nullsCache.computeIfAbsent(name, k -> new NullsCache(super.getCache(k), size, slardarCacheProp.getNullLive()));
        }
    }

    @Override
    @NotNull
    public Map<String, Integer> statsCacheSize() {
        Collection<DistributedObject> dst = getHazelcastInstance().getDistributedObjects();
        final Map<String, Integer> stats = new TreeMap<>();
        for (DistributedObject distributedObject : dst) {
            if (distributedObject instanceof IMap<?, ?> map) {
                stats.put(map.getName(), map.size());
            }
        }
        return stats;
    }

    @Override
    @NotNull
    @SuppressWarnings("unchecked")
    public Set<Object> statsCacheKeys(String name) {
        Collection<DistributedObject> dst = getHazelcastInstance().getDistributedObjects();
        for (DistributedObject distributedObject : dst) {
            if (distributedObject instanceof IMap<?, ?> map) {
                if (map.getName().equals(name)) {
                    return (Set<Object>) map.keySet();
                }
            }
        }
        return Collections.emptySet();
    }
}
