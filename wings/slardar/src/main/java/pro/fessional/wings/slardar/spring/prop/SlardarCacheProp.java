package pro.fessional.wings.slardar.spring.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import pro.fessional.wings.slardar.cache.WingsCache;

import java.util.HashMap;
import java.util.Map;

/**
 * LRU (Least Recently Used) default, unit is second, 0=infinitely
 *
 * @author trydofor
 * @see #Key
 * @since 2021-02-11
 */
@Data
@ConfigurationProperties(SlardarCacheProp.Key)
public class SlardarCacheProp {

    public static final String Key = "wings.slardar.cache";

    /**
     * <pre>
     * which CacheManager is primary: MemoryCacheManager | ServerCacheManager
     * - `MemoryCacheManager` - Cache2k Jvm cache
     * - `ServerCacheManager` - Hazelcast distributed cache
     * </pre>
     *
     * @see #Key$primary
     */
    private String primary = WingsCache.Manager.Memory;
    public static final String Key$primary = Key + ".primary";

    /**
     * whether to Resolve the cache name, that is, to append the concrete class name
     *
     * @see #Key$expand
     */
    private boolean expand = true;
    public static final String Key$expand = Key + ".expand";

    /**
     * <pre>
     * in principle, null is not cached. However, it can be handled uniformly.
     * - `positive` - cache size
     * - `0` - do not cache null
     * - `negative` - no uniform processing
     * </pre>
     *
     * @see #Key$nullSize
     */
    private int nullSize = 1000;
    public static final String Key$nullSize = Key + ".null-size";

    /**
     * in principle, null is not cached. However, it can be handled uniformly.
     *
     * @see #Key$nullLive
     */
    private int nullLive = 300;
    public static final String Key$nullLive = Key + ".null-live";

    /**
     * <pre>
     * default configuration other than level
     * - `max-live`=`3600`, expireAfterWrite(Time To Live)
     * - `max-idle`=`0`, expireAfterAccess(Time To Idle)
     * - `max-size`=`50000`, cache size
     * </pre>
     *
     * @see #Key$common
     */
    private Conf common;
    public static final String Key$common = Key + ".common";

    /**
     * Note, Server using hazelcast will ignore the common setting to avoid non-cache IMap errors.
     * level setting, you need to use wildcard like `program~*`, see WingsCache naming and separator
     *
     * @see #Key$level
     */
    private Map<String, Conf> level = new HashMap<>();
    public static final String Key$level = Key + ".level";

    @Data
    public static class Conf {
        /**
         * expireAfterWrite(Time To Live) seconds
         */
        private int maxLive = 3600;
        /**
         * expireAfterAccess(Time To Idle) seconds
         */
        private int maxIdle = 0;
        /**
         * cache size
         */
        private int maxSize = 0;
    }
}
