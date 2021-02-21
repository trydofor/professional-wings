package pro.fessional.wings.slardar.cache;

/**
 * 不使用enum或interface，可继承使用
 *
 * @author trydofor
 * @since 2020-07-30
 */
public interface WingsCache {

    String Splitter = ":";

    class Manager {
        /**
         * 内存缓存，默认 caffeine
         */
        public static final String Memory = "MemoryCacheManager";
        /**
         * 外部服务缓存，默认hazelcast
         */
        public static final String Server = "ServerCacheManager";
    }

    class Level {
        /**
         * 程序级，程序或服务运行期间
         */
        public static final String Forever = "program" + Splitter;
        /**
         * 通常，1天
         */
        public static final String General = "general" + Splitter;
        /**
         * 服务级，1小时
         */
        public static final String Service = "service" + Splitter;
        /**
         * 会话级，10分钟
         */
        public static final String Session = "session" + Splitter;
    }
}
