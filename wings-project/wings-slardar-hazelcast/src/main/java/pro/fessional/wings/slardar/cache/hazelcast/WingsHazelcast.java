package pro.fessional.wings.slardar.cache.hazelcast;

import com.hazelcast.config.InvalidConfigurationException;
import com.hazelcast.config.MapConfig;
import com.hazelcast.core.DistributedObject;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import com.hazelcast.spring.cache.HazelcastCacheManager;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import pro.fessional.wings.slardar.cache.NullsCache;
import pro.fessional.wings.slardar.cache.WingsCache;
import pro.fessional.wings.slardar.spring.prop.SlardarCacheProp;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import static pro.fessional.wings.slardar.spring.prop.SlardarCacheProp.wildcard;

/**
 * @author trydofor
 * @since 2021-02-12
 */
@Slf4j
public class WingsHazelcast {

    /**
     * https://docs.hazelcast.org/docs/4.0.3/manual/html-single/index.html#dynamically-adding-data-structure-configuration-on-a-cluster
     * https://docs.hazelcast.org/docs/4.0.3/manual/html-single/index.html#configuration-pattern-matcher
     */
    public static class Manager extends HazelcastCacheManager implements WingsCache.State {
        private final SlardarCacheProp slardarCacheProp;

        public Manager(SlardarCacheProp config, HazelcastInstance hazelcastInstance) {
            super(hazelcastInstance);
            this.slardarCacheProp = config;
            checkWingsLevelPattern();
        }

        @Override
        public void setHazelcastInstance(HazelcastInstance hazelcastInstance) {
            super.setHazelcastInstance(hazelcastInstance);
            checkWingsLevelPattern();
        }

        @Override
        public org.springframework.cache.Cache getCache(@NotNull String name) {
            final org.springframework.cache.Cache cache = super.getCache(name);

            if (slardarCacheProp.isNullWeak()) {
                return new NullsCache(cache, false);
            }
            else if (slardarCacheProp.isNullSkip()) {
                return new NullsCache(cache, true);
            }
            else {
                return cache;
            }
        }

        @Override
        @NotNull
        public Map<String, Integer> statsCacheSize() {
            Collection<DistributedObject> dst = getHazelcastInstance().getDistributedObjects();
            final Map<String, Integer> stats = new TreeMap<>();
            for (DistributedObject distributedObject : dst) {
                if (distributedObject instanceof IMap) {
                    IMap<?, ?> map = (IMap<?, ?>) distributedObject;
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
                if (distributedObject instanceof IMap) {
                    IMap<?, ?> map = (IMap<?, ?>) distributedObject;
                    if (map.getName().equals(name)) {
                        return (Set<Object>) map.keySet();
                    }
                }
            }
            return Collections.emptySet();
        }

        private void checkWingsLevelPattern() {
            final com.hazelcast.config.Config config = getHazelcastInstance().getConfig();
            final Map<String, MapConfig> mapCnf = config.getMapConfigs();

            /*
            final SlardarCacheProp.Conf common = slardarCacheProp.getCommon();
            checkMapConf(config, mapCnf, "default", common.getMaxLive(),
                    common.getMaxIdle(), common.getMaxSize());
            */

            // check level
            for (Map.Entry<String, SlardarCacheProp.Conf> entry : slardarCacheProp.getLevel().entrySet()) {
                // 前缀同
                final SlardarCacheProp.Conf lvl = entry.getValue();
                final String name = wildcard(entry.getKey());
                checkMapConf(config, mapCnf, name, lvl.getMaxLive(), lvl.getMaxIdle(), lvl.getMaxSize());
            }
        }

        private void checkMapConf(com.hazelcast.config.Config config, Map<String, MapConfig> mapCnf, String name, int ttl, int tti, int max) {
            // check default
            MapConfig mc = mapCnf.get(name);

            try {
                if (mc == null) {
                    mc = new MapConfig(name);
                    mc.setTimeToLiveSeconds(ttl);
                    mc.setMaxIdleSeconds(tti);
                    mc.getEvictionConfig().setSize(max);
                    log.info("Wings hazelcast addMapConfig name={}, ttl={}, tti={}, size={}", name, ttl, tti, max);
                    config.addMapConfig(mc);
                }
                else {
                    boolean diff = false;
                    final int ttl0 = mc.getTimeToLiveSeconds();
                    if (ttl0 != ttl) {
                        diff = true;
                        mc.setTimeToLiveSeconds(ttl);
                        log.warn("Wings hazelcast exist TimeToLiveSeconds of name={}, from {} to {}", name, ttl0, ttl);
                    }
                    final int tti0 = mc.getMaxIdleSeconds();
                    if (tti0 != tti) {
                        diff = true;
                        mc.setMaxIdleSeconds(tti);
                        log.warn("Wings hazelcast exist MaxIdleSeconds of name={}, from {} to {}", name, tti0, tti);
                    }

                    int max0 = mc.getEvictionConfig().getSize();
                    if (max0 != max) {
                        diff = true;
                        mc.getEvictionConfig().setSize(max);
                        log.warn("Wings hazelcast default Eviction-max0 of name={}, from {} to {}", name, max0, max);
                    }

                    if (diff) {
                        log.warn("Wings hazelcast dynamically change may has conflict. \nsee https://docs.hazelcast.org/docs/4.0.3/manual/html-single/index.html#dynamically-adding-data-structure-configuration-on-a-cluster");
                    }
                }
            }
            catch (InvalidConfigurationException e) {
                log.error("failed to change MapConfig, name=" + name, e);
            }
        }
    }
}
