package pro.fessional.wings.slardar.spring.bean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import pro.fessional.wings.slardar.cache.WingsCache;
import pro.fessional.wings.slardar.cache.WingsCacheHelper;
import pro.fessional.wings.slardar.cache.caffeine.WingsCaffeine;
import pro.fessional.wings.slardar.spring.prop.SlardarCacheProp;
import pro.fessional.wings.slardar.spring.prop.SlardarEnabledProp;

import java.util.Map;

import static pro.fessional.wings.slardar.cache.WingsCache.Manager;

/**
 * @author trydofor
 * @since 2019-12-03
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(name = SlardarEnabledProp.Key$caching, havingValue = "true")
@EnableCaching
public class SlardarCacheConfiguration {

    private static final Log logger = LogFactory.getLog(SlardarCacheConfiguration.class);

    // //////////////////// caffeine ////////////////////
    @Bean(Manager.Memory)
    @ConditionalOnMissingBean(CaffeineCacheManager.class)
    public CaffeineCacheManager caffeineCacheManager(SlardarCacheProp conf) {
        logger.info("Wings conf caffeine as " + Manager.Memory);
        return new WingsCaffeine.Manager(conf);
    }

    @Bean
    @Primary
    public CacheManager cacheManager(Map<String, CacheManager> managers, SlardarCacheProp prop) {
        final CacheManager ser = managers.get(WingsCache.Manager.Server);
        if (ser != null) {
            logger.info("Wings conf WingsCacheHelper Server=" + ser.getClass().getName());
            WingsCacheHelper.setServer(ser);
        }
        final CacheManager mem = managers.get(WingsCache.Manager.Memory);
        if (mem != null) {
            logger.info("Wings conf WingsCacheHelper Memory=" + mem.getClass().getName());
            WingsCacheHelper.setMemory(mem);
        }

        CacheManager pre = null;
        String cnm = null;
        String prim = prop.getPrimary();
        for (Map.Entry<String, CacheManager> en : managers.entrySet()) {
            final String name = en.getKey();
            if (name.equalsIgnoreCase(prim)) {
                logger.info("Wings conf primary CacheManager=" + name);
                return en.getValue();
            }
            else if (pre == null && name.startsWith(prim)) {
                cnm = name;
                pre = en.getValue();
            }
        }
        logger.info("Wings conf primary CacheManager=" + cnm);
        return pre;
    }
}
