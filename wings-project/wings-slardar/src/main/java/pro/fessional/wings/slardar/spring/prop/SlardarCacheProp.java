package pro.fessional.wings.slardar.spring.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import pro.fessional.wings.slardar.cache.WingsCache;

import java.util.HashMap;
import java.util.Map;

import static pro.fessional.wings.slardar.cache.WingsCache.Joiner;

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
     * 如何统一处理对null的缓存。weak:以Weak引用缓存; skip:不缓存null；其他值则不统一处理
     *
     * @see #Key$nulls
     */
    private String nulls = "weak";
    public static final String Key$nulls = Key + ".nulls";

    public boolean isNullWeak() {
        return "weak".equalsIgnoreCase(nulls);
    }

    public boolean isNullSkip() {
        return "skip".equalsIgnoreCase(nulls);
    }

    /**
     * level之外的默认配置
     *
     * @see #Key$common
     */
    private Conf common;
    public static final String Key$common = Key + ".common";

    /**
     * 不同的缓存级别
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

    // /////////////////

    public static int maxInt(int max) {
        return max <= 0 ? Integer.MAX_VALUE : max;
    }

    public static String wildcard(String level) {
        return WingsCache.Level.join(level, "*");
    }

    public static boolean inLevel(String name, String level) {
        if (name == null || level == null) return false;
        final int len = level.length();
        return name.regionMatches(true, 0, level, 0, len)
               && name.regionMatches(true, len, Joiner, 0, Joiner.length());
    }
}
