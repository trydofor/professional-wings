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
        public static final String Memory = "MemoryCacheManager";
        public static final String Server = "ServerCacheManager";
    }

    class Level {
        public static final String Forever = "forever" + Splitter;
        public static final String General = "general" + Splitter;
        public static final String Service = "service" + Splitter;
        public static final String Session = "session" + Splitter;
    }
}
