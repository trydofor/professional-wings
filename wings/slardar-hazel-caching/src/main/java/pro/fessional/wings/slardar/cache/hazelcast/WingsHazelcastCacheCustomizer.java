package pro.fessional.wings.slardar.cache.hazelcast;

import com.hazelcast.config.Config;
import com.hazelcast.config.InvalidConfigurationException;
import com.hazelcast.config.MapConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.hazelcast.HazelcastConfigCustomizer;
import pro.fessional.wings.slardar.cache.WingsCache;
import pro.fessional.wings.slardar.spring.prop.SlardarCacheProp;

import java.util.Map;

/**
 * @author trydofor
 * @since 2023-07-18
 */
@Slf4j
@RequiredArgsConstructor
public class WingsHazelcastCacheCustomizer implements HazelcastConfigCustomizer {

    private final SlardarCacheProp cacheProp;

    @Override
    public void customize(Config config) {
        final Map<String, MapConfig> mapCnf = config.getMapConfigs();

        // check level
        for (Map.Entry<String, SlardarCacheProp.Conf> entry : cacheProp.getLevel().entrySet()) {
            final SlardarCacheProp.Conf lvl = entry.getValue();
            final String name = WingsCache.Naming.wildcard(entry.getKey());
            checkMapConf(config, mapCnf, name, lvl.getMaxLive(), lvl.getMaxIdle(), lvl.getMaxSize());
        }
    }


    private void checkMapConf(Config config, Map<String, MapConfig> mapCnf, String name, int ttl, int tti, int max) {
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
                    log.warn("Wings hazelcast dynamically change may has conflict. \nsee https://docs.hazelcast.com/hazelcast/5.1/configuration/dynamic-config-programmatic-api");
                }
            }
        }
        catch (InvalidConfigurationException e) {
            log.error("failed to change MapConfig, name=" + name, e);
        }
    }
}
