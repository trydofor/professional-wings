package pro.fessional.wings.slardar.cache;

import com.hazelcast.config.InvalidConfigurationException;
import com.hazelcast.config.MapConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.spring.cache.HazelcastCacheManager;
import lombok.extern.slf4j.Slf4j;
import pro.fessional.wings.slardar.spring.prop.SlardarCacheProp;

import java.util.Map;

import static pro.fessional.wings.slardar.spring.prop.SlardarCacheProp.maxInt;
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
    public static class Manager extends HazelcastCacheManager {
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

        private void checkWingsLevelPattern() {
            final com.hazelcast.config.Config config = getHazelcastInstance().getConfig();
            final Map<String, MapConfig> mapCnf = config.getMapConfigs();

            final SlardarCacheProp.Conf common = slardarCacheProp.getCommon();
            checkMapConf(config, mapCnf, "default", common.getMaxLive(),
                    common.getMaxIdle(), common.getMaxSize());

            // check level
            for (Map.Entry<String, SlardarCacheProp.Conf> entry : slardarCacheProp.getLevel().entrySet()) {
                // 前缀同
                final SlardarCacheProp.Conf lvl = entry.getValue();
                checkMapConf(config, mapCnf, wildcard(entry.getKey()),
                        lvl.getMaxLive(), lvl.getMaxIdle(), lvl.getMaxSize());
            }
        }

        private void checkMapConf(com.hazelcast.config.Config config, Map<String, MapConfig> mapCnf, String name, int ttl, int tti, int max) {
            // check default
            final int ttl1 = maxInt(ttl);
            final int tti1 = maxInt(tti);
            final int max1 = maxInt(max);
            MapConfig mc = mapCnf.get(name);

            try {
                if (mc == null) {
                    mc = new MapConfig(name);
                    mc.setTimeToLiveSeconds(ttl1);
                    mc.setMaxIdleSeconds(tti1);
                    mc.getEvictionConfig().setSize(max1);
                    log.info("Wings hazelcast addMapConfig name={}, ttl={}, tti={}, size={}", name, ttl1, tti1, max1);
                    config.addMapConfig(mc);
                } else {
                    boolean diff = false;
                    final int ttl0 = mc.getTimeToLiveSeconds();
                    if (ttl0 != ttl1) {
                        diff = true;
                        mc.setTimeToLiveSeconds(ttl1);
                        log.warn("Wings hazelcast exist TimeToLiveSeconds diff {} to {}", ttl0, ttl1);
                    }
                    final int tti0 = mc.getMaxIdleSeconds();
                    if (tti0 != tti1) {
                        diff = true;
                        mc.setMaxIdleSeconds(tti1);
                        log.warn("Wings hazelcast exist MaxIdleSeconds diff {} to {}", tti0, tti1);
                    }

                    int max0 = mc.getEvictionConfig().getSize();
                    if (max0 != max1) {
                        diff = true;
                        mc.getEvictionConfig().setSize(max1);
                        log.warn("Wings hazelcast default Eviction-max0 diff {} to {}", max0, max);
                    }

                    if (diff) {
                        log.warn("Wings hazelcast default diff. dynamically change may has conflict. \nsee https://docs.hazelcast.org/docs/4.0.3/manual/html-single/index.html#dynamically-adding-data-structure-configuration-on-a-cluster");
                    }
                }
            } catch (InvalidConfigurationException e) {
                log.error("failed to change MapConfig, name=" + name, e);
            }
        }
    }
}
