package pro.fessional.wings.slardar.spring.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import pro.fessional.wings.slardar.cache.WingsCache;

import java.util.HashMap;
import java.util.Map;

import static pro.fessional.wings.slardar.cache.WingsCache.Splitter;

/**
 * @author trydofor
 * @see #Key
 * @since 2021-02-11
 */
@Data
@ConfigurationProperties(SlardarCacheProp.Key)
public class SlardarCacheProp {

    public static final String Key = "wings.slardar.cache";

    /**
     * 哪个CacheManager为primary: Memory | Server
     *
     * @see #Key$primary
     */
    private String primary = WingsCache.Manager.Memory;
    public static final String Key$primary = Key + ".primary";

    /**
     * 是否允许缓存null
     *
     * @see #Key$nulls
     */
    private boolean nulls = false;
    public static final String Key$nulls = Key + ".nulls";

    /**
     * expireAfterWrite(Time To Live) seconds
     *
     * @see #Key$maxLive
     */
    private int maxLive = 3600;
    public static final String Key$maxLive = Key + ".max-live";

    /**
     * expireAfterAccess(Time To Idle) seconds
     *
     * @see #Key$maxIdle
     */
    private int maxIdle = 0;
    public static final String Key$maxIdle = Key + ".max-idle";

    /**
     * cache size
     *
     * @see #Key$maxSize
     */
    private int maxSize = 0;
    public static final String Key$maxSize = Key + ".max-size";

    /**
     * 不同的缓存级别
     *
     * @see #Key$level
     */
    private Map<String, Level> level = new HashMap<>();
    public static final String Key$level = Key + ".level";

    @Data
    public static class Level {
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


    public static int maxInt(int max) {
        return max <= 0 ? Integer.MAX_VALUE : max;
    }

    public static String wildcard(String level) {
        return level + Splitter + "*";
    }

    public static boolean inLevel(String name, String level) {
        if (name == null || level == null) return false;
        final int len = level.length();
        return name.regionMatches(true, 0, level, 0, len)
                && name.regionMatches(true, len, Splitter, 0, Splitter.length());
    }
}
