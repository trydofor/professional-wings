package pro.fessional.wings.slardar.cache;

import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Set;

/**
 * 不使用enum或interface，可继承使用
 *
 * @author trydofor
 * @since 2020-07-30
 */
public interface WingsCache {

    /**
     * <a href="https://github.com/cache2k/cache2k/issues/201">valid chars</a>
     */
    String Joiner = "~";

    /**
     * 以此结尾表示扩展为实现类
     */
    String Extend = "!";

    class Manager {
        /**
         * 内存缓存，默认 cache2k
         */
        public static final String Memory = "MemoryCacheManager";
        /**
         * 外部服务缓存，默认hazelcast，可选用redis
         */
        public static final String Server = "ServerCacheManager";
    }

    class Resolver {

        public static final String _Suffix = "Resolver";

        /**
         * 内存缓存，默认 cache2k
         */
        public static final String Memory = Manager.Memory + _Suffix;
        /**
         * 外部服务缓存，默认hazelcast，可选用redis
         */
        public static final String Server = Manager.Server + _Suffix;
    }

    class Level {
        /**
         * 程序级，程序或服务运行期间
         */
        public static final String Forever = "program" + Joiner;
        /**
         * 通常，1天
         */
        public static final String General = "general" + Joiner;
        /**
         * 服务级，1小时
         */
        public static final String Service = "service" + Joiner;
        /**
         * 会话级，10分钟
         */
        public static final String Session = "session" + Joiner;

        public static String join(String... part) {
            return String.join(Joiner, part);
        }
    }

    interface State {
        /**
         * 获得 缓存的name及size
         *
         * @return name-size
         */
        @NotNull
        Map<String, Integer> statsCacheSize();

        /**
         * 获得缓存keys
         *
         * @param name 名字
         * @return keys
         */
        @NotNull
        Set<Object> statsCacheKeys(String name);
    }
}
