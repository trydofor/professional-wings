package pro.fessional.wings.slardar.cache;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

import static pro.fessional.wings.slardar.cache.WingsCache.Splitter;

/**
 * @author trydofor
 * @since 2021-02-11
 */
@Data
public class WingsCacheConfig {

    private String primary = WingsCache.Manager.Memory;
    private boolean nulls = false;

    private int maxLive = 3600;
    private int maxIdle = 0;
    private int maxSize = 5000;

    private Map<String, Level> level = new HashMap<>();

    @Data
    public static class Level {
        private int maxLive = 3600;
        private int maxIdle = 0;
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
