package pro.fessional.wings.silencer.cache;

/**
 * 不使用enum或interface，可继承使用
 *
 * @author trydofor
 * @since 2020-07-30
 */
public class WingsCache {

    public static class Manager {
        public static final String CAFFEINE = "caffeineCacheManager";
        public static final String REDISSON = "redissonCacheManager";
    }

    public static class Level {
        public static final String PROGRAM = "program.";
        public static final String GENERAL = "general.";
        public static final String SERVICE = "service.";
        public static final String SESSION = "session.";
    }
}
