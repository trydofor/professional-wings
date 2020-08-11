package pro.fessional.wings.slardar.cache;

import com.github.benmanes.caffeine.cache.Caffeine;

import java.util.concurrent.TimeUnit;

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

    /**
     * 创建一个caffeine cache
     *
     * @param max  最大值
     * @param ttl  存货秒数
     * @param idle 空闲秒数
     * @return cache
     */
    public static Caffeine<Object, Object> caffeine(int max, long ttl, long idle) {
        return Caffeine.newBuilder()
                       .maximumSize(max <= 0 ? Integer.MAX_VALUE : max)
                       .expireAfterWrite(ttl <= 0 ? Integer.MAX_VALUE : ttl, TimeUnit.SECONDS)
                       .expireAfterAccess(idle <= 0 ? Integer.MAX_VALUE : idle, TimeUnit.SECONDS);
    }
}
